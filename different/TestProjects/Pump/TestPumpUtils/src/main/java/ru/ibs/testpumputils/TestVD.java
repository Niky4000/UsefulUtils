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

/**
 *
 * @author me
 */
public class TestVD {

    static SessionFactoryInterface sessionFactory;

    public static void test() throws IOException {
        final long medicalCaseId = 275013580390L;
        final long rev = 404019050L;
        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        CopyEntitiesUtil<MedicalCaseAud, MedicalCase> copyEntitiesUtil = new CopyEntitiesUtil<>();
        Session session = sessionFactory.openSession();
        MedicalCaseAudPK medicalCaseAudPK = new MedicalCaseAudPK(medicalCaseId, rev);
        MedicalCaseAud medicalCaseAud = (MedicalCaseAud) session.get(MedicalCaseAud.class, medicalCaseAudPK);
        MedicalCase medicalCase = copyEntitiesUtil.copyAudEntities(medicalCaseAud);
        try {
            CheckSpecialistVD checkSpecialistVD = new CheckSpecialistVD() {
                Long practionerId;

                @Override
                protected PractJob getPractitionerJob(Long jobId) throws PmpFeatureException {
                    PractJobAud practJobAud = (PractJobAud) session.get(PractJobAud.class, new PractJobAudPK(jobId, jobId));
                    CopyEntitiesUtil<PractJobAud, PractJob> copyEntitiesUtil = new CopyEntitiesUtil<>();
                    PractJob practJob = copyEntitiesUtil.copyAudEntities(practJobAud);
                    practionerId = practJob.getPractitioner().getId();
                    return practJob;
                }

                @Override
                protected Practitioner getPractitioner() throws PmpFeatureException {
                    PractitionerAud practitionerAud = (PractitionerAud) session.get(PractitionerAud.class, new PractitionerAudPK(practionerId, rev));
                    CopyEntitiesUtil<PractitionerAud, Practitioner> copyEntitiesUtil = new CopyEntitiesUtil<>();
                    Practitioner practitioner = copyEntitiesUtil.copyAudEntities(practitionerAud);
                    List<PractCertificateAud> practCertificateAudList = session.createCriteria(PractCertificateAud.class).add(Restrictions.and(Restrictions.eq("rev", rev), Restrictions.eq("practid", practitioner.getId()))).list();
                    final CopyEntitiesUtil<PractCertificateAud, PractCertificate> copyEntitiesUtil2 = new CopyEntitiesUtil<>();
                    Set<PractCertificate> practCertificateSet = practCertificateAudList.stream().map(obj -> copyEntitiesUtil2.copyAudEntities(obj)).collect(Collectors.toSet());
                    practitioner.setCertificates(practCertificateSet);
                    return practitioner;
                }

            };
            ErrorMarker marker = new ErrorMarkerImpl("VD", new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new HashSet<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
            checkSpecialistVD.execute(medicalCase, marker);
        } finally {
            sessionFactory.cleanSessions();
            sessionFactory.close();
        }
    }
}
