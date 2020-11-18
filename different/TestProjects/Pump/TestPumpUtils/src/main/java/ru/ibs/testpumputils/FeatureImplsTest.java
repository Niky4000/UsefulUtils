/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ru.ibs.pmp.api.model.MedicalCase;
import ru.ibs.pmp.api.model.SimpleService;
import ru.ibs.pmp.api.model.SimpleServiceAud;
import ru.ibs.pmp.api.model.flk.CheckError;
import ru.ibs.pmp.api.model.flk.CheckErrorAud;
import ru.ibs.pmp.api.model.flk.CheckHospDeptStayError;
import ru.ibs.pmp.api.model.flk.CheckHospDeptStayErrorAud;
import ru.ibs.pmp.api.model.flk.CheckServiceError;
import ru.ibs.pmp.api.model.flk.CheckServiceErrorAud;
import ru.ibs.pmp.api.model.msk.TapInfo;
import ru.ibs.pmp.api.model.msk.TapInfoAud;
import ru.ibs.pmp.api.model.msk.TapInfoAudPK;
import ru.ibs.pmp.api.model.yar.HospCase;
import ru.ibs.pmp.api.model.yar.HospCaseAud;
import ru.ibs.pmp.api.model.yar.HospCaseAudPK;
import ru.ibs.pmp.api.model.yar.HospDeptStay;
import ru.ibs.pmp.api.model.yar.HospDeptStayAud;
import ru.ibs.pmp.dao.AudDAO;
import ru.ibs.pmp.dao.hibernate.CommonAudDAOHibernate;
import ru.ibs.pmp.features.bill.utils.CopyEntitiesUtil;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;
import ru.ibs.testpumputils.utils.FieldUtil;

/**
 *
 * @author me
 */
public class FeatureImplsTest {

    static SessionFactoryInterface sessionFactory;

    public static void test() throws Exception {
        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        try {
            CommonAudDAOHibernate commonAudDAOHibernate = new CommonAudDAOHibernate();
            FieldUtil.setField(commonAudDAOHibernate, AudDAO.class, sessionFactory, "sessionFactory");

            Long caseId = 884864401L;
            Long revision = 7266300L;
            Long moId = 2186L;
            Date period = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-11-01 00:00:00");

            TapInfoAud tapInfoAud = (TapInfoAud) commonAudDAOHibernate.get(new TapInfoAudPK(caseId, revision), TapInfoAud.class);

            List<SimpleServiceAud> simpleServiceAudList = commonAudDAOHibernate
                    .getList((Map) ImmutableMap.builder()
                            .put("caseId", caseId)
                            .put("revinfo.id", revision)
                            .put("moId", moId)
                            .put("period", period)
                            .build(), SimpleServiceAud.class);
            List<CheckServiceErrorAud> checkServiceErrorAudList = commonAudDAOHibernate
                    .getList((Map) ImmutableMap.builder()
                            .put("serviceId", simpleServiceAudList.stream().map(SimpleServiceAud::getId).collect(Collectors.toList()))
                            .put("revinfo.id", revision)
                            .put("moId", moId)
                            .put("period", period)
                            .build(), CheckServiceErrorAud.class);
            List<CheckErrorAud> сheckErrorAudList = commonAudDAOHibernate
                    .getList((Map) ImmutableMap.builder()
                            .put("pmpCheckErrorAudPK.id", checkServiceErrorAudList.stream().map(CheckServiceErrorAud::getId).collect(Collectors.toList()))
                            .put("revinfo.id", revision)
                            .put("moId", moId)
                            .put("period", period)
                            .build(), CheckErrorAud.class);
            CopyEntitiesUtil<TapInfoAud, TapInfo> copyTapInfoUtil = new CopyEntitiesUtil<TapInfoAud, TapInfo>();
            TapInfo tapInfo = copyTapInfoUtil.copyAudEntities(tapInfoAud);
            convertSimpleServices(simpleServiceAudList, checkServiceErrorAudList, сheckErrorAudList, tapInfo);
            System.out.println("Finished!");
        } finally {
            sessionFactory.cleanSessions();
            sessionFactory.close();
        }
    }

