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
import ru.ibs.pmp.smo.report.export.impl.UdFileExporter;
import ru.ibs.pmp.smo.report.export.impl.UpFileExporter;
import ru.ibs.testpumputils.utils.FieldUtil;

/**
 *
 * @author me
 */
public class UpAndUdFileExporterTest {

    static SessionFactory smoSessionFactory;

    public static void test() throws Exception {
        smoSessionFactory = TestPumpUtilsMain.buildSmoSessionFactory();
        try {
            UpFileExporter upFileExporter = new UpFileExporter();
            FieldUtil.setField(upFileExporter, smoSessionFactory, "sessionFactory");
            SmoEntity smoEntity = new SmoEntity();
            smoEntity.setCode("SMO");
            ReportExportContext context = new ReportExportContext(null, smoEntity);
            context.setDirectory("/home/me/tmp");
//            Parcel parcel = (Parcel) Db.select(smoSessionFactory, session -> session.get(Parcel.class, 76645L));
            Parcel parcel = new Parcel();
            parcel.setId(76645L);
            parcel.setMoId(2186L);
            parcel.setPeriod(new SimpleDateFormat("yyyy-MM-dd").parse("2020-11-01"));
            context.setParcel(parcel);
            upFileExporter.exportFile(context);
        } finally {
            smoSessionFactory.close();
        }
    }

    public static void test2() throws Exception {
        smoSessionFactory = TestPumpUtilsMain.buildSmoSessionFactory();
        try {
            UdFileExporter upFileExporter = new UdFileExporter();
            FieldUtil.setField(upFileExporter, smoSessionFactory, "sessionFactory");
            SmoEntity smoEntity = new SmoEntity();
            smoEntity.setCode("SMO");
            ReportExportContext context = new ReportExportContext(null, smoEntity);
            context.setDirectory("/home/me/tmp");
//            Parcel parcel = (Parcel) Db.select(smoSessionFactory, session -> session.get(Parcel.class, 76645L));
            Parcel parcel = new Parcel();
            parcel.setId(76797L);
            parcel.setMoId(1863L);
            parcel.setPeriod(new SimpleDateFormat("yyyy-MM-dd").parse("2020-11-01"));
            context.setParcel(parcel);
            upFileExporter.exportFile(context);
        } finally {
            smoSessionFactory.close();
        }
    }
}
