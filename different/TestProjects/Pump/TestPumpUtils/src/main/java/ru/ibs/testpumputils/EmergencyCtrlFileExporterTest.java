/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.hibernate.SessionFactory;
import ru.ibs.pmp.api.smo.model.Parcel;
import ru.ibs.pmp.auth.model.SmoEntity;
import ru.ibs.pmp.lpu.model.mo.Lpu;
import ru.ibs.pmp.smo.export.mo.ReportExportContext;
import ru.ibs.pmp.smo.report.export.impl.EmergencyCtrlFileExporter;
import ru.ibs.pmp.smo.report.export.impl.UpFileExporter;
import static ru.ibs.testpumputils.UpAndUdFileExporterTest.smoSessionFactory;
import ru.ibs.testpumputils.utils.FieldUtil;

/**
 *
 * @author me
 */
public class EmergencyCtrlFileExporterTest {

    static SessionFactory smoSessionFactory;

    public static void test() throws Exception {
        smoSessionFactory = TestPumpUtilsMain.buildSmoSessionFactory();
        try {
            EmergencyCtrlFileExporter emergencyCtrlFileExporter = new EmergencyCtrlFileExporter();
            FieldUtil.setField(emergencyCtrlFileExporter, smoSessionFactory, "sessionFactory");
            SmoEntity smoEntity = new SmoEntity();
            smoEntity.setCode("SMO");
            ReportExportContext context = new ReportExportContext(null, smoEntity);
            context.setDirectory("/home/me/tmp/reportsPdf");
//            Parcel parcel = (Parcel) Db.select(smoSessionFactory, session -> session.get(Parcel.class, 76645L));
            Date period = new SimpleDateFormat("yyyy-MM-dd").parse("2020-12-01");
            Long lpuId = 4708L;
            Parcel parcel = new Parcel();
            parcel.setId(77142L);
            parcel.setFlkVersion(724L);
            parcel.setMoId(lpuId);
            parcel.setPeriod(period);
            context.setParcel(parcel);
            context.setMoId(lpuId);
            Lpu lpu = new Lpu();
            lpu.setMoId(lpuId.toString());
            context.setLpu(lpu);
            context.setPeriod(period);
            boolean allowed = emergencyCtrlFileExporter.allowed(context);
            if (allowed) {
                emergencyCtrlFileExporter.exportFile(context);
            }
        } finally {
            smoSessionFactory.close();
        }
    }
}
