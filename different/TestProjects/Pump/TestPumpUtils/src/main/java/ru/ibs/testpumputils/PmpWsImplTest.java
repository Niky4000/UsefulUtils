package ru.ibs.testpumputils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.google.common.net.HttpHeaders.USER_AGENT;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import org.erzl.services.ErzlPump;
import org.erzl.services.Policies;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;
import ru.ibs.pmp.api.interfaces.GetMedicalCasePojo;
import ru.ibs.pmp.api.nsi.interfaces.FindNsiEntries;
import ru.ibs.pmp.api.nsi.interfaces.FindNsiEntry;
import ru.ibs.pmp.api.patients.interfaces.GetPatientPojo;
import ru.ibs.pmp.auth.model.Glue;
import ru.ibs.pmp.auth.model.UserEntity;
import ru.ibs.pmp.auth.reps.GlueRepository;
import ru.ibs.pmp.auth.reps.UserRepository;
import ru.ibs.pmp.auth.service.LpuService;
import ru.ibs.pmp.auth.service.impl.GlueServiceImpl;
import ru.ibs.pmp.auth.service.impl.LpuServiceImpl;
import ru.ibs.pmp.auth.service.impl.UserServiceImpl;
import ru.ibs.pmp.dao.BillDAO;
import ru.ibs.pmp.dao.GenericDAO;
import ru.ibs.pmp.dao.InvoiceDAO;
import ru.ibs.pmp.dao.SimpleServiceDAO;
import ru.ibs.pmp.dao.hibernate.AbstractGenericDAO;
import ru.ibs.pmp.dao.hibernate.BillDAOHibernate;
import ru.ibs.pmp.dao.hibernate.InvoiceDAOHibernate;
import ru.ibs.pmp.dao.hibernate.MedicalCaseDAOHibernate;
import ru.ibs.pmp.dao.hibernate.RequirementDAOHibernate;
import ru.ibs.pmp.dao.hibernate.SimpleServiceDAOHibernate;
import ru.ibs.pmp.dao.hibernate.SyncDAOHibernate;
import ru.ibs.pmp.features.GetMedicalCasePojoFeature;
import ru.ibs.pmp.lpu.dao.LpuDao;
import ru.ibs.pmp.lpu.dao.impl.LpuDaoImpl;
import ru.ibs.pmp.lpu.dao.impl.MoDaoImpl;
import ru.ibs.pmp.lpu.features.GetLpuListFeature;
import ru.ibs.pmp.lpu.features.impls.GetLpuListFeatureImpl;
import ru.ibs.pmp.lpu.model.mo.Mo;
import ru.ibs.pmp.lpu.service.LpuMultifilialService;
import ru.ibs.pmp.lpu.service.impl.LpuMultifilialServiceImpl;
import ru.ibs.pmp.nsi.features.impl.FindNsiEntriesFeature;
import ru.ibs.pmp.nsi.features.impl.FindNsiEntryFeature;
import ru.ibs.pmp.nsi.service.MoService;
import ru.ibs.pmp.nsi.service.MoServiceImpl;
import ru.ibs.pmp.nsi.service.NsiService;
import ru.ibs.pmp.nsi.service.NsiServiceImpl;
import ru.ibs.pmp.persons.features.patients.GetPatientPojoFeature;
import ru.ibs.pmp.persons.interfaces.PersonDaoReferencesManager;
import ru.ibs.pmp.persons.ws.ERZLPersonCall;
import ru.ibs.pmp.persons.ws.ERZLPersonCallImpl;
import ru.ibs.pmp.persons.ws.ErzlWsGatewayImpl;
import ru.ibs.pmp.persons.ws.PersonDAOImplWS;
import ru.ibs.pmp.persons.ws.PersonDaoReferencesManagerImpl;
import ru.ibs.pmp.pmp.ws.WsAuthInfo;
import ru.ibs.pmp.pmp.ws.CreateUpdateAmbCaseRequestBean190601;
import ru.ibs.pmp.pmp.ws.impl.PmpWsImpl;
import ru.ibs.pmp.service.RequirementService;
import ru.ibs.pmp.service.impl.MedicalCaseServiceImpl;
import ru.ibs.pmp.api.mailgw.service.impl.ModulePmpAbstractGenericService;
import ru.ibs.pmp.service.impl.RequirementServiceImpl;
import ru.ibs.pmp.service.impl.SimpleServiceServiceImpl;
import ru.ibs.pmp.service.impl.msk.BillServiceImpl;
import ru.ibs.pmp.validators.MedicalCaseValidator;
import ru.ibs.pmp.zlib.service.BillService;
import org.springframework.cache.CacheManager;
import ru.ibs.pmp.api.interfaces.SaveMedicalCasePojo;
import ru.ibs.pmp.api.model.Bill;
import ru.ibs.pmp.api.model.MedicalCase;
import ru.ibs.pmp.api.model.Requirement;
import ru.ibs.pmp.api.model.SimpleService;
import ru.ibs.pmp.api.model.msk.gateway.BillInfo;
import ru.ibs.pmp.api.patients.interfaces.GetPatientFromCrossTablePojo;
import ru.ibs.pmp.api.practitioners.features.impl.GetMOPractByBasicInfoFeatureImpl;
import ru.ibs.pmp.api.practitioners.features.impl.GetPractitionersByJobIdsImpl;
import ru.ibs.pmp.api.practitioners.interfaces.FindPractitioner;
import ru.ibs.pmp.api.practitioners.interfaces.GetMOPractByBasicInfoFeauture;
import ru.ibs.pmp.api.practitioners.interfaces.GetPractitionersByJobIds;
import ru.ibs.pmp.common.dao.payer.PayerDAO;
import ru.ibs.pmp.common.ex.ExceptionFactory;
import ru.ibs.pmp.common.ex.PmpFeatureGroupException;
//import ru.ibs.pmp.dao.AccountingPeriodDao;
import ru.ibs.pmp.dao.AudDAO;
import ru.ibs.pmp.dao.CostDAO;
import ru.ibs.pmp.dao.HospDeptStayDAO;
import ru.ibs.pmp.dao.HospMedicamentDAO;
import ru.ibs.pmp.dao.HospReanimationDAO;
import ru.ibs.pmp.dao.HospRehabilitationDAO;
import ru.ibs.pmp.dao.HospServiceDAO;
import ru.ibs.pmp.dao.MedCaseOnkConsDAO;
import ru.ibs.pmp.dao.MedCaseOnkDiagDAO;
import ru.ibs.pmp.dao.MedCaseOnkProtDAO;
import ru.ibs.pmp.dao.PayerDAOImpl;
import ru.ibs.pmp.dao.RequirementDAO;
import ru.ibs.pmp.dao.RevinfoDAO;
import ru.ibs.pmp.dao.ServiceMedicamentDAO;
import ru.ibs.pmp.dao.ServiceMedicamentDAOHibernate;
//import ru.ibs.pmp.dao.hibernate.AccountingPeriodDaoHibernate;
import ru.ibs.pmp.dao.hibernate.CommonAudDAOHibernate;
import ru.ibs.pmp.dao.hibernate.CostDAOHibernate;
import ru.ibs.pmp.dao.hibernate.HospDeptStayDAOHibernate;
import ru.ibs.pmp.dao.hibernate.HospMedicamentDAOHibernate;
import ru.ibs.pmp.dao.hibernate.HospReanimationDAOHibernate;
import ru.ibs.pmp.dao.hibernate.HospRehabilitationDAOHibernate;
import ru.ibs.pmp.dao.hibernate.HospServiceDAOHibernate;
import ru.ibs.pmp.dao.hibernate.MedCaseOnkConsDAOHibernate;
import ru.ibs.pmp.dao.hibernate.MedCaseOnkDiagHibernate;
import ru.ibs.pmp.dao.hibernate.MedCaseOnkProtHibernate;
import ru.ibs.pmp.dao.hibernate.MedcaseDirectionDAOHibernate;
import ru.ibs.pmp.dao.hibernate.RevinfoDAOHibernate;
import ru.ibs.pmp.features.IsLockOnMoAtPeriodExistsFeature;
import ru.ibs.pmp.features.SaveMedicalCasePojoFeature;
import ru.ibs.pmp.features.SaveTapInfoPojo;
import ru.ibs.pmp.features.SaveTapInfoPojoFeature;
import ru.ibs.pmp.features.impl.IsLockOnMoAtPeriodExistsFeatureImpl;
import ru.ibs.pmp.lpu.dao.NsiHelper;
import ru.ibs.pmp.lpu.features.GetLpuFeature;
import ru.ibs.pmp.lpu.features.GetLpuListByIdListFeature;
import ru.ibs.pmp.lpu.features.impls.GetLpuFeatureImpl;
import ru.ibs.pmp.lpu.features.impls.GetLpuListByIdListFeatureImpl;
import ru.ibs.pmp.nsi.features.NsiValidationDataValidateFeature;
import ru.ibs.pmp.nsi.features.impl.NsiValidationDataValidateFeatureImpl;
import ru.ibs.pmp.nsi.service.NsiValidationDataServiceImpl;
import ru.ibs.pmp.persons.interfaces.ERZLPersonDAO;
import ru.ibs.pmp.persons.ws.ERZLGuideDAOImpl;
import ru.ibs.pmp.persons.ws.ERZLPersonDAOImplWS;
import ru.ibs.pmp.practitioners.dao.PractitionerDao;
import ru.ibs.pmp.practitioners.dao.PractitionerDaoImpl;
//import ru.ibs.pmp.service.AccountingPeriodService;
import ru.ibs.pmp.service.HospCaseService;
import ru.ibs.pmp.service.SaveMedicalCaseService;
import ru.ibs.pmp.service.flk.FLKChecks;
import ru.ibs.pmp.service.flk.FLKChecksImpl;
import ru.ibs.pmp.service.flk.check.FLKCheck;
import ru.ibs.pmp.service.flk.state.FLKStateTracker;
import ru.ibs.pmp.service.flk.state.FLKStateTrackerImpl;
import ru.ibs.pmp.service.gateway.CreateUpdateServiceImpl;
//import ru.ibs.pmp.service.impl.AccountingPeriodServiceImpl;
import ru.ibs.pmp.service.impl.HospCaseServiceImpl;
import ru.ibs.pmp.service.impl.SaveMedicalCaseServiceImpl;
import ru.ibs.pmp.smo.dto.BillStatusDto;
import ru.ibs.pmp.zlib.service.export.msk.parcel.util.ServiceHelper;
import static ru.ibs.testpumputils.TestPumpUtilsMain.buildAuthSessionFactory;
import ru.ibs.testpumputils.interceptors.SQLInterceptor;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;
import ru.ibs.testpumputils.utils.FieldUtil;
import static ru.ibs.testpumputils.utils.ObjectUtils.getEntityManagerFactory;
import ru.ibs.testpumputils.utils.XmlUtils;
import sun.net.www.protocol.http.HttpURLConnection;

