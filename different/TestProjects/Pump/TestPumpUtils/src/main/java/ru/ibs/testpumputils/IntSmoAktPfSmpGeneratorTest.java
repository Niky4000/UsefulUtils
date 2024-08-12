///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package ru.ibs.testpumputils;
//
//import java.io.File;
//import java.lang.reflect.Method;
//import java.nio.file.Files;
//import java.nio.file.StandardOpenOption;
//import java.text.SimpleDateFormat;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//import org.hibernate.SessionFactory;
//import ru.ibs.pmp.api.service.export.msk.pdf.PdfReportServiceAbstract;
//import ru.ibs.pmp.api.smo.model.Parcel;
//import ru.ibs.pmp.auth.model.SmoEntity;
//import ru.ibs.pmp.service.utils.pdf.PdfHelper;
//import ru.ibs.pmp.smo.dto.pdf.ReportData;
//import ru.ibs.pmp.smo.export.mo.ReportExportContext;
//import ru.ibs.pmp.smo.services.pdf.IntSmoAktPfSmpGenerator;
//import ru.ibs.testpumputils.utils.FieldUtil;
//
///**
// *
// * @author me
// */
//public class IntSmoAktPfSmpGeneratorTest {
//
//    public static void test() throws Exception {
//        SessionFactory smoSessionFactory = TestPumpUtilsMain.buildSmoSessionFactory();
//        try {
//            IntSmoAktPfSmpFileExporter intSmoAktPfSmpFileExporter = new IntSmoAktPfSmpFileExporter();
//            FieldUtil.setField(intSmoAktPfSmpFileExporter, smoSessionFactory, "sessionFactory");
//            Map<String, List<Method>> methodMap = Arrays.asList(intSmoAktPfSmpFileExporter.getClass().getDeclaredMethods()).stream().collect(Collectors.groupingBy(Method::getName));
//            Method method = Arrays.asList(intSmoAktPfSmpFileExporter.getClass().getDeclaredMethods()).stream().filter(method_ -> method_.getName().equals("getSmoReportData")).findFirst().get();
////        Method method = intSmoAktPfSmpFileExporter.getClass().getMethod("getSmoReportData", Long.class, Long.class, Date.class, ReportExportContext.class);
//            method.setAccessible(true);
//            SmoEntity smoEntity = new SmoEntity();
//            smoEntity.setCode("SMO");
//            ReportExportContext context = new ReportExportContext(null, smoEntity);
//            context.setDirectory("/home/me/tmp/reportsPdf");
////            Parcel parcel = (Parcel) Db.select(smoSessionFactory, session -> session.get(Parcel.class, 76645L));
//            Parcel parcel = new Parcel();
//            parcel.setId(1053909L);
//            parcel.setMoId(4708L);
//            parcel.setPeriod(new SimpleDateFormat("yyyy-MM-dd").parse("2020-12-01"));
//            context.setParcel(parcel);
//            context.setPeriod(new SimpleDateFormat("yyyy-MM-dd").parse("2020-12-01"));
//            context.setMoId(4708L);
//            ReportData smoReportData = (ReportData) method.invoke(intSmoAktPfSmpFileExporter, parcel.getId(), parcel.getMoId(), parcel.getPeriod(), context);
//            PdfHelper pdf = new PdfHelper();
//            IntSmoAktPfSmpGenerator intSmoAktPfSmpGenerator = new IntSmoAktPfSmpGenerator();
//            FieldUtil.setField(intSmoAktPfSmpGenerator, PdfReportServiceAbstract.class, pdf, "pdf");
//            byte[] createReport = intSmoAktPfSmpGenerator.createReport(smoReportData, true);
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
//    private static ReportData createReportData() {
//        ReportData reportData = new ReportData();
//        Map<String, Object> head = new HashMap<>();
//        head.put("NUM", "2000");
//        head.put("DATE_CRT", "2020-12-12");
//        head.put("PERIOD_STR", "2020-12");
//        head.put("MO_NAME", "Какая-то ЛПУ");
//        head.put("MO_MCOD", "Код ЛПУ");
//        head.put("SMO_NAME", "Имя ЛПУ");
//        head.put("SMO_QQ", "M1");
//        head.put("CNT_S", "8888");
//        head.put("SERVICE_SUM", "57394.82");
//        head.put("DEFECT_CNT", "222.42");
//        head.put("DEFECT_SUM", "8642.12");
//        head.put("UD_SUM", "8812.54");
//        head.put("SERVICE_SUM_ALL", "100000");
//        head.put("DEFECT_PP", "32.84");
//        reportData.setHead(head);
////        reportData.setSmoId(2884L);
//        return reportData;
//    }
//}
