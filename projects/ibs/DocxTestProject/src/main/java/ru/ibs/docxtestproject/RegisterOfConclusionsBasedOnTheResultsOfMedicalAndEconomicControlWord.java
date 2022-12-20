package ru.ibs.docxtestproject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import ru.ibs.docxtestproject.bean.RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean;
import ru.ibs.docxtestproject.bean.RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean;
import ru.ibs.docxtestproject.bean.RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBean;
import ru.ibs.docxtestproject.bean.RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBean;
import ru.ibs.pmp.common.lib.Db;

public class RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlWord {

	SessionFactory smoSessionFactory;

	public void create(Long lpuId, Date period, Long parcelId, String path) throws IOException {
		String periodStr = new SimpleDateFormat("yyyy-MM").format(period);
		List<Object[]> headList = Db.select(smoSessionFactory, session -> session.createSQLQuery("select date_crt,date_send,num,period_str,last_day,mo_name from table(pmp_reestr_akt_v1.get_report(:periodStr, :lpuId, :parcelId, null, null))").setParameter("lpuId", lpuId).setParameter("periodStr", periodStr).setParameter("parcelId", parcelId).list());
		List<Object[]> firstTableList = Db.select(smoSessionFactory, session -> session.createSQLQuery("select opl_qtall,opl_sumall,opl_qt1,opl_sum1,opl_qt2,opl_sum2,opl_qt3,opl_sum3,opl_qt4,opl_sum4,itog_qtall,itog_sumall,itog_qt1,itog_sum1,itog_qt2,itog_sum2,itog_qt3,itog_sum3,itog_qt4,itog_sum4,err_qtall,err_sumall,err_qt1,err_sum1,err_qt2,err_sum2,err_qt3,err_sum3,err_qt4,err_sum4,qt_defect_pp,sum_defect_pp,qt_defect_pq,sum_defect_pq from table(pmp_reestr_akt_v1. get_tbl1(:periodStr, :lpuId, :parcelId, null, null))").setParameter("lpuId", lpuId).setParameter("periodStr", periodStr).setParameter("parcelId", parcelId).list());
		List<Object[]> secondTableList = Db.select(smoSessionFactory, session -> session.createSQLQuery("select usl_ok,fil_id,bill_id,iotd,date_mm,sn_pol,cod_terr,err_code_1,service_sum,cod_fine,err_other from table(pmp_reestr_akt_v1.get_tbl2(:periodStr, :lpuId, :parcelId, null, null))").setParameter("lpuId", lpuId).setParameter("periodStr", periodStr).setParameter("parcelId", parcelId).list());
		List<Object[]> thirdTableList = Db.select(smoSessionFactory, session -> session.createSQLQuery("select fil_id,iotd,bill_id,quarter,sum_pre,sum_fil,sum_pre as sum_pre2,sum_all,0 as sum_,qt_all,qt_all_1,sum_all_1,qt_all_2,sum_all_2,qt_all_3,sum_all_3,qt_all_4,sum_all_4 from table(pmp_reestr_akt_v1.get_tbl3(:periodStr, :lpuId, :parcelId, null, null))").setParameter("lpuId", lpuId).setParameter("periodStr", periodStr).setParameter("parcelId", parcelId).list());
		List<RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean> headBeanList = headList.stream().map(RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean::new).collect(Collectors.toList());
		List<RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean> firstTableBeanList = firstTableList.stream().map(RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean::new).collect(Collectors.toList());
		List<RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBean> secondTableBeanList = secondTableList.stream().map(RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBean::new).collect(Collectors.toList());
		List<RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBean> thirdTableBeanList = thirdTableList.stream().map(RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBean::new).collect(Collectors.toList());
		Map<String, List<RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBean>> secondTableBeanMap = secondTableBeanList.stream().collect(Collectors.groupingBy(RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBean::getUslOk));
		create(headBeanList.get(0), firstTableBeanList.get(0), secondTableBeanMap.getOrDefault("1", new ArrayList<>(1)), secondTableBeanMap.getOrDefault("2", new ArrayList<>(1)), secondTableBeanMap.getOrDefault("3", new ArrayList<>(1)), secondTableBeanMap.getOrDefault("4", new ArrayList<>(1)), thirdTableBeanList, path);
	}

