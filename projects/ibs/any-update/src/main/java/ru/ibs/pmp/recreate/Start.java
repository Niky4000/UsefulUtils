package ru.ibs.pmp.recreate;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.ibs.pmp.recreate.file.updater.FileUpdater;

/**
 * @author NAnishhenko
 */
public class Start {

    ApplicationContext applicationContext;

    public Start(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void start() throws Exception {
        FileUpdater fileUpdater = applicationContext.getBean(FileUpdater.class);
        fileUpdater.update();
    }

    public static void main(String[] args) throws Exception {
        Start start = new Start(new ClassPathXmlApplicationContext("module.xml"));
        start.start();
    }
}
