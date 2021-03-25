package ru.ibs.testpumputils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.Query;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import ru.ibs.pmp.api.interfaces.GetRequirementByPeriodFeature;
import ru.ibs.pmp.api.nsi.dao.HolidayDatesRepository;
import ru.ibs.pmp.api.nsi.interfaces.FindNsiEntries;
import ru.ibs.pmp.api.nsi.interfaces.FindNsiEntry;
import ru.ibs.pmp.api.practitioners.features.impl.GetMOPractByBasicInfoFeatureImpl;
import ru.ibs.pmp.api.practitioners.interfaces.GetMOPractByBasicInfoFeauture;
import ru.ibs.pmp.api.service.payer.PayersService;
import ru.ibs.pmp.api.service.smo.SmoService;
import ru.ibs.pmp.arm.oms.model.requirement.BillModel;
import ru.ibs.pmp.arm.oms.model.requirement.RequirementModel;
import ru.ibs.pmp.auth.feature.FindAvailableLpuFeature;
import ru.ibs.pmp.auth.feature.impl.FindAvailableLpuFeatureImpl;
import ru.ibs.pmp.auth.model.Glue;
import ru.ibs.pmp.auth.model.UserEntity;
import ru.ibs.pmp.auth.reps.GlueRepository;
import ru.ibs.pmp.auth.service.LpuService;
import ru.ibs.pmp.auth.service.impl.GlueServiceImpl;
import ru.ibs.pmp.auth.service.impl.LpuServiceImpl;
import ru.ibs.pmp.common.ex.ExceptionFactory;
import ru.ibs.pmp.controller.RequirementController;
//import ru.ibs.pmp.dao.AccountingPeriodDao;
import ru.ibs.pmp.dao.AudDAO;
import ru.ibs.pmp.dao.BillDAO;
import ru.ibs.pmp.dao.PayerDAOImpl;
import ru.ibs.pmp.dao.RequirementDAO;
import ru.ibs.pmp.dao.RevinfoDAO;
import ru.ibs.pmp.dao.SmoDao;
import ru.ibs.pmp.dao.hibernate.AbstractGenericDAO;
//import ru.ibs.pmp.dao.hibernate.AccountingPeriodDaoHibernate;
import ru.ibs.pmp.dao.hibernate.BillDAOHibernate;
import ru.ibs.pmp.dao.hibernate.CommonAudDAOHibernate;
import ru.ibs.pmp.dao.hibernate.RequirementDAOHibernate;
import ru.ibs.pmp.dao.hibernate.RevinfoDAOHibernate;
import ru.ibs.pmp.dao.hibernate.SmoDaoHibernate;
import ru.ibs.pmp.features.GetRequirementByPeriodPojoFeature;
//import ru.ibs.pmp.features.bill.builder.BillBuilder;
import ru.ibs.pmp.features.bill.builder.RequirementBuilder;
import ru.ibs.pmp.lpu.dao.LpuDao;
import ru.ibs.pmp.lpu.dao.NsiHelper;
import ru.ibs.pmp.lpu.dao.impl.LpuDaoImpl;
import ru.ibs.pmp.lpu.dao.impl.MoDaoImpl;
import ru.ibs.pmp.lpu.features.GetLpuListFeature;
import ru.ibs.pmp.lpu.features.GetLpuListWithChildByIdListFeature;
import ru.ibs.pmp.lpu.features.impls.GetLpuListFeatureImpl;
import ru.ibs.pmp.lpu.features.impls.GetLpuListWithChildByIdListFeatureImpl;
import ru.ibs.pmp.lpu.service.LpuMultifilialService;
import ru.ibs.pmp.lpu.service.impl.LpuMultifilialServiceImpl;
import ru.ibs.pmp.nsi.features.GetAccountingPeriodWorkingDatesFeature;
import ru.ibs.pmp.nsi.features.PayerNamesLoader;
import ru.ibs.pmp.nsi.features.impl.FindNsiEntriesFeature;
import ru.ibs.pmp.nsi.features.impl.FindNsiEntryFeature;
import ru.ibs.pmp.nsi.features.impl.GetAccountingPeriodWorkingDatesFeatureImpl;
import ru.ibs.pmp.nsi.features.impl.PayerNamesLoaderImpl;
import ru.ibs.pmp.nsi.service.NsiService;
import ru.ibs.pmp.service.ListBillsService;
import ru.ibs.pmp.service.RequirementService;
//import ru.ibs.pmp.service.impl.AccountingPeriodServiceImpl;
import ru.ibs.pmp.service.impl.ListBillsServiceImpl;
import ru.ibs.pmp.service.impl.ModulePmpAbstractGenericService;
import ru.ibs.pmp.service.impl.RequirementServiceImpl;
import ru.ibs.pmp.service.impl.msk.BillServiceImpl;
import ru.ibs.pmp.service.payer.AbstractPayerService;
import ru.ibs.pmp.service.payer.msk.PayerServiceImpl;
import ru.ibs.pmp.service.smo.SmoServiceImpl;
import ru.ibs.pmp.util.LpuNamesLoader;
import ru.ibs.pmp.zlib.service.BillService;
import static ru.ibs.testpumputils.TestPumpUtilsMain.buildAuthSessionFactory;
import ru.ibs.testpumputils.interceptors.SQLInterceptor;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;
import ru.ibs.testpumputils.utils.FieldUtil;
import static ru.ibs.testpumputils.utils.ObjectUtils.getEntityManagerFactory;
import ru.ibs.testpumputils.utils.XmlUtils;

