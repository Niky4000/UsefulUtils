package ru.ibs.pmp.ws;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.ReflectionUtils;
import ru.ibs.pmp.api.nsi.interfaces.FindNsiEntries;
import ru.ibs.pmp.arm.oms.controller.RequirementController;
import ru.ibs.pmp.arm.oms.model.requirement.BillModel;
import ru.ibs.pmp.arm.oms.model.requirement.RequirementModel;
import ru.ibs.pmp.arm.oms.utils.LpuNamesLoader;
import ru.ibs.pmp.auth.feature.impl.FindAvailableLpuFeatureImpl;
import ru.ibs.pmp.auth.model.UserEntity;
import ru.ibs.pmp.auth.reps.GlueRepository;
import ru.ibs.pmp.common.dao.payer.PayerDAOImpl;
import ru.ibs.pmp.common.ex.PmpFeatureException;
import ru.ibs.pmp.dao.hibernate.AbstractGenericDAO;
import ru.ibs.pmp.dao.hibernate.AccountingPeriodDaoHibernate;
import ru.ibs.pmp.dao.hibernate.BillDAOHibernate;
import ru.ibs.pmp.dao.hibernate.RequirementDAOHibernate;
import ru.ibs.pmp.features.GetRequirementByPeriodPojoFeature;
import ru.ibs.pmp.features.ListBillsFeatureImpl;
import ru.ibs.pmp.features.bill.builder.BillBuilder;
import ru.ibs.pmp.features.bill.builder.RequirementBuilder;
import ru.ibs.pmp.lpu.dao.LpuDao;
import ru.ibs.pmp.lpu.dao.NsiHelper;
import ru.ibs.pmp.lpu.dao.impl.LpuDaoImpl;
import ru.ibs.pmp.lpu.dao.impl.MoDaoImpl;
import ru.ibs.pmp.lpu.features.impls.GetLpuListByIdListFeatureImpl;
import ru.ibs.pmp.lpu.features.impls.GetLpuListWithChildByIdListFeatureImpl;
import ru.ibs.pmp.lpu.service.impl.LpuMultifilialServiceImpl;
import ru.ibs.pmp.nsi.features.PayerNamesLoader;
import ru.ibs.pmp.nsi.features.impl.FindNsiEntryFeature;
import ru.ibs.pmp.nsi.features.impl.PayerNamesLoaderImpl;
import ru.ibs.pmp.service.impl.AccountingPeriodServiceImpl;
import ru.ibs.pmp.service.impl.ModulePmpAbstractGenericService;
import ru.ibs.pmp.service.impl.RequirementServiceImpl;
import ru.ibs.pmp.service.impl.ServiceFacadeImpl;
import ru.ibs.pmp.service.impl.msk.BillServiceImpl;
import ru.ibs.pmp.service.payer.AbstractPayerService;
import ru.ibs.pmp.service.payer.msk.PayerServiceImpl;

/**
 *
 * @author NAnishhenko
 */
public class ListBillsTest {

    @PersistenceContext(unitName = "lpuEntityManager")
    private EntityManager entityManager;

