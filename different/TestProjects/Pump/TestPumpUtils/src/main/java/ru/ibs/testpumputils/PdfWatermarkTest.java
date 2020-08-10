package ru.ibs.testpumputils;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import ru.ibs.pmp.api.model.Invoice;
import ru.ibs.pmp.api.service.export.msk.pdf.PdfReportServiceAbstract;
import ru.ibs.pmp.service.utils.pdf.PdfHelper;
import ru.ibs.pmp.smo.dto.pdf.MgfomsReportData;
import ru.ibs.pmp.smo.dto.pdf.NilVmpCategoryData;
import ru.ibs.pmp.smo.dto.pdf.NilVmpReportData;
import ru.ibs.pmp.smo.dto.pdf.SmoReportData;
import ru.ibs.pmp.smo.report.dto.SmoCathegoryData;
import ru.ibs.pmp.smo.report.model.MgfomsCathegoryData;
import ru.ibs.pmp.smo.report.model.MgfomsHeadData;
import ru.ibs.pmp.smo.report.model.SmoCathegoryData;
import ru.ibs.pmp.smo.services.pdf.MgfomsProtocol;
import ru.ibs.pmp.smo.services.pdf.NilVmpProtocol;
import ru.ibs.pmp.smo.services.pdf.SmoProtocolGenerator;
import ru.ibs.testpumputils.utils.FieldUtil;

/**
 * @author NAnishhenko
 */
public class PdfWatermarkTest {

    public static void test() throws Exception {
        PdfHelper pdfHelper = new PdfHelper();
        MgfomsProtocol mgfomsProtocol = new MgfomsProtocol();
        FieldUtil.setField(mgfomsProtocol, PdfReportServiceAbstract.class, pdfHelper, "pdf");
        MgfomsReportData mgfomsReportData = new MgfomsReportData();
        mgfomsReportData.setCathegoryData(createCathegoryData());
        MgfomsHeadData mgfomsHeadData = new MgfomsHeadData();
        mgfomsHeadData.setMoName("Какое-то МО");
        mgfomsHeadData.setPeriodTXT("2020-02-02");
        mgfomsReportData.setMgfomsHeadData(mgfomsHeadData);
        byte[] createReport = mgfomsProtocol.createReport(mgfomsReportData,true);
        File reportFile = new File("D:\\tmp\\parcels\\report.pdf");
        if (reportFile.exists()) {
            reportFile.delete();
        }
        Files.write(reportFile.toPath(), createReport, StandardOpenOption.CREATE_NEW);
    }

    private static MgfomsCathegoryData createCathegoryData() {
        MgfomsCathegoryData cathegoryData = new MgfomsCathegoryData();
        cathegoryData.setAcceptedAll(BigDecimal.TEN);
        cathegoryData.setAcceptedApp(BigDecimal.TEN);
        cathegoryData.setAcceptedStationaryAll(BigDecimal.TEN);
        cathegoryData.setAcceptedStationaryReplacement(BigDecimal.TEN);
        cathegoryData.setAcceptedStationaryVmp(BigDecimal.TEN);
        cathegoryData.setBillCount(BigDecimal.TEN);
        cathegoryData.setMoId(1876L);
        cathegoryData.setParcel_Id(777L);
        cathegoryData.setPatientBillCount(BigDecimal.TEN);
        cathegoryData.setPatientCount(BigDecimal.TEN);
        cathegoryData.setPeriod(new Date());
        cathegoryData.setSumAll(BigDecimal.TEN);
        cathegoryData.setSumLsInput(BigDecimal.TEN);
        cathegoryData.setSumLsSend(BigDecimal.TEN);
        return cathegoryData;
    }

    public static void test2() throws Exception {
        PdfHelper pdfHelper = new PdfHelper();
        NilVmpProtocol nilVmpProtocol = new NilVmpProtocol();
        FieldUtil.setField(nilVmpProtocol, PdfReportServiceAbstract.class, pdfHelper, "pdf");
        NilVmpReportData data = new NilVmpReportData();
        data.setData(IntStream.rangeClosed(1, 200).mapToObj(i->createNilCathegoryData(i)).collect(Collectors.toList()));
        MgfomsHeadData mgfomsHeadData = new MgfomsHeadData();
        mgfomsHeadData.setMoName("Какое-то МО");
        mgfomsHeadData.setPeriodTXT("2020-02-02");
//        data.setMgfomsHeadData(mgfomsHeadData);
        byte[] createReport = nilVmpProtocol.createReport(data);
        File reportFile = new File("D:\\tmp\\parcels\\report.pdf");
        if (reportFile.exists()) {
            reportFile.delete();
        }
        Files.write(reportFile.toPath(), createReport, StandardOpenOption.CREATE_NEW);
    }

    private static NilVmpCategoryData createNilCathegoryData(int index) {
        NilVmpCategoryData cathegoryData = new NilVmpCategoryData();
        cathegoryData.setCountPatient(10);
        cathegoryData.setCountRegistryPatient(10);
        cathegoryData.setCountRegistryServices(10);
        cathegoryData.setName("Some Name "+index);
        cathegoryData.setSum(7777L);
        cathegoryData.setSunMP(8888L);
        return cathegoryData;
    }

//    public static void test3() throws Exception {
//        PdfHelper pdfHelper = new PdfHelper();
//        SmoProtocolGenerator protocol = new SmoProtocolGenerator();
//        FieldUtil.setField(protocol, PdfReportServiceAbstract.class, pdfHelper, "pdf");
//        SmoReportData data = new SmoReportData();
//        data.setCathegoryData(IntStream.rangeClosed(1, 200).mapToObj(i->createSmoCathegoryData(i)).collect(Collectors.toCollection(LinkedList::new)));
//        MgfomsHeadData mgfomsHeadData = new MgfomsHeadData();
//        mgfomsHeadData.setMoName("Какое-то МО");
//        mgfomsHeadData.setPeriodTXT("2020-02-02");
//        byte[] createReport = protocol.createReport(data,true);
//        File reportFile = new File("D:\\tmp\\parcels\\report.pdf");
//        if (reportFile.exists()) {
//            reportFile.delete();
//        }
//        Files.write(reportFile.toPath(), createReport, StandardOpenOption.CREATE_NEW);
//    }

    private static SmoCathegoryData createSmoCathegoryData(int index) {
        SmoCathegoryData cathegoryData = new SmoCathegoryData();
        cathegoryData.setCol1("10");
        cathegoryData.setCol2("10");
        cathegoryData.setCol3("10");
        cathegoryData.setCol4("10");
        cathegoryData.setCol5("10");
        cathegoryData.setCol6("10");
        cathegoryData.setCol7("10");
        cathegoryData.setCol8("10");
        cathegoryData.setCol9("10");
        cathegoryData.setCol10("10");
        cathegoryData.setCol11("10");
        cathegoryData.setCol12("10");
        cathegoryData.setCol13("10");
        cathegoryData.setCol14("10");
        return cathegoryData;
    }
}
