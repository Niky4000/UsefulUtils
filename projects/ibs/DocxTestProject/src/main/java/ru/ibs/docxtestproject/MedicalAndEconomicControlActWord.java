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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TableRowAlign;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.hibernate.SessionFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
import static ru.ibs.docxtestproject.WordUtils.changeOrientation;
import static ru.ibs.docxtestproject.WordUtils.createParagraph;
import static ru.ibs.docxtestproject.WordUtils.initTable;
import static ru.ibs.docxtestproject.WordUtils.mergeCellHorizontally;
import static ru.ibs.docxtestproject.WordUtils.mergeCellVertically;
import static ru.ibs.docxtestproject.WordUtils.setWidth;
import ru.ibs.docxtestproject.bean.MedicalAndEconomicControlActWordFirstTableBean;
import ru.ibs.docxtestproject.bean.MedicalAndEconomicControlActWordHeadBean;
import ru.ibs.docxtestproject.bean.MedicalAndEconomicControlActWordSecondTableBean;
import ru.ibs.pmp.common.lib.Db;

public class MedicalAndEconomicControlActWord {

	SessionFactory smoSessionFactory;
	private static final long EMERGENCY = 4708L;

	public void create(Long lpuId, Date period, Long parcelId, String path) throws IOException {
		String periodStr = new SimpleDateFormat("yyyy-MM").format(period);
		List<Object[]> headList;
		if (!lpuId.equals(EMERGENCY)) {
			headList = Db.select(smoSessionFactory, session -> session.createSQLQuery("select period,period_str,mo_id,parcel_id,num,date_crt,date_send,mo_name,mo_mcod,smo_name,smo_qq,service_sum_all,adm_name,last_day from table (pmp_akt_mek_v1.INIT_GET_report(:periodStr, :lpuId, :parcelId, null, null))").setParameter("lpuId", lpuId).setParameter("periodStr", periodStr).setParameter("parcelId", parcelId).list());
		} else {
			headList = Db.select(smoSessionFactory, session -> session.createSQLQuery("select period,period_str,mo_id,parcel_id,num,date_crt,date_send,mo_name,mo_mcod,smo_name,smo_qq,service_sum_all,adm_name,last_day from table (pmp_akt_mek_v1.init_get_report_smp(:periodStr, :lpuId, :parcelId, null, null))").setParameter("lpuId", lpuId).setParameter("periodStr", periodStr).setParameter("parcelId", parcelId).list());
		}
		List<Object[]> table1ObjectList = Db.select(smoSessionFactory, session -> session.createSQLQuery("select r_n,period,lpu_id,parcel_id,prof,usl_ok,opl_qt,opl_sum,err_qt,err_sum,itog_qt,itog_sum from table (pmp_akt_mek_v1.get_tbl1(:periodStr, :lpuId, :parcelId))").setParameter("lpuId", lpuId).setParameter("periodStr", periodStr).setParameter("parcelId", parcelId).list());
		List<Object[]> table2ObjectList = Db.select(smoSessionFactory, session -> session.createSQLQuery("select rn,period,lpu_id,parcel_id,recid,sn_pol,usl_ok,date_begin,date_end,prof_cod,prof,cod,count_err,err_code,err_code_1,err_code_2,err_code_3,err_code_4,err_code_5,sank_sum,err_sum,fine_sum,ds from table (pmp_akt_mek_v1.get_tbl2(:periodStr, :lpuId, :parcelId))").setParameter("lpuId", lpuId).setParameter("periodStr", periodStr).setParameter("parcelId", parcelId).list());
		MedicalAndEconomicControlActWordHeadBean medicalAndEconomicControlActWordHeadBean = new MedicalAndEconomicControlActWordHeadBean(headList.get(0));
		List<MedicalAndEconomicControlActWordFirstTableBean> medicalAndEconomicControlActWordFirstTableBeanCollection = table1ObjectList.stream().map(MedicalAndEconomicControlActWordFirstTableBean::new).collect(Collectors.toList());
		List<MedicalAndEconomicControlActWordSecondTableBean> medicalAndEconomicControlActWordSecondTableBeanCollection = table2ObjectList.stream().map(MedicalAndEconomicControlActWordSecondTableBean::new).collect(Collectors.toList());
		create(medicalAndEconomicControlActWordHeadBean, medicalAndEconomicControlActWordFirstTableBeanCollection, medicalAndEconomicControlActWordSecondTableBeanCollection, path);
	}

