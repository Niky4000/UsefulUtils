package ru.ibs.docxtestproject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TableRowAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
import static ru.ibs.docxtestproject.WordUtils.changeOrientation;
import static ru.ibs.docxtestproject.WordUtils.createParagraph;
import static ru.ibs.docxtestproject.WordUtils.initTable;
import static ru.ibs.docxtestproject.WordUtils.mergeCellHorizontally;
import static ru.ibs.docxtestproject.WordUtils.mergeCellVertically;
import static ru.ibs.docxtestproject.WordUtils.setWidth;

public class MedicalAndEconomicControlActWord {

	public void create() throws IOException {
		String path = "/home/me/tmp/reportDoc/";
		//Check the generated path. If it is not there, create it.
		if (!Paths.get(path).toFile().exists()) {
			Files.createDirectories(Paths.get(path));
		}
		try (FileOutputStream out = new FileOutputStream(path + "createdWord.docx");
				XWPFDocument document = new XWPFDocument();
				XWPFDocument document2 = new XWPFDocument();
				XWPFDocument document3 = new XWPFDocument();) {
			changeOrientation(document, STPageOrientation.PORTRAIT);
			changeOrientation(document2, STPageOrientation.LANDSCAPE);
			changeOrientation(document3, STPageOrientation.PORTRAIT);
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
			createFirstTable(document);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document2, "III. Результаты медико-экономического контроля:", ParagraphAlignment.LEFT, 12);
			createParagraph(document2, "", ParagraphAlignment.LEFT, 12);
			createSecondTable(document2);
			createParagraph(document2, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document2, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document2, "Итоговая сумма, принятая к оплате: get_report.service_sum_all", ParagraphAlignment.LEFT, 12);
			createParagraph(document2, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document3, "IV. Заверительная часть", ParagraphAlignment.LEFT, 12);
			createParagraph(document3, "", ParagraphAlignment.LEFT, 12);
			createSignTable(document3, "Специалист, проводивший медико-экономический контроль:", null);
			createParagraph(document3, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document3, "Руководитель (уполномоченное лицо) Федерального фонда обязательного медицинского страхования/территориального фонда обязательного медицинского страхования:", ParagraphAlignment.LEFT, 12);
			createParagraph(document3, "", ParagraphAlignment.LEFT, 12);
			createSignTable(document3, null, "М.П. (при наличии)");
			createParagraph(document3, "", ParagraphAlignment.LEFT, 12);
			createSignTable(document3, "Руководитель медицинской организации: ", "М.П. (при наличии)");
//		createTestTable(document);
			XWPFDocument mergeDocuments = WordUtils.mergeDocuments(Stream.of(document, document2, document3));
			mergeDocuments.write(out);
		}
	}

	private void createFirstTable(XWPFDocument document) {
		final int rowsCount = 3;
		final int columnsCount = 9;
		final int defaultTextSize = 12;
		XWPFTable table = document.createTable(rowsCount, columnsCount);
		table.setTableAlignment(TableRowAlign.CENTER);
		table.setWidth("100%");
		initTable(table, rowsCount, columnsCount);
		setWidth(table, new double[]{1d, 2d, 2d, 1d, 1.1d, 1d, 1.1d, 1d, 1.1d});
		mergeCellHorizontally(table, 0, 3, 4);
		mergeCellHorizontally(table, 0, 4, 5);
		mergeCellHorizontally(table, 0, 5, 6);
		mergeCellVertically(table, 0, 0, 1);
		mergeCellVertically(table, 1, 0, 1);
		mergeCellVertically(table, 2, 0, 1);

		createParagraph(table.getRows().get(0).getCell(0).getParagraphArray(0), "N п/п", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(1).getParagraphArray(0), "Условия оказания медицинской помощи", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(2).getParagraphArray(0), "Код профиля отделения (койки) или специальности медицинского работника", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(3).getParagraphArray(0), "Предоставлено к оплате", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(4).getParagraphArray(0), "Отказано в оплате", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(5).getParagraphArray(0), "Подлежит оплате", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(1).getCell(3).getParagraphArray(0), "количество", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(1).getCell(4).getParagraphArray(0), "сумма, рублей", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(1).getCell(5).getParagraphArray(0), "количество", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(1).getCell(6).getParagraphArray(0), "сумма, рублей", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(1).getCell(7).getParagraphArray(0), "количество", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(1).getCell(8).getParagraphArray(0), "сумма, рублей", ParagraphAlignment.CENTER, defaultTextSize);
		for (int i = 1; i <= columnsCount; i++) {
			createParagraph(table.getRows().get(2).getCell(i - 1).getParagraphArray(0), i + "", ParagraphAlignment.CENTER, defaultTextSize);
		}
		table.createRow();
		int rowsSize = table.getRows().size();
		table.getRows().get(rowsSize - 1).addNewTableCell();
		table.getRows().get(rowsSize - 1).addNewTableCell();
		table.getRows().get(rowsSize - 1).addNewTableCell();
		for (int i = 1; i <= columnsCount; i++) {
			createParagraph(table.getRows().get(rowsSize - 1).getCell(i - 1).getParagraphArray(0), (i + 200) + "", ParagraphAlignment.CENTER, defaultTextSize);
		}
	}

	private void createSecondTable(XWPFDocument document) {
		final int rowsCount = 3;
		final int columnsCount = 17;
		final int defaultTextSize = 6;
		XWPFTable table = document.createTable(rowsCount, columnsCount);
		table.setTableAlignment(TableRowAlign.CENTER);
		table.setWidth("100%");
		initTable(table, rowsCount, columnsCount);
		mergeCellHorizontally(table, 0, 5, 6);
		mergeCellHorizontally(table, 0, 11, 15);
		mergeCellVertically(table, 0, 0, 1);
		mergeCellVertically(table, 1, 0, 1);
		mergeCellVertically(table, 2, 0, 1);
		mergeCellVertically(table, 3, 0, 1);
		mergeCellVertically(table, 4, 0, 1);
		mergeCellVertically(table, 7, 0, 1);
		mergeCellVertically(table, 8, 0, 1);
		mergeCellVertically(table, 9, 0, 1);
		mergeCellVertically(table, 10, 0, 1);
		mergeCellVertically(table, 11, 0, 1);
		createParagraph(table.getRows().get(0).getCell(0).getParagraphArray(0), "N п/п", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(1).getParagraphArray(0), "N п/п в реестре", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(2).getParagraphArray(0), "N полиса обязательного медицинского страхования", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(3).getParagraphArray(0), "Код диагноза по МКБ-10", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(4).getParagraphArray(0), "Условия оказания медицинской помощи", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(5).getParagraphArray(0), "Даты оказания медицинской помощи", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(7).getParagraphArray(0), "Код профиля медицинской помощи", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(8).getParagraphArray(0), "Код профиля отделения (койки) или специальности медицинского работника", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(9).getParagraphArray(0), "Код услуги", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(10).getParagraphArray(0), "Количество нарушений (дефектов)", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(11).getParagraphArray(0), "Код нарушения (дефекта)", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(defaultTextSize).getParagraphArray(0), "Перечень кодов нарушений", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(1).getCell(5).getParagraphArray(0), "дата начала", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(1).getCell(6).getParagraphArray(0), "дата окончания", ParagraphAlignment.CENTER, defaultTextSize);
		for (int i = 1; i <= columnsCount; i++) {
			createParagraph(table.getRows().get(2).getCell(i - 1).getParagraphArray(0), i + "", ParagraphAlignment.CENTER, defaultTextSize);
		}
		table.createRow();
		int rowsSize = table.getRows().size();
		table.getRows().get(rowsSize - 1).addNewTableCell();
		table.getRows().get(rowsSize - 1).addNewTableCell();
		table.getRows().get(rowsSize - 1).addNewTableCell();
		table.getRows().get(rowsSize - 1).addNewTableCell();
		table.getRows().get(rowsSize - 1).addNewTableCell();
		for (int i = 1; i <= columnsCount; i++) {
			createParagraph(table.getRows().get(rowsSize - 1).getCell(i - 1).getParagraphArray(0), (i + 200) + "", ParagraphAlignment.CENTER, defaultTextSize);
		}
	}

	private void createSignTable(XWPFDocument document, String firstRow, String lastRow) {
		final int rowsCount = 2 + (firstRow != null ? 1 : 0) + (lastRow != null ? 1 : 0);
		final int columnsCount = 3;
		final int defaultTextSize = 12;
		XWPFTable table = document.createTable(rowsCount, columnsCount);
		table.setTableAlignment(TableRowAlign.CENTER);
		table.setWidth("100%");
		initTable(table, rowsCount, columnsCount);
		setWidth(table, new double[]{1d, 4d, 2d});
		if (firstRow != null) {
			mergeCellHorizontally(table, 0, 0, 2);
		}
		if (lastRow != null) {
			mergeCellHorizontally(table, rowsCount - 1, 0, 2);
		}
		int row = 0;
		if (firstRow != null) {
			createParagraph(table.getRows().get(row++).getCell(0).getParagraphArray(0), "Специалист, проводивший медико-экономический контроль:", ParagraphAlignment.LEFT, defaultTextSize);
		}
		createParagraph(table.getRows().get(row++).getCell(2).getParagraphArray(0), "\"__\" __________ 202_ г.", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(row).getCell(0).getParagraphArray(0), "(подпись)", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(row).getCell(1).getParagraphArray(0), "(фамилия, имя, отчество (отчество - при наличии)", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(row++).getCell(2).getParagraphArray(0), "(дата)", ParagraphAlignment.CENTER, defaultTextSize);
		if (lastRow != null) {
			createParagraph(table.getRows().get(row).getCell(0).getParagraphArray(0), lastRow, ParagraphAlignment.LEFT, defaultTextSize);
		}
	}
}