    private static TreeMap<Long, SimpleService> convertSimpleServices(List<SimpleServiceAud> simpleServiceAudList, List<CheckServiceErrorAud> checkServiceErrorAudList, List<CheckErrorAud> сheckErrorAudList, MedicalCase medicalCase) {
        TreeMap<Long, SimpleService> simpleServiceTreeMap = null;
        if (simpleServiceAudList != null && !simpleServiceAudList.isEmpty()) {
            List<SimpleService> simpleServiceList = new ArrayList<SimpleService>(simpleServiceAudList.size());
            CopyEntitiesUtil<SimpleServiceAud, SimpleService> copySimpleServiceUtil = new CopyEntitiesUtil<SimpleServiceAud, SimpleService>() {
                @Override
                protected void setField(Field field, Object newInstance, Object obj) throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
                    if (!field.getType().equals(MedicalCase.class)) {
                        super.setField(field, newInstance, obj);
                    }
                }
            };
            CopyEntitiesUtil<CheckServiceErrorAud, CheckServiceError> copySimpleServiceUtil2 = new CopyEntitiesUtil<CheckServiceErrorAud, CheckServiceError>() {
                @Override
                protected void setField(Field field, Object newInstance, Object obj) throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
                    if (!field.getType().equals(SimpleService.class)) {
                        super.setField(field, newInstance, obj);
                    }
                }
            };
            CopyEntitiesUtil<CheckErrorAud, CheckError> copySimpleServiceUtil3 = new CopyEntitiesUtil<CheckErrorAud, CheckError>();
            Map<Long, List<CheckServiceError>> checkServiceErrorMap = checkServiceErrorAudList.stream().map(obj -> copySimpleServiceUtil2.copyAudEntities(obj)).collect(Collectors.groupingBy(CheckServiceError::getSimpleServiceId));
            Set<Long> idSet = checkServiceErrorAudList.stream().map(CheckServiceErrorAud::getId).collect(Collectors.toSet());
            Map<Long, CheckServiceError> checkErrorMap = сheckErrorAudList.stream().filter(obj -> idSet.contains(obj.getId())).map(obj -> {
                CheckServiceError checkError = (CheckServiceError) copySimpleServiceUtil3.copyAudEntities(obj, CheckServiceError.class);
                return checkError;
            }).collect(Collectors.toMap(CheckError::getId, obj -> obj));
            Map<Long, SimpleService> simpleServiceMap = new HashMap<Long, SimpleService>(simpleServiceAudList.size());
            for (SimpleServiceAud simpleServiceAud : simpleServiceAudList) {
                SimpleService simpleService = copySimpleServiceUtil.copyAudEntities(simpleServiceAud);
                List<CheckServiceError> checkErrorList = checkServiceErrorMap.get(simpleService.getId()).stream().map(obj -> {
                    CheckServiceError checkServiceError = copySimpleServiceUtil2.copyRelatedEntities(checkErrorMap.get(obj.getId()), obj);
                    return checkServiceError;
                }).collect(Collectors.toList());
                simpleService.setErrors(checkErrorList);
                simpleService.setMedicalCase(medicalCase);
                simpleServiceList.add(simpleService);
                simpleServiceMap.put(simpleService.getId(), simpleService);
            }
            medicalCase.setSimpleServices(simpleServiceList);
            simpleServiceTreeMap = new TreeMap<Long, SimpleService>(simpleServiceMap);
        }
        return simpleServiceTreeMap;
    }

    public static void test2() throws Exception {
        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        try {
            CommonAudDAOHibernate commonAudDAOHibernate = new CommonAudDAOHibernate();
            FieldUtil.setField(commonAudDAOHibernate, AudDAO.class, sessionFactory, "sessionFactory");

            Long caseId = 874829401L;
            Long revision = 7253700L;
            Long moId = 4102L;
            Date period = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-10-01 00:00:00");

            HospCaseAud hospCaseAud = (HospCaseAud) commonAudDAOHibernate.get(new HospCaseAudPK(caseId, revision), HospCaseAud.class);
            List<HospDeptStayAud> hospDeptStayAudList = commonAudDAOHibernate
                    .getList((Map) ImmutableMap.builder()
                            .put("medicalCaseId", caseId)
                            .put("revinfo.id", revision)
                            .put("moId", moId)
                            .put("period", period)
                            .build(), HospDeptStayAud.class);
            List<SimpleServiceAud> simpleServiceAudList = commonAudDAOHibernate
                    .getList((Map) ImmutableMap.builder()
                            .put("caseId", caseId)
                            .put("revinfo.id", revision)
                            .put("moId", moId)
                            .put("period", period)
                            .build(), SimpleServiceAud.class);
            List<CheckServiceErrorAud> checkServiceErrorAudList = commonAudDAOHibernate
                    .getList((Map) ImmutableMap.builder()
                            .put("serviceId", simpleServiceAudList.stream().map(SimpleServiceAud::getId).collect(Collectors.toList()))
                            .put("revinfo.id", revision)
                            .put("moId", moId)
                            .put("period", period)
                            .build(), CheckServiceErrorAud.class);
            List<CheckHospDeptStayErrorAud> checkHospDeptStayErrorAudList = commonAudDAOHibernate
                    .getList((Map) ImmutableMap.builder()
                            .put("hospDeptStayId", hospDeptStayAudList.stream().map(HospDeptStayAud::getId).collect(Collectors.toList()))
                            .put("revinfo.id", revision)
                            .put("moId", moId)
                            .put("period", period)
                            .build(), CheckHospDeptStayErrorAud.class);
            List<CheckErrorAud> сheckErrorAudList = commonAudDAOHibernate
                    .getList((Map) ImmutableMap.builder()
                            .put("pmpCheckErrorAudPK.id", Stream.concat(checkServiceErrorAudList.stream().map(CheckServiceErrorAud::getId), checkHospDeptStayErrorAudList.stream().map(CheckHospDeptStayErrorAud::getId)).collect(Collectors.toList()))
                            .put("revinfo.id", revision)
                            .put("moId", moId)
                            .put("period", period)
                            .build(), CheckErrorAud.class);
            CopyEntitiesUtil<HospCaseAud, HospCase> copyTapInfoUtil = new CopyEntitiesUtil<HospCaseAud, HospCase>();
            HospCase hospCase = copyTapInfoUtil.copyAudEntities(hospCaseAud);
            TreeMap<Long, HospDeptStay> hospDeptStayTree = convertHospDeptStay(hospDeptStayAudList, checkHospDeptStayErrorAudList, сheckErrorAudList, hospCase);
            TreeMap<Long, SimpleService> simpleServiceTree = convertSimpleServices(simpleServiceAudList, checkServiceErrorAudList, сheckErrorAudList, hospCase);
            System.out.println("Finished!");
        } finally {
            sessionFactory.cleanSessions();
            sessionFactory.close();
        }
    }

    private static TreeMap<Long, HospDeptStay> convertHospDeptStay(List<HospDeptStayAud> hospDeptStayAudList, List<CheckHospDeptStayErrorAud> checkHospDeptStayErrorAudList, List<CheckErrorAud> сheckErrorAudList, HospCase hospCase) {
        TreeMap<Long, HospDeptStay> hospDeptStayTree = null;
        if (hospDeptStayAudList != null && !hospDeptStayAudList.isEmpty()) {
            List<HospDeptStay> hospDeptStayList = new ArrayList<HospDeptStay>(hospDeptStayAudList.size());
            CopyEntitiesUtil<HospDeptStayAud, HospDeptStay> copyHospDeptStayUtil = new CopyEntitiesUtil<HospDeptStayAud, HospDeptStay>() {
                @Override
                protected void setField(Field field, Object newInstance, Object obj) throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
                    if (!field.getType().equals(HospCase.class)) {
                        super.setField(field, newInstance, obj);
                    }
                }
            };
            CopyEntitiesUtil<CheckHospDeptStayErrorAud, CheckHospDeptStayError> copyHospDeptStayUtil2 = new CopyEntitiesUtil<CheckHospDeptStayErrorAud, CheckHospDeptStayError>() {
                @Override
                protected void setField(Field field, Object newInstance, Object obj) throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
                    if (!field.getType().equals(HospDeptStay.class)) {
                        super.setField(field, newInstance, obj);
                    }
                }
            };
            CopyEntitiesUtil<CheckErrorAud, CheckError> copySimpleServiceUtil3 = new CopyEntitiesUtil<CheckErrorAud, CheckError>();
            Map<Long, List<CheckHospDeptStayError>> checkHospDeptStayErrorAudMap = checkHospDeptStayErrorAudList.stream().map(obj -> copyHospDeptStayUtil2.copyAudEntities(obj)).collect(Collectors.groupingBy(CheckHospDeptStayError::getHospDeptStayId));
            Set<Long> idSet = checkHospDeptStayErrorAudList.stream().map(CheckHospDeptStayErrorAud::getId).collect(Collectors.toSet());
            Map<Long, CheckHospDeptStayError> checkErrorMap = сheckErrorAudList.stream().filter(obj -> idSet.contains(obj.getId())).map(obj -> {
                CheckHospDeptStayError copyAudEntities = (CheckHospDeptStayError) copySimpleServiceUtil3.copyAudEntities(obj, CheckHospDeptStayError.class);
                return copyAudEntities;
            }).collect(Collectors.toMap(CheckError::getId, obj -> obj));
            Map<Long, HospDeptStay> hospDeptStayMap = new HashMap<Long, HospDeptStay>(hospDeptStayAudList.size());
            for (HospDeptStayAud hospDeptStayAud : hospDeptStayAudList) {
                HospDeptStay hospDeptStay = copyHospDeptStayUtil.copyAudEntities(hospDeptStayAud);
                List<CheckHospDeptStayError> checkErrorList = checkHospDeptStayErrorAudMap.get(hospDeptStay.getId()).stream().map(obj -> {
                    CheckHospDeptStayError checkHospDeptStayError = copyHospDeptStayUtil2.copyRelatedEntities(checkErrorMap.get(obj.getId()), obj);
                    return checkHospDeptStayError;
                }).collect(Collectors.toList());
                hospDeptStay.setErrors(checkErrorList);
                hospDeptStay.setHospCase(hospCase);
                hospDeptStayList.add(hospDeptStay);
                hospDeptStayMap.put(hospDeptStay.getId(), hospDeptStay);
            }
            hospCase.setHospDeptStays(hospDeptStayList);
            hospDeptStayTree = new TreeMap<Long, HospDeptStay>(hospDeptStayMap);
        }
        return hospDeptStayTree;
    }
}