/**
 * @author NAnishhenko
 */
public class RequirementControllerTest {

    static SessionFactoryInterface sessionFactory;
    static SessionFactory nsiSessionFactoryProxy;
    static SessionFactoryInterface nsiSessionFactoryProxy2;
    static SessionFactoryInterface authSessionFactoryProxy;
    static Session currentSession;

    public static void test() throws Exception {
        authSessionFactoryProxy = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(buildAuthSessionFactory(), new SQLInterceptor(sql -> sql.replaceAll("PMP_PROD", "PMP_AUTH"))));
        currentSession = authSessionFactoryProxy.getCurrentSession();
        UserEntity userEntity = (UserEntity) currentSession.createCriteria(UserEntity.class).add(Restrictions.eq("uniqueid", "IBS4")).uniqueResult();

        RequirementController requirementController = new RequirementController() {
            @Override
            protected String getCurrentLpuId() {
                return "4708";
            }

            @Override
            protected UserEntity getUserEntity() {
                UserEntity userEntity = new UserEntity();
                return userEntity;
            }

        };

//        AccountingPeriodServiceImpl accountingPeriodServiceImpl = new AccountingPeriodServiceImpl();
        ExceptionFactory exceptionFactory = new ExceptionFactory();
//        FieldUtil.setField(accountingPeriodServiceImpl, exceptionFactory, "exceptionFactory");
        ListBillsService listBillsService = new ListBillsServiceImpl();

        FieldUtil.setField(requirementController, RequirementController.class, listBillsService, "listBillsFeature");

//        FieldUtil.setField(listBillsService, accountingPeriodServiceImpl, "accountingPeriodService");

        RequirementService requirementService = new RequirementServiceImpl();
//        FieldUtil.setField(accountingPeriodServiceImpl, listBillsService, "listBillsService");
//        FieldUtil.setField(accountingPeriodServiceImpl, requirementService, "requirementService");

        LpuMultifilialService lpuMultifilialService = new LpuMultifilialServiceImpl();
        FieldUtil.setField(listBillsService, lpuMultifilialService, "lpuMultifilialService");
        FindNsiEntries findNsiEntries = new FindNsiEntriesFeature();
        FieldUtil.setField(lpuMultifilialService, findNsiEntries, "findNsiEntries");
        ru.ibs.pmp.nsi.service.NsiServiceImpl nsiServiceImpl = new ru.ibs.pmp.nsi.service.NsiServiceImpl();
        FieldUtil.setField(findNsiEntries, nsiServiceImpl, "nsiService");

        RequirementBuilder requirementBuilder = new RequirementBuilder();
        FieldUtil.setField(requirementBuilder, lpuMultifilialService, "lpuMultifilialService");
        FieldUtil.setField(listBillsService, requirementBuilder, "requirementBuilder");

        RequirementDAO requirementDAO = new RequirementDAOHibernate();
        FieldUtil.setField(requirementService, ModulePmpAbstractGenericService.class, requirementDAO, "dao");
        FieldUtil.setField(requirementBuilder, requirementDAO, "requirementDAO");

//        AccountingPeriodDao accountingPeriodDao = new AccountingPeriodDaoHibernate();
//        FieldUtil.setField(accountingPeriodServiceImpl, ModulePmpAbstractGenericService.class, accountingPeriodDao, "dao");

        PayersService payersService = new PayerServiceImpl();
        RevinfoDAO revinfoDAO = new RevinfoDAOHibernate();
//        BillBuilder billBuilder = new BillBuilder();

        ru.ibs.pmp.service.NsiService nsiService = new ru.ibs.pmp.service.nsi.NsiServiceImpl();
        FieldUtil.setField(nsiService, findNsiEntries, "findNsiEntries");

