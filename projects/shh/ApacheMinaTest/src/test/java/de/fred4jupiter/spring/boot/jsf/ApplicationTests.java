package de.fred4jupiter.spring.boot.jsf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Map.Entry;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.ibs.pmp.apachemina.ApacheMinaClient;
import ru.ibs.pmp.apachemina.ApacheMinaServer;

public class ApplicationTests {

    private ApplicationContext applicationContext;

    String clientDir = "D:\\tmp\\zzzClient";
    String serverDir = "D:\\tmp\\zzzServer";
    String clientFileName = "Client.txt";
    String serverFileName = "Server.txt";
    String clientFile = clientDir + "\\" + clientFileName;
    String serverFile = serverDir + "\\" + serverFileName;

    @Test
    public void contextLoads() throws FileNotFoundException, IOException, Exception {
        File file = new File("runtime.properties");
        String absolutePath = file.getAbsolutePath();
        Properties properties = new Properties();
        properties.load(new FileInputStream(file));
        for (Entry<Object, Object> entry : properties.entrySet()) {
            System.setProperty((String) entry.getKey(), (String) entry.getValue());
        }
        applicationContext = new ClassPathXmlApplicationContext("module.xml");
        Assert.assertNotNull(applicationContext);
        ApacheMinaServer sshServer = applicationContext.getBean(ApacheMinaServer.class);
        ApacheMinaClient sshClient = applicationContext.getBean(ApacheMinaClient.class);
        sshServer.createSshServer();
//        new File(dir).mkdirs(); // debug stub!
        sshClient.createSshClient("127.0.0.1", properties.getProperty("server.user.name"), properties.getProperty("server.user.password"), Integer.valueOf(properties.getProperty("server.port")), new String[]{"mkdir " + clientDir});
        sshClient.createSshClient("127.0.0.1", properties.getProperty("server.user.name"), properties.getProperty("server.user.password"), Integer.valueOf(properties.getProperty("server.port")), new String[]{"mkdir " + serverDir});
        Assert.assertTrue(new File(clientDir).exists());
        Assert.assertTrue(new File(serverDir).exists());
        Files.write(new File(clientFile).toPath(), "Hello Client!!!".getBytes(), StandardOpenOption.CREATE_NEW);
        Files.write(new File(serverFile).toPath(), "Hello Server!!!".getBytes(), StandardOpenOption.CREATE_NEW);
        sshClient.scpFrom("127.0.0.1", Integer.valueOf(properties.getProperty("server.port")), properties.getProperty("server.user.name"), properties.getProperty("server.user.password"), serverFile, clientDir);
        sshClient.scpTo("127.0.0.1", Integer.valueOf(properties.getProperty("server.port")), properties.getProperty("server.user.name"), properties.getProperty("server.user.password"), serverDir + "\\" + clientFileName, clientFile);
//        sshClient.scpTo("172.29.4.26", 22, "mls", "%TGB5tgb", "/tmp/ZZXXXX.txt", clientFile);
        Assert.assertTrue(new File(clientDir + "\\" + serverFileName).exists());
        Assert.assertTrue(new File(serverDir + "\\" + clientFileName).exists());
    }

    @Before
    public void begin() throws IOException {
        FileUtils.deleteDirectory(new File(clientDir));
        FileUtils.deleteDirectory(new File(serverDir));
    }

    @After
    public void finish() throws IOException {
        FileUtils.deleteDirectory(new File(clientDir));
        FileUtils.deleteDirectory(new File(serverDir));
    }

}
