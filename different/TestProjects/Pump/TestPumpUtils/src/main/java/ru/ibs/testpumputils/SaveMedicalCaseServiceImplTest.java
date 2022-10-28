//package ru.ibs.testpumputils;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.ObjectInputStream;
//import java.lang.reflect.Proxy;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//import ru.ibs.pmp.api.model.MedicalCase;
//import ru.ibs.pmp.api.model.yar.MedDev;
//import ru.ibs.pmp.common.lib.Db;
//import ru.ibs.pmp.service.impl.SaveMedicalCaseServiceImpl;
//import static ru.ibs.testpumputils.TestPumpUtilsMain.buildSessionFactory;
//import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
//import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
//import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;
//
//public class SaveMedicalCaseServiceImplTest {
//
//    public static void test() throws Exception {
//        SaveMedicalCaseServiceImpl saveMedicalCaseServiceImpl = new SaveMedicalCaseServiceImpl();
//        SessionFactoryInterface sessionFactoryProxy = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(buildSessionFactory(), new SqlRewriteInterceptorExt()));
//        try {
////            MedicalCase medicalCase = Db.select(sessionFactoryProxy, session -> {
////                MedicalCase medicalCaseDb = (MedicalCase) session.get(MedicalCase.class, 394966335351L);
////                Hibernate.initialize(medicalCaseDb.getMedCaseOnkCons());
////                Hibernate.initialize(medicalCaseDb.getMedCaseOnkDiags());
////                Hibernate.initialize(medicalCaseDb.getMedCaseOnkProts());
////                Hibernate.initialize(medicalCaseDb.getMedCaseOnkSl());
////                Hibernate.initialize(medicalCaseDb.getMedCaseOnkUsls());
////                Hibernate.initialize(medicalCaseDb.getMedcaseDirections());
////                Hibernate.initialize(medicalCaseDb.getMedDevList());
////                Hibernate.initialize(medicalCaseDb.getSimpleServices());
////                Hibernate.initialize(medicalCaseDb.getInvoices());
////                Hibernate.initialize(medicalCaseDb.getErrors());
////                Hibernate.initialize(medicalCaseDb.getDiagnoses());
////                return medicalCaseDb;
////            });
//
//            MedicalCase medicalCase;
//            try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(new File("C:\\tmp\\16413\\medicalCase394966335351")))) {
//                medicalCase = (MedicalCase) objectInputStream.readObject();
//            }
////            medicalCase.setMedDevList(new ArrayList<>());
//            Db.update(session -> {
////                List<MedDev> medDevList = medicalCase.getMedDevList().stream().map(mc -> (MedDev) session.merge(mc)).collect(Collectors.toList());
////                medicalCase.setMedDevList(medDevList);
//                session.merge(medicalCase);
//            }, sessionFactoryProxy);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            sessionFactoryProxy.cleanSessions();
//            sessionFactoryProxy.close();
//        }
//    }
//    
//    
//}
