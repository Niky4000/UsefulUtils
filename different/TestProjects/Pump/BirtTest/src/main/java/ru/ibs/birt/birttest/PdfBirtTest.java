/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.birt.birttest;

import java.util.logging.Level;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;

/**
 *
 * @author User
 */
public class PdfBirtTest {

    public void executeReport() throws EngineException {

        IReportEngine engine = null;
        EngineConfig config = null;

        try {
            config = new EngineConfig();
            config.setBIRTHome("C:\\birthome");
            config.setLogConfig("C:\\birthome", Level.FINEST);
            Platform.startup(config);
            final IReportEngineFactory FACTORY = (IReportEngineFactory) Platform
                    .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
            engine = FACTORY.createReportEngine(config);

            // Open the report design
            IReportRunnable design = null;
            design = engine.openReportDesign("C:\\birt\\BirtTest\\new_report.rptdesign");
            IRunAndRenderTask task = engine.createRunAndRenderTask(design);
            // task.setParameterValue("Top Count", (new Integer(5)));
            // task.validateParameters();

//        final HTMLRenderOption HTML_OPTIONS = new HTMLRenderOption();       
//        HTML_OPTIONS.setOutputFileName("output/resample/Parmdisp.html");
//        HTML_OPTIONS.setOutputFormat("html");
            // HTML_OPTIONS.setHtmlRtLFlag(false);
            // HTML_OPTIONS.setEmbeddable(false);
            // HTML_OPTIONS.setImageDirectory("C:\\test\\images");
            PDFRenderOption PDF_OPTIONS = new PDFRenderOption();
            PDF_OPTIONS.setOutputFileName("C:\\birthome\\out.pdf");
            PDF_OPTIONS.setOutputFormat("pdf");

            task.setRenderOption(PDF_OPTIONS);
            task.run();
            task.close();
            engine.destroy();
        } catch (final Exception EX) {
            EX.printStackTrace();
        } finally {
            Platform.shutdown();
        }
    }
}
