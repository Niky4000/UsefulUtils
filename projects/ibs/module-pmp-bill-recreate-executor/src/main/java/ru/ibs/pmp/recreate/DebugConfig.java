package ru.ibs.pmp.recreate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.ibs.pmp.api.interfaces.bill.RecreateBillsFeature;
import ru.ibs.pmp.api.model.PmpSync;
import ru.ibs.pmp.api.model.RecreateBillsRequest;
import ru.ibs.pmp.dao.SyncDAO;
import ru.ibs.pmp.dao.SyncSaveDAO;

/**
 * @author NAnishhenko
 */
//@Configuration
public class DebugConfig {

//    @Bean
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

//    @Bean
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

//                    return Arrays.asList(new PmpSync[]{createPmpSync(1101, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
//                        createPmpSync(1102, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
//                        createPmpSync(1103, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
//                        createPmpSync(1104, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
//                        createPmpSync(1105, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
//                        createPmpSync(1106, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
//                        createPmpSync(1107, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
//                        createPmpSync(1108, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
//                        createPmpSync(1109, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
//                        createPmpSync(1110, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
//                        createPmpSync(1111, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
//                        createPmpSync(1112, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
//                        createPmpSync(1113, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
//                        createPmpSync(1114, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
//                        createPmpSync(1115, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
//                        createPmpSync(1116, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
//                        createPmpSync(1117, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
//                        createPmpSync(1118, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
//                        createPmpSync(1119, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId"),
//                        createPmpSync(1120, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId")});
                    return Arrays.asList(new PmpSync[]{createPmpSync(1863, period, RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId")});
//                    return Arrays.asList(new PmpSync[]{createPmpSync(4889, Date.from(LocalDateTime.of(2017, 11, 1, 0, 0, 0).toInstant(ZoneOffset.UTC)), RecreateBillsRequest.RECREATE_BILLS_VIRTUAL_REQUEST, RecreateBillsFeature.NAME, "", "userId")});

                } else if (methodName.equals("hashCode")) {
                    return 89;
                }
                return null;
            }
        });
        return syncDAO;
    }
}