/**
 * @author NAnishhenko
 */
public class PmpWsImplTest {

    private static BillDAOHibernate billDAOHibernate;

    public static void test() throws Exception {

//        WsAuthInfo authInfo = unmarshall(authInfoXml.getBytes(), WsAuthInfo.class);
        CreateUpdateAmbCaseRequestBean190601 createUpdateAmbCaseRequest = unmarshall(createUpdateAmbCaseRequestXml.getBytes(), CreateUpdateAmbCaseRequestBean190601.class);
        WsAuthInfo authInfo = (WsAuthInfo) parseJSONcommits(authInfoStr.getBytes(), new TypeReference<WsAuthInfo>() {
        });
//        CreateUpdateAmbCaseRequestBean190601 createUpdateAmbCaseRequest = (CreateUpdateAmbCaseRequestBean190601) parseJSONcommits(createUpdateAmbCaseRequestStr.getBytes(), new TypeReference<CreateUpdateAmbCaseRequestBean190601>() {
//        });
        String authInfoStr = XmlUtils.jaxbObjectToXML(authInfo, "wsAuthInfo");
        String createUpdateAmbCaseRequestStr = XmlUtils.jaxbObjectToXML(createUpdateAmbCaseRequest, "createUpdateAmbCaseRequest");
        PmpWsImpl pmpWsImpl = init();
//        Result createUpdateAmbCaseResponse = pmpWsImpl.createUpdateAmbCase(authInfo, createUpdateAmbCaseRequest);
//        String createUpdateAmbCaseResponseStr = XmlUtils.jaxbObjectToXML(createUpdateAmbCaseResponse, "createUpdateAmbCaseResponse");
//        System.out.println(createUpdateAmbCaseResponseStr);
//pmpWsImpl.listBills170801(authInfo, listBills170801Request);

        testBillDAOHibernate();
//        testBill();

        sessionFactory.cleanSessions();
        sessionFactory.close();
        authSessionFactoryProxy.cleanSessions();
        authSessionFactoryProxy.close();
//        nsiSessionFactoryProxy.cleanSessions();
        nsiSessionFactoryProxy.close();
    }

    private static void testBillDAOHibernate() {
        List<BillStatusDto> billStatusesForSmo = billDAOHibernate.getBillStatusesForSmo("2020-08", "22", "1868", Bill.BillStatus.SENT, Bill.BillFetchType.SMO);
//        List<BillStatus2Dto> billStatuses2ForSmo = billDAOHibernate.getBillStatuses2ForSmo("2020-08", "22", "1868", "SENT", Bill.BillFetchType.SMO);
        System.out.println(billStatusesForSmo.size());
    }

