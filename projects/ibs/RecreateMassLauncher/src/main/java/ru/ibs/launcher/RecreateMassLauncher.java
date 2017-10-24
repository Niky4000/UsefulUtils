package ru.ibs.launcher;

import com.google.common.collect.Iterables;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author NAnishhenko
 */
//@SpringBootApplication
//@Component
public class RecreateMassLauncher {

    private static final int MAX_PROCESSES = 8;
    private static final String WAIT = "WAIT";
    static boolean isWindowsOS = isWindows();

    public static void main(String[] args) throws Exception {
        if (args.length == 2 && args[0].equals("-f")) {
            File commandsFile = new File(args[1]);
            if (commandsFile.exists()) {
                String[] commands = new String(Files.readAllBytes(commandsFile.toPath())).split("\n");
                List<String> commandList = new ArrayList<>(commands.length);
                Collections.addAll(commandList, commands);
                List<Process> processList = new ArrayList<Process>(MAX_PROCESSES);
                for (List<String> commandsSlice : Iterables.partition(commandList, MAX_PROCESSES)) {
                    for (String command : commandsSlice) {
                        String executeString = command.replace("\r", "").replace("\n", "");
                        if (executeString.equals(WAIT)) {
                            waitForAllProcesses(processList);
                        } else {
                            Process process;
                            if (isWindowsOS) {
                                process = Runtime.getRuntime().exec("cmd.exe /c start /wait " + executeString);
                            } else {
                                process = Runtime.getRuntime().exec(executeString);
                            }
                            processList.add(process);
                        }
                    }
                    waitForAllProcesses(processList);
                }
            } else {
                System.out.println("commandsFile does not exist!");
            }
        } else {
            System.out.println("Usage: -sql [path-to-file with sql generating query]");
        }
    }

    private static void waitForAllProcesses(List<Process> processList) throws InterruptedException {
        for (Process process : processList) {
            process.waitFor();
        }
        processList.clear();
    }

    protected static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().indexOf("win") != -1;
    }
}
