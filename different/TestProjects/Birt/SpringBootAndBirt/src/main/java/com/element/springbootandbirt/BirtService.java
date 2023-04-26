package com.element.springbootandbirt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Service;

@Service
public class BirtService {

	public void run() {
		System.out.println("Hello!");
		try {
//			List<ReportParameter> parameters = Arrays.asList(new ReportParameter("reportPath", "/home/me/eclipse-birt/workspace/test/test.csv"));
//			buildReport("testReport", "xlsx", "/home/me/tmp/reports", "/home/me/eclipse-birt/workspace/test/new_template.rpttemplate", parameters);
			List<ReportParameter> parameters = Arrays.asList(new ReportParameter("reportPath", "/home/me/eclipse-birt/workspace/test/some_report.csv"), new ReportParameter("tableHeadText", "<b>Статистика по методам МПИ</b><br>\n"
					+ "Дата формирования отчёта: 01.04.2023 00:00:00<br>\n"
					+ "На дату: 01.04.2023<br>\n"
					+ "Тип организации: ТФОМС<br>\n"
					+ "Организации: Все"));
			buildReport("testReport", "xlsx", "/home/me/tmp/reports", "/home/me/eclipse-birt/workspace/test/some_report.rptdesign", parameters);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.exit(0);
	}

	public File buildReport(String fileName, String fileExtension, String tmpFolderPath, String reportDesign, List<ReportParameter> parameters) throws IllegalArgumentException, IllegalStateException, FileNotFoundException {
		BirtReportEngineImpl birtReportEngineImpl = new BirtReportEngineImpl();
		birtReportEngineImpl.prepareEngine(new EngineContext());
//		String reportContent = readFile(BirtService.class.getClassLoader().getResourceAsStream(reportDesign));
		String reportContent = readFile(new FileInputStream(new File(reportDesign)));
		File file = new File(tmpFolderPath + File.separator + fileName + "." + fileExtension.toLowerCase());
		birtReportEngineImpl.buildReport(reportContent, parameters, fileExtension.toUpperCase(), file.getAbsolutePath());
		return file;
	}

	private static String readFile(InputStream inputStream) {
		StringBuilder stringBuilder = new StringBuilder();
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
			String readLine = bufferedReader.readLine();
			while (readLine != null) {
				stringBuilder.append(readLine);
				readLine = bufferedReader.readLine();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return stringBuilder.toString();
	}
//
//    @Value("${reports.relative.path}")
//    private String reportsPath;
//    @Value("${images.relative.path}")
//    private String imagesPath;
//
////    private HTMLServerImageHandler htmlImageHandler = new HTMLServerImageHandler();
//
//    @Autowired
//    private ResourceLoader resourceLoader;
//    @Autowired
//    private ServletContext servletContext;
//
//    private IReportEngine birtEngine;
//    private ApplicationContext context;
//    private String imageFolder;
//
//    private Map<String, IReportRunnable> reports = new HashMap<>();
//
//
//    protected void initialize() throws BirtException {
//        EngineConfig config = new EngineConfig();
//        config.getAppContext().put("spring", this.context);
//        Platform.startup(config);
//        IReportEngineFactory factory = (IReportEngineFactory) Platform
//          .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
//        birtEngine = factory.createReportEngine(config);
//        imageFolder = System.getProperty("user.dir") + File.separatorChar + reportsPath + imagesPath;
//        loadReports();
//    }
//
//    public void setApplicationContext(ApplicationContext context) {
//        this.context = context;
//    }
//
//    /**
//     * Load report files to memory
//     *
//     */
//    public void loadReports() throws EngineException {
//        File folder = new File(reportsPath);
//        for (String file : Objects.requireNonNull(folder.list())) {
//            if (!file.endsWith(".rptdesign")) {
//                continue;
//            }
//
//            reports.put(file.replace(".rptdesign", ""),
//              birtEngine.openReportDesign(folder.getAbsolutePath() + File.separator + file));
//
//        }
//    }
//
////    public List<Report> getReports() {
////        List<Report> response = new ArrayList<>();
////        for (Map.Entry<String, IReportRunnable> entry : reports.entrySet()) {
////            IReportRunnable report = reports.get(entry.getKey());
////            IGetParameterDefinitionTask task = birtEngine.createGetParameterDefinitionTask(report);
////            Report reportItem = new Report(report.getDesignHandle().getProperty("title").toString(), entry.getKey());
////            for (Object h : task.getParameterDefns(false)) {
////                IParameterDefn def = (IParameterDefn) h;
////                reportItem.getParameters()
////                  .add(new Report.Parameter(def.getPromptText(), def.getName(), getParameterType(def)));
////            }
////            response.add(reportItem);
////        }
////        return response;
////    }
//
//    private Report.ParameterType getParameterType(IParameterDefn param) {
//        if (IParameterDefn.TYPE_INTEGER == param.getDataType()) {
//            return Report.ParameterType.INT;
//        }
//        return Report.ParameterType.STRING;
//    }
//
//    public void generateMainReport(String reportName, OutputType output, HttpServletResponse response, HttpServletRequest request) {
//        switch (output) {
//        case HTML:
//            generateHTMLReport(reports.get(reportName), response, request);
//            break;
//        case PDF:
//            generatePDFReport(reports.get(reportName), response, request);
//            break;
//        default:
//            throw new IllegalArgumentException("Output type not recognized:" + output);
//        }
//    }
//
//    /**
//     * Generate a report as HTML
//     */
//    @SuppressWarnings("unchecked")
//    private void generateHTMLReport(IReportRunnable report, HttpServletResponse response, HttpServletRequest request) {
//        IRunAndRenderTask runAndRenderTask = birtEngine.createRunAndRenderTask(report);
//        response.setContentType(birtEngine.getMIMEType("html"));
//        IRenderOption options = new RenderOption();
//        HTMLRenderOption htmlOptions = new HTMLRenderOption(options);
//        htmlOptions.setOutputFormat("html");
//        htmlOptions.setBaseImageURL("/" + reportsPath + imagesPath);
//        htmlOptions.setImageDirectory(imageFolder);
//        htmlOptions.setImageHandler(htmlImageHandler);
//        runAndRenderTask.setRenderOption(htmlOptions);
//        runAndRenderTask.getAppContext().put(EngineConstants.APPCONTEXT_BIRT_VIEWER_HTTPSERVET_REQUEST, request);
//
//        try {
//            htmlOptions.setOutputStream(response.getOutputStream());
//            runAndRenderTask.run();
//        } catch (Exception e) {
//            throw new RuntimeException(e.getMessage(), e);
//        } finally {
//            runAndRenderTask.close();
//        }
//    }
//
//    /**
//     * Generate a report as PDF
//     */
//    @SuppressWarnings("unchecked")
//    private void generatePDFReport(IReportRunnable report, HttpServletResponse response, HttpServletRequest request) {
//        IRunAndRenderTask runAndRenderTask = birtEngine.createRunAndRenderTask(report);
//        response.setContentType(birtEngine.getMIMEType("pdf"));
//        IRenderOption options = new RenderOption();
//        PDFRenderOption pdfRenderOption = new PDFRenderOption(options);
//        pdfRenderOption.setOutputFormat("pdf");
//        runAndRenderTask.setRenderOption(pdfRenderOption);
//        runAndRenderTask.getAppContext().put(EngineConstants.APPCONTEXT_PDF_RENDER_CONTEXT, request);
//
//        try {
//            pdfRenderOption.setOutputStream(response.getOutputStream());
//            runAndRenderTask.run();
//        } catch (Exception e) {
//            throw new RuntimeException(e.getMessage(), e);
//        } finally {
//            runAndRenderTask.close();
//        }
//    }
//
//    @Override
//    public void destroy() {
//        birtEngine.destroy();
//        Platform.shutdown();
//    }
}