    private static void testBill() {
	Session session = sessionFactory.openSession();
	try {
	    Requirement requirement = (Requirement) session.get(Requirement.class, 100023483L);
//        for (Bill bill : requirement.getBills()) {
//            BillInfo billInfo = billDAOHibernate.getBillInfo(bill, 0L);
//        }
	    List<BillInfo> billInfoList = requirement.getBills().stream().map(bill -> billDAOHibernate.getBillInfo(bill, 0L)).collect(Collectors.toList());
//        String billInfoListStr = billInfoList.stream().map(billInfo -> billInfo.getBillId() + " " + (billInfo.getSentAISOMSDate() != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(billInfo.getSentAISOMSDate()) : "null") + "\r\n").sorted().reduce((str1, str2) -> str1 + str2).get();
	    String billInfoListStr = billInfoList.stream().map(billInfo -> "select " + billInfo.getBillId() + " as bill_id, '" + (billInfo.getSentAISOMSDate() != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(billInfo.getSentAISOMSDate()) : "null") + "' as send_date from dual\r\nunion all\r\n").sorted().reduce((str1, str2) -> str1 + str2).get();
	    System.out.println(billInfoListStr);
	} finally {
	    session.close();
	}
    }

    private static Object parseJSONcommits(byte[] string, TypeReference ref) throws Exception {
//        byte[] bytes = new String(string, "cp1251").getBytes("utf-8");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Object readValue = objectMapper.readValue(string, ref);
            return readValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static SessionFactoryInterface sessionFactory;
    static SessionFactoryInterface authSessionFactoryProxy;
    static SessionFactory nsiSessionFactoryProxy;

    private static PmpWsImpl init() {
        PmpWsImpl pmpWsImpl = new PmpWsImpl();
        try {
            Properties p = new Properties();
            p.load(new FileInputStream(new File(System.getProperty("pmp.config.path"))));
            erzlUrlStr = p.getProperty("runtime.erzl.ws.url");
            urlStr = p.getProperty("runtime.erzl2.ws.url");
            sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
            authSessionFactoryProxy = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(buildAuthSessionFactory(), new SQLInterceptor(sql -> sql.replaceAll("PMP_PROD", "PMP_AUTH"))));
            nsiSessionFactoryProxy = TestPumpUtilsMain.buildNsiSessionFactory();
            final Session currentSession = authSessionFactoryProxy.getCurrentSession();
//            SQLInterceptor interceptor = new SQLInterceptor();
//            FieldUtil.setField(session, interceptor, "interceptor");
            LocalContainerEntityManagerFactoryBean entityManagerFactory = getEntityManagerFactory();
            EntityManagerFactory entityManagerFactory_ = entityManagerFactory.getNativeEntityManagerFactory();
            EntityManager entityManager = entityManagerFactory_.createEntityManager();
            Mo find = entityManager.find(Mo.class, 4774L);
            MoDaoImpl moDaoImpl = new MoDaoImpl();
            UserServiceImpl userServiceImpl = new UserServiceImpl();
            UserRepository userRepository = (UserRepository) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{UserRepository.class}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (method.getName().equals("findByUniqueid")) {
                        String uniqueid = (String) args[0];
//                        List<UserEntity> userEntityList = session.createSQLQuery("select * from PMP_AUTH.SYS_USERS where uniqueid=:uniqueid").addEntity(UserEntity.class).setParameter("uniqueid", uniqueid).list();
                        List<UserEntity> userEntityList = currentSession.createSQLQuery("select * from SYS_USERS where uniqueid=:uniqueid").addEntity(UserEntity.class).setParameter("uniqueid", uniqueid).list();
//                        List<UserEntity> userEntityList = session.createCriteria(UserEntity.class).add(Restrictions.eq("uniqueid", uniqueid)).list();
                        UserEntity user = userEntityList.get(0);
                        return user;
                    } else {
                        return null;
                    }
                }
            });
            GlueServiceImpl glueServiceImpl = new GlueServiceImpl();
            LpuService lpuService = new LpuServiceImpl();
            GetLpuListFeature getLpuListFeature = new GetLpuListFeatureImpl();
            FieldUtil.setField(lpuService, getLpuListFeature, "getLpuListFeature");
            LpuDao lpuRegistry = new LpuDaoImpl();
            NsiHelper nsiHelper = new NsiHelper();
            FieldUtil.setField(lpuRegistry, nsiHelper, "nsiHelper");
            FieldUtil.setField(getLpuListFeature, lpuRegistry, "lpuRegistry");
            FieldUtil.setField(glueServiceImpl, lpuService, "lpuService");
            FieldUtil.setField(lpuRegistry, moDaoImpl, "moDao");

            GlueRepository glueRepository = (GlueRepository) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{GlueRepository.class}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (method.getName().equals("findGlues")) {
                        UserEntity user = (UserEntity) args[0];
                        List<Glue> glueList = currentSession.createQuery("select distinct glue from Glue glue join glue.user u where u = :user").setParameter("user", user).list();
                        return glueList;
                    } else {
                        return null;
                    }
                }
            });
            TransactionOperations txTemplate = (TransactionOperations) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{TransactionOperations.class}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (method.getName().equals("execute")) {
                        TransactionCallback transactionCallback = (TransactionCallback) args[0];
                        return transactionCallback.doInTransaction(null);
                    } else {
                        return null;
                    }
                }
            });
            MedicalCaseDAOHibernate medicalCaseDAOHibernate = new MedicalCaseDAOHibernate() {
                boolean updateCalled = false;
                Session currentSession;

                @Override
                public void update(MedicalCase medicalCase) {
                    updateCalled = true;
                    getCurrentSession();

                    Transaction tx = null;
                    try {
                        tx = currentSession.getTransaction();
                        tx.begin();

                        super.update(medicalCase);

                        currentSession.flush();
                        tx.commit();
                    } catch (Exception ex2) {
                        if (tx != null) {
                            tx.rollback();
                        }
                    } finally {
                        if (currentSession != null) {
//                            currentSession.close();
                        }
                    }
                    updateCalled = false;
                }

                @Override
                protected Session getCurrentSession() {
                    if (currentSession == null && updateCalled) {
                        currentSession = super.getCurrentSession();
                        return currentSession;
                    } else if (updateCalled) {
                        return currentSession;
                    } else {
                        return super.getCurrentSession();
                    }
                }
            };
            SimpleServiceDAO simpleServiceDAO = new SimpleServiceDAOHibernate();
            FieldUtil.setField(simpleServiceDAO, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
            FieldUtil.setField(medicalCaseDAOHibernate, MedicalCaseDAOHibernate.class, simpleServiceDAO, "simpleServiceDAO");
            CostDAO costDAO = new CostDAOHibernate();
            FieldUtil.setField(costDAO, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
            FieldUtil.setField(medicalCaseDAOHibernate, MedicalCaseDAOHibernate.class, costDAO, "costDAO");
//            AccountingPeriodDaoHibernate accountingPeriodDaoHibernate = new AccountingPeriodDaoHibernate();
            RequirementDAO requirementDAO = new RequirementDAOHibernate();
            FieldUtil.setField(requirementDAO, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
//            FieldUtil.setField(accountingPeriodDaoHibernate, requirementDAO, "requirementDAO");
//            FieldUtil.setField(accountingPeriodDaoHibernate, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
//            AccountingPeriodService accountingPeriodService = new AccountingPeriodServiceImpl() {
//                @Override
//                protected AccountingPeriodDao getDAO() {
//                    return accountingPeriodDaoHibernate;
//                }
//            };
//            FieldUtil.setField(medicalCaseDAOHibernate, MedicalCaseDAOHibernate.class, accountingPeriodService, "accountingPeriodService");
            billDAOHibernate = new BillDAOHibernate();
            FieldUtil.setField(billDAOHibernate, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
            BillService billService = new BillServiceImpl() {
                @Override
                protected BillDAO getDAO() {
                    return billDAOHibernate;
                }
            };

            FieldUtil.setField(medicalCaseDAOHibernate, MedicalCaseDAOHibernate.class, billService, "billService");
            RequirementService requirementService = new RequirementServiceImpl();
            RequirementDAOHibernate requirementDAOHibernate = new RequirementDAOHibernate();
            FieldUtil.setField(requirementDAOHibernate, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
            LpuMultifilialService lpuMultifilialService = new LpuMultifilialServiceImpl();
            FieldUtil.setField(requirementDAOHibernate, lpuMultifilialService, "lpuMultifilialService");
            FindNsiEntries findNsiEntries = new FindNsiEntriesFeature();
            FieldUtil.setField(nsiHelper, findNsiEntries, "findNsiEntries");
            FieldUtil.setField(lpuMultifilialService, findNsiEntries, "findNsiEntries");
            FieldUtil.setField(requirementService, ModulePmpAbstractGenericService.class, requirementDAOHibernate, "dao");
            FieldUtil.setField(medicalCaseDAOHibernate, MedicalCaseDAOHibernate.class, requirementService, "requirementService");
            InvoiceDAO invoiceDAOHibernate = new InvoiceDAOHibernate();
            FieldUtil.setField(invoiceDAOHibernate, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
            FieldUtil.setField(medicalCaseDAOHibernate, MedicalCaseDAOHibernate.class, invoiceDAOHibernate, "invoiceDAOHibernate");
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
            FieldUtil.setField(medicalCaseDAOHibernate, MedicalCaseDAOHibernate.class, syncDAOHibernate, "syncDAO");
            FieldUtil.setField(medicalCaseDAOHibernate, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
            GetMedicalCasePojo getMedicalCasePojo = new GetMedicalCasePojoFeature();
            MedicalCaseServiceImpl medicalCaseService = new MedicalCaseServiceImpl() {
                @Override
                protected GenericDAO getDAO() {
                    return medicalCaseDAOHibernate;
                }
            };
            FieldUtil.setField(medicalCaseService, ModulePmpAbstractGenericService.class, medicalCaseDAOHibernate, "dao");
            FieldUtil.setField(getMedicalCasePojo, medicalCaseService, "medicalCaseService");
            FieldUtil.setField(getMedicalCasePojo, txTemplate, "txTemplate");
            FieldUtil.setField(pmpWsImpl, getMedicalCasePojo, "getMedicalCasePojo");
            FieldUtil.setField(glueServiceImpl, glueRepository, "glueRepository");
            FieldUtil.setField(pmpWsImpl, glueServiceImpl, "glueService");
            FieldUtil.setField(userServiceImpl, userRepository, "userRepository");
            FieldUtil.setField(pmpWsImpl, userServiceImpl, "userService");
            FieldUtil.setField(moDaoImpl, entityManager, "entityManager");
            MoService moService = new MoServiceImpl();
            FieldUtil.setField(pmpWsImpl, moService, "moService");
//            FieldUtil.setField(pmpWsImpl, moDaoImpl, "moDao");
            MedicalCaseValidator medicalCaseValidator = new MedicalCaseValidator();
            FindNsiEntry findNsiEntryFeature = new FindNsiEntryFeature();
            PayerDAO payerDAO = new PayerDAOImpl();
            FieldUtil.setField(payerDAO, findNsiEntryFeature, "findNsiEntry");
            FieldUtil.setField(billDAOHibernate, payerDAO, "payerDAO");
            FieldUtil.setField(medicalCaseValidator, findNsiEntryFeature, "findNsiEntryFeature");
            GetMOPractByBasicInfoFeauture getMOPractByBasicInfoFeature = new GetMOPractByBasicInfoFeatureImpl();
            FieldUtil.setField(getMOPractByBasicInfoFeature, findNsiEntryFeature, "findNsiEntry");
//            FieldUtil.setField(medicalCaseValidator, getMOPractByBasicInfoFeature, "getMOPractByBasicInfoFeature");
            PractitionerDao practitionerDao = new PractitionerDaoImpl();
            FieldUtil.setField(getMOPractByBasicInfoFeature, practitionerDao, "practitionerDao");
            FieldUtil.setField(practitionerDao, entityManager, "entityManager");
            GetPatientPojo getPatientPojo = new GetPatientPojoFeature();
            FieldUtil.setField(medicalCaseValidator, getPatientPojo, "getPatientPojo");
            PersonDAOImplWS personDAOImplWS = new PersonDAOImplWS();
            GetLpuListByIdListFeature getLpuListByIdListFeature = new GetLpuListByIdListFeatureImpl();
            FieldUtil.setField(getLpuListByIdListFeature, lpuRegistry, "lpuRegistry");
            FieldUtil.setField(personDAOImplWS, getLpuListByIdListFeature, "getLpuListByIdListFeature");
            ERZLPersonCall erzlPersonCall = new ERZLPersonCallImpl();
            ErzlPump erzlPump = getErzlPump();
            PersonDaoReferencesManager personDaoReferencesManager = new PersonDaoReferencesManagerImpl();
            ERZLGuideDAOImpl guideDAO = new ERZLGuideDAOImpl();
            FieldUtil.setField(personDaoReferencesManager, guideDAO, "guideDAO");
            FieldUtil.setField(personDaoReferencesManager, findNsiEntries, "findNsiEntries");
            ERZLPersonDAO wsDao = new ERZLPersonDAOImplWS();
            FieldUtil.setField(personDaoReferencesManager, wsDao, "wsDao");
            ErzlWsGatewayImpl erzlWsGatewayImpl = getErzlWsGatewayImpl();
            FieldUtil.setField(guideDAO, erzlWsGatewayImpl, "erzlWsGateway");
            FieldUtil.setField(wsDao, erzlWsGatewayImpl, "erzlWsGateway");
            Method declaredMethod = wsDao.getClass().getDeclaredMethod("init", null);
            declaredMethod.setAccessible(true);
            declaredMethod.invoke(wsDao, null);
            CacheManager cacheManager = getCacheManager();
            FieldUtil.setField(lpuRegistry, cacheManager, "lpuCacheManager");
            FieldUtil.setField(personDaoReferencesManager, cacheManager, "cacheManager");
            FieldUtil.setField(personDAOImplWS, personDaoReferencesManager, "personDaoReferencesManager");
            FieldUtil.setField(personDAOImplWS, erzlPersonCall, "erzlPersonCall");
            FieldUtil.setField(personDAOImplWS, erzlPump, "erzlPump");
            FieldUtil.setField(getPatientPojo, personDAOImplWS, "dao");
            FieldUtil.setField(findNsiEntryFeature, nsiServiceImpl, "nsiService");
            FieldUtil.setField(medicalCaseValidator, findNsiEntryFeature, "findNsiEntryFeature");
            SimpleServiceDAOHibernate simpleServiceDAOHibernate = new SimpleServiceDAOHibernate() {
                boolean updateCalled = false;
                Session currentSession;

                @Override
                public void remove(SimpleService entity) {

                    updateCalled = true;
                    getCurrentSession();

                    Transaction tx = null;
                    try {
                        tx = currentSession.getTransaction();
                        tx.begin();

                        super.remove(entity);

                        currentSession.flush();
                        tx.commit();
                    } catch (Exception ex2) {
                        if (tx != null) {
                            tx.rollback();
                        }
                    } finally {
                        if (currentSession != null) {
//                            currentSession.close();
                        }
                    }
                    updateCalled = false;
                }

                @Override
                protected Session getCurrentSession() {
                    if (currentSession == null && updateCalled) {
                        currentSession = super.getCurrentSession();
                        return currentSession;
                    } else if (updateCalled) {
                        return currentSession;
                    } else {
                        return super.getCurrentSession();
                    }
                }
            };
            SimpleServiceServiceImpl simpleServiceServiceImpl = new SimpleServiceServiceImpl() {
                @Override
                protected SimpleServiceDAO getDAO() {
                    return simpleServiceDAOHibernate;
                }
            };
            FieldUtil.setField(medicalCaseValidator, simpleServiceServiceImpl, "simpleServiceService");
//            FieldUtil.setField(medicalCaseValidator, moDaoImpl, "moDao");
            FieldUtil.setField(moService, nsiServiceImpl, "nsiService");
            FieldUtil.setField(medicalCaseValidator, moService, "moService");
            FieldUtil.setField(simpleServiceDAOHibernate, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
            FieldUtil.setField(pmpWsImpl, medicalCaseValidator, "tapValidator");
            SaveTapInfoPojo saveTapInfoPojo = new SaveTapInfoPojoFeature();
            FieldUtil.setField(pmpWsImpl, saveTapInfoPojo, "saveTapInfoPojo");
            SaveMedicalCasePojo saveMedicalCasePojo = new SaveMedicalCasePojoFeature();
            FieldUtil.setField(saveTapInfoPojo, saveMedicalCasePojo, "saveMedicalCasePojo");
            SaveMedicalCaseService saveMedicalCaseService = new SaveMedicalCaseServiceImpl();
            FieldUtil.setField(saveMedicalCasePojo, saveMedicalCaseService, "saveMedicalCaseService");
            FieldUtil.setField(saveMedicalCaseService, medicalCaseService, "medicalCaseService");

            FieldUtil.setField(saveMedicalCaseService, getPatientPojo, "getPatientPojo");
//            FieldUtil.setField(saveMedicalCaseService, billService, "billService");
            FieldUtil.setField(saveMedicalCaseService, simpleServiceDAOHibernate, "simpleServiceDAOHibernate");

            HospDeptStayDAO hospDeptStayDAOHibernate = new HospDeptStayDAOHibernate();
            FieldUtil.setField(hospDeptStayDAOHibernate, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
            FieldUtil.setField(saveMedicalCaseService, hospDeptStayDAOHibernate, "hospDeptStayDAOHibernate");

            HospServiceDAO hospServiceDAO = createInstanceOfProtectedConstructor(HospServiceDAOHibernate.class);
            FieldUtil.setField(hospServiceDAO, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
            FieldUtil.setField(saveMedicalCaseService, hospServiceDAO, "hospServiceDAO");

            HospMedicamentDAO hospMedicamentDAO = createInstanceOfProtectedConstructor(HospMedicamentDAOHibernate.class);
            FieldUtil.setField(hospMedicamentDAO, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
            FieldUtil.setField(saveMedicalCaseService, hospMedicamentDAO, "hospMedicamentDAO");

            MedCaseOnkConsDAO medCaseOnkConsDAO = createInstanceOfProtectedConstructor(MedCaseOnkConsDAOHibernate.class);
            FieldUtil.setField(medCaseOnkConsDAO, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
            FieldUtil.setField(saveMedicalCaseService, medCaseOnkConsDAO, "medCaseOnkConsDAO");

            HospReanimationDAO hospReanimationDAO = createInstanceOfProtectedConstructor(HospReanimationDAOHibernate.class);
            FieldUtil.setField(hospReanimationDAO, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
            FieldUtil.setField(saveMedicalCaseService, hospReanimationDAO, "hospReanimationDAO");

            MedCaseOnkDiagDAO medCaseOnkDiagDAO = new MedCaseOnkDiagHibernate();
            FieldUtil.setField(medCaseOnkDiagDAO, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
            FieldUtil.setField(saveMedicalCaseService, medCaseOnkDiagDAO, "medCaseOnkDiagDAO");

            MedCaseOnkProtDAO medCaseOnkProtDAO = new MedCaseOnkProtHibernate();
            FieldUtil.setField(medCaseOnkProtDAO, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
            FieldUtil.setField(saveMedicalCaseService, medCaseOnkProtDAO, "medCaseOnkProtDAO");

            ServiceMedicamentDAO serviceMedicamentDAO = createInstanceOfProtectedConstructor(ServiceMedicamentDAOHibernate.class);
            FieldUtil.setField(serviceMedicamentDAO, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
            FieldUtil.setField(saveMedicalCaseService, serviceMedicamentDAO, "serviceMedicamentDAO");

            HospRehabilitationDAO hospRehabilitationDAO = createInstanceOfProtectedConstructor(HospRehabilitationDAOHibernate.class);
            FieldUtil.setField(serviceMedicamentDAO, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
            FieldUtil.setField(saveMedicalCaseService, serviceMedicamentDAO, "serviceMedicamentDAO");

            MedcaseDirectionDAOHibernate medcaseDirectionDAOHibernate = new MedcaseDirectionDAOHibernate();
            FieldUtil.setField(medcaseDirectionDAOHibernate, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
            FieldUtil.setField(saveMedicalCaseService, medcaseDirectionDAOHibernate, "medcaseDirectionDAOHibernate");

            CreateUpdateServiceImpl createUpdateService = new CreateUpdateServiceImpl();
            FieldUtil.setField(createUpdateService, billService, "billService");
            FieldUtil.setField(saveMedicalCaseService, createUpdateService, "createUpdateService");

            ServiceHelper serviceHelper;

            ru.ibs.pmp.service.nsi.NsiServiceImpl nsiService = new ru.ibs.pmp.service.nsi.NsiServiceImpl();
            FieldUtil.setField(nsiService, findNsiEntries, "findNsiEntries");
            FieldUtil.setField(nsiService, findNsiEntryFeature, "findNsiEntry");
            FieldUtil.setField(saveMedicalCaseService, nsiService, "nsiService");

            RevinfoDAO revinfoDAO = new RevinfoDAOHibernate();
            FieldUtil.setField(revinfoDAO, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
            FieldUtil.setField(saveMedicalCaseService, revinfoDAO, "revinfoDAO");

            IsLockOnMoAtPeriodExistsFeature isLockOnMoAtPeriodExistsFeature = new IsLockOnMoAtPeriodExistsFeatureImpl();
            FieldUtil.setField(isLockOnMoAtPeriodExistsFeature, syncDAOHibernate, "syncDAO");
            FieldUtil.setField(saveMedicalCaseService, isLockOnMoAtPeriodExistsFeature, "isLockOnMoAtPeriodExistsFeature");

            FindPractitioner findPractitioner;

            GetPatientFromCrossTablePojo getPatientFromCrossTablePojo;

//            FindNsiEntry findNsiEntry;
            FieldUtil.setField(saveMedicalCaseService, findNsiEntryFeature, "findNsiEntry");

            CommonAudDAOHibernate commonAudDAOHibernate = new CommonAudDAOHibernate();
            FieldUtil.setField(commonAudDAOHibernate, AudDAO.class, sessionFactory, "sessionFactory");
            FieldUtil.setField(saveMedicalCaseService, commonAudDAOHibernate, "commonAudDAOHibernate");

            HospCaseService hospCaseService = new HospCaseServiceImpl();
            FieldUtil.setField(saveMedicalCaseService, hospCaseService, "hospCaseService");
            NsiValidationDataValidateFeature nsiValidationDataService = new NsiValidationDataValidateFeatureImpl();
            NsiValidationDataServiceImpl nsiValidationDataServiceImpl = new NsiValidationDataServiceImpl();
            FieldUtil.setField(nsiValidationDataServiceImpl, nsiSessionFactoryProxy, "sessionFactory");
            FieldUtil.setField(nsiValidationDataService, nsiValidationDataServiceImpl, "nsiValidationDataService");
            FieldUtil.setField(hospCaseService, nsiValidationDataService, "nsiValidationDataService");
            GetPractitionersByJobIds getPractitionersByJobIds = new GetPractitionersByJobIdsImpl();
            FieldUtil.setField(getPractitionersByJobIds, findNsiEntryFeature, "findNsiEntry");
            FieldUtil.setField(getPractitionersByJobIds, practitionerDao, "practitionerDao");
            FieldUtil.setField(hospCaseService, getPractitionersByJobIds, "getPractitionersByJobIds");
            GetLpuFeature getLpuFeature = new GetLpuFeatureImpl();
            FieldUtil.setField(getLpuFeature, lpuRegistry, "lpuRegistry");
//            FieldUtil.setField(saveMedicalCaseService, getLpuFeature, "getLpuFeature");
            ExceptionFactory exceptionFactory = new ExceptionFactory();
            FieldUtil.setField(saveMedicalCaseService, exceptionFactory, "exceptionFactory");
            FieldUtil.setField(saveMedicalCasePojo, exceptionFactory, "exceptionFactory");
            FLKStateTracker flkStateTracker = new FLKStateTrackerImpl() {
                @Override
                public void onRemove(MedicalCase case_) {
                }

                @Override
                public void onCreate(MedicalCase case_) {
                }

                @Override
                public void onUpdate(MedicalCase case_) {
                }
            };
            FLKChecks flkChecks = new FLKChecksImpl() {
                @Override
                public PmpFeatureGroupException validate(MedicalCase case_, FLKCheck.PerfInfoCollector perfInfoCollector) {
                    PmpFeatureGroupException pmpFeatureGroupException = new PmpFeatureGroupException(new ArrayList<>(1));
                    return pmpFeatureGroupException;
                }
            };
            FieldUtil.setField(saveMedicalCaseService, flkStateTracker, "flkStateTracker");
            FieldUtil.setField(saveMedicalCaseService, flkChecks, "flkChecks");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return pmpWsImpl;
    }

    private static <T> T createInstanceOfProtectedConstructor(Class<T> type) throws IllegalArgumentException, InstantiationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, SecurityException {
        Constructor<T> constructor = type.getDeclaredConstructor(null);
        constructor.setAccessible(true);
        T obj = constructor.newInstance(null);
        return obj;
    }

    @Value("${runtime.erzl.ws.url}")
    private static String erzlUrlStr;
    @Value("${runtime.erzl2.ws.url}")
    private static String urlStr;

    private static ErzlPump getErzlPump() {
        ErzlPump erzlPumpMock = (ErzlPump) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{ErzlPump.class}, new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals("hashCode")) {
                    return 1;
                }
                Object obj = null;
//				StopwatchBean stopwatch = recreateCommon.createStopWatch();
                try {
                    obj = sendPost(urlStr, args[0], method.getReturnType());
                } catch (Exception e) {
//					recreateUtils.logMessage("Http ErzlPump Exception in method: " + method.getName() + "!", true, stopwatch, null, RecreateImpl.LogType.ERROR, e);
                    throw new RuntimeException(e);
                }
                return obj;
            }
        });
        return erzlPumpMock;
    }

    private static CacheManager getCacheManager() {
        final Map<String, Cache> cacheMap = new HashMap<>();
        CacheManager cacheManager = new CacheManager() {
            @Override
            public Cache getCache(String name) {
                if (cacheMap.containsKey(name)) {
                    return cacheMap.get(name);
                } else {
                    Cache cache = (Cache) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Cache.class}, new InvocationHandler() {
                        Map<Object, Object> cacheMap2 = new HashMap<>();

                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            if (method.getName().equals("hashCode")) {
                                return 1;
                            }
                            if (method.getName().equals("get")) {
                                return cacheMap2.get(args[0]);
                            } else if (method.getName().equals("put")) {
                                cacheMap2.put(args[0], args[1]);
                                return null;
                            } else {
                                return null;
                            }
                        }
                    });
                    cacheMap.put(name, cache);
                    return cache;
                }
            }

            @Override
            public Collection<String> getCacheNames() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        return cacheManager;
    }

    private static ErzlWsGatewayImpl getErzlWsGatewayImpl() {
        return new ErzlWsGatewayImpl() {
            @Override
            public Policies getPolicies() {
                Policies policies = (Policies) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Policies.class}, new InvocationHandler() {

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if (method.getName().equals("hashCode")) {
                            return 1;
                        }
                        Object obj = null;
//						StopwatchBean stopwatch = recreateCommon.createStopWatch();
                        try {
                            obj = sendPost(erzlUrlStr, args[0], method.getReturnType());
                        } catch (Exception e) {
//							recreateUtils.logMessage("Http ErzlWsGateway Exception in method: " + method.getName() + "!", true, stopwatch, null, RecreateImpl.LogType.ERROR, e);
                            throw new RuntimeException(e);
                        }
                        return obj;
                    }
                });
                return policies;
            }
        };
    }

    private static final int HTTP_READ_TIMEOUT = 60 * 1000;

    // HTTP POST request
    @SuppressWarnings("all")
    private static <K> K sendPost(String urlStr, Object obj, Class<K> objClass) throws Exception {
        //                try {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setReadTimeout(HTTP_READ_TIMEOUT);
        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
        con.setRequestProperty("SOAPAction", "\"\"");
        con.setRequestProperty("User-Agent", USER_AGENT);

        StringBuilder sb = new StringBuilder("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://erzl.org/services\">"
                + "<soapenv:Header/><soapenv:Body>\n");
        sb.append(handleMarshalledObject(marshall(obj)));
        sb.append("\n");
        sb.append("</soapenv:Body></soapenv:Envelope>");
        String urlParameters = sb.toString();

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        //      int responseCode = con.getResponseCode();
        //      System.out.println("\nSending 'POST' request to URL : " + urlStr);
        //      System.out.println("Post parameters : " + urlParameters);
        //      System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        //print result
        //      System.out.println(response.toString());
        String httpResponse = handleHttpResponseString(response.toString());
        byte[] encodeToUtf8 = httpResponse.getBytes(StandardCharsets.UTF_8);
        K kk = unmarshall(encodeToUtf8, objClass);
//		answerQueue.offer(new H2Bean(kk, response.toString()));
        return kk;
    }

    private static String handleHttpResponseString(String obj) {
        Matcher matcher = Pattern.compile("^.+?Body>(.+?)<[^>]+?Body>.+$", Pattern.DOTALL).matcher(obj);
        if (matcher.find()) {
            String group = matcher.group(1);
            return group;
        }
        return null;
    }

    private static String handleMarshalledObject(String obj) {
        return obj.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "")
                .replace(" xmlns=\"http://erzl.org/services\"", "")
                .replaceAll("</", "</ser:").replaceAll("<", "<ser:")
                .replaceAll("<ser:/ser:", "</ser:");
    }

    private static String marshall(Object obj) throws JAXBException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JAXBContext jaxbContext = JAXBContext.newInstance(obj.getClass());
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
        jaxbMarshaller.marshal(obj, byteArrayOutputStream);
        return byteArrayOutputStream.toString();
    }

    private static <T> T unmarshall(byte[] bytes, Class<T> objClass) throws JAXBException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        JAXBContext jaxbContext = JAXBContext.newInstance(objClass);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement<T> obj = (JAXBElement<T>) jaxbUnmarshaller.unmarshal(new StreamSource(byteArrayInputStream), objClass);
        return obj.getValue();
    }

    // 119 11162
//    static String authInfoXml = "<authInfo>\n"
//            + "        <orgId>1919</orgId>\n"
//            + "        <system>spu</system>\n"
//            + "        <user>gp68_morozov2_fs_e_in</user>\n"
//            + "        <password></password>\n"
//            + "      </authInfo>";
//    static String createUpdateAmbCaseRequestXml ="<createUpdateAmbCaseRequest></createUpdateAmbCaseRequest>";
    static String createUpdateAmbCaseRequestXml = "<?xml version=\"1.0\" encoding=\"windows-1251\"?><createUpdateAmbCaseRequest>\n"
            + "        <medicalCaseDTO>\n"
            + "          <caseNumber>А1</caseNumber>\n"
            + "          <diagnosisMain>\n"
            + "            <code>H35.0</code>\n"
            + "          </diagnosisMain>\n"
            + "          <diseaseOutcome>304</diseaseOutcome>\n"
            + "          <lpuId>2390</lpuId>\n"
            + "          <patientType>0</patientType>\n"
            + "          <paymentSource>1</paymentSource>\n"
            + "          <servicePlace>1</servicePlace>\n"
            + "          <services>\n"
            + "            <quantity>1</quantity>\n"
            + "            <serviceCode>007021</serviceCode>\n"
            + "            <skipInvoice>true</skipInvoice>\n"
            + "            <specialCase>0</specialCase>\n"
            + "          </services>\n"
            + "          <services>\n"
            + "            <quantity>1</quantity>\n"
            + "            <serviceCode>007012</serviceCode>\n"
            + "            <skipInvoice>true</skipInvoice>\n"
            + "            <specialCase>0</specialCase>\n"
            + "          </services>\n"
            + "          <services>\n"
            + "            <quantity>1</quantity>\n"
            + "            <serviceCode>001262</serviceCode>\n"
            + "            <skipInvoice>false</skipInvoice>\n"
            + "            <specialCase>0</specialCase>\n"
            + "          </services>\n"
            + "          <services>\n"
            + "            <quantity>1</quantity>\n"
            + "            <serviceCode>007051</serviceCode>\n"
            + "            <skipInvoice>true</skipInvoice>\n"
            + "            <specialCase>0</specialCase>\n"
            + "          </services>\n"
            + "          <services>\n"
            + "            <quantity>1</quantity>\n"
            + "            <serviceCode>007032</serviceCode>\n"
            + "            <skipInvoice>true</skipInvoice>\n"
            + "            <specialCase>0</specialCase>\n"
            + "          </services>\n"
            + "          <treatmentFinish>1</treatmentFinish>\n"
            + "          <treatmentObjective>1</treatmentObjective>\n"
            + "          <treatmentPrimary>1</treatmentPrimary>\n"
            + "          <visitPurpose>1</visitPurpose>\n"
            + "          <caseDate>2019-12-12T00:00:00.000+03:00</caseDate>\n"
            + "          <cureResult>304</cureResult>\n"
            + "          <doctorJobId>65440</doctorJobId>\n"
            + "          <id>177195005390</id>\n"
            + "          <medCardNumber>А1</medCardNumber>\n"
            + "          <patientId>26169932</patientId>\n"
            + "          <specialCase>b</specialCase>\n"
            + "          <medCaseOnkSL xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>\n"
            + "          <visitPurposeFfoms>1.0</visitPurposeFfoms>\n"
            + "          <dsOnk>0</dsOnk>\n"
            + "        </medicalCaseDTO>\n"
            + "      </createUpdateAmbCaseRequest>";

//    // 111 Test
    static String authInfoStr = "{\n"
            + "  \"orgId\" : 2390,\n"
            + "  \"system\" : \"spu\",\n"
            + "  \"user\" : \"soapuser\",\n"
            + "  \"password\" : \"123\",\n"
            + "  \"requestId\" : null\n"
            + "}";
//    static String createUpdateAmbCaseRequestStr = "{\n"
//            + "  \"medicalCaseDTO\" : {\n"
//            + "    \"canEdit\" : null,\n"
//            + "    \"caseNumber\" : \"17853851\",\n"
//            + "    \"diagnosisAdditional\" : null,\n"
//            + "    \"diagnosisMain\" : {\n"
//            + "      \"code\" : \"I11.9\",\n"
//            + "      \"diceaseNature\" : \"2\",\n"
//            + "      \"dispensary\" : null\n"
//            + "    },\n"
//            + "    \"diseaseOutcome\" : \"304\",\n"
//            + "    \"flkErrorCodes\" : [ ],\n"
//            + "    \"forwardDate\" : null,\n"
//            + "    \"forwardedLpuId\" : null,\n"
//            + "    \"lpuId\" : \"2379\",\n"
//            + "    \"nurseJobId\" : null,\n"
//            + "    \"patientType\" : 0,\n"
//            + "    \"paymentSource\" : 1,\n"
//            + "    \"servicePlace\" : 1,\n"
//            + "    \"services\" : [ {\n"
//            + "      \"codePosobr\" : null,\n"
//            + "      \"id\" : null,\n"
//            + "      \"pictureURL\" : null,\n"
//            + "      \"protocolURL\" : null,\n"
//            + "      \"quantity\" : 4,\n"
//            + "      \"serviceCode\" : \"001926\",\n"
//            + "      \"skipInvoice\" : false,\n"
//            + "      \"specialCase\" : \"0\",\n"
//            + "      \"serviceMedicament\" : [ ],\n"
//            + "      \"medCaseOnkUsl\" : null,\n"
//            + "      \"medCaseOnkProts\" : [ ],\n"
//            + "      \"medCaseOnkDiags\" : [ ]\n"
//            + "    } ],\n"
//            + "    \"treatmentFinish\" : 1,\n"
//            + "    \"treatmentObjective\" : 1,\n"
//            + "    \"treatmentPrimary\" : 1,\n"
//            + "    \"visitPurpose\" : 4,\n"
//            + "    \"directionGoal\" : null,\n"
//            + "    \"caseDate\" : \"2019-12-12T00:00:00.000Z\",\n"
//            + "    \"changeDate\" : null,\n"
//            + "    \"cureResult\" : 355,\n"
//            + "    \"diagnosisSend\" : null,\n"
//            + "    \"directionDoctorSpec\" : null,\n"
//            + "    \"directionNumber\" : null,\n"
//            + "    \"doctorJobId\" : \"647042\",\n"
//            + "    \"hospitalizationType\" : null,\n"
//            + "    \"id\" : 141602140353,\n"
//            + "    \"injury\" : null,\n"
//            + "    \"medCardNumber\" : \"17853851\",\n"
//            + "    \"newbornWeightGr\" : null,\n"
//            + "    \"patientId\" : \"17853851\",\n"
//            + "    \"policyNumber\" : null,\n"
//            + "    \"specialCase\" : \"b\",\n"
//            + "    \"targetDoctorSpec\" : null,\n"
//            + "    \"filId\" : null,\n"
//            + "    \"medCaseOnkSL\" : null,\n"
//            + "    \"medCaseOnkDiags\" : [ ],\n"
//            + "    \"medCaseOnkCons\" : [ ],\n"
//            + "    \"medCaseOnkProts\" : [ ],\n"
//            + "    \"directionLpuRfId\" : null,\n"
//            + "    \"medcaseDirectionDTOs\" : [ ],\n"
//            + "    \"visitPurposeFfoms\" : \"2.2\",\n"
//            + "    \"dsOnk\" : 0\n"
//            + "  }\n"
//            + "}";
    // 119
//    static String authInfoStr = "{\n"
//            + "  \"orgId\" : 2162,\n"
//            + "  \"system\" : \"spu\",\n"
//            + "  \"user\" : \"dgp104_morozov_fs_e_in\",\n"
//            + "  \"password\" : \"oFUphW\",\n"
//            + "  \"requestId\" : null\n"
//            + "}";
//    static String createUpdateAmbCaseRequestStr = "{\n"
//            + "  \"medicalCaseDTO\" : {\n"
//            + "    \"canEdit\" : null,\n"
//            + "    \"caseNumber\" : \"1001984941\",\n"
//            + "    \"diagnosisAdditional\" : null,\n"
//            + "    \"diagnosisMain\" : {\n"
//            + "      \"code\" : \"Z01.8\",\n"
//            + "      \"diceaseNature\" : null,\n"
//            + "      \"dispensary\" : null\n"
//            + "    },\n"
//            + "    \"diseaseOutcome\" : \"304\",\n"
//            + "    \"flkErrorCodes\" : [ ],\n"
//            + "    \"forwardDate\" : \"2019-12-17T00:00:00.000Z\",\n"
//            + "    \"forwardedLpuId\" : \"2364\",\n"
//            + "    \"lpuId\" : \"2162\",\n"
//            + "    \"nurseJobId\" : null,\n"
//            + "    \"patientType\" : 0,\n"
//            + "    \"paymentSource\" : 1,\n"
//            + "    \"servicePlace\" : 1,\n"
//            + "    \"services\" : [ {\n"
//            + "      \"codePosobr\" : null,\n"
//            + "      \"id\" : null,\n"
//            + "      \"pictureURL\" : null,\n"
//            + "      \"protocolURL\" : null,\n"
//            + "      \"quantity\" : 1,\n"
//            + "      \"serviceCode\" : \"140101\",\n"
//            + "      \"skipInvoice\" : false,\n"
//            + "      \"specialCase\" : \"0\",\n"
//            + "      \"serviceMedicament\" : [ ],\n"
//            + "      \"medCaseOnkUsl\" : null,\n"
//            + "      \"medCaseOnkProts\" : [ ],\n"
//            + "      \"medCaseOnkDiags\" : [ ]\n"
//            + "    }, {\n"
//            + "      \"codePosobr\" : null,\n"
//            + "      \"id\" : null,\n"
//            + "      \"pictureURL\" : null,\n"
//            + "      \"protocolURL\" : null,\n"
//            + "      \"quantity\" : 1,\n"
//            + "      \"serviceCode\" : \"140001\",\n"
//            + "      \"skipInvoice\" : false,\n"
//            + "      \"specialCase\" : \"0\",\n"
//            + "      \"serviceMedicament\" : [ ],\n"
//            + "      \"medCaseOnkUsl\" : null,\n"
//            + "      \"medCaseOnkProts\" : [ ],\n"
//            + "      \"medCaseOnkDiags\" : [ ]\n"
//            + "    } ],\n"
//            + "    \"treatmentFinish\" : 1,\n"
//            + "    \"treatmentObjective\" : 2,\n"
//            + "    \"treatmentPrimary\" : 1,\n"
//            + "    \"visitPurpose\" : 4,\n"
//            + "    \"directionGoal\" : \"6\",\n"
//            + "    \"caseDate\" : \"2019-12-18T00:00:00.000Z\",\n"
//            + "    \"changeDate\" : null,\n"
//            + "    \"cureResult\" : 304,\n"
//            + "    \"diagnosisSend\" : \"Z01.8\",\n"
//            + "    \"directionDoctorSpec\" : null,\n"
//            + "    \"directionNumber\" : \"3540159\",\n"
//            + "    \"doctorJobId\" : \"737688\",\n"
//            + "    \"hospitalizationType\" : null,\n"
//            + "    \"id\" : 177830995375,\n"
//            + "    \"injury\" : null,\n"
//            + "    \"medCardNumber\" : \"1001984941\",\n"
//            + "    \"newbornWeightGr\" : null,\n"
//            + "    \"patientId\" : \"35525309\",\n"
//            + "    \"policyNumber\" : null,\n"
//            + "    \"specialCase\" : \"f\",\n"
//            + "    \"targetDoctorSpec\" : null,\n"
//            + "    \"filId\" : null,\n"
//            + "    \"medCaseOnkSL\" : null,\n"
//            + "    \"medCaseOnkDiags\" : [ ],\n"
//            + "    \"medCaseOnkCons\" : [ ],\n"
//            + "    \"medCaseOnkProts\" : [ ],\n"
//            + "    \"directionLpuRfId\" : null,\n"
//            + "    \"medcaseDirectionDTOs\" : [ ],\n"
//            + "    \"visitPurposeFfoms\" : \"2.6\",\n"
//            + "    \"dsOnk\" : 0\n"
//            + "  }\n"
//            + "}";
}
