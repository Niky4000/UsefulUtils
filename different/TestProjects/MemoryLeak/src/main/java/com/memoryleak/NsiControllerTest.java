package com.memoryleak;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class NsiControllerTest {

	public void testPoi() throws Exception {
		Map<String, Object> result = getResult();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		String dictionaryName = "dictionaryName";
		Workbook book = new HSSFWorkbook();
		//Workbook book = new XSSFWorkbook();
		getWorkbook(book, dictionaryName, result);

//		response.setContentType("application/vnd.ms-excel;charset=UTF-8");
//		String headerKey = "Content-Disposition";
//		String headerValue = String.format("attachment; filename=\"%s\"", ((String) result.get("dictionaryName")).concat(".xls"));
//		response.addHeader(headerKey, headerValue);
//		response.setCharacterEncoding("UTF-8");
		// Записываем всё в файл
		book.write(outputStream);
		outputStream.flush();
		outputStream.close();
//		writeToFile(outputStream);
	}

	private void writeToFile(ByteArrayOutputStream outputStream) throws IOException {
		File file = new File("/home/me/tmp/testPoi.xls");
		if (!file.exists()) {
			file.createNewFile();
		}
		Files.write(file.toPath(), outputStream.toByteArray(), StandardOpenOption.TRUNCATE_EXISTING);
	}

	private void getWorkbook(Workbook book, String dictionaryName, Map<String, Object> result) {
		Sheet sheet = book.createSheet(dictionaryName);
		int rowCount = 0;
		Map<String, String> headers = (Map<String, String>) result.get("headers");
		List<String> headersName = new ArrayList<>();
		List<String> headersKeys = new ArrayList<>();
		Row rowHeader = sheet.createRow(rowCount++);
		int colCount = 0;
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			Cell nameHeader = rowHeader.createCell(colCount++);
			nameHeader.setCellValue(entry.getValue());
			headersKeys.add(entry.getKey());
			headersName.add(entry.getValue());
		}
		DataFormat format = book.createDataFormat();
		CellStyle dateStyle = book.createCellStyle();
		dateStyle.setDataFormat(format.getFormat("dd.mm.yyyy HH:mm:ss"));
		List<HashMap<String, Object>> items = (List<HashMap<String, Object>>) result.get("items");
		for (HashMap<String, Object> item : items) {
			Row row = sheet.createRow(rowCount++);
			for (int i = 0; i < headersKeys.size(); i++) {
				Cell name = row.createCell(i);
				String key = headersKeys.get(i);
				Object field = item.get(key);
				if (field instanceof String) {
					name.setCellValue(Objects.toString(field, null));
				} else if (field instanceof BigDecimal) {
					name.setCellValue(((BigDecimal) field).doubleValue());
				} else if (field instanceof Integer) {
					name.setCellValue((Integer) field);
				} else if (field instanceof Date) {
					name.setCellStyle(dateStyle);
					name.setCellValue((Date) field);
				} else {
					name.setCellValue(Objects.toString(item.get(key), null));
				}
			}
		}
		for (int i = 0; i < headersKeys.size(); i++) {
			// Меняем размер столбца
			sheet.autoSizeColumn(i);
		}
	}

	private Map<String, Object> getResult() {
		Map<String, Object> result = new HashMap<>();
		Map<String, String> headers = new HashMap<>();
		headers.put("head1", "headValue1");
		headers.put("head2", "headValue2");
		headers.put("head3", "headValue3");
		headers.put("head4", "headValue4");

		List<HashMap<String, Object>> items = new ArrayList<>();
		for (int i = 0; i < 10000; i++) {
			items.add(getHashMap(new AbstractMap.SimpleEntry("head1", "value1"), new AbstractMap.SimpleEntry("head2", "value2"),
					new AbstractMap.SimpleEntry("head3", "value3"), new AbstractMap.SimpleEntry("head4", "value4")));
		}
		result.put("headers", headers);
		result.put("items", items);

		return result;
	}

	private HashMap<String, Object> getHashMap(Map.Entry<String, Object>... entryArray) {
		HashMap<String, Object> map = new HashMap<>();
		for (Map.Entry<String, Object> entry : entryArray) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}
}
