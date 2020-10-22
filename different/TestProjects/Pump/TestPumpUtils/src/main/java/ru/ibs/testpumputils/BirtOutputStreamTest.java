/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import com.google.common.collect.ImmutableMap;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.core.framework.PlatformFileContext;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.springframework.util.LinkedMultiValueMap;
import ru.ibs.pmp.reports.ReportConfigServiceImpl;
import ru.ibs.pmp.reports.ReportServiceImpl;
import ru.ibs.pmp.reports.common.ReportCommon;
import ru.ibs.pmp.reports.common.impl.ReportCommonImpl;
import ru.ibs.pmp.reports.controller.ReportControllerForInternalUsageOnly;
import ru.ibs.pmp.reports.engine.EngineContext;
import ru.ibs.pmp.reports.engine.EngineFactory;
import ru.ibs.pmp.reports.engine.EngineFactoryImpl;
import ru.ibs.pmp.reports.engine.Report;
import ru.ibs.pmp.reports.engine.ReportBuildTask;
import ru.ibs.pmp.reports.engine.ReportEngine;
import ru.ibs.pmp.reports.engine.ReportParameter;
import ru.ibs.pmp.reports.engine.ReportParameterType;
import ru.ibs.pmp.reports.engine.ReportTemplate;
import ru.ibs.pmp.reports.engine.impl.BirtReportEngineImpl;
import ru.ibs.pmp.reports.model.ReportOutputFormat;
import ru.ibs.testpumputils.utils.FieldUtil;

/**
 *
 * @author me
 */
public class BirtOutputStreamTest {

    public static void test() {
        new BirtOutputStreamTest().createReport();
    }

