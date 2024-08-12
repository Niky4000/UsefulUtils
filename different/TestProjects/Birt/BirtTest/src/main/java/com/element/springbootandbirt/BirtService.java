package com.element.springbootandbirt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder.BorderSide;
import org.eclipse.birt.report.engine.api.IRenderOption;

public class BirtService {

	public void run() {
		System.out.println("Hello!");
		try {
			//			List<ReportParameter> parameters = Arrays.asList(new ReportParameter("reportPath", "/home/me/eclipse-birt/workspace/test/test.csv"));
//			buildReport("testReport", "xlsx", "/home/me/tmp/reports", "/home/me/eclipse-birt/workspace/test/new_template.rpttemplate", parameters);            
			ByteArrayInputStream byteArrayInputStream = createReportAndPutItToTheInputStream("/home/me/eclipse-birt/workspace/test/some_report.csv");
			ByteArrayInputStream byteArrayInputStream2 = createReportAndPutItToTheInputStream("/home/me/eclipse-birt/workspace/test/some_report2.csv");
			ByteArrayInputStream byteArrayInputStream3 = createReportAndPutItToTheInputStream("/home/me/eclipse-birt/workspace/test/some_report3.csv");
//            readAndCopySheet("/home/me/Downloads/templates/template.xlsx", "/home/me/tmp/reports/test.xlsx");
//            readAndCopySheet("/home/me/tmp/reports/test.xlsx", new FileInputStream("/home/me/tmp/reports/testReport.xlsx"));
//			readAndCopySheetArray("/home/me/tmp/reports/test.xlsx", new AbstractMap.SimpleEntry<String, InputStream>("ТФОМС", byteArrayInputStream), new AbstractMap.SimpleEntry<String, InputStream>("СМО", byteArrayInputStream2), new AbstractMap.SimpleEntry<String, InputStream>("МО", byteArrayInputStream3));
			readAndCopySheetArray("/home/me/VMWareShared/reports/test.xlsx", new AbstractMap.SimpleEntry<String, InputStream>("ТФОМС", byteArrayInputStream), new AbstractMap.SimpleEntry<String, InputStream>("СМО", byteArrayInputStream2), new AbstractMap.SimpleEntry<String, InputStream>("МО", byteArrayInputStream3));
//			readAndCopySheetArray("/home/me/VMWareShared/reports/test.xlsx", new AbstractMap.SimpleEntry<String, InputStream>("ТФОМС", byteArrayInputStream));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.exit(0);
	}

	private ByteArrayInputStream createReportAndPutItToTheInputStream(String reportDataPath) throws FileNotFoundException, IllegalStateException, IllegalArgumentException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        List<ReportParameter> parameters = Arrays.asList(new ReportParameter("reportPath", reportDataPath), new ReportParameter("tableHeadText", "<b>Статистика по методам МПИ</b><br>"
//                + "Дата формирования отчёта: 01.04.2023 00:00:00</br>"
//                + "На дату: 01.04.2023<br>"
//                + "Тип организации: ТФОМС</br>"
//                + "Организации: Все"));
		List<ReportParameter> parameters = Arrays.asList(new ReportParameter("reportPath", reportDataPath),
				new ReportParameter("tableHeadText", "Статистика по методам МПИ"),
				new ReportParameter("tableHeadText1", "Дата формирования отчёта: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())),
				new ReportParameter("tableHeadText2", "На дату: 01.04.2023"),
				new ReportParameter("tableHeadText3", "Тип организации: ТФОМС"),
				new ReportParameter("tableHeadText4", "Организации: Все"));
//		List<ReportParameter> parameters = Arrays.asList(new ReportParameter("reportPath", reportDataPath), new ReportParameter("tableHeadText", "ZZZZZZZ<br></br>ZZZZZZZ<br></br>ZZZZZZZ"));
//		List<ReportParameter> parameters = Arrays.asList(new ReportParameter("reportPath", reportDataPath), new ReportParameter("tableHeadText", "Статистика по методам МПИ"));
		buildReport("testReport", "xlsx", "/home/me/tmp/reports", "/home/me/eclipse-birt/workspace/test/some_report.rptdesign", parameters, (options) -> options.setOutputStream(byteArrayOutputStream));
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		return byteArrayInputStream;
	}

	private void readAndCopySheetArray(String outputFileName, Entry<String, InputStream>... inputStream) throws IOException {
//        List<BufferedInputStream> streamList = Stream.of(inputStream).map(Entry::getValue).map(BufferedInputStream::new).collect(Collectors.toList());
		try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFileName, true))) {
			List<Entry<String, XSSFWorkbook>> workbookList = Stream.of(inputStream).map(entry -> {
				try {
					return new AbstractMap.SimpleEntry<String, XSSFWorkbook>(entry.getKey(), new XSSFWorkbook(new BufferedInputStream(entry.getValue())));
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}).collect(Collectors.toList());
//            XSSFWorkbook workbook = new XSSFWorkbook(bis);
			XSSFWorkbook myWorkBook = new XSSFWorkbook();
			for (Entry<String, XSSFWorkbook> workbookEntry : workbookList) {
				String sheetName = workbookEntry.getKey();
				XSSFWorkbook workbook = workbookEntry.getValue();
				int sheets = workbook.getNumberOfSheets();
				for (int iSheet = 0; iSheet < sheets; iSheet++) {
					XSSFSheet sheet = workbook.getSheetAt(iSheet);
					if (sheet != null) {
						XSSFSheet mySheet = myWorkBook.createSheet(sheetName);
						for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
							CellRangeAddress mergedRegion = sheet.getMergedRegion(i);
							mySheet.addMergedRegion(mergedRegion);
						}
						int fRow = sheet.getFirstRowNum();
						int lRow = sheet.getLastRowNum();
						for (int iRow = fRow; iRow <= lRow; iRow++) {
							XSSFRow row = sheet.getRow(iRow);
							XSSFRow myRow = mySheet.createRow(iRow);
//                        myRow.setHeight((short) -1);
							if (row != null) {
								int fCell = row.getFirstCellNum();
								int lCell = row.getLastCellNum();
								for (int iCell = fCell; iCell < lCell; iCell++) {
									XSSFCell cell = row.getCell(iCell);
									XSSFCell myCell = myRow.createCell(iCell);
//                                myCell.getRow().setHeight((short) -1);
									if (HSSFCell.CELL_TYPE_STRING == cell.getCellType() && cell.getStringCellValue().contains("\n")) {
										int numberOfLines = cell.getStringCellValue().split("\n").length;
										myRow.setHeightInPoints(numberOfLines * mySheet.getDefaultRowHeightInPoints());
									}
									if (cell != null) {
										myCell.setCellType(cell.getCellType());
										switch (cell.getCellType()) {
											case HSSFCell.CELL_TYPE_BLANK:
												myCell.setCellValue("");
												break;

											case HSSFCell.CELL_TYPE_BOOLEAN:
												myCell.setCellValue(cell.getBooleanCellValue());
												break;

											case HSSFCell.CELL_TYPE_ERROR:
												myCell.setCellErrorValue(cell.getErrorCellValue());
												break;

											case HSSFCell.CELL_TYPE_FORMULA:
												myCell.setCellFormula(cell.getCellFormula());
												break;

											case HSSFCell.CELL_TYPE_NUMERIC:
												myCell.setCellValue(cell.getNumericCellValue());
												break;

											case HSSFCell.CELL_TYPE_STRING:
												myCell.setCellValue(cell.getStringCellValue());
												break;
											default:
												myCell.setCellFormula(cell.getCellFormula());
										}
										XSSFCellStyle cellStyle = cell.getCellStyle();
										copyCellStyle(myCell, cellStyle);
									}
								}
							}
						}
						for (int i = 0; i < lRow; i++) {
							mySheet.autoSizeColumn(i, true);
						}
					}
				}
			}
			//            bis.close();
			myWorkBook.write(bos);
			bos.close();
		} finally {
			Stream.of(inputStream).forEach(inputStreamEntry -> {
				try {
					inputStreamEntry.getValue().close();
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			});
		}
	}

	private void readAndCopySheet(String outputFileName, InputStream inputStream) throws IOException {
		try (BufferedInputStream bis = new BufferedInputStream(inputStream); //
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFileName, true));) {
			XSSFWorkbook workbook = new XSSFWorkbook(bis);
			XSSFWorkbook myWorkBook = new XSSFWorkbook();
			int sheets = workbook.getNumberOfSheets();
			for (int iSheet = 0; iSheet < sheets; iSheet++) {
				XSSFSheet sheet = workbook.getSheetAt(iSheet);
				if (sheet != null) {
					XSSFSheet mySheet = myWorkBook.createSheet(sheet.getSheetName());
					for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
						CellRangeAddress mergedRegion = sheet.getMergedRegion(i);
						mySheet.addMergedRegion(mergedRegion);
					}
					int fRow = sheet.getFirstRowNum();
					int lRow = sheet.getLastRowNum();
					for (int iRow = fRow; iRow <= lRow; iRow++) {
						XSSFRow row = sheet.getRow(iRow);
						XSSFRow myRow = mySheet.createRow(iRow);
//                        myRow.setHeight((short) -1);
						if (row != null) {
							int fCell = row.getFirstCellNum();
							int lCell = row.getLastCellNum();
							for (int iCell = fCell; iCell < lCell; iCell++) {
								XSSFCell cell = row.getCell(iCell);
								XSSFCell myCell = myRow.createCell(iCell);
//                                myCell.getRow().setHeight((short) -1);
								if (HSSFCell.CELL_TYPE_STRING == cell.getCellType() && cell.getStringCellValue().contains("\n")) {
									int numberOfLines = cell.getStringCellValue().split("\n").length;
									myRow.setHeightInPoints(numberOfLines * mySheet.getDefaultRowHeightInPoints());
								}
								if (cell != null) {
									myCell.setCellType(cell.getCellType());
									switch (cell.getCellType()) {
										case HSSFCell.CELL_TYPE_BLANK:
											myCell.setCellValue("");
											break;

										case HSSFCell.CELL_TYPE_BOOLEAN:
											myCell.setCellValue(cell.getBooleanCellValue());
											break;

										case HSSFCell.CELL_TYPE_ERROR:
											myCell.setCellErrorValue(cell.getErrorCellValue());
											break;

										case HSSFCell.CELL_TYPE_FORMULA:
											myCell.setCellFormula(cell.getCellFormula());
											break;

										case HSSFCell.CELL_TYPE_NUMERIC:
											myCell.setCellValue(cell.getNumericCellValue());
											break;

										case HSSFCell.CELL_TYPE_STRING:
											myCell.setCellValue(cell.getStringCellValue());
											break;
										default:
											myCell.setCellFormula(cell.getCellFormula());
									}
									XSSFCellStyle cellStyle = cell.getCellStyle();
									copyCellStyle(myCell, cellStyle);
								}
							}
						}
					}
					for (int i = 0; i < lRow; i++) {
						mySheet.autoSizeColumn(i, true);
					}
				}
			}
//            bis.close();
			myWorkBook.write(bos);
//            bos.close();
		}
	}

	private void copyCellStyle(XSSFCell myCell, XSSFCellStyle otherCellStyle) {
		XSSFCellStyle cellStyle = myCell.getSheet().getWorkbook().createCellStyle();
		cellStyle.setFillForegroundColor(otherCellStyle.getFillForegroundColorColor());
		cellStyle.setFillPattern(otherCellStyle.getFillPattern());
		cellStyle.setAlignment(otherCellStyle.getAlignment());
		cellStyle.setVerticalAlignment(otherCellStyle.getVerticalAlignment());
		cellStyle.setBorderBottom(otherCellStyle.getBorderBottom());
		cellStyle.setBorderLeft(otherCellStyle.getBorderLeft());
		cellStyle.setBorderRight(otherCellStyle.getBorderRight());
		cellStyle.setBorderTop(otherCellStyle.getBorderTop());
		cellStyle.setBorderColor(BorderSide.TOP, otherCellStyle.getBorderColor(XSSFCellBorder.BorderSide.TOP));
		cellStyle.setBorderColor(BorderSide.BOTTOM, otherCellStyle.getBorderColor(XSSFCellBorder.BorderSide.BOTTOM));
		cellStyle.setBorderColor(BorderSide.LEFT, otherCellStyle.getBorderColor(XSSFCellBorder.BorderSide.LEFT));
		cellStyle.setBorderColor(BorderSide.RIGHT, otherCellStyle.getBorderColor(XSSFCellBorder.BorderSide.RIGHT));
		myCell.setCellStyle(cellStyle);
	}

	private File buildReport(String fileName, String fileExtension, String tmpFolderPath, String reportDesign, List<ReportParameter> parameters, Consumer<IRenderOption> optionsConsumer) throws IllegalArgumentException, IllegalStateException, FileNotFoundException {
		BirtReportEngineImpl birtReportEngineImpl = new BirtReportEngineImpl();
		birtReportEngineImpl.prepareEngine(new EngineContext());
//		String reportContent = readFile(BirtService.class.getClassLoader().getResourceAsStream(reportDesign));
		String reportContent = readFile(new FileInputStream(new File(reportDesign)));
		File file = new File(tmpFolderPath + File.separator + fileName + "." + fileExtension.toLowerCase());
		birtReportEngineImpl.buildReport(reportContent, parameters, fileExtension.toUpperCase(), optionsConsumer);
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
}
