package ru.kiokle.xlstest;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

public class AttachedAndInsuredPersonsReport extends XlsAbstractCreator {

	@Override
	protected int getHeaderLength() {
		return 11;
	}

	@Override
	protected Map.Entry<Integer[], List<Integer>> printHeader(Sheet sheet, List<List<Object>> dataList, String[] leftHeaderArray) {
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 1, 1));
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 2, 2));
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 3, 9));
		sheet.addMergedRegion(new CellRangeAddress(0, 1, 10, 10));
		Row row = sheet.createRow(0);
		changeCellBackgroundColor(createAndSetCell(row, 0, "Субъекты Российской Федерации"), IndexedColors.GREY_25_PERCENT, HorizontalAlignment.CENTER);
		changeCellBackgroundColor(createAndSetCell(row, 1, "Код ОКАТО субъекта РФ"), IndexedColors.GREY_25_PERCENT, HorizontalAlignment.CENTER);
		changeCellBackgroundColor(createAndSetCell(row, 2, "Количество застрахованных лиц (ЗЛ) в ФЕРЗЛ. Всего"), IndexedColors.GREY_25_PERCENT, HorizontalAlignment.CENTER);
		changeCellBackgroundColor(createAndSetCell(row, 3, "Количество застрахованных лиц (ЗЛ) в ФЕРЗЛ по типам полиса"), IndexedColors.GREY_25_PERCENT, HorizontalAlignment.CENTER);
		changeCellBackgroundColor(createAndSetCell(row, 10, "Количество прикрепленных ЗЛ по ФЕРЗЛ"), IndexedColors.GREY_25_PERCENT, HorizontalAlignment.CENTER);
		Row row2 = sheet.createRow(1);
		changeCellBackgroundColor(createAndSetCell(row2, 3, "Временное свидетельство"), IndexedColors.GREY_25_PERCENT, HorizontalAlignment.CENTER);
		changeCellBackgroundColor(createAndSetCell(row2, 4, "Полис ОМС в составе универсальной электронной карты"), IndexedColors.GREY_25_PERCENT, HorizontalAlignment.CENTER);
		changeCellBackgroundColor(createAndSetCell(row2, 5, "Бумажный полис ОМС единого образца"), IndexedColors.GREY_25_PERCENT, HorizontalAlignment.CENTER);
		changeCellBackgroundColor(createAndSetCell(row2, 6, "Полис ОМС старого образца"), IndexedColors.GREY_25_PERCENT, HorizontalAlignment.CENTER);
		changeCellBackgroundColor(createAndSetCell(row2, 7, "Цифровой полис ОМС"), IndexedColors.GREY_25_PERCENT, HorizontalAlignment.CENTER);
		changeCellBackgroundColor(createAndSetCell(row2, 8, "Электронный полис ОМС единого образца"), IndexedColors.GREY_25_PERCENT, HorizontalAlignment.CENTER);
		changeCellBackgroundColor(createAndSetCell(row2, 9, "Состояние на учёте без полиса ОМС"), IndexedColors.GREY_25_PERCENT, HorizontalAlignment.CENTER);
		ArrayList<Object> rowIndexs = new ArrayList<>(dataList.size());
		for (int i = 0; i < leftHeaderArray.length; i++) {
			String str = leftHeaderArray[i];
			Row row_ = sheet.createRow(i + 2);
			Cell cell = createAndSetCell(row_, 0, str);
			if (!str.startsWith(" ")) {
				changeCellBackgroundColor(cell, IndexedColors.LIGHT_TURQUOISE, HorizontalAlignment.LEFT);
				for (int j = 1; j < getHeaderLength(); j++) {
					Cell cell_ = createAndSetCell(row_, j, "");
					changeCellBackgroundColor(cell_, IndexedColors.LIGHT_TURQUOISE, HorizontalAlignment.LEFT);
				}
			} else {
				rowIndexs.add(i + 2);
			}
		}
		return new AbstractMap.SimpleEntry(new Integer[]{2, 1}, rowIndexs);
	}
}
