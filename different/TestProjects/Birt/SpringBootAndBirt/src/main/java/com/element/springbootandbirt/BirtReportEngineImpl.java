package com.element.springbootandbirt;

import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.core.framework.PlatformFileContext;
import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.eclipse.core.internal.registry.RegistryProviderFactory;

@Slf4j
public class BirtReportEngineImpl {

    private String fontConfigLocation;
    private final static String BIRT_TEMPLATE_FILE_EXTENSION = ".rptdesign";
    private final static String ENGINE_EXCEPTION_MSG_TEXT = "Ошибка движка отчетов: ";

    // Контекст движка
    private EngineContext context;

    // Непосредственно движок бирта
    private IReportEngine birtEngine;

    // Закешированные дизайны шаблонов
    private Map<String, IReportRunnable> birtCachedDesigns;

    void init() {
        birtCachedDesigns = new HashMap<String, IReportRunnable>();
        prepareEngine(new EngineContext());
    }

    // ******************************************************************************************************************
    public void prepareEngine(EngineContext context) throws IllegalStateException, IllegalArgumentException {
        if (context == null) {
            throw new IllegalArgumentException("Engine context is NULL.");
        }
        this.context = context;
        // Конфигурим движок
        EngineConfig engineConfig = prepareEngineConfig(this.context);
        try {
            Platform.startup(engineConfig);
        } catch (BirtException e) {
            throw new IllegalStateException(e);
        }
        FontFactory.register("fonts/SansSerifFLF.otf");
        Font font = FontFactory.getFont("fonts/SansSerifFLF.otf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(
                IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
        birtEngine = factory.createReportEngine(engineConfig);
    }

	public void buildReport(String reportContent, List<ReportParameter> parameters, String outputFormat, String outpuFile) {
		buildReport(reportContent, parameters, outputFormat, (options) -> options.setOutputFileName(outpuFile));
	}
	
    public void buildReport(String reportContent, List<ReportParameter> parameters, String outputFormat, Consumer<IRenderOption> optionsConsumer) {
        if (birtEngine == null) {
            init();
        }
        IRunAndRenderTask birtTask = null;
        try {
            // TODO: Какой-то прям костыль получился... сделать композицию
            // Template в Report!
            IReportRunnable design = getReportDesign(reportContent);
            birtTask = birtEngine.createRunAndRenderTask(design);
            for (ReportParameter parameter : parameters) {
                if (parameter != null) {
                    // TODO: Временный workaround для проблемы некорректной
                    // десериализации java.sql.Date в KryoNet
                    birtTask.setParameterValue(parameter.getName(), parameter.getValue());
                }
            }
            birtTask.setRenderOption(prepareRenderOptions(outputFormat, optionsConsumer));
            birtTask.run();
        } catch (Exception engEx) {
            log.error(ENGINE_EXCEPTION_MSG_TEXT, engEx);
            throw new RuntimeException(ENGINE_EXCEPTION_MSG_TEXT, engEx);
        }
        @SuppressWarnings("unchecked") List birtErrors = birtTask.getErrors();
        if (birtTask != null) {
            birtTask.close();
        }
        if (birtErrors != null && birtErrors.size() > 0) {
            throw new RuntimeException(String.join("\n", (List<String>) birtErrors.stream()
                                                                                  .map(birtError -> birtError.toString())
                                                                                  .collect(Collectors.toList())));
        }
    }

    /**
     * Метод возвращает дизайн отчета. Дизайн берется их кеша, или загружается
     * средствами BIRT, если он отсутствует в кеше
     *
     * @param reportContent - содержимое отчета
     * @return - дизайн отчета {@link IReportRunnable}
     * @throws EngineException - исключение движка
     */
    private IReportRunnable getReportDesign(String reportContent) throws EngineException, IOException {
        IReportRunnable design = null;
        String newReportContent = reportContent.replaceFirst("<report .+? version=\".+?>",
                                                             "<report xmlns=\"http://www.eclipse.org/birt/2005/design\" version=\"3.2" +
                                                                     ".23\" id=\"1\">")
                                               .replaceAll("pathToJar", FileUtils.getPathToJar()
                                                                                 .getAbsolutePath());
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(newReportContent.getBytes());
        design = birtEngine.openReportDesign(byteArrayInputStream);
        return design;
    }

	private IRenderOption prepareRenderOptions(String outputFormat, Consumer<IRenderOption> optionsConsumer) throws IllegalArgumentException {
		IRenderOption options = null;
		if ("HTML".equalsIgnoreCase(outputFormat)) {
			options = prepareHTMLRenderOptions();
		} else if ("PDF".equalsIgnoreCase(outputFormat)) {
			options = preparePDFRenderOptions();
		} else if ("XLS".equalsIgnoreCase(outputFormat)) {
			options = prepareXLSRenderOptions();
		} else if ("DOC".equalsIgnoreCase(outputFormat)) {
			options = prepareDOCRenderOptions();
		} else if ("XLSX".equalsIgnoreCase(outputFormat)) {
			options = prepareXLSXRenderOptions();
		} else if ("XML".equalsIgnoreCase(outputFormat)) {
			options = prepareXMLRenderOptions();
		} else {
			throw new IllegalArgumentException("Not supported output format:" + outputFormat);
		}
		optionsConsumer.accept(options);
//        options.setOutputFileName(outputFile);
		return options;
	}

    private IRenderOption prepareHTMLRenderOptions() {
        HTMLRenderOption options = new HTMLRenderOption();
        options.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_HTML);
        options.setImageHandler(new HTMLServerImageHandler());
        return options;
    }

