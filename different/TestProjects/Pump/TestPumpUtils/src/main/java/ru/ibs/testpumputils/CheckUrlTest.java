package ru.ibs.testpumputils;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import ru.ibs.pmp.api.model.MedicalCase;
import ru.ibs.pmp.api.model.msk.TapInfo;
import ru.ibs.pmp.api.model.yar.HospCase;
import ru.ibs.pmp.common.ex.PmpFeatureException;
import ru.ibs.pmp.common.lib.Db;
import ru.ibs.pmp.service.flk.ErrorMarker;
import ru.ibs.pmp.service.flk.check.AbstractFLKCheck;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;

/**
 *
 * @author me
 */
public class CheckUrlTest {

    static SessionFactoryInterface sessionFactory;

    public static void test() throws IOException {
        class CheckUrl extends AbstractFLKCheck<MedicalCase> {

            private static final String delimeter = "–";
            private static final String HTTPS = "https?://";
            private static final String zeroTo255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";
            private static final String IP_REGEXP = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;
            private static final String DOMAIN_NAME_REGEXP = "[\\w.]+?";
            private static final String prefixRegexp = ".*?/?";
            private static final String patientIdRegexp = "\\d+?";
            private static final String patientTypeRegexp = "[0-3]";
            private static final String diagnosisRegexp = "\\w\\d+\\.?\\d{0,2}";
            private static final String serviceCodeRegexp = "\\d+?";
            private static final String serviceDateRegexp = "\\d{8}";
            private static final String serviceNumberRegexp = "(" + delimeter + "\\d{2})?";
            private static final String changeDateRegexp = "(" + delimeter + "\\d{8})?";

            public CheckUrl() {
                super("PMP.URL.01");
            }

            @Override
            public void execute(MedicalCase case_, ErrorMarker marker) throws PmpFeatureException {
                if (case_.getSimpleServices().stream().filter(ss -> ss.getProtocolURL() != null && ss.getProtocolURL().length() > 0).anyMatch(ss -> !urlAnalisys(ss.getProtocolURL()))) {
                    marker.markCase();
                }
                if (case_ instanceof HospCase && ((HospCase) case_).getHospDeptStays().stream().filter(hds -> hds.getEpicrisisUrl() != null && hds.getEpicrisisUrl().length() > 0).anyMatch(hds -> !urlAnalisys(hds.getEpicrisisUrl()))) {
                    marker.markCase();
                }
                if (case_.getDiagnoses().stream().filter(di -> di.getEpicrisisUrl() != null && di.getEpicrisisUrl().length() > 0).anyMatch(di -> !urlAnalisys(di.getEpicrisisUrl()))) {
                    marker.markCase();
                }
            }

            private final boolean urlAnalisys(String urlToAnalyse) {
                return Pattern.compile("^" + HTTPS + IP_REGEXP + "/" + prefixRegexp + patientIdRegexp + delimeter + patientTypeRegexp + delimeter + diagnosisRegexp + delimeter + serviceCodeRegexp + delimeter + serviceDateRegexp + serviceNumberRegexp + changeDateRegexp + "$").matcher(urlToAnalyse).matches() || Pattern.compile("^" + HTTPS + DOMAIN_NAME_REGEXP + "/" + prefixRegexp + patientIdRegexp + delimeter + patientTypeRegexp + delimeter + diagnosisRegexp + delimeter + serviceCodeRegexp + delimeter + serviceDateRegexp + serviceNumberRegexp + changeDateRegexp + "$").matcher(urlToAnalyse).matches();
            }

            @Override
            public List<Class> getExecuteClass() {
                return ImmutableList.<Class>builder().add(TapInfo.class).add(HospCase.class).build();
            }
        }
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        ErrorMarker marker = (ErrorMarker) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{ErrorMarker.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals("markCase")) {
                    atomicBoolean.set(true);
                    return null;
                } else {
                    return null;
                }
            }
        });

        CheckUrl checkUrl = new CheckUrl();
        sessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        Db.select(session -> {
            MedicalCase medicalCase = (MedicalCase) session.get(MedicalCase.class, 388551790352L);
            checkUrl.execute(medicalCase, marker);
        }, sessionFactory);
        try {
        } finally {
            sessionFactory.cleanSessions();
            sessionFactory.close();
        }
    }
}
