package ru.ibs.pmp.zzztestapplication;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TableRowAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

public class MedicalAndEconomicControlActWord {

	public void create() throws IOException {
		String path = "/home/me/tmp/reportDoc/";
		//Check the generated path. If it is not there, create it.
		if (!Paths.get(path).toFile().exists()) {
			Files.createDirectories(Paths.get(path));
		}
		try (FileOutputStream out = new FileOutputStream(path + "createdWord.docx");
				XWPFDocument document = new XWPFDocument();) {

			createParagraph(document, "Заключение", ParagraphAlignment.CENTER, 12);
			createParagraph(document, "по результатам медико-экономического контроля", ParagraphAlignment.CENTER, 12);
			createParagraph(document, "от get_report.DATE_CRT г. N get_report.NUM", ParagraphAlignment.CENTER, 12);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "I. Общая часть", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "Федеральный фонд обязательного медицинского страхования/территориальный\n"
					+ "фонд обязательного медицинского страхования «Московский городской фонд обязательного медицинского страхования»\n"
					+ "Наименование страховой медицинской организации get_report.SMO_NAME \n"
					+ "Наименование медицинской организации get_report.MO_NAME\n"
					+ "Наименование территориального фонда обязательного медицинского страхования\n"
					+ "по    месту    страхования    застрахованного    лица    (при    проведении\n"
					+ "межтерриториальных взаиморасчетов) _____Не заполняем________________________\n"
					+ "Номер счета/реестра счетов get_report.NUM\n"
					+ "Период, за который предоставлен счет/реестр счетов:\n"
					+ "с \"01\" get_report.period_str г. по \" get_report.last_day\" get_report.period_str г.", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "II. Сведения об оказанной медицинской помощи:", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);

			XWPFTable table = document.createTable(3, 9);
			table.setWidth("100%");
			table.setTableAlignment(TableRowAlign.CENTER);

			//create first row
			XWPFTableRow tableRowOne = table.getRow(0);
			XWPFTableRow tableRowTwo = table.getRow(1);
			XWPFTableRow tableRowThree = table.getRow(2);
//			mergeCells(tableRowOne.getCell(0), BigInteger.valueOf(2L));
			mergeCellsVertically3(table, 0, 0, 1);
			XWPFTableRow row = table.getRow(0);
			List<XWPFTableCell> tableCells = row.getTableCells();
//			mergeCellsVertically3(table, 2, 0, 1);
//			mergeCellsVertically3(table, 5, 0, 1);
//			mergeCellsVertically(table, 2, 0, 1);
//			spanCellsAcrossRow(table, 0, 0, 2);
//			spanCellsAcrossRow(table, 0, 1, 2);
//			spanCellsAcrossRow(table, 0, 2, 2);
//
//
//			tableRowOne.getCell(0).setText("N п/п");
//			tableRowOne.getCell(1).setText("Условия оказания медицинской помощи");
//			tableRowOne.getCell(2).setText("Код профиля отделения (койки) или специальности медицинского работника");
//			tableRowOne.getCell(3).setText("Предоставлено к оплате");
//			tableRowOne.getCell(5).setText("Отказано в оплате");
//			tableRowOne.getCell(7).setText("Подлежит оплате");
//			tableRowTwo.getCell(3).setText("количество");
//			tableRowTwo.getCell(4).setText("сумма, рублей");
//			tableRowTwo.getCell(5).setText("количество");
//			tableRowTwo.getCell(6).setText("сумма, рублей");
//			tableRowTwo.getCell(7).setText("количество");
//			tableRowTwo.getCell(8).setText("сумма, рублей");
//			for (int i = 0; i < 9; i++) {
//				tableRowThree.getCell(i).setText((i + 1) + "");
//			}

			// First Row
//			CTVMerge vmerge = CTVMerge.Factory.newInstance();
//			vmerge.setVal(STMerge.RESTART);
//			table.getRow(0).getCell(0).getCTTc().addNewTcPr().setVMerge(vmerge);
//			table.getRow(0).getCell(1).getCTTc().addNewTcPr().setVMerge(vmerge);
//
//			// Secound Row cell will be merged 
//			CTVMerge vmerge1 = CTVMerge.Factory.newInstance();
//			vmerge1.setVal(STMerge.CONTINUE);
//			table.getRow(1).getCell(0).getCTTc().addNewTcPr().setVMerge(vmerge1);
//			table.getRow(1).getCell(1).getCTTc().addNewTcPr().setVMerge(vmerge1);
//
//			CTVMerge vmerge2 = CTVMerge.Factory.newInstance();
//			vmerge2.setVal(STMerge.CONTINUE);
//			table.getRow(0).getCell(7).getCTTc().getTcPr().setVMerge(vmerge2);
//			table.getRow(0).getCell(8).getCTTc().getTcPr().setVMerge(vmerge2);
			document.write(out);

		}
	}

	public static void mergeCellsVertically3(XWPFTable table, int col, int fromRow, int toRow) {
		if (toRow <= fromRow) {
			return;
		}
		for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
			XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
			CTTcPr tcPr = getTcPr(cell);
//			CTVMerge vMerge = tcPr.addNewVMerge();
			CTVMerge vMerge = CTVMerge.Factory.newInstance();
			if (rowIndex == fromRow) {
				// The first merged cell is set with RESTART merge value
				vMerge.setVal(STMerge.RESTART);
			} else {
				// Cells which join (merge) the first one, are set with CONTINUE
				vMerge.setVal(STMerge.CONTINUE);
			}
			tcPr.setVMerge(vMerge);
		}
	}

	public static void mergeCellsVertically(XWPFTable table, int col, int fromRow, int toRow) {
		if (toRow <= fromRow) {
			return;
		}
		for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
			XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
			CTTcPr tcPr = getTcPr(cell);
			CTVMerge vMerge = tcPr.addNewVMerge();
			if (rowIndex == fromRow) {
				// The first merged cell is set with RESTART merge value
				vMerge.setVal(STMerge.RESTART);
			} else {
				// Cells which join (merge) the first one, are set with CONTINUE
				vMerge.setVal(STMerge.CONTINUE);
			}
		}
	}

	private void mergeCellsVertically2(XWPFTable table, int col, int fromRow, int toRow) {
		for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
			XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
			if (rowIndex == fromRow) {
				cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
			} else {
				cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
			}
		}
	}

	private static CTTcPr getTcPr(XWPFTableCell cell) {
		if (cell.getCTTc().getTcPr() == null) {
			return cell.getCTTc().addNewTcPr();
		} else {
			return cell.getCTTc().getTcPr();
		}
	}

	private void mergeCells(XWPFTableCell cell, BigInteger span) {
		if (cell.getCTTc().getTcPr() == null) {
			cell.getCTTc().addNewTcPr();
		}
		if (cell.getCTTc().getTcPr().getGridSpan() == null) {
			cell.getCTTc().getTcPr().addNewGridSpan();
		}
		cell.getCTTc().getTcPr().getGridSpan().setVal(span);
	}

	private void spanCellsAcrossRow(XWPFTable table, int rowNum, int colNum, int span) {
//		table.getRow(rowNum).getCell(colNum).getCTTc().addNewTcPr().addNewGridSpan().setVal(BigInteger.valueOf(2L));
//		table.getRow(rowNum).getCell(colNum + 1).getCTTc().addNewTcPr().addNewGridSpan();
//		CTHMerge vmerge = CTHMerge.Factory.newInstance();
//		vmerge.setVal(STMerge.RESTART);
//		table.getRow(rowNum).getCell(colNum).getCTTc().getTcPr().setHMerge(vmerge);
//		table.getRow(rowNum).getCell(colNum + 1).getCTTc().getTcPr().setHMerge(vmerge);

		// First Row
		CTVMerge vmerge = CTVMerge.Factory.newInstance();
		vmerge.setVal(STMerge.RESTART);
		table.getRow(rowNum).getCell(colNum).getCTTc().addNewTcPr().setVMerge(vmerge);
//		table.getRow(rowNum).getCell(colNum).getCTTc().getTcPr().setVMerge(vmerge);

		// Secound Row cell will be merged 
		CTVMerge vmerge1 = CTVMerge.Factory.newInstance();
		vmerge1.setVal(STMerge.CONTINUE);
		table.getRow(rowNum + 1).getCell(colNum).getCTTc().addNewTcPr().setVMerge(vmerge1);
//		table.getRow(rowNum+1).getCell(colNum).getCTTc().getTcPr().setVMerge(vmerge1);
	}

	private void createParagraph(XWPFDocument document, String text, ParagraphAlignment align, int size) {
		XWPFParagraph paragraph = document.createParagraph();
		paragraph.setAlignment(align);
		XWPFRun run = paragraph.createRun();
		run.setText(text);
		run.setFontSize(size);
	}
}