        FieldUtil.setField(requirementBuilder, payersService, "payersService");
        FieldUtil.setField(requirementBuilder, revinfoDAO, "revinfoDAO");
//        FieldUtil.setField(requirementBuilder, billBuilder, "billBuilder");

        GetAccountingPeriodWorkingDatesFeature getAccountingPeriodWorkingDatesFeature = new GetAccountingPeriodWorkingDatesFeatureImpl();
//        FieldUtil.setField(billBuilder, getAccountingPeriodWorkingDatesFeature, "getAccountingPeriodWorkingDatesFeature");

        CommonAudDAOHibernate commonAudDAOHibernate = new CommonAudDAOHibernate();
//        FieldUtil.setField(billBuilder, commonAudDAOHibernate, "commonAudDAOHibernate");

        BillDAOHibernate billDAOHibernate = new BillDAOHibernate();
        FieldUtil.setField(billDAOHibernate, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
//        FieldUtil.setField(billBuilder, billDAOHibernate, "billDAO");
        BillService billService = new BillServiceImpl() {
            @Override
            protected BillDAO getDAO() {
                return billDAOHibernate;
            }
        };

        FindNsiEntry findNsiEntryFeature = new FindNsiEntryFeature();
//            FieldUtil.setField(medicalCaseValidator, findNsiEntryFeature, "findNsiEntryFeature");
        GetMOPractByBasicInfoFeauture getMOPractByBasicInfoFeature = new GetMOPractByBasicInfoFeatureImpl();
        FieldUtil.setField(getMOPractByBasicInfoFeature, findNsiEntryFeature, "findNsiEntry");
        FieldUtil.setField(findNsiEntryFeature, nsiServiceImpl, "nsiService");

        PayerDAOImpl payerDAOImpl = new PayerDAOImpl();
        FieldUtil.setField(billDAOHibernate, payerDAOImpl, "payerDAO");
        FieldUtil.setField(payerDAOImpl, findNsiEntryFeature, "findNsiEntry");

        FieldUtil.setField(listBillsService, billService, "billService");

        SmoService smoService = new SmoServiceImpl();
        SmoDao smoDao = new SmoDaoHibernate();
        FieldUtil.setField(smoService, smoDao, "smoDao");
//        FieldUtil.setField(billBuilder, smoService, "smoService");

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

        FieldUtil.setField(payersService, AbstractPayerService.class, nsiService, "nsiService");

        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        nsiSessionFactoryProxy2 = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildNsiSessionFactory(), new SQLInterceptor(sql -> {
            String sqlRet = sql.replaceAll("PMP_PROD", "PMP_NSI_NEW");
            return sqlRet;
        })));
        nsiSessionFactoryProxy = TestPumpUtilsMain.buildNsiSessionFactory();

        FieldUtil.setField(nsiServiceImpl, nsiSessionFactoryProxy, "sessionFactory");
        FieldUtil.setField(requirementDAO, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
//        FieldUtil.setField(accountingPeriodDao, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
        FieldUtil.setField(revinfoDAO, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
        FieldUtil.setField(billDAOHibernate, AbstractGenericDAO.class, sessionFactory, "sessionFactory");
        FieldUtil.setField(smoDao, sessionFactory, "sessionFactory");
        FieldUtil.setField(commonAudDAOHibernate, AudDAO.class, sessionFactory, "sessionFactory");
        HolidayDatesRepository holidayDatesRepository = (HolidayDatesRepository) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{HolidayDatesRepository.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals("findDayOfMonthByYearAndMonth")) {
                    String query = HolidayDatesRepository.class.getMethod("findDayOfMonthByYearAndMonth", int.class, int.class).getAnnotation(Query.class).value();
                    Session session = nsiSessionFactoryProxy2.getCurrentSession();
//                    try {
                    List<Integer> integerList = session.createQuery(query).setParameter("year", (int) args[0]).setParameter("month", (int) args[1]).list();
                    return integerList;
//                    } finally {
//                        session.close();
//                    }
                } else {
                    return null;
                }
            }
        });
        FieldUtil.setField(getAccountingPeriodWorkingDatesFeature, holidayDatesRepository, "holidayDatesRepository");

        PayerNamesLoader payerNamesLoader = new PayerNamesLoaderImpl();
        FieldUtil.setField(payerNamesLoader, findNsiEntries, "findNsiEntries");
        LpuNamesLoader lpuNamesLoader = new LpuNamesLoader();
        FieldUtil.setField(requirementController, RequirementController.class, lpuNamesLoader, "lpuNamesLoader");
        FieldUtil.setField(requirementController, RequirementController.class, payerNamesLoader, "payerNamesLoader");
        FindAvailableLpuFeature findAvailableLpuFeature = new FindAvailableLpuFeatureImpl();
        FieldUtil.setField(lpuNamesLoader, findAvailableLpuFeature, "findAvailableLpuFeature");

        GlueRepository glueRepository = (GlueRepository) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{GlueRepository.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals("findGlues")) {
                    UserEntity user = (UserEntity) args[0];
                    List<Glue> glueList = currentSession.createQuery("select distinct glue from Glue glue join glue.user u where u = :user").setParameter("user", user).list();
                    return glueList;
                } else if (method.getName().equals("findGlueListForUser")) {
//                    UserEntity user = (UserEntity) args[0];
                    List<Glue> glueList = currentSession.createQuery("select distinct glue from Glue glue join fetch glue.group join fetch glue.organizationType where glue.user = :user").setParameter("user", userEntity).list();
                    return glueList;
                } else {
                    return null;
                }
            }
        });
        FieldUtil.setField(findAvailableLpuFeature, glueRepository, "glueRepository");

        MoDaoImpl moDaoImpl = new MoDaoImpl();
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

        FieldUtil.setField(findAvailableLpuFeature, getLpuListFeature, "getLpuListFeature");

        LocalContainerEntityManagerFactoryBean entityManagerFactory = getEntityManagerFactory();
        EntityManagerFactory entityManagerFactory_ = entityManagerFactory.getNativeEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory_.createEntityManager();
        FieldUtil.setField(moDaoImpl, entityManager, "entityManager");

        GetLpuListWithChildByIdListFeature getLpuListWithChildByIdListFeature = new GetLpuListWithChildByIdListFeatureImpl();
        FieldUtil.setField(getLpuListWithChildByIdListFeature, lpuRegistry, "lpuRegistry");
        FieldUtil.setField(findAvailableLpuFeature, getLpuListWithChildByIdListFeature, "getLpuListWithChildByIdListFeature");

        GetRequirementByPeriodFeature getRequirementByPeriodFeature = new GetRequirementByPeriodPojoFeature();
        FieldUtil.setField(requirementController, RequirementController.class, getRequirementByPeriodFeature, "getRequirementByPeriodFeature");
        FieldUtil.setField(getRequirementByPeriodFeature, requirementService, "requirementService");
        FieldUtil.setField(requirementService, lpuMultifilialService, "lpuMultifilialService");
        try {
//            RequirementModel searchRequirementsSmpAdd = requirementController.searchRequirementsSmpAdd("2020", "2", "payerName", true);
//            RequirementModel respone = requirementController.searchRequirementsSmp("2020", "2", "payerName", true);
            RequirementModel respone = requirementController.searchRequirements("2020", "03", "payerName", true);
            String searchRequirementsSmpAddStr = XmlUtils.jaxbObjectToXML(respone, "model");
            System.out.println(searchRequirementsSmpAddStr);
            for (BillModel billModel : respone.getBills()) {
                String str = toString(billModel);
                System.out.println(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sessionFactory.cleanSessions();
            sessionFactory.close();
            nsiSessionFactoryProxy.close();
            nsiSessionFactoryProxy2.cleanSessions();
            nsiSessionFactoryProxy2.close();
//            if (currentSession.isOpen()) {
//                currentSession.close();
//            }
            authSessionFactoryProxy.cleanSessions();
            authSessionFactoryProxy.close();
        }
    }

    private static String toString(BillModel billModel) {
        return "BillModel{" + "id=" + billModel.getId() + ", status=" + billModel.getStatus() + ", amount=" + billModel.getAmount() + ", errorAmount=" + billModel.getErrorAmount() + ", payerName=" + billModel.getPayerName() + ", patientCount=" + billModel.getPatientCount() + ", patientAttachedCount=" + billModel.getPatientAttachedCount() + ", invoiceCount=" + billModel.getInvoiceCount() + ", costPF=" + billModel.getCostPF() + ", decreasePF=" + billModel.getDecreasePF() + ", dateCreated=" + billModel.getDateCreated() + ", dateSent=" + billModel.getDateSent() + ", dateReceived=" + billModel.getDateReceived() + ", lpuName=" + billModel.getLpuName() + ", details=" + billModel.getDetails() + ", duplicateMessages=" + billModel.getDuplicateMessages() + ", requirementId=" + billModel.getRequirementId() + ", errorAmountFlkBill=" + billModel.getErrorAmountFlkBill() + '}';
    }
}
