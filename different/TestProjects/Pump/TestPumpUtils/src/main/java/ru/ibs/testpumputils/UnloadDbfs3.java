package ru.ibs.testpumputils;

import com.google.common.io.Files;
import fr.opensagres.xdocreport.core.io.IOUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;
import org.apache.cxf.helpers.FileUtils;
import org.hibernate.Session;
import ru.ibs.testpumputils.bean.UnloadZipBean4;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;

/**
 *
 * @author me
 */
public class UnloadDbfs3 {

    public void unload4() throws Exception {
        SessionFactoryInterface sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        try {
//            unload4(sessionFactory, "/u01/parcelsForTestUpload", new SimpleDateFormat("yyyy-MM-dd").parse("2021-03-01"), new HashSet<>(Arrays.asList("S5", "ИН")), "CORP", "IBS_ERZL", "ERZL100!", "smb://hq-fs01/MGFOMS/PUMP-EXPORT/");
//            File zipFile = new File("/u01/parcelsForTestUpload/2021_02.zip");
//            copySmpFile("CORP", "IBS_ERZL", "ERZL100!", "smb://hq-fs01/MGFOMS/PUMP-EXPORT/" + zipFile.getName(), zipFile);
//            unload4(sessionFactory, "C:\\tmp\\parcelsForTestUpload", new SimpleDateFormat("yyyy-MM-dd").parse("2021-03-01"), new HashSet<>(Arrays.asList("ВМП", "ПД-ИН", "ПД", "ИН", "НИЛ", "СМП")), "CORP", "IBS_ERZL", "ERZL100!", "smb://192.168.195.26/MGFOMS/PUMP-EXPORT/");
            unload4(sessionFactory, "C:\\tmp\\parcelsForTestUpload", new SimpleDateFormat("yyyy-MM-dd").parse("2021-03-01"), new HashSet<>(Arrays.asList("ВМП", "ПД-ИН", "ПД", "ИН", "НИЛ", "СМП")), "CORP", "IBS_ERZL", "ERZL100!", "smb://192.168.192.106/pump-import/");
//            File zipFile = new File("/u01/parcelsForTestUpload/2021_02.zip");
//            copySmpFile("CORP", "IBS_ERZL", "ERZL100!", "smb://192.168.195.26/MGFOMS/PUMP-EXPORT/" + zipFile.getName(), zipFile);
        } finally {
            sessionFactory.cleanSessions();
            sessionFactory.close();
        }
    }

    private static final String RESPONSE = "response";
    private static final String MESSAGE = "message";