    public static void test2() throws IOException {
        BirtReportEngineImpl birtReportEngineImpl = new BirtReportEngineImpl();
        Map<String, IReportRunnable> birtCachedDesigns = new HashMap<>();
        FieldUtil.setField(birtReportEngineImpl, birtCachedDesigns, "birtCachedDesigns");
        ReportConfigServiceImpl reportConfigService = new ReportConfigServiceImpl();
        FieldUtil.setField(birtReportEngineImpl, reportConfigService, "reportConfigService");
        FieldUtil.setField(reportConfigService, "/home/me/tmp/", "pathToResultFolder");
        ReportBuildTask task = new ReportBuildTask();
        Report report = new Report();
        task.setReport(report);
        report.setParameters(Arrays.asList(new ReportParameter(ReportParameterType.STRING, "moId", "8888"), new ReportParameter(ReportParameterType.STRING, "login", "7777")));
        report.setReportFileName("report.pdf");
        task.setOutputFormat(ReportOutputFormat.PDF);
        ReportTemplate template = new ReportTemplate();
        template.setFormats(Arrays.asList(ReportOutputFormat.PDF));
        template.setFileName("/home/me/VMwareShared/bugcollector.rptdesign");
        birtReportEngineImpl.prepareEngine(new EngineContext());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        birtReportEngineImpl.buildReport(task, byteArrayOutputStream, template);
        File file = new File("/home/me/tmp/report.pdf");
        if (!file.exists()) {
            file.createNewFile();
        }
        Files.write(file.toPath(), byteArrayOutputStream.toByteArray(), StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static void test3() throws IOException {
        ReportControllerForInternalUsageOnly controller = new ReportControllerForInternalUsageOnly();
        Map<String, ReportTemplate> cachedTemplates = Collections.synchronizedMap(new LinkedHashMap<String, ReportTemplate>());
        Map<String, List<String>> templatesByScope = new LinkedHashMap<String, List<String>>();
        ReportServiceImpl reportService = new ReportServiceImpl() {
            @Override
            public ReportEngine getEngine(ReportBuildTask task, ReportTemplate template) {
                BirtReportEngineImpl birtReportEngineImpl = new BirtReportEngineImpl();
                Map<String, IReportRunnable> birtCachedDesigns = new HashMap<>();
                FieldUtil.setField(birtReportEngineImpl, birtCachedDesigns, "birtCachedDesigns");
                ReportConfigServiceImpl reportConfigService = new ReportConfigServiceImpl();
                FieldUtil.setField(birtReportEngineImpl, reportConfigService, "reportConfigService");
                FieldUtil.setField(reportConfigService, "/home/me/tmp/", "pathToResultFolder");
                birtReportEngineImpl.prepareEngine(new EngineContext());
                return birtReportEngineImpl;
            }

        };
        ReportCommon reportCommon = new ReportCommonImpl() {
            @Override
            public Set<String> getUserActions() {
                return new HashSet<>(Arrays.asList("action"));
            }
        };
        ReportTemplate template = new ReportTemplate();
        template.setFormats(Arrays.asList(ReportOutputFormat.PDF));
        template.setFileName("/home/me/VMwareShared/bugcollector.rptdesign");
        template.setAccessActions(Arrays.asList("action"));
        template.setId("bugcollector");
        cachedTemplates.put(template.getId(), template);
        templatesByScope.put(scope, Arrays.asList("bugcollector"));
        FieldUtil.setField(reportService, ReportServiceImpl.class, reportCommon, "reportCommon");
        FieldUtil.setField(reportService, ReportServiceImpl.class, cachedTemplates, "cachedTemplates");
        FieldUtil.setField(reportService, ReportServiceImpl.class, templatesByScope, "templatesByScope");
        EngineFactory engineFactory = new EngineFactoryImpl();
        FieldUtil.setField(reportService, ReportServiceImpl.class, engineFactory, "engineFactory");
        FieldUtil.setField(controller, scope, "scope");
        FieldUtil.setField(controller, reportService, "reportService");
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap(ImmutableMap.<String, List<String>>builder().put("moId", Arrays.asList("8888")).put("login", Arrays.asList("7777")).build());
//        controller.createReport("bugcollector", "user", "password", ReportOutputFormat.PDF.name(), params);
    }
    private static final String scope = "ru.ibs.pmp.arm.oms.controller.ReportController";

    private EngineConfig prepareEngineConfig() {
        EngineConfig engineConfig = new EngineConfig();
        engineConfig.getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, Thread.currentThread().getContextClassLoader());
        engineConfig.setEngineHome("");
        engineConfig.setPlatformContext(new PlatformFileContext());

//		if (fontConfigLocation != null)
//			try {
//				engineConfig.setFontConfig(new File(fontConfigLocation).toURI().toURL());
//			} catch (MalformedURLException e) {
//				throw new RuntimeException(e);
//			}
//		else {
//			LOGGER.info("You must set property 'runtime.report.birt.fontconfig.location' "
//					+ "in tomcat/modules/conf/runtime.properties");
//		}
        return engineConfig;
    }

    public void createReport() {
        EngineConfig engineConfig = prepareEngineConfig();

        try {
            Platform.startup(engineConfig);
        } catch (BirtException e) {
            throw new IllegalStateException(e);
        }
        IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);

        IReportEngine birtReportEngine = factory.createReportEngine(engineConfig);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IReportRunnable design;
        try {
            //Open report design
            design = birtReportEngine.openReportDesign("/home/me/VMwareShared/bugcollector.rptdesign");
            //create task to run and render report
            IRunAndRenderTask task = birtReportEngine.createRunAndRenderTask(design);
//            task.getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, WebReport.class.getClassLoader()); 
//            task.getAppContext().put("BIRT_VIEWER_HTTPSERVLET_REQUEST", req );          
            task.setParameterValue("moId", "1865");
            task.setParameterValue("login", "Me");
            RenderOption options = new RenderOption();
            options.setOutputFormat("pdf");
            options.setOutputStream(byteArrayOutputStream);
            //options.setEnableAgentStyleEngine(true);
            //options.setEnableInlineStyle(true);
            task.setRenderOption(options);
            //run report
            task.run();
            task.close();
            File file = new File("/home/me/tmp/report.pdf");
            if (!file.exists()) {
                file.createNewFile();
            }
            Files.write(file.toPath(), byteArrayOutputStream.toByteArray(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
