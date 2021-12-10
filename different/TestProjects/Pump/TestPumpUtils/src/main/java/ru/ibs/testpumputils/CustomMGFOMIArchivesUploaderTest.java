/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Proxy;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.helpers.FileUtils;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.ibs.pmp.auth.model.SmoEntity;
import ru.ibs.pmp.reports.engine.Report;
import ru.ibs.pmp.reports.engine.ReportBuildTask;
import ru.ibs.pmp.reports.engine.ReportParameter;
import ru.ibs.pmp.reports.engine.ReportParameterType;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;

/**
 *
 * @author me
 */
public class CustomMGFOMIArchivesUploaderTest {

	static SessionFactoryInterface sessionFactory;

	public static void test() throws Exception {
		sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
		try {
			ReportBuildTask buildTask = new ReportBuildTask();
			Report report = new Report();
			report.setParameters(Arrays.asList(new ReportParameter(ReportParameterType.STRING, "period", "2021-11")));
			buildTask.setReport(report);
			Session session = sessionFactory.openSession();
			try {
				new CustomMGFOMIArchivesUploaderTest().buildReport(buildTask, session);
			} finally {
				session.close();
			}
		} finally {
			sessionFactory.cleanSessions();
			sessionFactory.close();
		}
	}

	private static final String RESPONSE = "response";
	private static final String MESSAGE = "message";