	public void create(RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean, RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean, Collection<RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBean> registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBeanCollection1, Collection<RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBean> registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBeanCollection2, Collection<RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBean> registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBeanCollection3, Collection<RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBean> registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBeanCollection4, Collection<RegisterOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBean> registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBeanCollection, String path) throws IOException {
		if (!new File(path).getParentFile().exists()) {
			Files.createDirectories(Paths.get(new File(path).getParentFile().getAbsolutePath()));
		}
		try (FileOutputStream out = new FileOutputStream(path);
				XWPFDocument document = new XWPFDocument();) {
			changeOrientation(document, STPageOrientation.LANDSCAPE);
			createParagraph(document, "Реестр", ParagraphAlignment.CENTER, 12);
			createParagraph(document, "заключений по результатам медико-экономического контроля", ParagraphAlignment.CENTER, 12);
			createParagraph(document, "от " + dh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean.getDateCrt()) + "  г. № " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean.getNum()), ParagraphAlignment.CENTER, 12);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "Период с \"01\" " + registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean.getPeriodStr() + " г. по \" " + registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean.getLastDay() + " \" " + registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean.getPeriodStr() + " г.", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "Федеральный фонд обязательного медицинского страхования/территориальный фонд обязательного медицинского страхования, получивший счета от медицинской организации Московский городской фонд обязательного медицинского страхования", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "Код Федерального фонда обязательного медицинского страхования/территориального фонда обязательного медицинского страхования, получившего счета от медицинской организации ________________________________________________________ ", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "Код территории местонахождения Федерального фонда обязательного медицинского страхования/территориального фонда обязательного медицинского страхования ________________________________________________________________ ", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "Наименование медицинской организации, предоставившей счет " + registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean.getMoName(), ParagraphAlignment.LEFT, 12);
			createParagraph(document, "Код медицинской организации, предоставившей счет ____________________________", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "Код территории местонахождения медицинской организации, предоставившей счет ", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "1. Сведения о результатах медико-экономического контроля:", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createFirstTable(document, registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "2.1. Не подлежит оплате, всего " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter3TotalServiceCount()) + " случаев оказания медицинской помощи на сумму " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter3TotalServiceSum()) + " рублей.", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "2.1.1. За оказание медицинской помощи в стационарных условиях " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter3Row1ServiceSum()) + " случаев оказания медицинской помощи на сумму " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter3Row1ServiceSum()) + " рублей.", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createSecondTable(document, registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBeanCollection1);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "2.1.2. За оказание медицинской помощи в условиях дневного стационара " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter3Row2ServiceSum()) + " случаев оказания медицинской помощи на сумму " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter3Row2ServiceSum()) + " рублей.", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createSecondTable(document, registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBeanCollection2);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "2.1.3. За оказание медицинской помощи в амбулаторных условиях " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter3Row3ServiceSum()) + " случаев оказания медицинской помощи на сумму " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter3Row3ServiceSum()) + " рублей.", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createSecondTable(document, registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBeanCollection3);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "2.1.4. За оказание медицинской помощи вне медицинской организации " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter3Row4ServiceSum()) + " случаев оказания медицинской помощи на сумму " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlFirstTableBean.getChapter3Row4ServiceSum()) + " рублей.", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createSecondTable(document, registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlSecondTableBeanCollection4);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "2.2. Не принято к оплате в связи с превышением установленных комиссией по разработке территориальной программы обязательного медицинского страхования объемов медицинской помощи, всего " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBeanCollection.iterator().next().getQtAll()) + " случаев оказания медицинской помощи на сумму " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBeanCollection.iterator().next().getSumAll()) + " рублей.", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "В том числе:", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "	а) за оказание медицинской помощи в стационарных условиях " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBeanCollection.iterator().next().getQtAll1()) + " случаев оказания медицинской помощи на сумму " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBeanCollection.iterator().next().getSumAll1()) + " рублей;", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "	б) за оказание медицинской помощи в условиях дневного стационара " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBeanCollection.iterator().next().getQtAll2()) + " случаев оказания медицинской помощи на сумму " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBeanCollection.iterator().next().getSumAll2()) + " рублей;", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "	в) за оказание медицинской помощи в амбулаторных условиях случаев " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBeanCollection.iterator().next().getQtAll3()) + " оказания медицинской помощи на сумму " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBeanCollection.iterator().next().getSumAll3()) + " рублей;", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "	г) за оказание медицинской помощи вне медицинской организации " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBeanCollection.iterator().next().getQtAll4()) + " случаев оказания медицинской помощи на сумму " + nh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBeanCollection.iterator().next().getSumAll4()) + " рублей.", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createThirdTable(document, registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlThirdTableBeanCollection);
			createParagraph(document, "", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "Дата предоставления счетов Федеральному фонду обязательного медицинского страхования/территориальному фонду обязательного медицинского страхования " + dh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean.getDateSend()) + " г.", ParagraphAlignment.LEFT, 12);
			createParagraph(document, "Дата проверки счетов (реестров) " + dh(registerOfConclusionsBasedOnTheResultsOfMedicalAndEconomicControlHeadBean.getDateCrt()) + ".", ParagraphAlignment.LEFT, 12);
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
			createParagraph(table.getRows().get(rowIndex).getCell(3).getParagraphArray(0), next.getDateMn(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(4).getParagraphArray(0), next.getPolicyNumber(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(5).getParagraphArray(0), next.getTerritoryCode(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(6).getParagraphArray(0), next.getErrorCode(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(7).getParagraphArray(0), nh(next.getServiceSum()), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(8).getParagraphArray(0), next.getFinancialSanctionsCode(), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(9).getParagraphArray(0), nh(next.getServiceSum()), ParagraphAlignment.CENTER, defaultTextSize);
			createParagraph(table.getRows().get(rowIndex).getCell(10).getParagraphArray(0), next.getOtherErrorCodes(), ParagraphAlignment.CENTER, defaultTextSize);
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
			createParagraph(table.getRows().get(rowIndex).getCell(3).getParagraphArray(0), next.getQuarter(), ParagraphAlignment.CENTER, defaultTextSize);
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

	private static final String zeroEnding = ".0";
	private static final String dot = ".";

	private String nh(Number n) {
		if (n != null) {
			String str = n.toString();
			if (str.endsWith(zeroEnding)) {
				return str.substring(0, str.length() - zeroEnding.length());
			} else if (str.contains(dot)) {
				if (str.substring(str.indexOf(dot), str.length()).length() < 2) {
					return str + "0";
				} else {
					return str;
				}
			} else {
				return str;
			}
		} else {
			return "";
		}
	}

	private String dh(Date d) {
		return d != null ? new SimpleDateFormat("dd.MM.yyyy").format(d) : "";
	}
}
