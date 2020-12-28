/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import ru.ibs.pmp.api.nsi.interfaces.FindNsiEntries;
import ru.ibs.pmp.api.smo.model.Parcel;
import ru.ibs.pmp.auth.model.SmoEntity;
import static ru.ibs.pmp.auth.model.SmoEntity.MGFOMS_CODE;
import ru.ibs.pmp.dao.hibernate.AbstractGenericDAO;
import ru.ibs.pmp.dao.hibernate.RequirementDAOHibernate;
import ru.ibs.pmp.dao.hibernate.SyncDAOHibernate;
import ru.ibs.pmp.lpu.dao.LpuDao;
import ru.ibs.pmp.lpu.dao.NsiHelper;
import ru.ibs.pmp.lpu.dao.impl.LpuDaoImpl;
import ru.ibs.pmp.lpu.service.LpuMultifilialService;
import ru.ibs.pmp.lpu.service.impl.LpuMultifilialServiceImpl;
import ru.ibs.pmp.nsi.features.impl.FindNsiEntriesFeature;
import ru.ibs.pmp.nsi.service.NsiService;
import ru.ibs.pmp.nsi.service.NsiServiceImpl;
import ru.ibs.pmp.service.RequirementService;
import ru.ibs.pmp.service.impl.RequirementServiceImpl;
import ru.ibs.pmp.smo.export.mo.ReportExportContext;
import ru.ibs.pmp.smo.report.export.impl.UdFileExporter;
import ru.ibs.pmp.smo.report.export.impl.UpFileExporter;
import ru.ibs.testpumputils.interceptors.SQLInterceptor;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;
import ru.ibs.testpumputils.utils.FieldUtil;
import static ru.ibs.testpumputils.utils.ObjectUtils.buildAuthSessionFactory;

/**
 *
 * @author me
 */
public class UpAndUdFileExporterTest {

    static SessionFactory smoSessionFactory;
    static SessionFactoryInterface sessionFactory;
    static SessionFactory nsiSessionFactoryProxy;

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
        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        nsiSessionFactoryProxy = TestPumpUtilsMain.buildNsiSessionFactory();
        try {
            LpuDao lpuRegistry = new LpuDaoImpl();
            NsiHelper nsiHelper = new NsiHelper();
            FieldUtil.setField(lpuRegistry, nsiHelper, "nsiHelper");
//            FieldUtil.setField(getLpuListFeature, lpuRegistry, "lpuRegistry");
//            FieldUtil.setField(glueServiceImpl, lpuService, "lpuService");
//            FieldUtil.setField(lpuRegistry, moDaoImpl, "moDao");
            RequirementService requirementService = new RequirementServiceImpl();
            RequirementDAOHibernate requirementDAOHibernate = new RequirementDAOHibernate();
            FieldUtil.setField(requirementDAOHibernate, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
            LpuMultifilialService lpuMultifilialService = new LpuMultifilialServiceImpl();
            FieldUtil.setField(requirementDAOHibernate, lpuMultifilialService, "lpuMultifilialService");
            FindNsiEntries findNsiEntries = new FindNsiEntriesFeature();
            FieldUtil.setField(nsiHelper, findNsiEntries, "findNsiEntries");
            FieldUtil.setField(lpuMultifilialService, findNsiEntries, "findNsiEntries");
            SyncDAOHibernate syncDAOHibernate = new SyncDAOHibernate();
            NsiServiceImpl nsiServiceImpl = new NsiServiceImpl();
            FieldUtil.setField(findNsiEntries, nsiServiceImpl, "nsiService");
            ApplicationContext appContext = (ApplicationContext) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{ApplicationContext.class}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (method.getName().equals("getBean")) {
                        Class cl = (Class) args[0];
                        if (cl.equals(NsiService.class)) {
                            return nsiServiceImpl;
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                }
            });
            FieldUtil.setField(nsiServiceImpl, appContext, "appContext");
            FieldUtil.setField(nsiServiceImpl, nsiSessionFactoryProxy, "sessionFactory");
            FieldUtil.setField(syncDAOHibernate, sessionFactory, "sessionFactory");
            FieldUtil.setField(syncDAOHibernate, nsiServiceImpl, "nsiService");

            UdFileExporter upFileExporter = new UdFileExporter();
            FieldUtil.setField(upFileExporter, findNsiEntries, "findNsiEntries");
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
            boolean allowed = upFileExporter.allowed(context);
            if (allowed) {
                upFileExporter.exportFile(context);
            }
        } finally {
            smoSessionFactory.close();
            sessionFactory.cleanSessions();
            sessionFactory.close();
            nsiSessionFactoryProxy.close();
        }
    }
}