    private IRenderOption preparePDFRenderOptions() {
        PDFRenderOption options = new PDFRenderOption();
        options.setOutputFormat(PDFRenderOption.OUTPUT_FORMAT_PDF);
        return options;
    }

    private IRenderOption prepareXLSRenderOptions() {
        return prepareExcelOrtion("xls");
    }

    private IRenderOption prepareXLSXRenderOptions() {
        return prepareExcelOrtion("xlsx");
    }

    private IRenderOption prepareXMLRenderOptions() {
        return prepareXMLOrtion("xml");
    }

    private IRenderOption prepareExcelOrtion(String format) {
        EXCELRenderOption options = new EXCELRenderOption();
        options.setOutputFormat(format);
        return options;
    }

    private IRenderOption prepareDOCRenderOptions() {
        HTMLRenderOption options = new HTMLRenderOption();
        options.setOutputFormat("doc");
        return options;
    }

    private IRenderOption prepareXMLOrtion(String format) {
        throw new IllegalStateException("xml format is not supported");
    }

    @SuppressWarnings("unchecked")
    private EngineConfig prepareEngineConfig(EngineContext context) {
        EngineConfig engineConfig = new EngineConfig();
        engineConfig.getAppContext()
                    .put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, Thread.currentThread()
                                                                           .getContextClassLoader());
        engineConfig.setEngineHome("");
        engineConfig.setPlatformContext(new PlatformFileContext());
        if (fontConfigLocation != null) {
            try {
                engineConfig.setFontConfig(new File(fontConfigLocation).toURI()
                                                                       .toURL());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        } else {
            log.info("You must set property 'runtime.report.birt.fontconfig.location' in tomcat/modules/conf/runtime.properties");
        }
        return engineConfig;
    }

    // Метод корректно уничтожает ресурсы бирта
    private void destroyEngine() {
        if (birtEngine != null) {
            birtEngine.destroy();
            Platform.shutdown();
            RegistryProviderFactory.releaseDefault();
            birtEngine = null;
        }
    }

    public String getSupportedTemplateFileExtansion() {
        return BIRT_TEMPLATE_FILE_EXTENSION;
    }

    public void reset() {
        birtCachedDesigns.clear();
    }
}
