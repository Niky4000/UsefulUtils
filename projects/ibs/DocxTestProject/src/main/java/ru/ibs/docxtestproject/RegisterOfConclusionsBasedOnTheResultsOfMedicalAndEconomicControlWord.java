package ru.ibs.docxtestproject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TableRowAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
import static ru.ibs.docxtestproject.WordUtils.changeOrientation;
import static ru.ibs.docxtestproject.WordUtils.createParagraph;
import static ru.ibs.docxtestproject.WordUtils.initTable;
import static ru.ibs.docxtestproject.WordUtils.mergeCellHorizontally;
import static ru.ibs.docxtestproject.WordUtils.mergeCellVertically;
import static ru.ibs.docxtestproject.WordUtils.setWidth;
import ru.ibs.docxtestproject.bean.RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean;
import ru.ibs.docxtestproject.bean.RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean;
import ru.ibs.docxtestproject.bean.RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBean;
import ru.ibs.docxtestproject.bean.RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBean;

public class RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlWord {

	public void create(RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean, RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean, Collection<RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBean> registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBeanCollection1, Collection<RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBean> registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBeanCollection2, Collection<RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBean> registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBeanCollection3, Collection<RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBean> registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBeanCollection4, Collection<RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBean> registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBeanCollection, String path) throws IOException {
		if (!new File(path).getParentFile().exists()) {
			Files.createDirectories(Paths.get(new File(path).getParentFile().getAbsolutePath()));
		}
		try (FileOutputStream out = new FileOutputStream(path);
				XWPFDocument document = new XWPFDocument();) {
			changeOrientation(document, STPageOrientation.LANDSCAPE);
			createParagraph(document, "Реестр", ParagraphAlignment.CENTER, 12);
			createParagraph(document, "заключений по результатам медико-экономического контроля", ParagraphAlignment.CENTER, 12);
			createParagraph(document, "от 01 " + registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean.getPeriodStr() + " г. по " + registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean.getLastDayOfPeriod() + " " + registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean.getPeriodStr() + " г.", ParagraphAlignment.CENTER, 12);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "Федеральный фонд обязательного медицинского страхования/территориальный фонд обязательного медицинского страхования, получивший счета от медицинской организации Московский городской фонд обязательного медицинского страхования", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "Код Федерального фонда обязательного медицинского страхования/территориального фонда обязательного медицинского страхования, получившего счета от медицинской организации ________________________________________________________ ", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "Код территории местонахождения Федерального фонда обязательного медицинского страхования/территориального фонда обязательного медицинского страхования ________________________________________________________________ ", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "Наименование медицинской организации, предоставившей счет " + registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean.getLpuFullName(), ParagraphAlignment.LEFT, 12);
			createParagraph(document, "Код медицинской организации, предоставившей счет ____________________________", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "Код территории местонахождения медицинской организации, предоставившей счет ", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "(Код ОКТМО)", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "1. Сведения о результатах медико-экономического контроля:", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createFirstTable(document, registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "2.1. Не подлежит оплате, всего " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean.getCaseIdCount()) + " случаев оказания медицинской помощи на сумму " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean.getServiceSum()) + " рублей.", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "2.1.1. За оказание медицинской помощи в стационарных условиях " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean.getCaseIdCount2Stationary()) + " случаев оказания медицинской помощи на сумму " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean.getServiceSum2Stationary()) + " рублей.", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createSecondTable(document, registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBeanCollection1);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "2.1.2. За оказание медицинской помощи в условиях дневного стационара _______случаев оказания медицинской помощи на сумму ______ рублей.", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createSecondTable(document, registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBeanCollection2);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "2.1.3. За оказание медицинской помощи в амбулаторных условиях _______ случаев оказания медицинской помощи на сумму ________ рублей.", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createSecondTable(document, registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBeanCollection3);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "2.1.4. За оказание медицинской помощи вне медицинской организации ________ случаев оказания медицинской помощи на сумму ______ рублей.", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createSecondTable(document, registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBeanCollection4);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "2.2. Не принято к оплате в связи с превышением установленных комиссией по разработке территориальной программы обязательного медицинского страхования объемов медицинской помощи, всего " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean.getCaseIdCount()) + " случаев оказания медицинской помощи на сумму " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean.getServiceSum2Stationary()) + " рублей.", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "В том числе:", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "	а) за оказание медицинской помощи в стационарных условиях __________ случаев оказания медицинской помощи на сумму __________ рублей;", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "	б) за оказание медицинской помощи в условиях дневного стационара __________ случаев оказания медицинской помощи на сумму __________ рублей;", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "	в) за оказание медицинской помощи в амбулаторных условиях случаев __________ оказания медицинской помощи на сумму __________ рублей;", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "	г) за оказание медицинской помощи вне медицинской организации __________ случаев оказания медицинской помощи на сумму __________ рублей.", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createThirdTable(document, registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBeanCollection);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "Дата предоставления счетов Федеральному фонду обязательного медицинского страхования/территориальному фонду обязательного медицинского страхования " + registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean.getLastSendingDate() + " г.", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "Дата проверки счетов (реестров) " + new SimpleDateFormat("dd.MM.yyyy").format(new Date()) + " г.", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "Руководитель (уполномоченное лицо) Федерального фонда обязательного медицинского страхования/территориального фонда обязательного медицинского страхования:", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createSignTable(document, null, null);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createSignTable(document, "Руководитель медицинской организации: ", null);
			document.write(out);
		}
	}

	private void createFirstTable(XWPFDocument document, RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean) {
		final int rowsCount = 18;
		final int columnsCount = 4;
		final int defaultTextSize = 12;
		XWPFTable table = document.createTable(rowsCount, columnsCount);
		table.setTableAlignment(TableRowAlign.CENTER);
		table.setWidth("100%");
		initTable(table, rowsCount, columnsCount);
		setWidth(table, new double[]{1d, 1d, 1d, 1d});
		mergeCellVertically(table, 0, 1, 5);
		mergeCellVertically(table, 0, 6, 10);
		mergeCellVertically(table, 0, 11, 15);
		mergeCellVertically(table, 0, 16, 17);
		createParagraph(table.getRows().get(0).getCell(0).getParagraphArray(0), "Результаты", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(1).getParagraphArray(0), "Условия оказания медицинской помощи", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(2).getParagraphArray(0), "Количество случаев", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(3).getParagraphArray(0), "На сумму, рублей", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(1).getCell(0).getParagraphArray(0), "Предоставлены счета/реестры счетов за медицинскую помощь, оказанную застрахованным лицам", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(1).getCell(1).getParagraphArray(0), "всего, в том числе:", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(1).getCell(2).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter1TotalServiceCount().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(1).getCell(3).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter1TotalServiceSum().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(2).getCell(1).getParagraphArray(0), "стационарно", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(2).getCell(2).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter1Row1ServiceCount().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(2).getCell(3).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter1Row1ServiceSum().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(3).getCell(1).getParagraphArray(0), "в дневном стационаре", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(3).getCell(2).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter1Row2ServiceCount().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(3).getCell(3).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter1Row2ServiceSum().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(4).getCell(1).getParagraphArray(0), "амбулаторно", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(4).getCell(2).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter1Row3ServiceCount().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(4).getCell(3).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter1Row3ServiceSum().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(5).getCell(1).getParagraphArray(0), "вне медицинской организации", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(5).getCell(2).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter1Row4ServiceCount().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(5).getCell(3).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter1Row4ServiceSum().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(6).getCell(0).getParagraphArray(0), "Принято к оплате за медицинскую помощь, оказанную", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(6).getCell(1).getParagraphArray(0), "всего, в том числе:", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(6).getCell(2).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter2TotalServiceCount().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(6).getCell(3).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter2TotalServiceSum().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(7).getCell(1).getParagraphArray(0), "стационарно", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(7).getCell(2).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter2Row1ServiceCount().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(7).getCell(3).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter2Row1ServiceSum().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(8).getCell(1).getParagraphArray(0), "в дневном стационаре", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(8).getCell(2).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter2Row2ServiceCount().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(8).getCell(3).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter2Row2ServiceSum().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(9).getCell(1).getParagraphArray(0), "амбулаторно", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(9).getCell(2).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter2Row3ServiceCount().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(9).getCell(3).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter2Row3ServiceSum().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(10).getCell(1).getParagraphArray(0), "вне медицинской организации", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(10).getCell(2).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter2Row4ServiceCount().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(10).getCell(3).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter2Row4ServiceSum().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(11).getCell(0).getParagraphArray(0), "Снято с оплаты за медицинскую помощь, оказанную", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(11).getCell(1).getParagraphArray(0), "всего, в том числе:", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(11).getCell(2).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter3TotalServiceCount().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(11).getCell(3).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter3TotalServiceSum().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(12).getCell(1).getParagraphArray(0), "стационарно", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(12).getCell(2).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter3Row1ServiceCount().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(12).getCell(3).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter3Row1ServiceSum().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(13).getCell(1).getParagraphArray(0), "в дневном стационаре", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(13).getCell(2).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter3Row2ServiceCount().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(13).getCell(3).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter3Row2ServiceSum().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(14).getCell(1).getParagraphArray(0), "амбулаторно", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(14).getCell(2).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter3Row3ServiceCount().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(14).getCell(3).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter3Row3ServiceSum().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(15).getCell(1).getParagraphArray(0), "вне медицинской организации", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(15).getCell(2).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter3Row4ServiceCount().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(15).getCell(3).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter3Row4ServiceSum().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(16).getCell(0).getParagraphArray(0), "В том числе снято с оплаты за предъявление к оплате за оказанную медицинскую помощь:", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(16).getCell(1).getParagraphArray(0), "сверх распределенного объема", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(16).getCell(2).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter4Row1ServiceCount().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(16).getCell(3).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter4Row1ServiceSum().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(17).getCell(1).getParagraphArray(0), "сверх размера финансового обеспечения", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(17).getCell(2).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter4Row2ServiceCount().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(17).getCell(3).getParagraphArray(0), registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter4Row2ServiceSum().toString(), ParagraphAlignment.CENTER, defaultTextSize);
	}

	private void createSecondTable(XWPFDocument document, Collection<RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBean> registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBeanCollection) {
		final int rowsCount = 2;
		final int columnsCount = 11;
		final int defaultTextSize = 12;
		XWPFTable table = document.createTable(rowsCount, columnsCount);
		table.setTableAlignment(TableRowAlign.CENTER);
		table.setWidth("100%");
		initTable(table, rowsCount, columnsCount);
		setWidth(table, new double[]{1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d});
		createParagraph(table.getRows().get(0).getCell(0).getParagraphArray(0), "Код структурного подразделения медицинской организации", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(1).getParagraphArray(0), "Код профиля отделения (коек)", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(2).getParagraphArray(0), "N индивидуального счета", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(3).getParagraphArray(0), "Период (месяц)", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(4).getParagraphArray(0), "N полиса обязательного медицинского страхования", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(5).getParagraphArray(0), "Код территории страхования", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(6).getParagraphArray(0), "Код нарушения (дефекта)", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(7).getParagraphArray(0), "Сумма, подлежащая неоплате и (или) уменьшению оплаты, рублей", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(8).getParagraphArray(0), "Код финансовых санкций", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(9).getParagraphArray(0), "Сумма финансовых санкций, рублей", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(10).getParagraphArray(0), "Прочие коды выявленных нарушений (дефектов)", ParagraphAlignment.CENTER, defaultTextSize);
		for (int i = 0; i < 11; i++) {
			createParagraph(table.getRows().get(1).getCell(i).getParagraphArray(0), (i + 1) + "", ParagraphAlignment.CENTER, defaultTextSize);
		}
		int rowIndex = rowsCount;
		Iterator<RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBean> registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBeanIterator = registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBeanCollection.iterator();
		while (registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBeanIterator.hasNext()) {
			table.createRow();
			RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBean next = registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBeanIterator.next();
			createParagraph(table.getRows().get(rowIndex).getCell(0).getParagraphArray(0), nh(next.getFilId()), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(1).getParagraphArray(0), next.getIotd(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(2).getParagraphArray(0), nh(next.getBillId()), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(3).getParagraphArray(0), next.getMonth(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(4).getParagraphArray(0), next.getPolicyNumber(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(5).getParagraphArray(0), nh(next.getTerritoryCode()), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(6).getParagraphArray(0), next.getViolationCode(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(7).getParagraphArray(0), nh(next.getServiceSum()), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(8).getParagraphArray(0), nh(next.getFinancialSanctionsCode()), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(9).getParagraphArray(0), nh(next.getFinancialSanctionsSum()), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(10).getParagraphArray(0), next.getOtherViolationCodes(), ParagraphAlignment.CENTER, defaultTextSize);
			rowIndex++;
		}
	}

	private void createThirdTable(XWPFDocument document, Collection<RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBean> registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBeanCollection) {
		final int rowsCount = 2;
		final int columnsCount = 10;
		final int defaultTextSize = 12;
		XWPFTable table = document.createTable(rowsCount, columnsCount);
		table.setTableAlignment(TableRowAlign.CENTER);
		table.setWidth("100%");
		initTable(table, rowsCount, columnsCount);
		setWidth(table, new double[]{1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d});
		createParagraph(table.getRows().get(0).getCell(0).getParagraphArray(0), "Код структурного подразделения медицинской организации", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(1).getParagraphArray(0), "Код профиля отделения (коек)", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(2).getParagraphArray(0), "N индивидуального счета", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(3).getParagraphArray(0), "Период, в котором произошло превышение согласованных объемов (квартал)", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(4).getParagraphArray(0), "Величина превышения согласованных объемов медицинских услуг, рублей", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(5).getParagraphArray(0), "Сумма, не подлежащая оплате в связи с превышением согласованных объемов, рублей", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(6).getParagraphArray(0), "Сумма, не принятая к оплате в связи с превышением согласованных объемов, рублей", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(7).getParagraphArray(0), "в том числе до проведения повторного медико-экономического контроля", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(8).getParagraphArray(0), "Сумма, удерживаемая в текущем месяце, рублей", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(9).getParagraphArray(0), "Сумма, подлежащая удержанию в последующий период, рублей", ParagraphAlignment.CENTER, defaultTextSize);
		for (int i = 0; i < 10; i++) {
			createParagraph(table.getRows().get(1).getCell(i).getParagraphArray(0), (i + 1) + "", ParagraphAlignment.CENTER, defaultTextSize);
		}
		int rowIndex = rowsCount;
		Iterator<RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBean> registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBeanIterator = registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBeanCollection.iterator();
		while (registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBeanIterator.hasNext()) {
			table.createRow();
			RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBean next = registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBeanIterator.next();
			createParagraph(table.getRows().get(rowIndex).getCell(0).getParagraphArray(0), nh(next.getFilId()), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(1).getParagraphArray(0), next.getIotd(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(2).getParagraphArray(0), nh(next.getBillId()), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(3).getParagraphArray(0), next.getMonth(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(4).getParagraphArray(0), nh(next.getExcessSum()), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(5).getParagraphArray(0), nh(next.getServiceSum()), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(6).getParagraphArray(0), nh(next.getNotAcceptedServiceSum()), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(7).getParagraphArray(0), nh(next.getNotAcceptedServiceSumBeforeRepeatedCheck()), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(8).getParagraphArray(0), nh(next.getHoldedServiceSum()), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(9).getParagraphArray(0), nh(next.getServiceSumThatShouldBeHoldedOnTheNextPeriod()), ParagraphAlignment.CENTER, defaultTextSize);
			rowIndex++;
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
			createParagraph(table.getRows().get(row++).getCell(0).getParagraphArray(0), firstRow, ParagraphAlignment.LEFT, defaultTextSize);
		}
		createParagraph(table.getRows().get(row++).getCell(2).getParagraphArray(0), "\"__\" __________ 202_ г.", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(row).getCell(0).getParagraphArray(0), "(подпись)", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(row).getCell(1).getParagraphArray(0), "(фамилия, имя, отчество (отчество - при наличии)", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(row++).getCell(2).getParagraphArray(0), "(дата)", ParagraphAlignment.CENTER, defaultTextSize);
		if (lastRow != null) {
			createParagraph(table.getRows().get(row).getCell(0).getParagraphArray(0), lastRow, ParagraphAlignment.LEFT, defaultTextSize);
		}
	}

	private String nh(Number n) {
		return n != null ? n.toString() : "";
	}
}
