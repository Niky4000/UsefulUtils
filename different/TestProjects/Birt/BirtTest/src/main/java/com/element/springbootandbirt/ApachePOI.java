package com.element.springbootandbirt;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ApachePOI {

	public static void printSomeReport() throws FileNotFoundException, IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Java Books");
		Object[][] bookData = {
			{"      Head First Java Head First Java Head First Java Head First Java", "Kathy Serria", 79},
			{"       Effective Java", "Joshua Bloch", 36},
			{"     Clean Code", "Robert martin", 42},
			{"     Thinking in Java", "Bruce Eckel", 35},};
		int rowCount = 0;
		for (Object[] aBook : bookData) {
			Row row = sheet.createRow(++rowCount);
			sheet.setColumnWidth(1, 4000);
			int columnCount = 0;

			for (Object field : aBook) {
				Cell cell = row.createCell(++columnCount);
				if (field instanceof String) {
					cell.setCellValue((String) field);
					if (((String) field).length() > 50) {
						cell.getRow().setHeightInPoints(cell.getSheet().getDefaultRowHeightInPoints() * 4);
					}
					CellStyle style = workbook.createCellStyle(); //Create new style
					style.setWrapText(true); //Set wordwrap
					cell.setCellStyle(style); //Apply style to cell
				} else if (field instanceof Integer) {
					cell.setCellValue((Integer) field);
				}
			}
		}
		try (FileOutputStream outputStream = new FileOutputStream("/home/me/tmp/reports/JavaBooks.xlsx")) {
			workbook.write(outputStream);
		}
	}
}
