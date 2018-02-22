package ru.ibs.pmp.module.recreate.exec.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;
import ru.ibs.pmp.api.interfaces.bill.RecreateBillsFeature;
import ru.ibs.pmp.api.model.PmpSync;
import ru.ibs.pmp.api.model.RecreateBillsRequest;
import ru.ibs.pmp.dao.SyncDAO;
import ru.ibs.pmp.dao.SyncSaveDAO;
import ru.ibs.pmp.module.recreate.exec.ExecuteRecreate;
import ru.ibs.pmp.module.recreate.exec.ExecuteRecreateDAO;
import ru.ibs.pmp.module.recreate.exec.ExecuteUtils;
import ru.ibs.pmp.module.recreate.exec.ExecuteUtilsImpl;
import ru.ibs.pmp.module.recreate.exec.bean.OsProcessBean;
import ru.ibs.pmp.module.recreate.exec.bean.RunProcessResultBean;
import ru.ibs.pmp.module.recreate.exec.bean.TargetSystemBeanWrapper;

/**
 * @author NAnishhenko
 */
@Configuration
public class AdditionalDebugConfig {

    @Bean
//    @Primary
    public ExecuteRecreate getExecuteRecreate() {
        return new ExecuteRecreate();
    }

    Map<String, Object[]> lpuIdToServiceCountMap = ImmutableMap.<String, Object[]>builder()
            .put("2290", new Object[]{Long.valueOf("2290"), 150L * 1000L})
            .put("1816", new Object[]{Long.valueOf("1816"), 100L * 1000L})
            .put("4889", new Object[]{Long.valueOf("4889"), 400L * 1000L})
            .put("1101", new Object[]{Long.valueOf("1101"), 1L * 1000L})
            .put("1102", new Object[]{Long.valueOf("1102"), 2L * 1000L})
            .put("1103", new Object[]{Long.valueOf("1103"), 3L * 1000L})
            .put("1104", new Object[]{Long.valueOf("1104"), 4L * 1000L})
            .put("1105", new Object[]{Long.valueOf("1105"), 5L * 1000L})
            .put("1106", new Object[]{Long.valueOf("1106"), 6L * 1000L})
            .put("1107", new Object[]{Long.valueOf("1107"), 7L * 1000L})
            .put("1108", new Object[]{Long.valueOf("1108"), 8L * 1000L})
            .put("1109", new Object[]{Long.valueOf("1109"), 9L * 1000L})
            .put("1110", new Object[]{Long.valueOf("1110"), 10L * 1000L})
            .put("1111", new Object[]{Long.valueOf("1111"), 11L * 1000L})
            .put("1112", new Object[]{Long.valueOf("1112"), 12L * 1000L})
            .put("1113", new Object[]{Long.valueOf("1113"), 13L * 1000L})
            .put("1114", new Object[]{Long.valueOf("1114"), 14L * 1000L})
            .put("1115", new Object[]{Long.valueOf("1115"), 15L * 1000L})
            .put("1116", new Object[]{Long.valueOf("1116"), 16L * 1000L})
            .put("1117", new Object[]{Long.valueOf("1117"), 17L * 1000L})
            .put("1118", new Object[]{Long.valueOf("1118"), 18L * 1000L})
            .put("1119", new Object[]{Long.valueOf("1119"), 19L * 1000L})
            .put("1120", new Object[]{Long.valueOf("1120"), 20L * 1000L})
            .build();

    @Bean
//    @Primary
    public ExecuteRecreateDAO getExecuteRecreateDAO() {
        return new ExecuteRecreateDAO() {
            @Override
            public boolean checkForRecreateJarExisting(File recreateJarPar) {
                return true;
            }

            @Override
            public List<Object[]> getServiceCount(Date period, Date periodEnd, Set<String> lpuIdSetForRecreate) throws TransactionException {
                ImmutableList.Builder<Object[]> builder = ImmutableList.<Object[]>builder();
                for (String lpuId : lpuIdSetForRecreate) {
                    if (lpuIdToServiceCountMap.containsKey(lpuId)) {
                        builder.add(lpuIdToServiceCountMap.get(lpuId));
                    } else {
                        builder.add(new Object[]{Long.valueOf(lpuId), 100L * 1000L});
                    }
                }
                return builder.build();
            }

        };
    }

