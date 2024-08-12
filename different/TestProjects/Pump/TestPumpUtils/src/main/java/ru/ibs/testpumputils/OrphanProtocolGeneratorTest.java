//package ru.ibs.testpumputils;
//
//import java.io.File;
//import java.nio.file.Files;
//import java.nio.file.StandardOpenOption;
//import ru.ibs.pmp.api.service.export.msk.pdf.PdfReportServiceAbstract;
//import ru.ibs.pmp.service.utils.pdf.PdfHelper;
//import ru.ibs.pmp.smo.services.pdf.OrphanProtocolGenerator;
//import ru.ibs.testpumputils.utils.FieldUtil;
//
///**
// *
// * @author me
// */
//public class OrphanProtocolGeneratorTest {
//
//    public static void test() throws Exception {
//        PdfHelper pdf = new PdfHelper();
//        OrphanProtocolGenerator orphanProtocolGenerator = new OrphanProtocolGenerator();
//        FieldUtil.setField(orphanProtocolGenerator, PdfReportServiceAbstract.class, pdf, "pdf");
//        byte[] createReport = orphanProtocolGenerator.createReport(null, false);
//        File reportFile = new File("/home/me/tmp/reportsPdf/report.pdf");
//        if (reportFile.exists()) {
//            reportFile.delete();
//        }
//        Files.write(reportFile.toPath(), createReport, StandardOpenOption.CREATE_NEW);
//    }
//}
