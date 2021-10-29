/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.ibs.pmp.api.model.MedicalCase;
import ru.ibs.pmp.api.model.MedicalCaseAud;
import ru.ibs.pmp.api.model.MedicalCaseAudPK;
import ru.ibs.pmp.api.model.msk.TapInfo;
import ru.ibs.pmp.api.model.msk.TapInfoAud;
import ru.ibs.pmp.api.model.msk.TapInfoAudPK;
import ru.ibs.pmp.api.practitioners.model.practitioner.PractCertificate;
import ru.ibs.pmp.api.practitioners.model.practitioner.PractJob;
import ru.ibs.pmp.api.practitioners.model.practitioner.Practitioner;
import ru.ibs.pmp.api.practitioners.model.practitioner.aud.PractCertificateAud;
import ru.ibs.pmp.api.practitioners.model.practitioner.aud.PractJobAud;
import ru.ibs.pmp.api.practitioners.model.practitioner.aud.PractJobAudPK;
import ru.ibs.pmp.api.practitioners.model.practitioner.aud.PractitionerAud;
import ru.ibs.pmp.api.practitioners.model.practitioner.aud.PractitionerAudPK;
import ru.ibs.pmp.common.ex.PmpFeatureException;
import ru.ibs.pmp.features.bill.utils.CopyEntitiesUtil;
import ru.ibs.pmp.service.check.msk.CheckSpecialistVD;
import ru.ibs.pmp.service.flk.ErrorMarker;
import ru.ibs.pmp.service.flk.ErrorMarkerImpl;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;
import ru.ibs.testpumputils.utils.FieldUtil;

/**
 *
 * @author me
 */
public class TestVD {

    static SessionFactoryInterface sessionFactory;
    static SessionFactoryInterface practSessionFactory;

    public static void test() throws IOException {
        final long medicalCaseId = 261250705393L;
        final long rev = 391234700L;
        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        CopyEntitiesUtil<MedicalCaseAud, MedicalCase> copyEntitiesUtil = new CopyEntitiesUtil<>();
        CopyEntitiesUtil<TapInfoAud, TapInfo> copyEntitiesUtil_ = new CopyEntitiesUtil<>();
        Session session = sessionFactory.openSession();
        MedicalCaseAudPK medicalCaseAudPK = new MedicalCaseAudPK(medicalCaseId, rev);
        MedicalCaseAud medicalCaseAud = (MedicalCaseAud) session.get(MedicalCaseAud.class, medicalCaseAudPK);
        TapInfoAud tapInfoAud = (TapInfoAud) session.get(TapInfoAud.class, new TapInfoAudPK(medicalCaseId, rev));
        TapInfo tapInfo = copyEntitiesUtil_.copyAudEntities(tapInfoAud);
        MedicalCase medicalCase = copyEntitiesUtil.copyAudEntities(medicalCaseAud);
        MedicalCase medicalCase2 = copyEntitiesUtil.copyRelatedEntities(medicalCase, TapInfo.class, tapInfo);
        try {
            CheckSpecialistVD checkSpecialistVD = new CheckSpecialistVD() {
                Long practionerId;

                @Override
                protected PractJob getPractitionerJob(Long jobId) throws PmpFeatureException {
                    PractJobAud practJobAud = (PractJobAud) session.get(PractJobAud.class, new PractJobAudPK(jobId, rev));
                    CopyEntitiesUtil<PractJobAud, PractJob> copyEntitiesUtil = new CopyEntitiesUtil<>();
                    PractJob practJob = copyEntitiesUtil.copyAudEntities(practJobAud, PractJob.class);
                    practionerId = practJob.getPractitionerId();
                    return practJob;
                }

                @Override
                protected Practitioner getPractitioner() throws PmpFeatureException {
                    PractitionerAud practitionerAud = (PractitionerAud) session.get(PractitionerAud.class, new PractitionerAudPK(practionerId, rev));
                    CopyEntitiesUtil<PractitionerAud, Practitioner> copyEntitiesUtil = new CopyEntitiesUtil<>();
                    Practitioner practitioner = copyEntitiesUtil.copyAudEntities(practitionerAud, Practitioner.class);
                    List<PractCertificateAud> practCertificateAudList = session.createCriteria(PractCertificateAud.class).add(Restrictions.and(Restrictions.eq("practsertificatAudPK.rev", rev), Restrictions.eq("practid", practitioner.getId()))).list();
                    final CopyEntitiesUtil<PractCertificateAud, PractCertificate> copyEntitiesUtil2 = new CopyEntitiesUtil<>();
                    Set<PractCertificate> practCertificateSet = practCertificateAudList.stream().map(obj -> copyEntitiesUtil2.copyAudEntities(obj, PractCertificate.class)).collect(Collectors.toSet());
                    practitioner.setCertificates(practCertificateSet);
                    return practitioner;
                }

                @Override
                protected Practitioner getNurse() throws PmpFeatureException {
                    return getPractitioner();
                }

            };
            FieldUtil.setField(checkSpecialistVD, CheckSpecialistVD.class, sessionFactory, "sessionFactory");
            ErrorMarker marker = new ErrorMarkerImpl("VD", new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new HashSet<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
            checkSpecialistVD.execute(medicalCase2, marker);
        } finally {
	    session.close();
            sessionFactory.cleanSessions();
            sessionFactory.close();
        }
    }

    public static void test2() throws IOException {
        final long medicalCaseId = 859769400L;
        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        practSessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildPractSessionFactory(), new SqlRewriteInterceptorExt()));
        Session session = sessionFactory.openSession();
        Session practSession = practSessionFactory.openSession();
        MedicalCase medicalCase = (MedicalCase) session.get(MedicalCase.class, medicalCaseId);
//        TapInfoAud tapInfoAud = (TapInfoAud) session.get(TapInfoAud.class, new TapInfoAudPK(medicalCaseId, rev));
        try {
            CheckSpecialistVD checkSpecialistVD = new CheckSpecialistVD() {
                Long practionerId;

                @Override
                protected PractJob getPractitionerJob(Long jobId) throws PmpFeatureException {
                    PractJob practJob = (PractJob) practSession.get(PractJob.class, jobId);
                    practionerId = practJob.getPractitionerId();
                    return practJob;
                }

                @Override
                protected Practitioner getPractitioner() throws PmpFeatureException {
                    Practitioner practitioner = (Practitioner) practSession.get(Practitioner.class, practionerId);
                    List<PractCertificate> practCertificateList = practSession.createCriteria(PractCertificate.class).add(Restrictions.eq("practitionerId", practitioner.getId())).list();
                    practitioner.setCertificates(new HashSet<>(practCertificateList));
                    return practitioner;
                }

                @Override
                protected Practitioner getNurse() throws PmpFeatureException {
                    return getPractitioner();
                }
            };
            FieldUtil.setField(checkSpecialistVD, CheckSpecialistVD.class, sessionFactory, "sessionFactory");
            ErrorMarker marker = new ErrorMarkerImpl("VD", new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new HashSet<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
            checkSpecialistVD.execute(medicalCase, marker);
        } finally {
	    session.close();
	    practSession.close();
            practSessionFactory.cleanSessions();
            practSessionFactory.close();
            sessionFactory.cleanSessions();
            sessionFactory.close();
        }
    }
}
