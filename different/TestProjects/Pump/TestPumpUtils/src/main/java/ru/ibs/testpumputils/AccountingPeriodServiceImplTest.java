package ru.ibs.testpumputils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.Query;
import ru.ibs.pmp.api.model.Bill;
import ru.ibs.pmp.api.model.dto.AccountingPeriodRequestDTO;
import ru.ibs.pmp.api.model.dto.AccountingPeriodResponseDTO;
import ru.ibs.pmp.api.nsi.dao.HolidayDatesRepository;
import ru.ibs.pmp.api.nsi.interfaces.FindNsiEntries;
import ru.ibs.pmp.api.service.payer.PayersService;
import ru.ibs.pmp.api.service.smo.SmoService;
import ru.ibs.pmp.common.ex.ExceptionFactory;
import ru.ibs.pmp.dao.AccountingPeriodDao;
import ru.ibs.pmp.dao.AudDAO;
import ru.ibs.pmp.dao.RequirementDAO;
import ru.ibs.pmp.dao.RevinfoDAO;
import ru.ibs.pmp.dao.SmoDao;
import ru.ibs.pmp.dao.hibernate.AbstractGenericDAO;
import ru.ibs.pmp.dao.hibernate.AccountingPeriodDaoHibernate;
import ru.ibs.pmp.dao.hibernate.BillDAOHibernate;
import ru.ibs.pmp.dao.hibernate.CommonAudDAOHibernate;
import ru.ibs.pmp.dao.hibernate.RequirementDAOHibernate;
import ru.ibs.pmp.dao.hibernate.RevinfoDAOHibernate;
import ru.ibs.pmp.dao.hibernate.SmoDaoHibernate;
import ru.ibs.pmp.features.bill.builder.BillBuilder;
import ru.ibs.pmp.features.bill.builder.RequirementBuilder;
import ru.ibs.pmp.lpu.service.LpuMultifilialService;
import ru.ibs.pmp.lpu.service.impl.LpuMultifilialServiceImpl;
import ru.ibs.pmp.nsi.features.GetAccountingPeriodWorkingDatesFeature;
import ru.ibs.pmp.nsi.features.impl.FindNsiEntriesFeature;
import ru.ibs.pmp.nsi.features.impl.GetAccountingPeriodWorkingDatesFeatureImpl;
import ru.ibs.pmp.nsi.service.NsiService;
import ru.ibs.pmp.service.ListBillsService;
import ru.ibs.pmp.service.RequirementService;
import ru.ibs.pmp.service.impl.AccountingPeriodServiceImpl;
import ru.ibs.pmp.service.impl.ListBillsServiceImpl;
import ru.ibs.pmp.service.impl.ModulePmpAbstractGenericService;
import ru.ibs.pmp.service.impl.RequirementServiceImpl;
import ru.ibs.pmp.service.payer.AbstractPayerService;
import ru.ibs.pmp.service.payer.msk.PayerServiceImpl;
import ru.ibs.pmp.service.smo.SmoServiceImpl;
import ru.ibs.testpumputils.interceptors.SQLInterceptor;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;
import ru.ibs.testpumputils.utils.FieldUtil;
import ru.ibs.testpumputils.utils.XmlUtils;

/**
 * @author NAnishhenko
 */
public class AccountingPeriodServiceImplTest {

    static SessionFactoryInterface sessionFactory;
    static SessionFactory nsiSessionFactoryProxy;
    static SessionFactoryInterface nsiSessionFactoryProxy2;

    public static void test() throws Exception {
        AccountingPeriodRequestDTO accountingPeriodDTO = createDTO();
        AccountingPeriodServiceImpl accountingPeriodServiceImpl = new AccountingPeriodServiceImpl();
        ExceptionFactory exceptionFactory = new ExceptionFactory();
        FieldUtil.setField(accountingPeriodServiceImpl, exceptionFactory, "exceptionFactory");
        ListBillsService listBillsService = new ListBillsServiceImpl();
        RequirementService requirementService = new RequirementServiceImpl();
        FieldUtil.setField(accountingPeriodServiceImpl, listBillsService, "listBillsService");
        FieldUtil.setField(accountingPeriodServiceImpl, requirementService, "requirementService");

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
        
        AccountingPeriodDao accountingPeriodDao=new AccountingPeriodDaoHibernate();
        

        PayersService payersService = new PayerServiceImpl();
        RevinfoDAO revinfoDAO = new RevinfoDAOHibernate();
        BillBuilder billBuilder = new BillBuilder();

        ru.ibs.pmp.service.NsiService nsiService = new ru.ibs.pmp.service.nsi.NsiServiceImpl();
        FieldUtil.setField(nsiService, findNsiEntries, "findNsiEntries");

        FieldUtil.setField(requirementBuilder, payersService, "payersService");
        FieldUtil.setField(requirementBuilder, revinfoDAO, "revinfoDAO");
        FieldUtil.setField(requirementBuilder, billBuilder, "billBuilder");

        GetAccountingPeriodWorkingDatesFeature getAccountingPeriodWorkingDatesFeature = new GetAccountingPeriodWorkingDatesFeatureImpl();
        FieldUtil.setField(billBuilder, getAccountingPeriodWorkingDatesFeature, "getAccountingPeriodWorkingDatesFeature");

        CommonAudDAOHibernate commonAudDAOHibernate = new CommonAudDAOHibernate();
        FieldUtil.setField(billBuilder, commonAudDAOHibernate, "commonAudDAOHibernate");

        BillDAOHibernate billDAOHibernate = new BillDAOHibernate();
        FieldUtil.setField(billBuilder, billDAOHibernate, "billDAO");

        SmoService smoService = new SmoServiceImpl();
        SmoDao smoDao = new SmoDaoHibernate();
        FieldUtil.setField(smoService, smoDao, "smoDao");
        FieldUtil.setField(billBuilder, smoService, "smoService");

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
        try {
            AccountingPeriodResponseDTO accountingPeriods = accountingPeriodServiceImpl.getAccountingPeriods(accountingPeriodDTO);
            String accountingPeriodsStr = XmlUtils.jaxbObjectToXML(accountingPeriods, "accountingPeriods");
            System.out.println(accountingPeriodsStr);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sessionFactory.cleanSessions();
            sessionFactory.close();
            nsiSessionFactoryProxy.close();
            nsiSessionFactoryProxy2.cleanSessions();
            nsiSessionFactoryProxy2.close();
        }
    }

    private static AccountingPeriodRequestDTO createDTO() {
        AccountingPeriodRequestDTO accountingPeriodDTO = new AccountingPeriodRequestDTO();
        accountingPeriodDTO.setLpuIds(Arrays.asList(2346));
        accountingPeriodDTO.setBillType(Bill.BillFetchType.SMO);
        accountingPeriodDTO.setYear(2019);
        accountingPeriodDTO.setMonth(12);
        return accountingPeriodDTO;
    }
}