	public void buildReport(ReportBuildTask buildTask, Session session) {
		Map<String, String> softInfoConfigs = getAndSetSoftInfoConfigs(session);
		String dir = softInfoConfigs.get(UPLOAD_DIR_NAME);
		String user = softInfoConfigs.get(UPLOAD_DIR_USER_NAME);
		String password = softInfoConfigs.get(UPLOAD_DIR_PASSWORD_NAME);
		String domain = softInfoConfigs.get(UPLOAD_DIR_DOMAIN_NAME);
		String smbPath = softInfoConfigs.get(UPLOAD_DIR_NETWORK_NAME);
		List<ReportParameter> reportParam = buildTask.getReport().getParameters();
		Map<String, ReportParameter> paramMap = reportParam.stream().collect(Collectors.toMap(ReportParameter::getName, param -> param));
		ReportParameter periodPar = paramMap.get("period");
		Set<String> qqSet = getSmoQqSet(paramMap, session);
		String periodStr = periodPar.getValueAsString();
		try {
			Date period = new SimpleDateFormat("yyyy-MM").parse(periodStr);
			unload(session, dir, period, qqSet, domain, user, password, smbPath);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private Set<String> getSmoQqSet(Map<String, ReportParameter> paramMap, Session session) {
		ReportParameter smoIdPar = paramMap.get("smoMass");
		if (smoIdPar == null || smoIdPar.getValueAsString() == null || smoIdPar.getValueAsString().length() == 0) {
			return getSmoList(session);
		}
		String smoIdStr = smoIdPar.getValueAsString();
		Set<Long> smoIdSet = Arrays.asList(smoIdStr.split(",")).stream().map(Long::valueOf).collect(Collectors.toSet());
		Set<String> qqSet = convertSmoIdToQQ(session, smoIdSet);
		return qqSet;
	}

	private Set<String> getSmoList(Session session) {
		return ((List<SmoEntity>) session.createCriteria(SmoEntity.class).list()).stream().map(SmoEntity::getCode).collect(Collectors.toSet());
	}

	protected static final String UPLOAD_DIR_NAME = "UPLOAD_DIR";
	protected static final String UPLOAD_DIR_USER_NAME = "UPLOAD_DIR_USER";
	protected static final String UPLOAD_DIR_PASSWORD_NAME = "UPLOAD_DIR_PASSWORD";
	protected static final String UPLOAD_DIR_DOMAIN_NAME = "UPLOAD_DIR_DOMAIN";
	protected static final String UPLOAD_DIR_NETWORK_NAME = "UPLOAD_DIR_NETWORK";

	private Set<String> convertSmoIdToQQ(Session session, Collection<Long> idList) {
		List<SmoEntity> smoEntityList = session.createCriteria(SmoEntity.class).add(Restrictions.in("id", idList)).list();
		return smoEntityList.stream().map(SmoEntity::getCode).collect(Collectors.toSet());
	}

	protected Map<String, String> getAndSetSoftInfoConfigs(Session session) {
		List<Object[]> list = session.createSQLQuery("select s_name,s_value from soft_info where s_name in (:names)").setParameterList("names", new String[]{UPLOAD_DIR_NAME, UPLOAD_DIR_USER_NAME, UPLOAD_DIR_PASSWORD_NAME, UPLOAD_DIR_DOMAIN_NAME, UPLOAD_DIR_NETWORK_NAME}).list();
		Map<String, String> valuesMap = list.stream().collect(Collectors.toMap(obj -> (String) obj[0], obj -> (String) obj[1]));
		return valuesMap;
	}

	private void unload(Session session, String dir, Date period, Set<String> qqList, String domain, String user, String password, String remotePath) throws Exception {
		new File(dir).mkdirs();
		final List<File> dirsToRemove = new ArrayList<>();
		final List<String> filesPathes = new ArrayList<>();
		String sql = "SELECT rownum as id,SMO_NAME as qq,LPU_ID,MESSAGE_FILE,MESSAGE_FILE_NAME,RESPONSE_FILE_NAME,RESPONSE_FILE\n"
				+ "FROM\n"
				+ "(\n"
				+ "SELECT MIO.*,MIN(RESP_NUM_PER_BILL) OVER (PARTITION BY LPU_ID,SMO_ID,PERIOD) MIN_RESPONSE --Последний ответ из отобранных\n"
				+ "FROM MSG_IN_OUT_CONNECTION_FILES MIO\n"
				+ "WHERE MIO.PERIOD = ? --Период\n"
				+ "AND MIO.QQ IN (" + qqList.stream().map(str -> "?").reduce((str1, str2) -> str1 + "," + str2).get() + ") --список СМО\n"
				+ "AND MSG_NUM_PER_BILL = 1 --критерий последней посылки\n"
				+ "AND MESSAGE_STATUS IN ('OK')\n"
				+ "AND RESPONSE_STATUS IN ('OK') --Критерий наличия ответа\n"
				+ "AND RESP_NUM_PER_MSG = 1 -- Критерий последнего ответа\n"
				+ ") MIO_RESPONSE\n"
				+ "WHERE MIO_RESPONSE.RESP_NUM_PER_BILL=MIO_RESPONSE.MIN_RESPONSE\n"
				+ "ORDER BY PERIOD,SMO_ID,LPU_ID";
		session.doWork(connection -> {
			try (CallableStatement statement = connection.prepareCall(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
				statement.setDate(1, new java.sql.Date(period.getTime()));
				List<String> qqList2 = new ArrayList<>(qqList);
				for (int i = 0; i < qqList.size(); i++) {
					statement.setString(i + 2, qqList2.get(i));
				}
				try (ResultSet resultSet = statement.executeQuery()) {
					while (resultSet.next()) {
						long id = resultSet.getLong(1);
						String qq = resultSet.getString(2);
						long lpuId = resultSet.getLong(3);
						Blob messageFile = resultSet.getBlob(4);
						String messageFileName = resultSet.getString(5);
						Blob responseFile = resultSet.getBlob(4);
						String responseFileName = resultSet.getString(5);
						dirsToRemove.add(new File(dir + File.separator + lpuId));
						String handledQq = handleQq(qq);
						new File(dir + File.separator + lpuId + File.separator + handledQq + File.separator + MESSAGE).mkdirs();
						new File(dir + File.separator + lpuId + File.separator + handledQq + File.separator + RESPONSE).mkdirs();
						File messageFilePath = new File(dir + File.separator + lpuId + File.separator + handledQq + File.separator + MESSAGE + File.separator + messageFileName);
						File responseFilePath = new File(dir + File.separator + lpuId + File.separator + handledQq + File.separator + RESPONSE + File.separator + responseFileName);
						try {
							writeBlobToFile(messageFile.getBinaryStream(), new FileOutputStream(messageFilePath));
							writeBlobToFile(responseFile.getBinaryStream(), new FileOutputStream(responseFilePath));
							filesPathes.add(lpuId + File.separator + handledQq + File.separator + MESSAGE + File.separator + messageFileName);
							filesPathes.add(lpuId + File.separator + handledQq + File.separator + RESPONSE + File.separator + responseFileName);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				}
			}
		});
		File zipFile = new File(dir + File.separator + new SimpleDateFormat("yyyy_MM").format(period) + ".zip");
		if (zipFile.exists()) {
			zipFile.delete();
		}
		zipFiles(dir + File.separator, filesPathes, zipFile.getAbsolutePath());
		dirsToRemove.stream().forEach(dirToRemove -> FileUtils.removeDir(dirToRemove));
		copySmpFile(domain, user, password, remotePath, zipFile.getName(), zipFile);
		zipFile.delete();
	}

	private static final int BUFFER = 1024 * 1024;

	private void writeBlobToFile(InputStream inputStream, OutputStream outputStream) throws IOException {
		byte[] bytes = new byte[BUFFER];
		int read = 0;
		do {
			read = inputStream.read(bytes);
			if (read < 0) {
				break;
			}
			outputStream.write(bytes, 0, read);
		} while (read > 0);
	}

	private void zipFiles(String dir, List<String> filesPathes, String zipFilePath) throws IOException {
		Map<String, Integer> map = new HashMap<>(filesPathes.size());
		try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
			for (String filePath : filesPathes) {
				File file = new File(dir + filePath);
				if (file.exists()) {
					try {
						Integer i = map.get(filePath);
						String postfix;
						if (i == null) {
							map.put(filePath, 1);
							postfix = "";
						} else {
							i++;
							map.put(filePath, i);
							postfix = "_" + i;
						}
						out.putNextEntry(new ZipEntry(filePath + postfix));
						Files.copy(file, out);
						out.closeEntry();
					} catch (Exception ee) {
						throw new RuntimeException(ee);
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	private String handleQq(String qq) {
		return qq.replace("(", "").replace(")", "").replace("\"", "");
	}

	private void copySmpFile(String domain, String user, String password, String remotePath, String zipFileName, File localFile) throws IOException {
		NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(domain, user, password);
		SmbFile remoteDir = new SmbFile(remotePath, auth);
		checkFreeSpace(remoteDir, localFile);
		SmbFile remoteFile = new SmbFile(remotePath + zipFileName, auth);
		if (remoteFile.exists()) {
			remoteFile.delete();
		}
		SmbFileOutputStream out = new SmbFileOutputStream(remoteFile);
		FileInputStream fis = new FileInputStream(localFile);
		out.write(IOUtils.toByteArray(fis));
		out.close();
	}

	private void checkFreeSpace(SmbFile remoteDir, File localFile) throws SmbException {
		long diskFreeSpace = remoteDir.getDiskFreeSpace();
		long length = localFile.length();
		if (diskFreeSpace - length < 0) {
			List<SmbFile> listFiles = new ArrayList<>(Arrays.asList(remoteDir.listFiles()));
			Collections.sort(listFiles, (file1, file2) -> Long.valueOf(file1.getLastModified()).compareTo(file2.getLastModified()));
			while (diskFreeSpace - length < 0 && !listFiles.isEmpty()) {
				listFiles.get(0).delete();
				listFiles.remove(0);
				diskFreeSpace = remoteDir.getDiskFreeSpace();
			}
		}
	}

	private Integer getFileCountDir(String domain, String user, String password, String userDir) {
		Integer cnt = 0;
		try {
			NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(domain, user, password);
			SmbFile baseDir = new SmbFile(userDir, auth);
			SmbFile[] files = baseDir.listFiles();
			for (int i = 0; i < files.length; i++) {
				SmbFile file = files[i];
				if (file.isDirectory()) {
					continue;
				} else {
					String fileSource = file.toString();
					if (fileSource.toUpperCase().lastIndexOf(".XML") > 0) {
						cnt++;
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}
		return cnt;
	}
}
