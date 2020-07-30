package ru.elementlab;

import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;

public class BirtDynamicImageExample {

	public static void main(String[] args) throws Exception{
		Class.forName("oracle.jdbc.OracleDriver");
		Platform.startup();
		IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(
				IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
		IReportEngine birtEngine = factory.createReportEngine(new EngineConfig());
		IReportRunnable design=birtEngine.openReportDesign("new_report.rptdesign");
		IRunAndRenderTask birtTask = birtEngine.createRunAndRenderTask(design);
		PDFRenderOption options = new PDFRenderOption();
		options.setOutputFormat(PDFRenderOption.OUTPUT_FORMAT_PDF);
		birtTask.setRenderOption(options);
		birtTask.run();
		System.out.println("create task success");
	}
}
