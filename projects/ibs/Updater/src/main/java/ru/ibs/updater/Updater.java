package ru.ibs.updater;

import java.io.File;
import java.nio.file.Files;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author NAnishhenko
 */
//@SpringBootApplication
//@Component
public class Updater {

    public static void main(String[] args) throws Exception {
        if (args.length >= 2 && args[0].equals("-sql") && args[1] != null && args[1].length() > 0 && new File(args[1]).exists()) {
//            ApplicationContext context = new AnnotationConfigApplicationContext(ru.ibs.updater.configs.MainConfiguration.class);
            ApplicationContext context = new ClassPathXmlApplicationContext("module.xml");
            System.out.println("---------------------------------------------------");
            System.out.println("---------------------------------------------------");
            UpdaterMain updaterMain = context.getBean(UpdaterMain.class);
            if (args.length == 3 && args[2].equals("-loop")) {
                boolean ret = true;
                while (ret) {
                    ret = updaterMain.update(new String(Files.readAllBytes(new File(args[1]).toPath())), true);
                }
                Thread.sleep(10000);
            } else {
                boolean update = updaterMain.update(new String(Files.readAllBytes(new File(args[1]).toPath())), false);
                if (!update) {
                    Thread.sleep(10000);
                }
            }
        } else {
            System.out.println("Usage: -sql [path-to-file with sql generating query]");
        }
    }

}
