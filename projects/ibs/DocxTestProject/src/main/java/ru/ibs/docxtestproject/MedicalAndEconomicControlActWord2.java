package ru.ibs.docxtestproject;

import com.spire.doc.CellWidthType;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.Section;
import com.spire.doc.Table;
import com.spire.doc.collections.RowCollection;
import com.spire.doc.documents.BorderStyle;
import com.spire.doc.documents.HorizontalAlignment;
import com.spire.doc.documents.Paragraph;
import com.spire.doc.documents.VerticalAlignment;
import com.spire.doc.fields.TextRange;
import java.io.IOException;

public class MedicalAndEconomicControlActWord2 {

	public void create() throws IOException {
		Document document = new Document();
		Section section = document.addSection();
		createParagraph(section, "Заключение", HorizontalAlignment.Center, 12);
		createParagraph(section, "по результатам медико-экономического контроля", HorizontalAlignment.Center, 12);
		createParagraph(section, "от get_report.DATE_CRT г. N get_report.NUM", HorizontalAlignment.Center, 12);
		createParagraph(section, "", HorizontalAlignment.Left, 12);
		createParagraph(section, "I. Общая часть", HorizontalAlignment.Left, 12);
		createParagraph(section, "", HorizontalAlignment.Left, 12);
		createParagraph(section, "Федеральный фонд обязательного медицинского страхования/территориальный\n"
				+ "фонд обязательного медицинского страхования «Московский городской фонд обязательного медицинского страхования»\n"
				+ "Наименование страховой медицинской организации get_report.SMO_NAME \n"
				+ "Наименование медицинской организации get_report.MO_NAME\n"
				+ "Наименование территориального фонда обязательного медицинского страхования\n"
				+ "по    месту    страхования    застрахованного    лица    (при    проведении\n"
				+ "межтерриториальных взаиморасчетов) _____Не заполняем________________________\n"
				+ "Номер счета/реестра счетов get_report.NUM\n"
				+ "Период, за который предоставлен счет/реестр счетов:\n"
				+ "с \"01\" get_report.period_str г. по \" get_report.last_day\" get_report.period_str г.", HorizontalAlignment.Left, 12);
		createParagraph(section, "", HorizontalAlignment.Left, 12);
		createParagraph(section, "II. Сведения об оказанной медицинской помощи:", HorizontalAlignment.Left, 12);
		createParagraph(section, "", HorizontalAlignment.Left, 12);
		createFirstTable(section);
		createParagraph(section, "", HorizontalAlignment.Left, 12);
		createParagraph(section, "III. Результаты медико-экономического контроля:", HorizontalAlignment.Left, 12);
		createParagraph(section, "", HorizontalAlignment.Left, 12);
		createSecondTable(section);
		createParagraph(section, "", HorizontalAlignment.Left, 12);
		createTestTable(section);
//Save the document 
		document.saveToFile("/home/me/tmp/reportDoc/Output.docx", FileFormat.Docx_2013);
	}

	private void createFirstTable(Section section) {
		final int rowsCount = 3;
		final int columnsCount = 9;
		Table table = section.addTable();
		table.resetCells(rowsCount, columnsCount);
		RowCollection rows = table.getRows();
		for (int row = 0; row < rowsCount; row++) {
			for (int col = 0; col < columnsCount; col++) {
				table.getRows().get(row).getCells().get(col).getCellFormat().setVerticalAlignment(VerticalAlignment.Top);
//				table.getRows().get(row).getCells().get(col).setCellWidth(10, CellWidthType.Percentage);
			}
		}
//		table.applyStyle(DefaultTableStyle.Colorful_List);
		table.getTableFormat().getBorders().setBorderType(BorderStyle.Single);
		table.applyHorizontalMerge(0, rowsCount, 4);
		table.applyHorizontalMerge(0, 5, 6);
		table.applyHorizontalMerge(0, 7, 8);
		table.applyVerticalMerge(0, 0, 1);
		table.applyVerticalMerge(1, 0, 1);
		table.applyVerticalMerge(2, 0, 1);
		table.getRows().get(0).getCells().get(0).addParagraph().appendText("N п/п");
		table.getRows().get(0).getCells().get(1).addParagraph().appendText("Условия оказания медицинской помощи");
		table.getRows().get(0).getCells().get(2).addParagraph().appendText("Код профиля отделения (койки) или специальности медицинского работника");
		table.getRows().get(0).getCells().get(rowsCount).addParagraph().appendText("Предоставлено к оплате");
		table.getRows().get(0).getCells().get(5).addParagraph().appendText("Отказано в оплате");
		table.getRows().get(0).getCells().get(7).addParagraph().appendText("Подлежит оплате");
		table.getRows().get(1).getCells().get(rowsCount).addParagraph().appendText("количество");
		table.getRows().get(1).getCells().get(4).addParagraph().appendText("сумма, рублей");
		table.getRows().get(1).getCells().get(5).addParagraph().appendText("количество");
		table.getRows().get(1).getCells().get(6).addParagraph().appendText("сумма, рублей");
		table.getRows().get(1).getCells().get(7).addParagraph().appendText("количество");
		table.getRows().get(1).getCells().get(8).addParagraph().appendText("сумма, рублей");
		for (int i = 1; i <= columnsCount; i++) {
			table.getRows().get(2).getCells().get(i - 1).addParagraph().appendText(i + "");
		}
		for (int row = 0; row < rowsCount; row++) {
			for (int col = 0; col < columnsCount; col++) {
				if (table.getRows().get(row).getCells().get(col).getParagraphs().getCount() == 1) {
					table.getRows().get(row).getCells().get(col).getParagraphs().get(0).getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
				}
			}
		}
		table.addRow();
		for (int i = 1; i <= columnsCount; i++) {
			table.getRows().get(rowsCount).getCells().get(i - 1).addParagraph().appendText((i + 200) + "");
		}
	}

