/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import com.github.fluent.hibernate.cfg.scanner.EntityScanner;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import ru.ibs.testpumputils.bean.UnloadZipBean;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.cxf.helpers.FileUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.ibs.pmp.api.model.msk.export.MailGwAttachment;
import ru.ibs.pmp.api.model.msk.export.MailGwLogEntry;
import ru.ibs.pmp.common.lib.Db;
import ru.ibs.testpumputils.bean.UnloadZipBean2;
import ru.ibs.testpumputils.bean.UnloadZipBean3;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;

/**
 *
 * @author me
 */
public class UnloadDbfs2 {

    static SessionFactoryInterface sessionFactory;

    public static void unload() throws Exception {
        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        Date period = new SimpleDateFormat("yyyy-MM-dd").parse("2020-11-01");
//        String ogrn = "1027739099772";
        String ogrn = "1027739449913";
        try {
            Session session = sessionFactory.openSession();
            try {
                List<UnloadZipBean> dbList = (List<UnloadZipBean>) session.createSQLQuery("select ma.id,ma.name,ma.payload,ma.mo_id from PMP_MAILGW_ATTACHMENT ma \n"
                        + "inner join pmp_mailgw_log ml on ml.id=ma.log_entry_id and ml.period=ma.period and ml.mo_id=ma.mo_id\n"
                        + "inner join pmp_parcel p on p.id=ml.parcel_id and p.period=ml.period and p.lpu_id=ml.mo_id\n"
                        + "inner join pmp_bill b on b.last_send_parcel_id=p.id and b.mo_id=p.lpu_id and b.period=p.period\n"
                        + "left join MOSPRSMO smo on smo.q_ogrn=b.payer_ogrn\n"
                        + "where ma.period=:period and direction=:direction and b.payer_ogrn=:ogrn").addEntity(UnloadZipBean.class)
                        .setParameter("period", period).setParameter("direction", "OUT").setParameter("ogrn", ogrn).list();
                for (UnloadZipBean obj : dbList) {
                    String name = obj.getName();
                    byte[] bytes = obj.getPayload();
                    String lpuId = obj.getMoId();
                    new File("C:\\tmp\\parcels\\" + lpuId + "\\").mkdirs();
                    Files.write(bytes, new File("C:\\tmp\\parcels\\" + lpuId + "\\" + name));
                    System.out.println("lpuId = " + lpuId + " name = " + name + "!");
                };
            } finally {
                session.close();
            }
        } finally {
            sessionFactory.cleanSessions();
            sessionFactory.close();
        }
    }

