package ru.kiokle.birttest2;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.core.framework.PlatformFileContext;
import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;

public class Birt2Start {

	private IReportEngine engine;

	public static void main(String[] args) throws Exception {
		Birt2Start birt2Start = new Birt2Start();
		birt2Start.start(new File("/home/me/tmp/reports/testReport.xls"), new File("/home/me/GIT/ferzl/mpi-reporter/src/main/resources/reports/STATISTICS.rptdesign"), new File("/home/me/tmp/reports/data.csv"));
	}

	public void start(File outputFile, File rptdesign, File fileURI) throws BirtException, IOException {
		EngineConfig engineConfig = prepareEngineConfig();
		Platform.startup(engineConfig);
		IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
		engine = factory.createReportEngine(engineConfig);
		String rptDesignStr = new String(new FileInputStream(rptdesign).readAllBytes());
		String replacement = rptDesignStr.replace("<report xmlns=\"http://www.eclipse.org/birt/2005/design\" version=\"3.2.24\" id=\"1\">", "<report xmlns=\"http://www.eclipse.org/birt/2005/design\" version=\"3.2.23\" id=\"1\">");
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(replacement.getBytes());
		IReportRunnable reportDesign = engine.openReportDesign(byteArrayInputStream);
		Map<String, String> parameters = new HashMap<>();
		parameters.put("fileURI", fileURI.getAbsolutePath());
		parameters.put("pageFooter", getPageFooter(new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
		buildReport(reportDesign, parameters, "xls", outputFile.getAbsolutePath());
	}
	public static final String source = "ФЕРЗЛ АИС ОМС";

	private String getPageFooter(String date) {
		return source + " " + date;
	}

	private EngineConfig prepareEngineConfig() {
		EngineConfig engineConfig = new EngineConfig();
		engineConfig.getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, Birt2Start.class.getClassLoader());
		engineConfig.setPlatformContext(new PlatformFileContext());
		return engineConfig;
	}

	private IRenderOption prepareRenderOptions(String outputFormat, String outputFile) throws IllegalArgumentException {
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
		options.setOutputFileName(outputFile);
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
		return prepareExcelOption("xls");
	}

	private IRenderOption prepareXLSXRenderOptions() {
		return prepareExcelOption("xlsx");
	}

	private IRenderOption prepareXMLRenderOptions() {
		return prepareXMLOption("xml");
	}

	private IRenderOption prepareExcelOption(String format) {
		EXCELRenderOption options = new EXCELRenderOption();
		options.setOutputFormat(format);
		return options;
	}

	private IRenderOption prepareDOCRenderOptions() {
		HTMLRenderOption options = new HTMLRenderOption();
		options.setOutputFormat("doc");
		return options;
	}

	private IRenderOption prepareXMLOption(String format) {
		throw new IllegalStateException("xml format is not supported");
	}

	private void buildReport(IReportRunnable design, Map<String, String> parameters, String outputFormat, String outputFile) {
		IRunAndRenderTask task = null;
		try {
//			IReportRunnable design = designs.get(reportDesign);
			task = engine.createRunAndRenderTask(design);
			task.setRenderOption(prepareRenderOptions(outputFormat, outputFile));
			for (Map.Entry<String, String> entry : parameters.entrySet()) {
				task.setParameterValue(entry.getKey(), entry.getValue());
			}
			task.run();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			if (task != null) {
				task.close();
			}
		}
	}
}
