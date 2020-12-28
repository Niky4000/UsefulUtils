/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import java.text.SimpleDateFormat;
import org.hibernate.SessionFactory;
import ru.ibs.pmp.api.smo.model.Parcel;
import ru.ibs.pmp.auth.model.SmoEntity;
import ru.ibs.pmp.smo.export.mo.ReportExportContext;
import ru.ibs.pmp.smo.report.export.impl.IntSmoAktTableFormFileExporter;
import ru.ibs.testpumputils.utils.FieldUtil;

/**
 *
 * @author me
 */
public class IntSmoAktTableFormFileExporterTest {

    static SessionFactory smoSessionFactory;

    public static void test() throws Exception {
        smoSessionFactory = TestPumpUtilsMain.buildSmoSessionFactory();
        try {
            IntSmoAktTableFormFileExporter intSmoAktTableFormFileExporter = new IntSmoAktTableFormFileExporter();
            FieldUtil.setField(intSmoAktTableFormFileExporter, smoSessionFactory, "sessionFactory");
            FieldUtil.setField(intSmoAktTableFormFileExporter, "http://test.drzsrv.ru:8082/module-reports-pmp/api/reportsInternal/buildSync", "pmpReportUrl");
            SmoEntity smoEntity = new SmoEntity();
            smoEntity.setCode("SMO");
            ReportExportContext context = new ReportExportContext(null, smoEntity);
            context.setDirectory("/home/me/tmp/reportsPdf");
//            Parcel parcel = (Parcel) Db.select(smoSessionFactory, session -> session.get(Parcel.class, 76645L));
            Parcel parcel = new Parcel();
            parcel.setId(74538L);
            parcel.setMoId(2186L);
            parcel.setPeriod(new SimpleDateFormat("yyyy-MM-dd").parse("2020-11-01"));
            context.setParcel(parcel);
            context.setPeriod(new SimpleDateFormat("yyyy-MM-dd").parse("2020-11-01"));
            context.setMoId(2186L);
            intSmoAktTableFormFileExporter.exportFile(context);
        } finally {
            smoSessionFactory.close();
        }
    }
}