	private void createSecondTable(Section section) {
		final int rowsCount = 3;
		final int columnsCount = 17;
		final float defaultTextSize = 6f;
		Table table = section.addTable();
		table.resetCells(rowsCount, columnsCount);
		RowCollection rows = table.getRows();
		for (int row = 0; row < rowsCount; row++) {
			for (int col = 0; col < columnsCount; col++) {
				table.getRows().get(row).getCells().get(col).getCellFormat().setVerticalAlignment(VerticalAlignment.Top);
			}
			float width = table.getRows().get(row).getCells().get(0).getWidth();
			table.getRows().get(row).getCells().get(0).setWidth(40f);
//			table.getRows().get(row).getCells().get(0).setCellWidth(20f, CellWidthType.Percentage);
//			table.getRows().get(row).getCells().get(1).setCellWidth(5f, CellWidthType.Percentage);
//			table.getRows().get(row).getCells().get(2).setCellWidth(5f, CellWidthType.Percentage);
//			table.getRows().get(row).getCells().get(3).setCellWidth(5f, CellWidthType.Percentage);
//			table.getRows().get(row).getCells().get(4).setCellWidth(5f, CellWidthType.Percentage);
//			table.getRows().get(row).getCells().get(5).setCellWidth(10f, CellWidthType.Percentage);
//			table.getRows().get(row).getCells().get(6).setCellWidth(5f, CellWidthType.Percentage);
//			table.getRows().get(row).getCells().get(7).setCellWidth(5f, CellWidthType.Percentage);
//			table.getRows().get(row).getCells().get(8).setCellWidth(5f, CellWidthType.Percentage);
//			table.getRows().get(row).getCells().get(9).setCellWidth(10f, CellWidthType.Percentage);
//			table.getRows().get(row).getCells().get(10).setCellWidth(10f, CellWidthType.Percentage);
//			table.getRows().get(row).getCells().get(11).setCellWidth(5f, CellWidthType.Percentage);
//			table.getRows().get(row).getCells().get(12).setCellWidth(5f, CellWidthType.Percentage);
//			table.getRows().get(row).getCells().get(13).setCellWidth(5f, CellWidthType.Percentage);
//			table.getRows().get(row).getCells().get(14).setCellWidth(5f, CellWidthType.Percentage);
//			table.getRows().get(row).getCells().get(15).setCellWidth(5f, CellWidthType.Percentage);
//			table.getRows().get(row).getCells().get(16).setCellWidth(5f, CellWidthType.Percentage);
		}
//		table.applyStyle(DefaultTableStyle.Colorful_List);
		table.getTableFormat().getBorders().setBorderType(BorderStyle.Single);
		table.applyHorizontalMerge(0, 12, 16);
		table.applyHorizontalMerge(0, 5, 6);
		table.applyVerticalMerge(0, 0, 1);
		table.applyVerticalMerge(1, 0, 1);
		table.applyVerticalMerge(2, 0, 1);
		table.applyVerticalMerge(3, 0, 1);
		table.applyVerticalMerge(4, 0, 1);
		table.applyVerticalMerge(7, 0, 1);
		table.applyVerticalMerge(8, 0, 1);
		table.applyVerticalMerge(9, 0, 1);
		table.applyVerticalMerge(10, 0, 1);
		table.applyVerticalMerge(11, 0, 1);
		setCellTextSize(table.getRows().get(0).getCells().get(0).addParagraph().appendText("N п/п"), defaultTextSize);
		setCellTextSize(table.getRows().get(0).getCells().get(1).addParagraph().appendText("N п/п в реестре"), defaultTextSize);
		setCellTextSize(table.getRows().get(0).getCells().get(2).addParagraph().appendText("N полиса обязательного медицинского страхования"), defaultTextSize);
		setCellTextSize(table.getRows().get(0).getCells().get(3).addParagraph().appendText("Код диагноза по МКБ-10"), defaultTextSize);
		setCellTextSize(table.getRows().get(0).getCells().get(4).addParagraph().appendText("Условия оказания медицинской помощи"), defaultTextSize);
		setCellTextSize(table.getRows().get(0).getCells().get(5).addParagraph().appendText("Даты оказания медицинской помощи"), defaultTextSize);
		setCellTextSize(table.getRows().get(0).getCells().get(7).addParagraph().appendText("Код профиля медицинской помощи"), defaultTextSize);
		setCellTextSize(table.getRows().get(0).getCells().get(8).addParagraph().appendText("Код профиля отделения (койки) или специальности медицинского работника"), defaultTextSize);
		setCellTextSize(table.getRows().get(0).getCells().get(9).addParagraph().appendText("Код услуги"), defaultTextSize);
		setCellTextSize(table.getRows().get(0).getCells().get(10).addParagraph().appendText("Количество нарушений (дефектов)"), defaultTextSize);
		setCellTextSize(table.getRows().get(0).getCells().get(11).addParagraph().appendText("Код нарушения (дефекта)"), defaultTextSize);
		setCellTextSize(table.getRows().get(0).getCells().get(12).addParagraph().appendText("Перечень кодов нарушений"), defaultTextSize);
		setCellTextSize(table.getRows().get(1).getCells().get(5).addParagraph().appendText("дата начала"), defaultTextSize);
		setCellTextSize(table.getRows().get(1).getCells().get(6).addParagraph().appendText("дата окончания"), defaultTextSize);

		for (int i = 1; i <= columnsCount; i++) {
			setCellTextSize(table.getRows().get(2).getCells().get(i - 1).addParagraph().appendText(i + ""), defaultTextSize);
		}
		for (int row = 0; row < rowsCount; row++) {
			for (int col = 0; col < columnsCount; col++) {
				if (table.getRows().get(row).getCells().get(col).getParagraphs().getCount() == 1) {
					table.getRows().get(row).getCells().get(col).getParagraphs().get(0).getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
				}
			}
		}
		table.addRow();
		for (int i = 1; i <= columnsCount; i++) {
			setCellTextSize(table.getRows().get(rowsCount).getCells().get(i - 1).addParagraph().appendText((i + 200) + ""), defaultTextSize);
		}
	}