    public void unload4(SessionFactoryInterface sessionFactory, String dir, Date period, Set<String> qqList, String domain, String user, String password, String remotePath) throws Exception {
        Session session = sessionFactory.openSession();
        try {
            new File(dir).mkdirs();
//            List<UnloadZipBean4> dbList = (List<UnloadZipBean4>) session.createSQLQuery("SELECT rownum as id, MIO.SMO_NAME as qq,LPU_ID,MIO.MESSAGE_FILE,MIO.MESSAGE_FILE_NAME,RESPONSE_FILE_NAME,RESPONSE_FILE\n"
//                    + "FROM MSG_IN_OUT_CONNECTION_FILES MIO\n"
//                    + "WHERE MIO.PERIOD = :period --Период\n"
//                    + "AND MIO.QQ IN (:qqList) --список СМО\n"
//                    + "AND MIO.RESP_NUM_PER_BILL = 1 --критерий последнего ответа\n"
//                    + "AND MSG_NUM_PER_BILL = 1 --Критерий последней посылки\n"
//                    + "ORDER BY MIO.SMO_NAME,LPU_ID").addEntity(UnloadZipBean4.class)
//                    .setParameter("period", period).setParameterList("qqList", qqList)
//                    .list();
            List<UnloadZipBean4> dbList = (List<UnloadZipBean4>) session.createSQLQuery("SELECT rownum as id,SMO_NAME as qq,LPU_ID,MESSAGE_FILE,MESSAGE_FILE_NAME,RESPONSE_FILE_NAME,RESPONSE_FILE\n"
                    + "FROM\n"
                    + "(\n"
                    + "SELECT MIO.*,MIN (RESP_NUM_PER_BILL ) OVER (PARTITION BY LPU_ID,SMO_ID,PERIOD) MIN_RESPONSE --Последний ответ из отобранных\n"
                    + "FROM MSG_IN_OUT_CONNECTION_FILES MIO\n"
                    + "WHERE\n"
                    + "MSG_NUM_PER_BILL=1 --критерий последней посылки\n"
                    + "AND STATUS IN ('ACCEPTED') --Принято\n"
                    + "AND RESPONSE_STATUS IN ('OK') --Критерий наличия ответа\n"
                    + "AND RESP_NUM_PER_MSG=1 -- Критерий последнего ответа\n"
                    + "AND PERIOD=:period\n"
                    + "AND MIO.QQ IN (:qqList) --список СМО\n"
                    + "--AND SMO_ID IS NULL\n"
                    + ") MIO_RESPONSE\n"
                    + "WHERE MIO_RESPONSE.RESP_NUM_PER_BILL=MIO_RESPONSE.MIN_RESPONSE\n"
                    + "ORDER BY PERIOD,SMO_ID,LPU_ID").addEntity(UnloadZipBean4.class)
                    .setParameter("period", period).setParameterList("qqList", qqList)
                    .list();
            List<File> dirsToRemove = new ArrayList<>(dbList.size());
            List<String> filesPathes = new ArrayList<>(dbList.size() * 2);
            for (UnloadZipBean4 obj : dbList) {
                String qq = obj.getQq();
                String messageFileName = obj.getMessageFileName();
                byte[] messageFile = obj.getMessageFile();
                String responseFileName = obj.getResponseFileName();
                byte[] responseFile = obj.getResponseFile();
                Long lpuId = obj.getLpuId();
                dirsToRemove.add(new File(dir + File.separator + lpuId));
                String handledQq = handleQq(qq);
                new File(dir + File.separator + lpuId + File.separator + handledQq + File.separator + MESSAGE).mkdirs();
                new File(dir + File.separator + lpuId + File.separator + handledQq + File.separator + RESPONSE).mkdirs();
                File messageFilePath = new File(dir + File.separator + lpuId + File.separator + handledQq + File.separator + MESSAGE + File.separator + messageFileName);
                File responseFilePath = new File(dir + File.separator + lpuId + File.separator + handledQq + File.separator + RESPONSE + File.separator + responseFileName);
                try {
                    Files.write(messageFile, messageFilePath);
                    Files.write(responseFile, responseFilePath);
                    filesPathes.add(lpuId + File.separator + handledQq + File.separator + MESSAGE + File.separator + messageFileName);
                    filesPathes.add(lpuId + File.separator + handledQq + File.separator + RESPONSE + File.separator + responseFileName);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
//                System.out.println("lpuId = " + lpuId + "!");
            };
            File zipFile = new File(dir + File.separator + new SimpleDateFormat("yyyy_MM").format(period) + ".zip");
            if (zipFile.exists()) {
                zipFile.delete();
            }
            zipFiles(dir + File.separator, filesPathes, zipFile.getAbsolutePath());
            dirsToRemove.stream().forEach(dirToRemove -> FileUtils.removeDir(dirToRemove));
            copySmpFile(domain, user, password, remotePath, zipFile.getName(), zipFile);
            zipFile.delete();
        } finally {
            session.close();
        }
    }

    public void zipFiles(String dir, List<String> filesPathes, String zipFilePath) throws IOException {
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
                        ee.printStackTrace();
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

    public Integer getFileCountDir(String domain, String user, String password, String userDir) {
        Integer cnt = 0;
//        String period1 = formatD.format(period);
//        Map<String,String> mp = updNpjt.query("select \n" +
//            "      max(case when S_NAME='FORM8_UPLOAD_DIR_USER'     then s_value else null end) USER_NAME \n" +
//            "    , max(case when S_NAME='FORM8_UPLOAD_DIR_PASSWORD' then s_value else null end) USER_PASSWORD \n" +
//            "    , max(case when S_NAME='FORM8_UPLOAD_DIR_DOMAIN'   then s_value else null end) USER_DOMAIN \n" +
//            "    , max(case when S_NAME='FORM8_UPLOAD_DIR_NETWORK'  then s_value else null end) USER_DIR \n" +
//            "from erzl_ws.soft_info",
//            new MapSqlParameterSource()
//            , new ResultSetExtractor<Map<String,String>>() {
//                public Map<String,String> extractData(ResultSet resultSetObj) throws SQLException, DataAccessException {
//                    Map<String,String> mp = new HashMap<>();
//                    while(resultSetObj.next()) {
//                        mp.put("USER_NAME", resultSetObj.getString("USER_NAME") );
//                        mp.put("USER_PASSWORD", resultSetObj.getString("USER_PASSWORD") );
//                        mp.put("USER_DOMAIN", resultSetObj.getString("USER_DOMAIN") );
//                        mp.put("USER_DIR", resultSetObj.getString("USER_DIR") );
//                    }
//                    return mp;
//                }
//            }
//        );
        try {

            // Создаем объект для аутентификации на шаре
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(domain, user, password);

            SmbFile baseDir = new SmbFile(userDir, auth);

            // Вычитываем все содержимое шары в массив
            SmbFile[] files = baseDir.listFiles();

            // Делаем что-нибудь со списком файлов
            for (int i = 0; i < files.length; i++) {
                SmbFile file = files[i];
                if (file.isDirectory()) {
                    //System.out.println("Is DIR: "+file.toString());
                    continue;
                } else {
                    String fileSource = file.toString();
                    if (fileSource.toUpperCase().lastIndexOf(".XML") > 0) {
                        cnt++;
                    }
                }
            }

        } catch (Exception e) {
//            String msg = "Ошибка при обращении к папке по Samba: " + e.getLocalizedMessage();
//            LOG.error( msg, e);
//            log(period1, rev,"",msg);
//            throw new ResultException(new ErrorSubject("900", msg));
            throw new RuntimeException();
        }
//        log(period1, rev,"Файлов в папке для копирование ="+cnt,"");
        return cnt;
    }

//    public void copyDirSamba(Date period, Long rev) throws ResultException {
//        String period1 = formatD.format(period);
//        try{
//            updNpjt.execute("{call erzl_wsr.form8_queue_api.SET_QUEUE('COPY_DIR_SAMBA', :PERIOD,:REV)}"
//                , new MapSqlParameterSource("PERIOD", period1).addValue("REV", rev)
//                , new PreparedStatementCallback<Boolean>() {
//                    @Override
//                    public Boolean doInPreparedStatement(PreparedStatement ps)
//                            throws SQLException, DataAccessException {
//                        return ps.execute();
//                    }
//                }
//            );
//        }catch (Exception e) {
//            String msg = "Ошибка при постановке заявки на копирование по Samba: " + e.getLocalizedMessage();
//            LOG.error( msg, e);
//            log(period1, rev,"",msg);
//            throw new ResultException(new ErrorSubject("900", msg));
//        }
//        log(period1, rev,"Создана завка на копирование файлов","");
//   }
}