    @Bean
//    @Primary
    public ExecuteUtils getExecuteUtils() {
        final AtomicBoolean uploadNewVersionWasCalled = new AtomicBoolean(false);
        return new ExecuteUtilsImpl() {

            @Override
            public List<OsProcessBean> getProcessList(TargetSystemBeanWrapper targetSystemBean) {
                if (targetSystemBean.getHost().contains("116")) {
                    return ImmutableList.<OsProcessBean>builder()
                            .add(new OsProcessBean("123", 123, "java -jar recreate.jar -m 2290 2018-01"))
                            .add(new OsProcessBean("124", 124, "java -jar recreate.jar -m 1816 2018-01"))
                            .build();
                } else {
                    return new ArrayList<>();
                }
            }

            @Override
            public RunProcessResultBean runProcess(String[] commands, boolean agregateResult, boolean runInCmd) throws InterruptedException, IOException {
                return null;
            }

            @Override
            public RunProcessResultBean runProcessInBackgroung(String[] commands) throws InterruptedException, IOException {
                return null;
            }

            @Override
            public boolean runProcessInCmd(String[] commands) throws InterruptedException, IOException {
                return true;
            }

            @Override
            public void uploadNewVersion(TargetSystemBeanWrapper targetSystemBean, String remoteDirName, File recreateJar, File pmpConfigPath) {
                uploadNewVersionWasCalled.getAndSet(true);
            }

            @Override
            public List<String> deleteOldJars(TargetSystemBeanWrapper targetSystemBean, String remoteDirName, Pattern dirnamePattern) {
                return ImmutableList.<String>builder().add("some_dir_that_was_deleted").build();
            }

            @Override
            public Long getFreeMemory(TargetSystemBeanWrapper targetSystemBean) {
                return 40L * 1024L;
            }

            @Override
            public boolean executeRemoteProcess(TargetSystemBeanWrapper targetSystemBean) {
                return true;
            }

        };
    }

    @Bean
//    @Primary
    public SyncSaveDAO getSyncSaveDAO() {
        SyncSaveDAO syncSaveDAO = (SyncSaveDAO) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SyncSaveDAO.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String methodName = method.getName();
                if (methodName.equals("hashCode")) {
                    return 86549;
                }
                return null;
            }
        });
        return syncSaveDAO;
    }

    @Bean
//    @Primary
    public SyncDAO getSyncDAO() {

        Date period = Date.from(LocalDateTime.of(2018, 1, 1, 0, 0, 0).toInstant(ZoneId.systemDefault().getRules().getOffset(Instant.now())));

        SyncDAO syncDAO = (SyncDAO) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SyncDAO.class}, new InvocationHandler() {

            private PmpSync createPmpSync(int lpuId, Date period, String callData, String featureName, String parameters, String userId) {
                PmpSync pmpSync = new PmpSync(lpuId, period, callData);
                pmpSync.setFeatureName(featureName);
                pmpSync.setParameters(parameters);
                pmpSync.setUserId(userId);
                return pmpSync;
            }

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String methodName = method.getName();
                if (methodName.equals("getLockCount")) {
                    return 0;
                } else if (methodName.equals("getAll")) {

                    return Arrays.asList(new PmpSync[]{ //
                        createPmpSync(1101, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
                        createPmpSync(1102, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
                        createPmpSync(1103, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
                        createPmpSync(1104, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
                        createPmpSync(1105, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
                        createPmpSync(1106, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
                        createPmpSync(1107, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
                        createPmpSync(1108, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
                        createPmpSync(1109, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
                        createPmpSync(1110, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
                        createPmpSync(1111, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
                        createPmpSync(1112, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
                        createPmpSync(1113, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
                        createPmpSync(1114, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
                        createPmpSync(1115, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
                        createPmpSync(1116, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
                        createPmpSync(1117, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
                        createPmpSync(1118, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
                        createPmpSync(1119, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
                        createPmpSync(1120, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId")});
//
//                    return Arrays.asList(new PmpSync[]{createPmpSync(1863, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
//                        createPmpSync(4889, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId2")});
//                    return Arrays.asList(new PmpSync[]{createPmpSync(4889, Date.from(LocalDateTime.of(2017, 11, 1, 0, 0, 0).toInstant(ZoneOffset.UTC)), RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId")});

                } else if (methodName.equals("hashCode")) {
                    return 89;
                }
                return null;
            }
        });
        return syncDAO;
    }

    @Bean
    @Qualifier("pmpSessionFactory")
    public SessionFactory getPmpSessionFactory() {
        SessionFactory sessionFactory = (SessionFactory) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactory.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String methodName = method.getName();
                if (methodName.equals("hashCode")) {
                    return 35244;
                }
                return null;
            }
        });
        return sessionFactory;
    }

    @Bean
    @Qualifier("pmpTransactionTemplate")
    public TransactionTemplate getPmpTransactionTemplate() {
        return new TransactionTemplate(new org.springframework.orm.hibernate4.HibernateTransactionManager());
    }
}