    public static void replace() throws Exception {
        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(buildSessionFactory(), new SqlRewriteInterceptorExt()));
        Date period = new SimpleDateFormat("yyyy-MM-dd").parse("2020-11-01");
//        String ogrn = "1027739099772";
        String ogrn = "1027739449913";
        Map<String, File> pdfFileMap = getPdfFileMap2("C:\\tmp\\parcels4acts");
        Map<String, File> pdfFileMap2 = getPdfFileMap2("C:\\tmp\\parcels5protocols");
        try {
            List<UnloadZipBean2> dbList = Db.select(sessionFactory, session -> (List<UnloadZipBean2>) session.createSQLQuery("select mailgw_log_id,mo_id from pmp_bill b where period = :period and b.payer_ogrn=:ogrn and mailgw_log_id is not null order by mailgw_log_id asc").addEntity(UnloadZipBean2.class).setParameter("period", period).setParameter("ogrn", ogrn).list());
            for (UnloadZipBean2 obj : dbList) {
                Long id = obj.getMailgwLogId();
//                String name = obj.getName();
//                byte[] bytes = obj.getPayload();
                String lpuId = obj.getMoId();
//                new File("C:\\tmp\\parcels\\" + lpuId + "\\").mkdirs();
//                File zipArchieve = new File("C:\\tmp\\parcels\\" + lpuId + "\\" + name);
//                Files.write(bytes, zipArchieve);
                if (pdfFileMap.containsKey(lpuId)) {
                    final File fileForReplacement = pdfFileMap.get(lpuId);
                    handleReplacement(id, lpuId, period, fileForReplacement);
                }
                if (pdfFileMap2.containsKey(lpuId)) {
                    final File fileForReplacement2 = pdfFileMap2.get(lpuId);
                    handleReplacement(id, lpuId, period, fileForReplacement2);
                }
//                if (replaceFileInArchieve(zipArchieve, lpuId, pdfFileMap, pdfFileMap2)) {
//                    byte[] fileData = Files.toByteArray(zipArchieve);
//                    int executeUpdate = Db.update(sessionFactory, session -> session.createSQLQuery("update PMP_MAILGW_ATTACHMENT set payload=:data where id=:id and mo_id=:lpuId and period=:period").setParameter("data", fileData).setParameter("id", id).setParameter("lpuId", lpuId).setParameter("period", period).executeUpdate());
//                    if (executeUpdate == 1) {
//                        System.out.println("lpuId = " + lpuId + " was updated!");
//                    }
//                }
//                System.out.println("lpuId = " + lpuId + "!");
            }
            Set<String> lpuIdSet = dbList.stream().map(UnloadZipBean2::getMoId).collect(Collectors.toSet());
            lpuIdSet.forEach(lpuId -> pdfFileMap.remove(lpuId));
            lpuIdSet.forEach(lpuId -> pdfFileMap2.remove(lpuId));
            printMap(pdfFileMap, "pdfFileMap");
            printMap(pdfFileMap2, "pdfFileMap2");
        } finally {
            sessionFactory.cleanSessions();
            sessionFactory.close();
        }
    }

    private static void printMap(Map<String, File> pdfFileMap, String mapName) {
        if (pdfFileMap.isEmpty()) {
            System.out.println(mapName + " is Empty!");
        } else {
            for (Entry<String, File> entry : pdfFileMap.entrySet()) {
                System.out.println("entry: " + entry.getKey() + "!");
            }
        }
    }

    private static void handleReplacement(Long id, String lpuId, Date period, final File fileForReplacement) throws IOException {
        List<UnloadZipBean3> fileNameList = Db.select(sessionFactory, session -> session.createSQLQuery("select id,name from PMP_MAILGW_ATTACHMENT where log_entry_id=:id and mo_id=:lpuId and period=:period").addEntity(UnloadZipBean3.class).setParameter("id", id).setParameter("lpuId", lpuId).setParameter("period", period).list());
        Map<String, UnloadZipBean3> fileNameMap = fileNameList.stream().collect(Collectors.toMap(UnloadZipBean3::getName, obj_ -> obj_));
//                    Set<String> fileNameSet = fileNameList.stream().map(UnloadZipBean3::getName).collect(Collectors.toSet());
        String modifiedName = modifyName(fileForReplacement.getName());
        final byte[] fileData = Files.toByteArray(fileForReplacement);
        if (fileNameMap.containsKey(modifiedName)) {
            UnloadZipBean3 bean = fileNameMap.get(modifiedName);
            Db.update(session -> {
                MailGwAttachment mailGwAttachment = (MailGwAttachment) session.get(MailGwAttachment.class, bean.getId());
                mailGwAttachment.setPayload(fileData);
                session.merge(mailGwAttachment);
            }, sessionFactory);
        } else {
            Db.update(session -> {
                MailGwLogEntry mailGwLogEntry = (MailGwLogEntry) session.get(MailGwLogEntry.class, id);
                MailGwAttachment mailGwAttachment = new MailGwAttachment();
                mailGwAttachment.setLogEntry(mailGwLogEntry);
                mailGwAttachment.setMoId(Long.valueOf(lpuId));
                mailGwAttachment.setName(modifiedName);
                mailGwAttachment.setPayload(fileData);
                mailGwAttachment.setPeriod(period);
                session.merge(mailGwAttachment);
//                    session.createSQLQuery("INSERT INTO PMP_MAILGW_ATTACHMENT (ID, LOG_ENTRY_ID, NAME, PAYLOAD, PERIOD, MO_ID) VALUES (:id, :mailGwLogId, :fileName, :fileData, :period, :moId)");
            }, sessionFactory);
        }
        System.out.println("lpuId = " + lpuId + ": file " + fileForReplacement.getName() + " was added!");
    }

    public static boolean replaceFileInArchieve(File zipArchieve, String lpuId, Map<String, File> pdfFileMap, Map<String, File> pdfFileMap2) throws IOException {
        if (pdfFileMap.containsKey(lpuId)) {
            final File fileForReplacement = pdfFileMap.get(lpuId);
            final File fileForReplacement2 = pdfFileMap2.get(lpuId);
//            File newZipArchieve = new File(zipArchieve.getAbsolutePath() + "_2");
//            newZipArchieve.delete();
            File unzipFolder = new File(zipArchieve.getParentFile().getAbsolutePath() + File.separator + "tmp");
            unzipFolder.mkdirs();
            unZipIt(zipArchieve.getAbsolutePath(), unzipFolder.getAbsolutePath(), file -> {
                try {
                    Files.copy(fileForReplacement, new File(file.getAbsolutePath() + File.separator + modifyName(fileForReplacement.getName())));
                    Files.copy(fileForReplacement2, new File(file.getAbsolutePath() + File.separator + modifyName(fileForReplacement2.getName())));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            List<String> filePathList = Arrays.asList(unzipFolder.listFiles()).stream().map(file -> file.getAbsolutePath()).collect(Collectors.toList());
            zipFiles(filePathList, zipArchieve.getAbsolutePath());
            FileUtils.removeDir(unzipFolder);
            return true;
        } else {
            return false;
        }
    }

    private static String modifyName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf(".")) + "_2" + ".pdf";
    }

    public static Map<String, File> getPdfFileMap(String directory) {
        Map<String, File> fileMap = Arrays.asList(new File(directory).listFiles()).stream().filter(dir -> dir.isDirectory()).filter(dir -> dir.getName().length() == 4).map(dir -> dir.listFiles()).filter(fileArray -> fileArray.length == 1).map(fileArray -> fileArray[0]).collect(Collectors.toMap(file -> file.getParentFile().getName(), file -> file));
        return fileMap;
    }

    public static Map<String, File> getPdfFileMap2(String directory) {
        Map<String, File> fileMap = Arrays.asList(new File(directory).listFiles()).stream().filter(dir -> !dir.isDirectory()).filter(dir -> dir.getName().endsWith(".pdf")).collect(Collectors.toMap(file -> file.getName().substring(3, 7), file -> file));
        return fileMap;
    }

    private static void zipFiles(List<String> filesPathes, String zipFilePath) throws IOException {
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
            for (String filePath : filesPathes) {
                File file = new File(filePath);
                if (file.exists()) {
                    out.putNextEntry(new ZipEntry(file.getName()));
                    Files.copy(file, out);
                    out.closeEntry();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void unZipIt(String zipFile, String outputFolder, Consumer<File> replaceFunction) {
        byte[] buffer = new byte[1024];
        try {
            //create output directory is not exists
            File folder = new File(outputFolder);
            if (!folder.exists()) {
                folder.mkdir();
            }
            //get the zip file content
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);
                System.out.println("file unzip : " + newFile.getAbsoluteFile());
                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                if (!ze.isDirectory()) {
                    new File(newFile.getParent()).mkdirs();
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                } else {
                    newFile.mkdirs();
                }
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            replaceFunction.accept(folder);
            System.out.println("Done");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static SessionFactory buildSessionFactory() throws FileNotFoundException, IOException {
        // Create the SessionFactory from hibernate.cfg.xml
        Properties p = new Properties();
        p.load(new FileInputStream(new File(System.getProperty("pmp.config.path"))));
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.url", p.getProperty("runtime.pmp.db.url"));
        configuration.setProperty("hibernate.connection.username", p.getProperty("runtime.pmp.db.username"));
        configuration.setProperty("hibernate.connection.password", p.getProperty("runtime.pmp.db.password"));
        EntityScanner.scanPackages("ru.ibs.testpumputils.bean", "ru.ibs.pmp.api.model", "ru.ibs.pmp.api.model.msk.export").addTo(configuration);
        configuration.configure();
//        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
//        return configuration.buildSessionFactory(serviceRegistry);
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        return sessionFactory;
    }

    public static void unload2() throws Exception {
        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        Date period = new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-01");
//        String ogrn = "1027739099772";
//        String ogrn = "1027739449913";
        Long smoId = 2884L;
        try {
            Session session = sessionFactory.openSession();
            try {
                List<UnloadZipBean> dbList = (List<UnloadZipBean>) session.createSQLQuery("SELECT rownum as id, MIO.MESSAGE_FILE_NAME, MIO.LPU_ID, MIO.MESSAGE_FILE\n"
                        + "FROM MSG_IN_OUT_CONNECTION_FILES MIO\n"
                        + "WHERE MIO.PERIOD = :period\n"
                        + "AND SMO_ID = :smoId\n"
                        + "AND MIO.RESP_NUM_PER_BILL = :num").addEntity(UnloadZipBean.class)
                        .setParameter("period", period).setParameter("smoId", smoId).setParameter("num", 1).list();
                for (UnloadZipBean obj : dbList) {
                    String name = obj.getName();
                    byte[] bytes = obj.getPayload();
                    String lpuId = obj.getMoId();
                    new File("C:\\tmp\\parcels\\" + lpuId + "\\").mkdirs();
                    Files.write(bytes, new File("C:\\tmp\\parcels\\" + lpuId + "\\" + name));
                    System.out.println("lpuId = " + lpuId + " name = " + name + "!");
                };
            } finally {
                session.close();
            }
        } finally {
            sessionFactory.cleanSessions();
            sessionFactory.close();
        }
    }
}
