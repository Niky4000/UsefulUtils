/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import ru.ibs.pmp.api.service.export.msk.pdf.PdfReportServiceAbstract;
import ru.ibs.pmp.api.smo.model.Parcel;
import ru.ibs.pmp.service.utils.pdf.PdfHelper;
import ru.ibs.pmp.smo.dao.SmoReportDaoImpl;
import ru.ibs.pmp.smo.dto.pdf.SmpSmoFiReportData;
import ru.ibs.pmp.smo.export.mo.ReportExportContext;
import ru.ibs.pmp.smo.report.model.SmpSmoFiCategoryData;
import ru.ibs.pmp.smo.report.model.SmpSmoFiHeadData;
import ru.ibs.pmp.smo.services.pdf.SmpSmoFiProtocol;
import ru.ibs.testpumputils.utils.FieldUtil;
import static ru.ibs.testpumputils.utils.ObjectUtils.getSmoEntityManagerFactory;

/**
 *
 * @author me
 */
public class SmpSmoFiProtocolTest {

    public static void test() throws Exception {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = getSmoEntityManagerFactory();
        EntityManagerFactory entityManagerFactory_ = entityManagerFactory.getNativeEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory_.createEntityManager();
        try {
            SmoReportDaoImpl smoReportDao = new SmoReportDaoImpl();
            FieldUtil.setField(smoReportDao, entityManager, "entityManager");
            ReportExportContext context = new ReportExportContext(null, null);
            context.setSmoRequestId(0L);
            context.setFlkVersion(0L);
            Parcel parcel = new Parcel();
            context.setMoId(4708L);
            context.setPeriod(new SimpleDateFormat("yyyy-MM-dd").parse("2020-12-01"));
            parcel.setId(77829L);
            context.setParcel(parcel);
//            Bill bill = new Bill();
//            bill.setSmoId(2884L);
//            parcel.setBill(bill);
            SmpSmoFiReportData smpSmoFiReportData = smoReportDao.getProtocolReportData(context, SmpSmoFiReportData.class, SmpSmoFiCategoryData.class, SmpSmoFiHeadData.class, "getSmpSmoFiHeadData", "getSmpSmoFiCategoryData");
            smpSmoFiReportData.getHead().setArchFileName("12345");
            smpSmoFiReportData.getHead().setIdBox("id_8888");
            PdfHelper pdf = new PdfHelper();
            SmpSmoFiProtocol smpSmoFiProtocol = new SmpSmoFiProtocol();
            FieldUtil.setField(smpSmoFiProtocol, PdfReportServiceAbstract.class, pdf, "pdf");
            byte[] createReport = smpSmoFiProtocol.createReport(smpSmoFiReportData, true);
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
}
