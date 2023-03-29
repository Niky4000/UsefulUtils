package ru.kiokle.xlstest;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public abstract class XlsAbstractCreator {

	protected abstract int getHeaderLength();

	protected abstract Map.Entry<Integer[], List<Integer>> printHeader(Sheet sheet, List<List<Object>> dataList, String[] leftHeaderArray);

	protected void changeCellBackgroundColor(Cell cell, IndexedColors color, HorizontalAlignment alignment) {
		CellStyle cellStyle = cell.getSheet().getWorkbook().createCellStyle();
		cellStyle.setFillForegroundColor(color.getIndex());
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		cellStyle.setAlignment(alignment);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cell.setCellStyle(cellStyle);
	}

	protected Cell createAndSetCell(Row row, int index, String value) {
		Cell cell = row.createCell(index);
		cell.setCellValue(value);
		return cell;
	}

	public void createXls(List<List<Object>> dataList, String[] leftHeaderArray, OutputStream outputStream) throws Exception {
		if (dataList.size() > leftHeaderArray.length) {
			throw new RuntimeException("Input data array is too big! It's size is " + dataList.size() + "!");
		}
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("DemoVersion");
		Map.Entry<Integer[], List<Integer>> printHeader = printHeader(sheet, dataList, leftHeaderArray);
		int rowCount = 0;
		for (List<Object> rowList : dataList) {
			Row row = sheet.getRow(printHeader.getValue().get(rowCount++));
			int columnCount = printHeader.getKey()[1];
			for (Object field : rowList) {
				Cell cell = row.createCell(columnCount++);
				if (field instanceof String) {
					cell.setCellValue((String) field);
				} else if (field instanceof Integer) {
					cell.setCellValue((Integer) field);
				}
			}
		}
		for (int i = 0; i < getHeaderLength(); i++) {
			sheet.autoSizeColumn(i);
		}
		sheet.setColumnWidth(1, 6000);
		sheet.setColumnWidth(2, 14000);
		sheet.setColumnWidth(10, 10000);
		workbook.write(outputStream);
	}
}
