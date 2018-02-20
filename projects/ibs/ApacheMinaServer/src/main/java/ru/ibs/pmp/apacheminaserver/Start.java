package ru.ibs.pmp.apacheminaserver;

import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author NAnishhenko
 */
public class Start {

    ApplicationContext applicationContext;

    public Start(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void start() throws Exception {
        Object bean = applicationContext.getBean("propertyConfigurer");
        ApacheMinaServer sshServer = applicationContext.getBean(ApacheMinaServer.class);
        sshServer.createSshServer();
        new LinkedBlockingQueue<Object>().take();
    }

    public static void main(String[] args) throws Exception {
        Start start = new Start(new ClassPathXmlApplicationContext("module.xml"));
        start.start();
    }
}
