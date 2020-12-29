/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.Arrays;
import java.util.Properties;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 *
 * @author me
 */
public class DownloadService {

    private static final String SCAN_SERVICE_CERT_PASS = "836529";
    private final File dir;
    private final File certificate;
    private String url;

    public DownloadService(String url, File dir) throws URISyntaxException {
        this.url = url;
        this.dir = dir;
        certificate = new File(getPathToRootFolder().getAbsolutePath() + File.separator + "PpakScan.jks");
        init();
    }

    public void init() {
        Properties props = System.getProperties();
        props.setProperty("javax.net.ssl.trustStore", certificate.getAbsolutePath());
        props.setProperty("javax.net.ssl.trustStorePassword", SCAN_SERVICE_CERT_PASS);
        props.setProperty("javax.net.ssl.keyStore", certificate.getAbsolutePath());
        props.setProperty("javax.net.ssl.keyStorePassword", SCAN_SERVICE_CERT_PASS);
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String string, SSLSession ssls) {
                return true;
            }
        });
    }

    public void download() {
        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, new File(dir.getAbsolutePath() + "/kkk").toPath(), REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static File getPathToRootFolder() {
        try {
            File file = new File(DownloadService.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (file.getAbsolutePath().contains("classes")) { // Launched from debugger!
                file = Arrays.stream(file.getParentFile().listFiles()).filter(localFile -> localFile.getName().endsWith(".jar")).findFirst().get();
            }
            File parentFile = file.getParentFile();
            if (parentFile.getAbsolutePath().contains("target")) {
                parentFile = parentFile.getParentFile();
            }
            return parentFile;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
