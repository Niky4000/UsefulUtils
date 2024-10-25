package ru.elementlab;

import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;
import java.net.URL;
import java.util.function.Function;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;

public class BirtDynamicImageExample {

    public static void main(String[] args) throws Exception {
        new BirtDynamicImageExample().createReport();
    }

    public void createReport() throws Exception {
        Class.forName("oracle.jdbc.OracleDriver");
//        System.setProperty("birt.font.dirs", "fonts");
//        EngineConfig engineConfig = new EngineConfig();
//        URL fontsConfigurationURL = new URL("file:///fontsConfig.xml");
//URL fontsConfigurationURL = new URL("file:///fonts/");
//        engineConfig.setFontConfig(fontsConfigurationURL);
//        Platform.startup(engineConfig);
        Platform.startup();
        FontFactory.register("fonts/arial.ttf");
        Font font = FontFactory.getFont("fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        FontFactory.register("fonts/MTCORSVA.TTF");
        Font font2 = FontFactory.getFont("fonts/MTCORSVA.TTF", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        FontFactory.register("fonts/SansSerifFLF.otf");
        Font font3 = FontFactory.getFont("fonts/SansSerifFLF.otf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//        FontFactory.register("fonts/sserifer.fon");
//        Font font4 = FontFactory.getFont("fonts/sserifer.fon", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
        IReportEngine birtEngine = factory.createReportEngine(new EngineConfig());
        IReportRunnable design = birtEngine.openReportDesign(getClass().getClassLoader().getResourceAsStream("hello_world.rptdesign"));
        IRunAndRenderTask birtTask = birtEngine.createRunAndRenderTask(design);
        PDFRenderOption options = new PDFRenderOption();
        options.setOutputFormat(PDFRenderOption.OUTPUT_FORMAT_PDF);
        birtTask.setRenderOption(options);
        Function<String, byte[]> barCodeGenerator = s -> BarCodeUtils.generate(s);
        birtTask.getAppContext().put("barCodeGenerator", barCodeGenerator);
        birtTask.run();
        System.out.println("create task success");
    }
}
