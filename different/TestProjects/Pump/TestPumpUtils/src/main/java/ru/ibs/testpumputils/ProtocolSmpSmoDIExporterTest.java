///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package ru.ibs.testpumputils;
//
//import com.google.common.io.Files;
//import java.io.File;
//import java.text.SimpleDateFormat;
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import org.hibernate.SessionFactory;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import ru.ibs.pmp.api.model.Bill;
//import ru.ibs.pmp.api.service.export.msk.pdf.PdfReportServiceAbstract;
//import ru.ibs.pmp.api.smo.model.Parcel;
//import ru.ibs.pmp.service.utils.pdf.PdfHelper;
//import ru.ibs.pmp.smo.dao.SmoReportDao;
//import ru.ibs.pmp.smo.dao.SmoReportDaoImpl;
//import ru.ibs.pmp.smo.dto.pdf.SmpSmoDIReportData;
//import ru.ibs.pmp.smo.dto.request.ReportExportFileType;
//import ru.ibs.pmp.smo.export.mo.ReportExportContext;
//import ru.ibs.pmp.smo.report.model.SmpSmoDICategoryData;
//import ru.ibs.pmp.smo.report.model.SmpSmoDIHeadData;
//import ru.ibs.pmp.smo.services.pdf.SmpSmoDIProtocol;
//import ru.ibs.testpumputils.utils.FieldUtil;
//import static ru.ibs.testpumputils.utils.ObjectUtils.getSmoEntityManagerFactory;
//
///**
// *
// * @author me
// */
//public class ProtocolSmpSmoDIExporterTest {
//
//    static SessionFactory smoSessionFactory;
//
//    public static void test() throws Exception {
//        smoSessionFactory = TestPumpUtilsMain.buildSmoSessionFactory();
//        LocalContainerEntityManagerFactoryBean entityManagerFactory = getSmoEntityManagerFactory();
//        EntityManagerFactory entityManagerFactory_ = entityManagerFactory.getNativeEntityManagerFactory();
//        EntityManager entityManager = entityManagerFactory_.createEntityManager();
//        try {
//            SmoReportDao smoReportDao = new SmoReportDaoImpl();
//            FieldUtil.setField(smoReportDao, entityManager, "entityManager");
//            SmpSmoDIProtocol smpSmoDIProtocol = new SmpSmoDIProtocol();
//            PdfHelper pdfHelper = new PdfHelper();
//            FieldUtil.setField(smpSmoDIProtocol, PdfReportServiceAbstract.class, pdfHelper, "pdf");
//            Parcel parcel = new Parcel();
//            parcel.setId(967259L);
//            ReportExportContext context = new ReportExportContext(0L, 0L, null, parcel, null, null, 4708L, new SimpleDateFormat("yyyy-MM-dd").parse("2020-03-01"), null, ReportExportFileType.FINAL, Bill.BillFetchType.SMP_ADD);
//            SmpSmoDIReportData smpSmoDIReportData = smoReportDao.getProtocolReportData(context, SmpSmoDIReportData.class, SmpSmoDICategoryData.class, SmpSmoDIHeadData.class, "getSmpSmoDIHeadData", "getSmpSmoDICategoryData");
//            byte[] createReport = smpSmoDIProtocol.createReport(smpSmoDIReportData, true);
//            File file = new File("C:\\tmp\\report.pdf");
//            Files.write(createReport, file);
//        } finally {
//            entityManager.close();
//            entityManagerFactory.destroy();
//            smoSessionFactory.close();
//        }
//    }
//}
