//package ru.ibs.testpumputils;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.GregorianCalendar;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.EntityTransaction;
//import javax.xml.datatype.DatatypeConfigurationException;
//import javax.xml.datatype.DatatypeFactory;
//import javax.xml.datatype.XMLGregorianCalendar;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import ru.ibs.pmp.common.ex.ErrorDomain;
//import ru.ibs.pmp.common.ex.ErrorType;
//import ru.ibs.pmp.common.ex.PmpFeatureGroupException;
//import ru.ibs.pmp.lpu.dao.MoDao;
//import ru.ibs.pmp.lpu.dao.impl.MoDaoImpl;
//import ru.ibs.pmp.lpu.messages.ImportCode;
//import ru.ibs.pmp.lpu.model.mo.MoDepartment;
//import ru.ibs.pmp.lpu.service.MoDeptService;
//import ru.ibs.pmp.lpu.service.impl.MoDeptServiceImpl;
//import ru.ibs.pmp.lpu.ws.DeptStatusMessage;
//import ru.ibs.pmp.lpu.ws.SaveMoDeptRequest;
//import ru.ibs.pmp.lpu.ws.WsAuthInfo;
//import ru.ibs.pmp.lpu.ws.impl.LpuWsImpl;
//import ru.ibs.pmp.mo.check.MoDeptCheckImpl;
//import ru.ibs.testpumputils.utils.FieldUtil;
//
///**
// * @author NAnishhenko
// */
//public class MoDepartmentSaveTest {
//
//    public static void test() throws Exception {
//        String authInfoStr = "<authInfo>\n"
//                + "        <orgId>2336</orgId>\n"
//                + "        <system>spu</system>\n"
//                + "        <user>gp214_morozov_fs_spu_in</user>\n"
//                + "        <password></password>\n"
//                + "      </authInfo>";
//        String dataStr = "<request>\n"
//                + "        <bedCount>0</bedCount>\n"
//                + "        <depServiceCondition>00</depServiceCondition>\n"
//                + "        <deptStartDate>2020-02-14+03:00</deptStartDate>\n"
//                + "        <deptVozCode>3</deptVozCode>\n"
//                + "        <id>481338</id>\n"
//                + "        <name>Терапевтическое отделение № 2</name>\n"
//                + "        <profileCode>097</profileCode>\n"
//                + "      </request>";
////        WsAuthInfo wsAuthInfo = XmlUtils.unmarshall(authInfoStr.getBytes(), ru.ibs.pmp.lpu.ws.WsAuthInfo.class);
////        SaveMoDeptRequest saveMoDeptRequest = XmlUtils.unmarshall(dataStr.getBytes(), ru.ibs.pmp.lpu.ws.SaveMoDeptRequest.class);
//        WsAuthInfo wsAuthInfo = new WsAuthInfo();
//        wsAuthInfo.setOrgId(2336L);
//        wsAuthInfo.setSystem("spu");
//        wsAuthInfo.setUser("gp214_morozov_fs_spu_in");
//        wsAuthInfo.setPassword("");
//        SaveMoDeptRequest saveMoDeptRequest = new SaveMoDeptRequest();
//        saveMoDeptRequest.setBedCount(0);
//        saveMoDeptRequest.setDepServiceCondition("00");
//        saveMoDeptRequest.setDeptStartDate(getXmlDate("2020-02-14 03:00"));
//        saveMoDeptRequest.setDeptVozCode("3");
//        saveMoDeptRequest.setId(481338L);
//        saveMoDeptRequest.setName("Терапевтическое отделение № 2");
//        saveMoDeptRequest.setProfileCode("097");
//        LocalContainerEntityManagerFactoryBean entityManagerFactory = getEntityManagerFactory();
//        EntityManagerFactory entityManagerFactory_ = entityManagerFactory.getNativeEntityManagerFactory();
//        EntityManager entityManager = entityManagerFactory_.createEntityManager();
//        LpuWsImpl lpuWsImpl = new LpuWsImpl();
//        MoDeptService moDeptService = new MoDeptServiceImpl();
//        FieldUtil.setField(lpuWsImpl, moDeptService, "moDeptService");
//        MoDao moRegistry = new MoDaoImpl();
//        FieldUtil.setField(moDeptService, moRegistry, "moRegistry");
//        FieldUtil.setField(moRegistry, entityManager, "entityManager");
//        MoDeptCheckImpl check = new MoDeptCheckImpl() {
//            @Override
//            public ImportCode check(MoDepartment dept, Long moKey, boolean isSmp) throws PmpFeatureGroupException {
//                if (!chechVozCodeEqualsMoVozCode(dept, moKey)) {
//                    Object[] args = {dept.getDeptVozCode()};
//                }
//                return ImportCode.ERROR;
//            }
//
//        };
//        FieldUtil.setField(moDeptService, check, "check");
//        FieldUtil.setField(check, MoDeptCheckImpl.class, moRegistry, "moDao");
//        EntityTransaction transaction = entityManager.getTransaction();
//        transaction.begin();
//        try {
//            DeptStatusMessage createUpdateMoDept = lpuWsImpl.createUpdateMoDept(wsAuthInfo, saveMoDeptRequest);
//        } finally {
//            transaction.rollback();
//        }
//    }
//
//    public static LocalContainerEntityManagerFactoryBean getEntityManagerFactory() throws FileNotFoundException, IOException {
////        new org.apache.commons.dbcp2.BasicDataSource
//
//        Properties p = new Properties();
//        p.load(new FileInputStream(new File(System.getProperty("pmp.config.path"))));
//
//        Map<String, String> properties = new HashMap<>();
//        properties.put("javax.persistence.jdbc.driver", p.getProperty("db.driver"));
//        properties.put("javax.persistence.jdbc.url", p.getProperty("runtime.pmp.db.url"));
//        properties.put("javax.persistence.jdbc.user", p.getProperty("runtime.pmp.db.username")); //if needed
//        properties.put("javax.persistence.jdbc.password", p.getProperty("runtime.pmp.db.password"));
//
//        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
////        emf.setPersistenceProviderClass(org.eclipse.persistence.jpa.PersistenceProvider.class); //If your using eclipse or change it to whatever you're using
//        emf.setJpaVendorAdapter(new org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter());
//        emf.setPackagesToScan("ru.ibs.pmp.lpu.model"); //The packages to search for Entities, line required to avoid looking into the persistence.xml
////        emf.setPersistenceUnitName(SysConstants.SysConfigPU);
//        emf.setPersistenceUnitName("SysConfigPU");
//        emf.setJpaPropertyMap(properties);
////        emf.setLoadTimeWeaver(new ReflectiveLoadTimeWeaver()); //required unless you know what your doing
//        emf.afterPropertiesSet();
//        return emf;
//    }
//
//    private static XMLGregorianCalendar getXmlDate(String dateStr) throws DatatypeConfigurationException, ParseException {
//        GregorianCalendar cal = new GregorianCalendar();
//        cal.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateStr));
//        XMLGregorianCalendar newXMLGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
//        return newXMLGregorianCalendar;
//    }
//}
