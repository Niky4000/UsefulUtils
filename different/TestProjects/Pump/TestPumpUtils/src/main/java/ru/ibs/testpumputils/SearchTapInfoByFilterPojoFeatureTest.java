package ru.ibs.testpumputils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import ru.ibs.pmp.api.model.dto.Paging;
import ru.ibs.pmp.api.model.dto.TapFilter;
import ru.ibs.pmp.features.impl.SearchTapInfoByFilterPojoFeature;
import ru.ibs.pmp.util.DbUtils;
import static ru.ibs.testpumputils.TestPmpParcelInsertion.sessionFactory;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;
import ru.ibs.testpumputils.utils.FieldUtil;

public class SearchTapInfoByFilterPojoFeatureTest {

    public static void test(String args[]) throws Exception {
        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        try {
            SearchTapInfoByFilterPojoFeature searchTapInfoByFilterPojoFeature = new SearchTapInfoByFilterPojoFeature() {
                @Override
                protected boolean isUsesInH2() {
                    return false;
                }
            };
            FieldUtil.setField(searchTapInfoByFilterPojoFeature, SearchTapInfoByFilterPojoFeature.class, sessionFactory, "sessionFactory");
            Method findTapsByRevisonLigthMethod = SearchTapInfoByFilterPojoFeature.class.getDeclaredMethod("findTapsByRevisonLigth", TapFilter.class, Paging.class);
            findTapsByRevisonLigthMethod.setAccessible(true);
            TapFilter tapFilter = new TapFilter();
            tapFilter.setBillId(101819897L);
            tapFilter.setBillRevision(868189600L);
            tapFilter.setMoHeadId(Arrays.asList(4708L));
            tapFilter.setPeriod(new SimpleDateFormat("yyyy-MM-dd").parse("2022-08-01"));
            tapFilter.setDateStart(new SimpleDateFormat("yyyy-MM-dd").parse("2022-08-01"));
            Paging paging = Paging.initByOffset(0, 25);
            Object result = findTapsByRevisonLigthMethod.invoke(searchTapInfoByFilterPojoFeature, tapFilter, paging);
            System.out.println();
        } finally {
            sessionFactory.cleanSessions();
            sessionFactory.close();
        }
    }
}
