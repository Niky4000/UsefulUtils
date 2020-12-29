/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.Properties;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 *
 * @author me
 */
public class DownloadService {

//  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadService.class);
    private static final String SCAN_SERVICE_CERT_PATH = "C:/tmp/emias/PpakScan.jks";
    private static final String SCAN_SERVICE_CERT_PASS = "836529";
    

    private String url;
    private long interval = 1000;

    public DownloadService(String url, File dir) {
        this.url = url;
//        getClass().getClassLoader().getResource(url);
        init();
    }

    public void init() {
        Properties props = System.getProperties();
        props.setProperty("javax.net.ssl.trustStore", SCAN_SERVICE_CERT_PATH);
        props.setProperty("javax.net.ssl.trustStorePassword", SCAN_SERVICE_CERT_PASS);
        props.setProperty("javax.net.ssl.keyStore", SCAN_SERVICE_CERT_PATH);
        props.setProperty("javax.net.ssl.keyStorePassword", SCAN_SERVICE_CERT_PASS);
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String string, SSLSession ssls) {
                return true;
            }
        });
    }

    private void download() {
        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, Paths.get("./image.jpg"), REPLACE_EXISTING);
        } catch (IOException e) {
            interval += 1000;
//      LOGGER.error(String.format("I/O excetion, the check interval was increased up to %d ms", interval),e);
            e.printStackTrace();
        } catch (Exception e) {
//      LOGGER.error(String.format("Unexpected errror; certPath=%s", SCAN_SERVICE_CERT_PATH), e);
            e.printStackTrace();
        }
    }

}
