/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package startup;

import java.io.IOException;

/**
 *
 * @author NAnishhenko
 */
public class Startup {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length == 1 && args[0].equals("-d")) {
            openDirs();
        } else {
            for (int i = 1; i <= 20; i++) {
                Thread.sleep(1000);
                System.out.println("Wait " + i + "...");
            }
            Runtime.getRuntime().exec("cmd.exe /c start D:\\GIT\\UsefulUtils\\projects\\ibs\\Startup\\tomcat_script.cmd");
            Runtime.getRuntime().exec("notepad D:\\Documents\\catalina_opts.txt");
            Thread.sleep(4000);
            Runtime.getRuntime().exec("cmd.exe /c start D:\\GIT\\UsefulUtils\\projects\\ibs\\Startup\\build_path.cmd");
            Thread.sleep(4000);
            openDirs();
            Thread.sleep(100);
            Runtime.getRuntime().exec("C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe");
//        Runtime.getRuntime().exec("D:\\Distributives\\sqldeveloper\\sqldeveloper.exe");
            Runtime.getRuntime().exec("C:\\Program Files (x86)\\Notepad++\\notepad++.exe");
        }
    }

    private static void openDirs() throws InterruptedException, IOException {
        Runtime.getRuntime().exec("explorer D:\\GIT\\pmp\\tools\\appserver\\tomcat");
        Thread.sleep(4000);
        Runtime.getRuntime().exec("explorer D:\\GIT\\pmp\\pmp\\build");
        Thread.sleep(4000);
        Runtime.getRuntime().exec("explorer D:\\GIT\\pmp\\tools\\appserver\\tomcat\\modules\\logs");
        Thread.sleep(4000);
        Runtime.getRuntime().exec("explorer D:\\GIT");
        Thread.sleep(4000);
        Runtime.getRuntime().exec("explorer D:\\Documents\\Project");
        Thread.sleep(4000);
        Runtime.getRuntime().exec("explorer D:\\tmp");
        Thread.sleep(4000);
        Runtime.getRuntime().exec("explorer D:\\GIT\\pmp\\pmp");
    }

    private static void executeCommand(String command) throws IOException, InterruptedException {
        Process child = Runtime.getRuntime().exec(command);
        child.waitFor();
    }

}
