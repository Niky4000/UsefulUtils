//package ru.ibs.testpumputils;
//
//import com.itextpdf.text.Document;
//import static com.itextpdf.text.Element.ALIGN_LEFT;
//import com.itextpdf.text.PageSize;
//import com.itextpdf.text.pdf.PdfPTable;
//import com.itextpdf.text.pdf.PdfWriter;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.nio.file.Files;
//import java.nio.file.StandardOpenOption;
//import java.text.SimpleDateFormat;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.stream.Collectors;
//import org.hibernate.SessionFactory;
//import ru.ibs.pmp.api.service.export.msk.pdf.TransparentWatermark;
//import ru.ibs.pmp.api.smo.model.Parcel;
//import ru.ibs.pmp.common.lib.Db;
//import ru.ibs.pmp.service.utils.pdf.PdfHelper;
//import ru.ibs.pmp.smo.export.mo.ReportExportContext;
//import ru.ibs.pmp.smo.report.export.impl.bean.AcceptanceProtocolBean;
//import ru.ibs.pmp.smo.report.export.impl.bean.AcceptanceProtocolBeanHead;
//import ru.ibs.pmp.smo.report.export.impl.bean.AcceptanceProtocolBeanTail;
//
///**
// *
// * @author me
// */
//public class AcceptanceProtocolExporterTest {
//
//    PdfHelper pdf;
//
//    {
//        try {
//            pdf = new PdfHelper();
//        } catch (Exception ex) {
//            throw new RuntimeException(ex);
//        }
//    }
//
//    static SessionFactory smoSessionFactory;
//
//    public static void test() throws Exception {
//        smoSessionFactory = TestPumpUtilsMain.buildSmoSessionFactory();
//        try {
//            Parcel parcel = new Parcel();
//            parcel.setMoId(4102L);
//            parcel.setPeriod(new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-01"));
//            parcel.setId(2233L);
//            ReportExportContext context = new ReportExportContext(null, null);
//            context.setSmoRequestId(111L);
//            context.setFlkVersion(222L);
//            AcceptanceProtocolBeanHead acceptanceProtocolBeanHead = Db.select(smoSessionFactory, session -> (AcceptanceProtocolBeanHead) session.createSQLQuery("select rownum as id,period,period_txt,mo_id,parcel_id,smo_name,mo_name from table (pmp_smo.rpt_smo_akt_calc.get_head(:period, :lpuId, :parcelId, :answer_num, :flk_version))").addEntity(AcceptanceProtocolBeanHead.class).setParameter("period", parcel.getPeriod()).setParameter("lpuId", parcel.getMoId()).setParameter("parcelId", parcel.getId()).setParameter("answer_num", context.getSmoRequestId()).setParameter("flk_version", context.getFlkVersion()).uniqueResult());
//            List<AcceptanceProtocolBean> acceptanceProtocolBeanList = Db.select(smoSessionFactory, session -> (List<AcceptanceProtocolBean>) session.createSQLQuery("select rownum as id,period,period_txt,mo_id,parcel_id,col_group,col_01,col_02,col_03,col_04,col_05,col_06,col_07,col_08,col_09,col_10,col_11,col_12 from table (pmp_smo.rpt_smo_akt_calc.get_table(:period, :lpuId, :parcelId, :answer_num, :flk_version))").addEntity(AcceptanceProtocolBean.class).setParameter("period", parcel.getPeriod()).setParameter("lpuId", parcel.getMoId()).setParameter("parcelId", parcel.getId()).setParameter("answer_num", context.getSmoRequestId()).setParameter("flk_version", context.getFlkVersion()).list());
//            AcceptanceProtocolBeanTail acceptanceProtocolBeanTail = Db.select(smoSessionFactory, session -> (AcceptanceProtocolBeanTail) session.createSQLQuery("select rownum as id,period,period_txt,mo_id,parcel_id,sum1 from table (pmp_smo.rpt_smo_akt_calc.get_footer(:period, :lpuId, :parcelId, :answer_num, :flk_version))").addEntity(AcceptanceProtocolBeanTail.class).setParameter("period", parcel.getPeriod()).setParameter("lpuId", parcel.getMoId()).setParameter("parcelId", parcel.getId()).setParameter("answer_num", context.getSmoRequestId()).setParameter("flk_version", context.getFlkVersion()).uniqueResult());
//            AcceptanceProtocolExporterTest acceptanceProtocolExporterTest = new AcceptanceProtocolExporterTest();
//            byte[] createReport = acceptanceProtocolExporterTest.createReport(acceptanceProtocolBeanHead, acceptanceProtocolBeanList, acceptanceProtocolBeanTail, false);
//            File reportFile = new File("/home/me/tmp/reportsPdf/report.pdf");
//            if (reportFile.exists()) {
//                reportFile.delete();
//            }
//            Files.write(reportFile.toPath(), createReport, StandardOpenOption.CREATE_NEW);
//        } finally {
//            smoSessionFactory.close();
//        }
//    }
//
//    private static final String title2 = "2. Прикрепленные к данной МО с ПФ *";
//    private static final String title3 = "3. Прикрепленные к другим МО с ПФ";
//    private static final String title4 = "4. Неприкрепленные";
//    private static final String title5 = "5. Итого по ПМСП: (2+3+4)";
//    private static final String title6 = "6. ПМСП по профилю \"стоматология\" (стоматологический участок)";
//    private static final String title7 = "7. Прикрепленные к данной МО с ПФ**";
//    private static final String title8 = "8. Прикрепленные к другим МО с ПФ";
//    private static final String title9 = "9. Неприкрепленные";
//    private static final String title10 = "10. Итого ПМСП по профилю \"стоматология\": (7+8+9)";
//    private static final String title11 = "11. Скорая и неотложная медицинская помощь, оказанная вне МО";
//    private static final String title12 = "12. Вызовы бригад СМП";
//    private static final String title13 = "13. Вызовы бригад НМП";
//    private static final String title14 = "14. ИТОГО по Акту (5+10+12+13):";
//    private static final String title15 = "15. Удержано за медицинскую помощь, оказанную прикрепленным к данной МО в других МО-участниках гор. расчетов";
//    private static final String title16 = "16. Полная сумма авансирования медицинской организации";
//    private static final String title17 = "17. Сумма удержаний по итогам ранее проведенных контрольно-экспертных мероприятий";
//    private static final String title18 = "18. Итого подлежит перечислению в МО (14-15-16-17)";
//
//    private byte[] createReport(AcceptanceProtocolBeanHead acceptanceProtocolBeanHead, List<AcceptanceProtocolBean> acceptanceProtocolBeanList, AcceptanceProtocolBeanTail acceptanceProtocolBeanTail, boolean isWatermark) throws Exception {
//        Map<String, AcceptanceProtocolBean> acceptanceProtocolBeanMap = acceptanceProtocolBeanList.stream().collect(Collectors.toMap(AcceptanceProtocolBean::getCol01, obj -> obj));
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        Document document = new Document(PageSize.A4.rotate(), 15, 15, 30, 25);
//        PdfWriter.getInstance(document, stream);
//        document.open();
//
//        document.add(pdf.hdrCenterBoldParahraph("1.2 Форма Акта расчётов с МО за медицинскую помощь, оказанную застрахованным СМО за отчётный период.").get());
//        document.add(pdf.hdrCenterBoldParahraph("Акт расчётов " + acceptanceProtocolBeanHead.getSmoName()).get());
//        document.add(pdf.hdrCenterSmallBoldParahraph("(наименование СМО, код СМО)").get());
//        document.add(pdf.hdrCenterBoldParahraph("с МО " + acceptanceProtocolBeanHead.getMoName()).get());
//        document.add(pdf.hdrCenterSmallBoldParahraph("(наименование МО, округ МО, код МО)").get());
//        document.add(pdf.hdrCenterBoldParahraph("за медицинскую помощь, оказанную по территориальной программе ОМС г. Москвы застрахованным лицам").get());
//        document.add(pdf.hdrCenterBoldParahraph("за " + acceptanceProtocolBeanHead.getPeriodStr()).get());
//        document.add(pdf.hdrCenterSmallBoldParahraph("(месяц)").get());
//
//        PdfPTable table = new PdfPTable(12);
//        table.setWidths(new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1});
//        table.setWidthPercentage(100);
//        table.setHorizontalAlignment(ALIGN_LEFT);
//        table.setSpacingBefore(5);
//
//        table.addCell(pdf.tblSmallCell("Категория пациентов МО (с учетом типа прикрепления)").rowspan(5).get());
//        table.addCell(pdf.tblSmallCell("").get());
//        table.addCell(pdf.tblSmallCell("Принято МГФОМС по ").colspan(10).get());
//        table.addCell(pdf.tblSmallCell("Пациентов (записей в реестре пациентов) всего").rowspan(4).get());
//        table.addCell(pdf.tblSmallCell("").get());
//        table.addCell(pdf.tblSmallCell("Стоимость, руб., коп.").colspan(8).get());
//        table.addCell(pdf.tblSmallCell("").get());
//        table.addCell(pdf.tblSmallCell("АПП МО/ ПО МО ").colspan(3).get());
//        table.addCell(pdf.tblSmallCell("Дополнительные услуги в МО").rowspan(3).get());
//        table.addCell(pdf.tblSmallCell("МП вне МО").rowspan(3).get());
//        table.addCell(pdf.tblSmallCell("ВМП (дневной стационар)").rowspan(3).get());
//        table.addCell(pdf.tblSmallCell("Стационарная помощь").colspan(2).get());
//        table.addCell(pdf.tblSmallCell("ВСЕГО (3+6+7+8 +9)").rowspan(3).get());
//        table.addCell(pdf.tblSmallCell("Стоимость лекарственных средств (ЗНО), принятых МГФОМС").rowspan(3).get());
//        table.addCell(pdf.tblSmallCell("Всего").rowspan(2).get());
//        table.addCell(pdf.tblSmallCell("в т.ч.").colspan(2).get());
//        table.addCell(pdf.tblSmallCell("Всего").rowspan(2).get());
//        table.addCell(pdf.tblSmallCell("в т.ч. ВМП").rowspan(2).get());
//        table.addCell(pdf.tblSmallCell("по направл").get());
//        table.addCell(pdf.tblSmallCell("неотложн МП").get());
//
//        table.addCell(pdf.tblSmallCell("1").get());
//        table.addCell(pdf.tblSmallCell("2").get());
//        table.addCell(pdf.tblSmallCell("3").get());
//        table.addCell(pdf.tblSmallCell("4").get());
//        table.addCell(pdf.tblSmallCell("5").get());
//        table.addCell(pdf.tblSmallCell("6").get());
//        table.addCell(pdf.tblSmallCell("7").get());
//        table.addCell(pdf.tblSmallCell("8").get());
//        table.addCell(pdf.tblSmallCell("9").get());
//        table.addCell(pdf.tblSmallCell("10").get());
//        table.addCell(pdf.tblSmallCell("11").get());
//        table.addCell(pdf.tblSmallCell("12").get());
//
//        table.addCell(pdf.tblSmallCell2Left("1. ПМСП (терапевтический/педиатрический участок)").colspan(12).get());
//
//        table.addCell(pdf.tblSmallCell(title2).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title2)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title2)).map(AcceptanceProtocolBean::getCol03).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title2)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title2)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title2)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title2)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title2)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title2)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title2)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title2)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title2)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//
//        table.addCell(pdf.tblSmallCell(title3).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title3)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title3)).map(AcceptanceProtocolBean::getCol03).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title3)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title3)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title3)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title3)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title3)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title3)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title3)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title3)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title3)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//
//        table.addCell(pdf.tblSmallCell(title4).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title4)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title4)).map(AcceptanceProtocolBean::getCol03).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title4)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title4)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title4)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title4)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title4)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title4)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title4)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title4)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title4)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//
//        table.addCell(pdf.tblSmallCell(title5).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title5)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title5)).map(AcceptanceProtocolBean::getCol03).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title5)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title5)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title5)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title5)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title5)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title5)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title5)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title5)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title5)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//
//        table.addCell(pdf.tblSmallCell2Left(title6).colspan(12).get());
//
//        table.addCell(pdf.tblSmallCell(title7).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title7)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title7)).map(AcceptanceProtocolBean::getCol03).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title7)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title7)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title7)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title7)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title7)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title7)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title7)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title7)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title7)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//
//        table.addCell(pdf.tblSmallCell(title8).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title8)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title8)).map(AcceptanceProtocolBean::getCol03).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title8)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title8)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title8)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title8)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title8)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title8)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title8)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title8)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title8)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//
//        table.addCell(pdf.tblSmallCell(title9).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title9)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title9)).map(AcceptanceProtocolBean::getCol03).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title9)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title9)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title9)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title9)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title9)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title9)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title9)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title9)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title9)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//
//        table.addCell(pdf.tblSmallCell(title10).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title10)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title10)).map(AcceptanceProtocolBean::getCol03).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title10)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title10)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title10)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title10)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title10)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title10)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title10)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title10)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title10)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//
//        table.addCell(pdf.tblSmallCell2Left(title11).colspan(12).get());
//
//        table.addCell(pdf.tblSmallCell(title12).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title12)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title12)).map(AcceptanceProtocolBean::getCol03).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title12)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title12)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title12)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title12)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title12)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title12)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title12)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title12)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title12)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//
//        table.addCell(pdf.tblSmallCell(title13).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title13)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title13)).map(AcceptanceProtocolBean::getCol03).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title13)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title13)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title13)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title13)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title13)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title13)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title13)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title13)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title13)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//
//        table.addCell(pdf.tblSmallCell(title14).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title14)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title14)).map(AcceptanceProtocolBean::getCol03).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title14)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title14)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title14)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title14)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title14)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title14)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title14)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title14)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title14)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//
//        table.addCell(pdf.tblSmallCell(title15).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title15)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title15)).map(AcceptanceProtocolBean::getCol03).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title15)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title15)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title15)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title15)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title15)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title15)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title15)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title15)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title15)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//
//        table.addCell(pdf.tblSmallCell(title16).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title16)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title16)).map(AcceptanceProtocolBean::getCol03).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title16)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title16)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title16)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title16)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title16)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title16)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title16)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title16)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title16)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//
//        table.addCell(pdf.tblSmallCell(title17).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title17)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title17)).map(AcceptanceProtocolBean::getCol03).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title17)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title17)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title17)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title17)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title17)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title17)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title17)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title17)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title17)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//
//        table.addCell(pdf.tblSmallCell(title18).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title18)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title18)).map(AcceptanceProtocolBean::getCol03).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title18)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title18)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title18)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title18)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title18)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title18)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title18)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title18)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        table.addCell(pdf.tblSmallCell(Optional.ofNullable(acceptanceProtocolBeanMap.get(title18)).map(AcceptanceProtocolBean::getCol02).orElse("")).get());
//        document.add(table);
//        document.add(pdf.lftSmallParagraph("Справочно:").get());
//        document.add(pdf.lftSmallParagraph("*Стоимость медицинской помощи, финансируемой по ПФ, рассчитанных в соответствии с установленными тарифами на медицинские услуги (руб., коп.) " + acceptanceProtocolBeanTail.getSum()).get());
//
//        document.add(pdf.lftUnderlinedParagraph("                                                                "));
//
//        PdfPTable table2 = new PdfPTable(4);
//        table2.setWidths(new float[]{1, 1, 1, 1});
//        table2.setWidthPercentage(100);
//        table2.setHorizontalAlignment(ALIGN_LEFT);
//        table2.setSpacingBefore(5);
//        table2.addCell(pdf.tblSmallCellWithoutABorder("От страховой медицинской организации:").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("ОЗНАКОМЛЕН").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//
//        table2.addCell(pdf.tblSmallCellWithoutABorder("_______________________________").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("От медицинской организации:").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//
//        table2.addCell(pdf.tblSmallCellWithoutABorder("(должность, подпись, фамилия И.О.)").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("_______________________________").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("(должность, подпись, фамилия и.о.)").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//
//        table2.addCell(pdf.tblSmallCellWithoutABorder("Дата __________ 202___г").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("Дата __________ 202___г").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//
//        table2.addCell(pdf.tblSmallCellWithoutABorder("МП").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("МП").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//
//        table2.addCell(pdf.tblSmallCellWithoutABorder("СОГЛАСОВАНО МГФОМС:").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//
//        table2.addCell(pdf.tblSmallCellWithoutABorder("_______________________________").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//
//        table2.addCell(pdf.tblSmallCellWithoutABorder("(должность, подпись, фамилия И.О.)").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//        table2.addCell(pdf.tblSmallCellWithoutABorder("").get());
//
//        document.add(table2);
//        document.close();
//        if (isWatermark) {
//            return TransparentWatermark.addPreliminaryWatermarkToA4(stream.toByteArray());
//        }
//        return stream.toByteArray();
//    }
//}
