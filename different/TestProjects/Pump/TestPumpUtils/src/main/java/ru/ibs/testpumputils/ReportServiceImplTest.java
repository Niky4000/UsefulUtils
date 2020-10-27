/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import ru.ibs.pmp.reports.ReportServiceImpl;
import ru.ibs.pmp.reports.config.GlobalConfig;
import ru.ibs.pmp.reports.engine.EngineFactory;
import ru.ibs.pmp.reports.engine.EngineFactoryImpl;
import ru.ibs.pmp.reports.engine.ReportTemplate;
import ru.ibs.pmp.reports.threads.FileSystemObserverThread;
import ru.ibs.pmp.reports.threads.WatchThread;
import ru.ibs.testpumputils.utils.FieldUtil;

/**
 *
 * @author me
 */
public class ReportServiceImplTest {

    private static final String REPORTS_FOLDER = "/home/me/tmp/reports";

    public static void test() throws Exception {
        class ReportServiceImplExt extends ReportServiceImpl {

            @Override
            protected void loadTemplateParameters(ReportTemplate template) {
            }

            public ReportServiceImplExt() {
                cachedTemplates = Collections.synchronizedMap(new LinkedHashMap<String, ReportTemplate>());
                templatesByScope = new LinkedHashMap<String, List<String>>();
                File folder = new File(REPORTS_FOLDER);
                watchThread = new WatchThread((b) -> {
                    cachedTemplates.clear();
                    templatesByScope.clear();
                    loadTemplates();
                }, modificationCounter);
                watchThread.start();
                fileSystemObserverThread1 = new FileSystemObserverThread(1, folder, watchThread, modificationCounter);
                fileSystemObserverThread2 = new FileSystemObserverThread(2, new File(folder.getAbsolutePath() + s + GlobalConfig.MAPPINGS_FILES_SUBFOLDER), watchThread, modificationCounter);
                fileSystemObserverThread1.start();
                fileSystemObserverThread2.start();
            }

            @Override
            public void loadTemplates() {
                super.loadTemplates();
            }
        }
        ReportServiceImplExt reportService = new ReportServiceImplExt();

        class GlobalConfigExt extends GlobalConfig {

            public GlobalConfigExt() {
            }

            @Override
            public void loadConfigs() throws IOException {
                super.loadConfigs();
            }

            @Override
            public File getPathToConfigs() {
                return new File(REPORTS_FOLDER);
            }
        }
        EngineFactory engineFactory = new EngineFactoryImpl();
        FieldUtil.setField(reportService, ReportServiceImpl.class, engineFactory, "engineFactory");
        GlobalConfigExt globalConfigExt = new GlobalConfigExt();
        Field instanceField = GlobalConfig.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, globalConfigExt);
        reportService.loadTemplates();
    }

    private static final String s = FileSystems.getDefault().getSeparator();

    public static void test2() throws Exception {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        File dir = new File(REPORTS_FOLDER);
        dir.toPath().register(watchService, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                System.out.println("Event kind:" + event.kind() + ". File affected: " + event.context() + ".");
                if (event.context() instanceof Path) {
                    File file = ((Path) event.context()).toFile();
                    File modifiedFile = new File(dir.getAbsolutePath() + s + file.getName());
                    System.out.println("file = " + modifiedFile.getAbsolutePath() + " size = " + (modifiedFile.exists() ? modifiedFile.length() : 0) + "!");
                }
                if (event.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                    System.out.println("Modification!");
                } else if (event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                    System.out.println("Deletion!");
                }
            }
            key.reset();
        }
    }

    public static void test3() throws Exception {
        AtomicInteger modificationCounter = new AtomicInteger(0);
        File folder = new File(REPORTS_FOLDER);
        WatchThread watchThread = new WatchThread((b) -> {
            System.out.println("WatchThread was interrupted!");
        }, modificationCounter);
        watchThread.start();
        FileSystemObserverThread fileSystemObserverThread1 = new FileSystemObserverThread(1, folder, watchThread, modificationCounter);
        fileSystemObserverThread1.start();
        Thread.sleep(20 * 1000);
        fileSystemObserverThread1.setInterruptFlag();
//		fileSystemObserverThread2.setInterraptFlag();
        watchThread.setInterruptFlag();
        fileSystemObserverThread1.interrupt();
//		fileSystemObserverThread2.interrupt();
        watchThread.interrupt();
    }
}