	private void createTestTable(Section section) {
		final int rowsCount = 3;
		final int columnsCount = 4;
		Table table = section.addTable();
		table.resetCells(rowsCount, columnsCount);
		//		table.setColumnWidth(new float[]{400f, 100f, 100f, 400f});
		table.getRows().get(0).getCells().get(0).setWidth(40f);
//		table.getRows().get(0).getCells().get(0).setCellWidth(10f, CellWidthType.Percentage);
//		table.getRows().get(0).getCells().get(1).setCellWidth(40f, CellWidthType.Percentage);
//		table.getRows().get(0).getCells().get(2).setCellWidth(40f, CellWidthType.Percentage);
//		table.getRows().get(0).getCells().get(3).setCellWidth(10f, CellWidthType.Percentage);
	}

	private void setColumnWidth(float[] widths, Table table) {
		int count = table.getRows().get(0).getCells().getCount();
		if (widths.length != count) {
			throw new RuntimeException("widths.length != count");
		}
		float widthSum = 0f;
		for (int i = 0; i < count; i++) {
			widthSum += table.getRows().get(0).getCells().get(0).getWidth();
		}
		float width = table.getRows().get(0).getCells().get(0).getWidth();
		table.getRows().get(0).getCells().get(0).setWidth(40f);
	}

	private void createParagraph(Section section, String text, HorizontalAlignment align, int size) {
		Paragraph paragraph = section.addParagraph();
		paragraph.getFormat().setHorizontalAlignment(align);
		paragraph.appendText(text);
	}

	private void setCellTextSize(TextRange textRange, float size) {
		textRange.getCharacterFormat().setFontSize(size);
	}
}
