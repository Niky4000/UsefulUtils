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
import ru.ibs.testpumputils.bean.UnloadZipBean;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.cxf.helpers.FileUtils;
import org.hibernate.Session;
import ru.ibs.pmp.common.lib.Db;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;

/**
 *
 * @author me
 */
public class UnloadDbfs {

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
        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        Date period = new SimpleDateFormat("yyyy-MM-dd").parse("2020-11-01");
//        String ogrn = "1027739099772";
        String ogrn = "1027739449913";
        Map<String, File> pdfFileMap = getPdfFileMap("/home/me/tmp/zip2");
        try {
            List<UnloadZipBean> dbList = Db.select(sessionFactory, session -> (List<UnloadZipBean>) session.createSQLQuery("select ma.id,ma.name,ma.payload,ma.mo_id from PMP_MAILGW_ATTACHMENT ma \n"
                    + "inner join pmp_mailgw_log ml on ml.id=ma.log_entry_id and ml.period=ma.period and ml.mo_id=ma.mo_id\n"
                    + "inner join pmp_parcel p on p.id=ml.parcel_id and p.period=ml.period and p.lpu_id=ml.mo_id\n"
                    + "inner join pmp_bill b on b.last_send_parcel_id=p.id and b.mo_id=p.lpu_id and b.period=p.period\n"
                    + "left join MOSPRSMO smo on smo.q_ogrn=b.payer_ogrn\n"
                    + "where ma.period=:period and direction=:direction and b.payer_ogrn=:ogrn").addEntity(UnloadZipBean.class)
                    .setParameter("period", period).setParameter("direction", "OUT").setParameter("ogrn", ogrn).list());
            for (UnloadZipBean obj : dbList) {
                Long id = obj.getId();
                String name = obj.getName();
                byte[] bytes = obj.getPayload();
                String lpuId = obj.getMoId();
                new File("C:\\tmp\\parcels\\" + lpuId + "\\").mkdirs();
                File zipArchieve = new File("C:\\tmp\\parcels\\" + lpuId + "\\" + name);
                Files.write(bytes, zipArchieve);
                if (replaceFileInArchieve(zipArchieve, lpuId, pdfFileMap)) {
                    byte[] fileData = Files.toByteArray(zipArchieve);
                    int executeUpdate = Db.update(sessionFactory, session -> session.createSQLQuery("update PMP_MAILGW_ATTACHMENT set payload=:data where id=:id and mo_id=:lpuId and period=:period").setParameter("data", fileData).setParameter("id", id).setParameter("lpuId", lpuId).setParameter("period", period).executeUpdate());
                    if (executeUpdate == 1) {
                        System.out.println("lpuId = " + lpuId + " was updated!");
                    }
                }
                System.out.println("lpuId = " + lpuId + " name = " + name + "!");
            };
        } finally {
            sessionFactory.cleanSessions();
            sessionFactory.close();
        }
    }

    public static boolean replaceFileInArchieve(File zipArchieve, String lpuId, Map<String, File> pdfFileMap) throws IOException {
        if (pdfFileMap.containsKey(lpuId)) {
            final File fileForReplacement = pdfFileMap.get(lpuId);
//            File newZipArchieve = new File(zipArchieve.getAbsolutePath() + "_2");
//            newZipArchieve.delete();
            File unzipFolder = new File(zipArchieve.getParentFile().getAbsolutePath() + File.separator + "tmp");
            unzipFolder.mkdirs();
            unZipIt(zipArchieve.getAbsolutePath(), unzipFolder.getAbsolutePath(), file -> {
                if (file.getName().endsWith(".pdf")) {
                    try {
                        Files.copy(fileForReplacement, file);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
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

    public static Map<String, File> getPdfFileMap(String directory) {
        Map<String, File> fileMap = Arrays.asList(new File(directory).listFiles()).stream().filter(dir -> dir.isDirectory()).filter(dir -> dir.getName().length() == 4).map(dir -> dir.listFiles()).filter(fileArray -> fileArray.length == 1).map(fileArray -> fileArray[0]).collect(Collectors.toMap(file -> file.getParentFile().getName(), file -> file));
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
                    replaceFunction.accept(newFile);
                } else {
                    newFile.mkdirs();
                }
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            System.out.println("Done");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
