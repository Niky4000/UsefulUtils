package ru.ibs.testpumputils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.GZIPInputStream;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import ru.ibs.pmp.api.service.export.msk.pdf.PdfReportServiceAbstract;
import ru.ibs.pmp.api.smo.model.Bill;
import ru.ibs.pmp.api.smo.model.Parcel;
import ru.ibs.pmp.service.utils.pdf.PdfHelper;
import ru.ibs.pmp.smo.dao.SmoReportDaoImpl;
import ru.ibs.pmp.smo.dto.pdf.MgfomsReportData;
import ru.ibs.pmp.smo.dto.pdf.SmoReportData;
import ru.ibs.pmp.smo.export.mo.ReportExportContext;
import ru.ibs.pmp.smo.report.model.MgfomsCathegoryData;
import ru.ibs.pmp.smo.report.model.MgfomsHeadData;
import ru.ibs.pmp.smo.report.model.SmoCathegoryData;
import ru.ibs.pmp.smo.report.model.SmoHeadData;
import ru.ibs.pmp.smo.services.pdf.MgfomsProtocol;
import ru.ibs.pmp.smo.services.pdf.SmoProtocolGenerator;
import ru.ibs.testpumputils.utils.FieldUtil;
import static ru.ibs.testpumputils.utils.ObjectUtils.getSmoEntityManagerFactory;

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
        byte[] createReport = mgfomsProtocol.createReport(mgfomsReportData, true);
//        File reportFile = new File("C:\\tmp\\parcels\\report.pdf");
        File reportFile = new File("/home/me/tmp/reportsPdf/report.pdf");
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

//    public static void test2() throws Exception {
//        PdfHelper pdfHelper = new PdfHelper();
//        NilVmpProtocol nilVmpProtocol = new NilVmpProtocol();
//        FieldUtil.setField(nilVmpProtocol, PdfReportServiceAbstract.class, pdfHelper, "pdf");
//        NilVmpReportData data = new NilVmpReportData();
//        data.setData(IntStream.rangeClosed(1, 200).mapToObj(i->createNilCathegoryData(i)).collect(Collectors.toList()));
//        MgfomsHeadData mgfomsHeadData = new MgfomsHeadData();
//        mgfomsHeadData.setMoName("Какое-то МО");
//        mgfomsHeadData.setPeriodTXT("2020-02-02");
////        data.setMgfomsHeadData(mgfomsHeadData);
//        byte[] createReport = nilVmpProtocol.createReport(data);
//        File reportFile = new File("D:\\tmp\\parcels\\report.pdf");
//        if (reportFile.exists()) {
//            reportFile.delete();
//        }
//        Files.write(reportFile.toPath(), createReport, StandardOpenOption.CREATE_NEW);
//    }
//    private static NilVmpCategoryData createNilCathegoryData(int index) {
//        NilVmpCategoryData cathegoryData = new NilVmpCategoryData();
//        cathegoryData.setCountPatient(10);
//        cathegoryData.setCountRegistryPatient(10);
//        cathegoryData.setCountRegistryServices(10);
//        cathegoryData.setName("Some Name "+index);
//        cathegoryData.setSum(7777L);
//        cathegoryData.setSunMP(8888L);
//        return cathegoryData;
//    }
    public static void test3() throws Exception {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = getSmoEntityManagerFactory();
        EntityManagerFactory entityManagerFactory_ = entityManagerFactory.getNativeEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory_.createEntityManager();
        try {
            SmoReportDaoImpl smoReportDaoImpl = new SmoReportDaoImpl();
            FieldUtil.setField(smoReportDaoImpl, entityManager, "entityManager");
            ReportExportContext context = new ReportExportContext(null, null);
            context.setSmoRequestId(0L);
            context.setFlkVersion(0L);
            Parcel parcel = new Parcel();
            context.setMoId(2186L);
            context.setPeriod(new SimpleDateFormat("yyyy-MM-dd").parse("2020-11-01"));
            parcel.setId(74538L);
            context.setParcel(parcel);
            Bill bill = new Bill();
            bill.setSmoId(2884L);
            parcel.setBill(bill);
            SmoReportData smoReportData = smoReportDaoImpl.getSmoReportData(context);
//            SmoReportData smoReportData = deSerializeObject(new File("/home/me/tmp/reportsPdf/SmoReportData.bin"));
//            smoReportData.setMgfoms(true);
            PdfHelper pdfHelper = new PdfHelper();
            SmoProtocolGenerator protocol = new SmoProtocolGenerator();
            FieldUtil.setField(protocol, PdfReportServiceAbstract.class, pdfHelper, "pdf");
            SmoReportData data = new SmoReportData();
            SmoHeadData head = new SmoHeadData();
            head.setArchFileName("ArchFileName");
            head.setCntPat("cntPat");
            head.setCntRec("cntRec");
            head.setCountFile(5);
            head.setMoId("moId");
            head.setMoName("moName");
            head.setParcelId("parcelId");
            head.setPeriodTXT("periodTXT");
            head.setSmoId("smoId");
            head.setSumRec("sumRec");
            data.setHead(head);
            data.setData(IntStream.rangeClosed(1, 200).mapToObj(i -> createSmoCathegoryData(i)).collect(Collectors.toCollection(LinkedList::new)));
            MgfomsHeadData mgfomsHeadData = new MgfomsHeadData();
            mgfomsHeadData.setMoName("Какое-то МО");
            mgfomsHeadData.setPeriodTXT("2020-02-02");
            byte[] createReport = protocol.createReport(smoReportData, true);
            File reportFile = new File("/home/me/tmp/reportsPdf/report.pdf");
            if (reportFile.exists()) {
                reportFile.delete();
            }
            Files.write(reportFile.toPath(), createReport, StandardOpenOption.CREATE_NEW);
        } finally {
            entityManager.close();
            entityManagerFactory_.close();
        }
    }

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
        cathegoryData.setGroup("GROUP");
        return cathegoryData;
    }

    private static final int BUFFER_SIZE = 1024 * 1024;

    private static ObjectInput getInputStream(File serfile, boolean gzip) throws FileNotFoundException, IOException {
        ObjectInput input;
        if (gzip) {
            try {
                InputStream file = new FileInputStream(serfile);
                InputStream zip = new GZIPInputStream(file, BUFFER_SIZE); // Use zip!
                input = new ObjectInputStream(zip); // Use zip!
            } catch (IOException ioe) {
                InputStream file = new FileInputStream(serfile);
                InputStream buffer = new BufferedInputStream(file, BUFFER_SIZE); // Do not use zip!
                input = new ObjectInputStream(buffer); // Do not use zip!
            }
        } else {
            InputStream file = new FileInputStream(serfile);
            InputStream buffer = new BufferedInputStream(file, BUFFER_SIZE); // Do not use zip!
            input = new ObjectInputStream(buffer); // Do not use zip!
        }
        return input;
    }

    private static <T> T deSerializeObject(File serfile) {
        if (!serfile.exists()) {
            return null;
        }
        try (ObjectInput input = getInputStream(serfile, false)) {
            T obj = (T) input.readObject();
            return obj;
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
