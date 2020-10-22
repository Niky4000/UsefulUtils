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
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import ru.ibs.pmp.reports.ReportServiceImpl;
import ru.ibs.pmp.reports.config.GlobalConfig;
import ru.ibs.pmp.reports.engine.EngineFactory;
import ru.ibs.pmp.reports.engine.EngineFactoryImpl;
import ru.ibs.pmp.reports.engine.ReportTemplate;
import ru.ibs.testpumputils.utils.FieldUtil;

/**
 *
 * @author me
 */
public class ReportServiceImplTest {

    public static void test() throws Exception {
        class ReportServiceImplExt extends ReportServiceImpl {

            @Override
            protected void loadTemplateParameters(ReportTemplate template) {
            }

            public ReportServiceImplExt() {
                cachedTemplates = Collections.synchronizedMap(new LinkedHashMap<String, ReportTemplate>());
                templatesByScope = new LinkedHashMap<String, List<String>>();
            }

            @Override
            public void loadTemplates() throws IOException {
                super.loadTemplates(); //To change body of generated methods, choose Tools | Templates.
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
            public File getPathToConfigs() throws IOException {
                return new File("/home/me/GIT/pmp/pmp/module-reports-pmp/src/main/resources/reports");
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

    public static void test2() throws Exception {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        new File("/home/me/tmp/reports").toPath().register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                System.out.println("Event kind:" + event.kind() + ". File affected: " + event.context() + ".");
            }
            key.reset();
        }
    }
}
