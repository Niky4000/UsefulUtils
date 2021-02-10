/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import java.text.SimpleDateFormat;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import ru.ibs.pmp.api.smo.model.Parcel;
import ru.ibs.pmp.smo.dao.SmoReportDao;
import ru.ibs.pmp.smo.dao.SmoReportDaoImpl;
import ru.ibs.pmp.smo.dto.pdf.SmpSmoDIReportData;
import ru.ibs.pmp.smo.dto.request.ReportExportFileType;
import ru.ibs.pmp.smo.export.mo.ReportExportContext;
import ru.ibs.pmp.smo.report.export.impl.ProtocolSmpSmoDIExporter;
import ru.ibs.pmp.smo.report.model.SmpSmoDICategoryData;
import ru.ibs.pmp.smo.report.model.SmpSmoDIHeadData;
import ru.ibs.pmp.smo.services.pdf.SmpSmoDIProtocol;
import static ru.ibs.testpumputils.utils.ObjectUtils.getSmoEntityManagerFactory;

/**
 *
 * @author me
 */
public class ProtocolSmpSmoDIExporterTest {

    static SessionFactory smoSessionFactory;

    public static void test() throws Exception {
        smoSessionFactory = TestPumpUtilsMain.buildSmoSessionFactory();
        LocalContainerEntityManagerFactoryBean entityManagerFactory = getSmoEntityManagerFactory();
        EntityManagerFactory entityManagerFactory_ = entityManagerFactory.getNativeEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory_.createEntityManager();
        try {
            ProtocolSmpSmoDIExporter protocolSmpSmoDIExporter = new ProtocolSmpSmoDIExporter();
            SmoReportDao smoReportDao = new SmoReportDaoImpl();
            SmpSmoDIProtocol smpSmoDIProtocol = new SmpSmoDIProtocol();
            Parcel parcel = new Parcel();
            parcel.setId(1053912L);
            ReportExportContext context = new ReportExportContext(0L, 0L, null, parcel, null, null, 4708L, new SimpleDateFormat("yyyy-MM-dd").parse("2020-10-01"), null, ReportExportFileType.FINAL, "SMP_ADD");
            SmpSmoDIReportData smpSmoDIReportData = smoReportDao.getProtocolReportData(context, SmpSmoDIReportData.class, SmpSmoDICategoryData.class, SmpSmoDIHeadData.class, "getSmpSmoDIHeadData", "getSmpSmoDICategoryData");
            smpSmoDIProtocol.createReport(smpSmoDIReportData, true);
        } finally {
            entityManager.close();
            smoSessionFactory.close();
        }
    }
}