    public void testListBills(FindNsiEntries findNsiEntries, SessionFactory sessionFactory,
            SessionFactory nsiSessionFactory, TransactionTemplate tx, SessionFactory authSessionFactory,
            TransactionTemplate authtx) throws Exception {
        final String yearStr = "2017";
        final String monthStr = "01";
        final String sortField = "payerName";
        final String lpuId = "2039";
        final String userName = "IBS4";

        final UserEntity userEntity = authtx.execute(new TransactionCallback<UserEntity>() {
            @Override
            public UserEntity doInTransaction(TransactionStatus ts) {
                Session session = authSessionFactory.openSession();
                UserEntity userEntity = (UserEntity) session.createCriteria(UserEntity.class).add(Restrictions.eq("uniqueid", userName)).uniqueResult();
                session.close();
                return userEntity;
            }
        });

        RequirementController requirementController = new RequirementController() {
            @Override
            protected String getCurrentLpuId() {
                return lpuId;
            }

            @Override
            protected UserEntity getUserEntity() {
//                UserEntity userEntity = new UserEntity();
                return userEntity;
            }
        };
        ListBillsFeatureImpl listBillsFeatureImpl = new ListBillsFeatureImpl();

        injectField("listBillsFeature", RequirementController.class, requirementController, listBillsFeatureImpl);

        LpuMultifilialServiceImpl lpuMultifilialService = new LpuMultifilialServiceImpl();

        injectField("findNsiEntries", lpuMultifilialService, findNsiEntries);

        injectField("lpuMultifilialService", listBillsFeatureImpl, lpuMultifilialService);

        RequirementBuilder requirementBuilder = new RequirementBuilder();
        injectField("requirementBuilder", listBillsFeatureImpl, requirementBuilder);
        injectField("lpuMultifilialService", requirementBuilder, lpuMultifilialService);
        RequirementDAOHibernate requirementDAOHibernate = new RequirementDAOHibernate();
        injectField("requirementDAO", requirementBuilder, requirementDAOHibernate);
        injectField("sessionFactory", AbstractGenericDAO.class, requirementDAOHibernate, sessionFactory);

        PayerServiceImpl payersService = new PayerServiceImpl();
        injectField("payersService", requirementBuilder, payersService);

        ru.ibs.pmp.service.nsi.NsiServiceImpl nsiServiceImpl = new ru.ibs.pmp.service.nsi.NsiServiceImpl();
        injectField("findNsiEntries", nsiServiceImpl, findNsiEntries);

        injectField("nsiService", AbstractPayerService.class, payersService, nsiServiceImpl);

        BillBuilder billBuilder = new BillBuilder();
        BillDAOHibernate billDAO = new BillDAOHibernate();
        injectField("sessionFactory", AbstractGenericDAO.class, billDAO, sessionFactory);
        injectField("billDAO", billBuilder, billDAO);
        injectField("billBuilder", requirementBuilder, billBuilder);

        AccountingPeriodServiceImpl accountingPeriodService = new AccountingPeriodServiceImpl();

        injectField("accountingPeriodService", listBillsFeatureImpl, accountingPeriodService);

        AccountingPeriodDaoHibernate accountingPeriodDao = new AccountingPeriodDaoHibernate();

        injectField("dao", ModulePmpAbstractGenericService.class, accountingPeriodService, accountingPeriodDao);
        injectField("sessionFactory", AbstractGenericDAO.class, accountingPeriodDao, sessionFactory);
        injectField("requirementDAO", accountingPeriodDao, requirementDAOHibernate);

        BillServiceImpl billService = new BillServiceImpl();
        injectField("dao", ModulePmpAbstractGenericService.class, billService, billDAO);
        injectField("billService", listBillsFeatureImpl, billService);

        PayerDAOImpl payerDAO = new PayerDAOImpl();
        injectField("findNsiEntries", payerDAO, findNsiEntries);
        injectField("payerDAO", billDAO, payerDAO);

        ru.ibs.pmp.nsi.service.NsiServiceImpl nsiService = new ru.ibs.pmp.nsi.service.NsiServiceImpl();
        injectField("sessionFactory", nsiService, nsiSessionFactory);

        FindNsiEntryFeature findNsiEntry = new FindNsiEntryFeature();
        injectField("nsiService", findNsiEntry, nsiService);
        injectField("findNsiEntry", payerDAO, findNsiEntry);

        PayerNamesLoader payerNamesLoader = new PayerNamesLoaderImpl();
        injectField("findNsiEntries", payerNamesLoader, findNsiEntries);

        injectField("payerNamesLoader", RequirementController.class, requirementController, payerNamesLoader);

        LpuNamesLoader lpuNamesLoader = new LpuNamesLoader();
        injectField("lpuNamesLoader", RequirementController.class, requirementController, lpuNamesLoader);

        FindAvailableLpuFeatureImpl findAvailableLpuFeature = new FindAvailableLpuFeatureImpl();

        injectField("findAvailableLpuFeature", lpuNamesLoader, findAvailableLpuFeature);

        GlueRepository glueRepository = (GlueRepository) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{GlueRepository.class}, new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Annotation[] annotations = method.getAnnotations();
                if (annotations != null && annotations.length == 1) {
                    String annotation = annotations[0].toString();
                    Matcher matcher = Pattern.compile("value=(.+?),", Pattern.DOTALL).matcher(annotation);
                    if (matcher.find()) {
                        String query = matcher.group(1);
                        Matcher matcher2 = Pattern.compile(":([\\w]+?)[\\W$]", Pattern.DOTALL).matcher(annotation);
                        List<String> parameterNames = new ArrayList<String>();
                        while (matcher2.find()) {
                            String parameter = matcher2.group(1);
                            parameterNames.add(parameter);
                        }
                        Session session = authSessionFactory.openSession();
                        Query sqlQuery = session.createQuery(query);
                        for (int i = 0; i < parameterNames.size(); i++) {
                            sqlQuery.setParameter(parameterNames.get(i), args[i]);
                        }
                        List list = sqlQuery.list();
                        session.close();
                        return list;
                    }
                }
                return null;
            }
        });

        injectField("glueRepository", findAvailableLpuFeature, glueRepository);

        GetLpuListWithChildByIdListFeatureImpl getLpuListWithChildByIdListFeature = new GetLpuListWithChildByIdListFeatureImpl();
        GetLpuListByIdListFeatureImpl getLpuListByIdListFeature = new GetLpuListByIdListFeatureImpl();

        LpuDao lpuDao = new LpuDaoImpl();

        MoDaoImpl moDao = new MoDaoImpl();
        injectField("entityManager", moDao, entityManager);

        NsiHelper nsiHelper = new NsiHelper();
        injectField("findNsiEntries", nsiHelper, findNsiEntries);

        injectField("moDao", lpuDao, moDao);
        injectField("nsiHelper", lpuDao, nsiHelper);

        injectField("lpuRegistry", getLpuListWithChildByIdListFeature, lpuDao);
        injectField("lpuRegistry", getLpuListByIdListFeature, lpuDao);

        injectField("getLpuListWithChildByIdListFeature", findAvailableLpuFeature, getLpuListWithChildByIdListFeature);
        injectField("getLpuListByIdListFeature", findAvailableLpuFeature, getLpuListByIdListFeature);

        GetRequirementByPeriodPojoFeature getRequirementByPeriodFeature = new GetRequirementByPeriodPojoFeature();
        RequirementServiceImpl requirementService = new RequirementServiceImpl();

        ServiceFacadeImpl serviceFacade = new ServiceFacadeImpl();

        injectField("serviceFacade", ModulePmpAbstractGenericService.class, requirementService, serviceFacade);
        injectField("lpuMultifilialService", requirementService, lpuMultifilialService);
        injectField("accountingPeriodService", requirementService, accountingPeriodService);
        injectField("dao", ModulePmpAbstractGenericService.class, requirementService, requirementDAOHibernate);

        injectField("requirementService", getRequirementByPeriodFeature, requirementService);

        injectField("getRequirementByPeriodFeature", RequirementController.class, requirementController, getRequirementByPeriodFeature);

        RequirementModel searchRequirements = tx.execute(new TransactionCallback<RequirementModel>() {
            @Override
            public RequirementModel doInTransaction(TransactionStatus ts) {
                RequirementModel searchRequirements = null;
                try {
                    searchRequirements = requirementController.searchRequirements(yearStr, monthStr, sortField, true);
                } catch (PmpFeatureException ex) {
                    Logger.getLogger(ListBillsTest.class.getName()).log(Level.SEVERE, null, ex);
                }
                return searchRequirements;
            }
        });
        for (BillModel billModel : searchRequirements.getBills()) {
            System.out.println(billModel.getId() + " " + billModel.getPayerName() + " " + billModel.getAmount().toString());
        }
        System.out.println(searchRequirements.toString());
    }

    protected void injectField(String fieldName, Object target, Object object) throws NoSuchFieldException, SecurityException {
        Field listBillsFeatureField = target.getClass().getDeclaredField(fieldName);
        listBillsFeatureField.setAccessible(true);
        ReflectionUtils.setField(listBillsFeatureField, target, object);
    }

    protected void injectField(String fieldName, Class targetClass, Object target, Object object) throws NoSuchFieldException, SecurityException {
        Field listBillsFeatureField = targetClass.getDeclaredField(fieldName);
        listBillsFeatureField.setAccessible(true);
        ReflectionUtils.setField(listBillsFeatureField, target, object);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