	public void create(MedicalAndEconomicControlActWordHeadBean medicalAndEconomicControlActWordHeadBean, Collection<MedicalAndEconomicControlActWordFirstTableBean> medicalAndEconomicControlActWordFirstTableBeanCollection, Collection<MedicalAndEconomicControlActWordSecondTableBean> medicalAndEconomicControlActWordSecondTableBeanCollection, String path) throws IOException {
		//Check the generated path. If it is not there, create it.
		if (!new File(path).getParentFile().exists()) {
			Files.createDirectories(Paths.get(new File(path).getParentFile().getAbsolutePath()));
		}
		try (FileOutputStream out = new FileOutputStream(path);
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
			createParagraph(document, new StringBuilder("Федеральный фонд обязательного медицинского страхования/территориальный\n")
					.append("фонд обязательного медицинского страхования «Московский городской фонд обязательного медицинского страхования»\n")
					.append("Наименование страховой медицинской организации ").append(medicalAndEconomicControlActWordHeadBean.getSmoName()).append("\n")
					.append("Наименование медицинской организации ").append(medicalAndEconomicControlActWordHeadBean.getMoName()).append("\n")
					.append("Наименование территориального фонда обязательного медицинского страхования\n")
					.append("по    месту    страхования    застрахованного    лица    (при    проведении\n")
					.append("межтерриториальных взаиморасчетов) _________________________________________\n")
					.append("Номер счета/реестра счетов ").append(medicalAndEconomicControlActWordHeadBean.getNum()).append("\n")
					.append("Период, за который предоставлен счет/реестр счетов:\n")
					.append("с \"01\" ").append(medicalAndEconomicControlActWordHeadBean.getPeriodStr()).append(" г. по \"").append(medicalAndEconomicControlActWordHeadBean.getLastDay()).append("\" ").append(medicalAndEconomicControlActWordHeadBean.getPeriodStr()).append(" г.").toString(), ParagraphAlignment.LEFT, 12);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "II. Сведения об оказанной медицинской помощи:", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createFirstTable(document, medicalAndEconomicControlActWordFirstTableBeanCollection);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document2, "III. Результаты медико-экономического контроля:", ParagraphAlignment.LEFT, 12);
			createParagraph(document2, "", ParagraphAlignment.LEFT, 12);
			createSecondTable(document2, medicalAndEconomicControlActWordSecondTableBeanCollection);
			createParagraph(document2, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document2, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document2, "Итоговая сумма, принятая к оплате: " + medicalAndEconomicControlActWordHeadBean.getServiceSumAll().toString(), ParagraphAlignment.LEFT, 12);
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
			XWPFDocument mergeDocuments = WordUtils.mergeDocuments(Stream.of(document, document2, document3));
			mergeDocuments.write(out);
		}
	}

	private void createFirstTable(XWPFDocument document, Collection<MedicalAndEconomicControlActWordFirstTableBean> medicalAndEconomicControlActWordFirstTableBeanCollection) {
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
		Iterator<MedicalAndEconomicControlActWordFirstTableBean> iterator = medicalAndEconomicControlActWordFirstTableBeanCollection.iterator();
		while (iterator.hasNext()) {
			MedicalAndEconomicControlActWordFirstTableBean next = iterator.next();
			table.createRow();
			int rowsSize = table.getRows().size();
			table.getRows().get(rowsSize - 1).addNewTableCell();
			table.getRows().get(rowsSize - 1).addNewTableCell();
			table.getRows().get(rowsSize - 1).addNewTableCell();
			createParagraph(table.getRows().get(rowsSize - 1).getCell(0).getParagraphArray(0), next.getRn().toString(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(1).getParagraphArray(0), next.getUslOk(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(2).getParagraphArray(0), next.getProf(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(3).getParagraphArray(0), next.getOplQt().toString(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(4).getParagraphArray(0), next.getOplSum().toString(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(5).getParagraphArray(0), next.getErrQt().toString(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(6).getParagraphArray(0), next.getErrSum().toString(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(7).getParagraphArray(0), next.getItogQt().toString(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(8).getParagraphArray(0), next.getItogSum().toString(), ParagraphAlignment.CENTER, defaultTextSize);
		}
	}

	private void createSecondTable(XWPFDocument document, Collection<MedicalAndEconomicControlActWordSecondTableBean> medicalAndEconomicControlActWordSecondTableBeanCollection) {
		final int rowsCount = 3;
		final int columnsCount = 20;
		final int defaultTextSize = 6;
		XWPFTable table = document.createTable(rowsCount, columnsCount);
		table.setTableAlignment(TableRowAlign.CENTER);
		table.setWidth("100%");
		initTable(table, rowsCount, columnsCount);
		setWidth(table, new double[]{1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d, 1d});
		mergeCellVertically(table, 0, 0, 1);
		mergeCellVertically(table, 1, 0, 1);
		mergeCellVertically(table, 2, 0, 1);
		mergeCellVertically(table, 3, 0, 1);
		mergeCellVertically(table, 4, 0, 1);
		mergeCellVertically(table, 6, 0, 1);
		mergeCellVertically(table, 7, 0, 1);
		mergeCellVertically(table, 8, 0, 1);
		mergeCellVertically(table, 9, 0, 1);
		mergeCellVertically(table, 10, 0, 1);
		mergeCellVertically(table, 11, 0, 1);
		mergeCellVertically(table, 17, 0, 1);
		mergeCellVertically(table, 18, 0, 1);
		mergeCellVertically(table, 19, 0, 1);
		mergeCellHorizontally(table, 0, 5, 6);
		mergeCellHorizontally(table, 0, 11, 15);
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
		createParagraph(table.getRows().get(0).getCell(12).getParagraphArray(0), "Сумма финансовых санкций, по коду нарушения (дефекта), рублей", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(13).getParagraphArray(0), "Сумма неоплаты и (или) уменьшения оплаты, рублей", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(14).getParagraphArray(0), "Сумма штрафа, рублей ", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(0).getCell(defaultTextSize).getParagraphArray(0), "Перечень кодов нарушений", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(1).getCell(5).getParagraphArray(0), "дата начала", ParagraphAlignment.CENTER, defaultTextSize);
		createParagraph(table.getRows().get(1).getCell(6).getParagraphArray(0), "дата окончания", ParagraphAlignment.CENTER, defaultTextSize);
		for (int i = 1; i <= columnsCount; i++) {
			createParagraph(table.getRows().get(2).getCell(i - 1).getParagraphArray(0), i + "", ParagraphAlignment.CENTER, defaultTextSize);
		}
		Iterator<MedicalAndEconomicControlActWordSecondTableBean> iterator = medicalAndEconomicControlActWordSecondTableBeanCollection.iterator();
		while (iterator.hasNext()) {
			MedicalAndEconomicControlActWordSecondTableBean next = iterator.next();
			table.createRow();
			int rowsSize = table.getRows().size();
			table.getRows().get(rowsSize - 1).addNewTableCell();
			table.getRows().get(rowsSize - 1).addNewTableCell();
			table.getRows().get(rowsSize - 1).addNewTableCell();
			table.getRows().get(rowsSize - 1).addNewTableCell();
			table.getRows().get(rowsSize - 1).addNewTableCell();
			createParagraph(table.getRows().get(rowsSize - 1).getCell(0).getParagraphArray(0), next.getRn().toString(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(1).getParagraphArray(0), next.getRecid(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(2).getParagraphArray(0), next.getSnPol(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(3).getParagraphArray(0), next.getDs(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(4).getParagraphArray(0), next.getUslOk(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(5).getParagraphArray(0), next.getDateBegin(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(6).getParagraphArray(0), next.getDateEnd(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(7).getParagraphArray(0), next.getProfCode(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(8).getParagraphArray(0), next.getProf(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(9).getParagraphArray(0), next.getCode(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(10).getParagraphArray(0), next.getCountErr().toString(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(11).getParagraphArray(0), next.getErrCode(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(12).getParagraphArray(0), next.getErrCode1(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(13).getParagraphArray(0), next.getErrCode2(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(14).getParagraphArray(0), next.getErrCode3(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(15).getParagraphArray(0), next.getErrCode4(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(16).getParagraphArray(0), next.getErrCode5(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(17).getParagraphArray(0), next.getSankSum(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(18).getParagraphArray(0), next.getErrSum().toString(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowsSize - 1).getCell(19).getParagraphArray(0), next.getFineSum(), ParagraphAlignment.CENTER, defaultTextSize);
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
}
