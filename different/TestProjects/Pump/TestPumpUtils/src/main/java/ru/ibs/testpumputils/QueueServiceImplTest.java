/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import com.itextpdf.text.DocumentException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.erzl.services.Policies;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import ru.ibs.pmp.api.nsi.interfaces.FindNsiEntries;
import ru.ibs.pmp.api.persons.interfaces.GetRepPumpAttachedAgeSexExtFeature;
import ru.ibs.pmp.api.service.export.msk.pdf.PdfReportServiceAbstract;
import ru.ibs.pmp.api.smo.model.SmoSyncRequest;
import ru.ibs.pmp.auth.reps.AuditEntryRepositoryImpl;
import ru.ibs.pmp.auth.service.LpuService;
import ru.ibs.pmp.auth.service.impl.GlueServiceImpl;
import ru.ibs.pmp.auth.service.impl.LpuServiceImpl;
import ru.ibs.pmp.common.lib.Db;
import ru.ibs.pmp.lpu.dao.LpuDao;
import ru.ibs.pmp.lpu.dao.NsiHelper;
import ru.ibs.pmp.lpu.dao.impl.LpuDaoImpl;
import ru.ibs.pmp.lpu.dao.impl.MoDaoImpl;
import ru.ibs.pmp.lpu.features.GetLpuFeature;
import ru.ibs.pmp.lpu.features.GetLpuListByIdListFeature;
import ru.ibs.pmp.lpu.features.GetLpuListFeature;
import ru.ibs.pmp.lpu.features.impls.GetLpuFeatureImpl;
import ru.ibs.pmp.lpu.features.impls.GetLpuListByIdListFeatureImpl;
import ru.ibs.pmp.lpu.features.impls.GetLpuListFeatureImpl;
import ru.ibs.pmp.nsi.features.impl.FindNsiEntriesFeature;
import ru.ibs.pmp.persons.features.GetRepPumpAttachedAgeSexExtFeatureImpl;
import ru.ibs.pmp.persons.interfaces.ERZLStatisticsDAO;
import ru.ibs.pmp.persons.ws.ERZLStatisticsDAOImpl;
import ru.ibs.pmp.persons.ws.ErzlWsGatewayImpl;
import ru.ibs.pmp.service.PmpSettings;
import ru.ibs.pmp.service.impl.PmpSettingsImpl;
import ru.ibs.pmp.service.utils.pdf.PdfHelper;
import ru.ibs.pmp.smo.dao.CuredPatientRepository;
import ru.ibs.pmp.smo.dao.InvoiceRecordRepository;
import ru.ibs.pmp.smo.dao.ParcelProtocolRepository;
import ru.ibs.pmp.smo.dao.ServiceRepository;
import ru.ibs.pmp.smo.dao.SmoReportDao;
import ru.ibs.pmp.smo.dao.SmoReportDaoImpl;
import ru.ibs.pmp.smo.dto.request.ReportExportFileType;
import ru.ibs.pmp.smo.export.mo.ReportExportContext;
import ru.ibs.pmp.smo.export.mo.SmoResponseFileExporter;
import ru.ibs.pmp.smo.report.export.impl.EmergencyCtrlFileExporter;
import ru.ibs.pmp.smo.report.export.impl.ErrorsFileExporter;
import ru.ibs.pmp.smo.report.export.impl.HorizontalWithdrawalFileExporter;
import ru.ibs.pmp.smo.report.export.impl.IntSmoAktMekFileExporter;
import ru.ibs.pmp.smo.report.export.impl.IntSmoAktMekSmpFileExporter;
import ru.ibs.pmp.smo.report.export.impl.IntSmoAktPfFileExporter;
import ru.ibs.pmp.smo.report.export.impl.IntSmoAktPfSmpFileExporter;
import ru.ibs.pmp.smo.report.export.impl.IntSmoAktTableFormFileExporter;
import ru.ibs.pmp.smo.report.export.impl.IntSmoReestAktFileExporter;
import ru.ibs.pmp.smo.report.export.impl.MekReestrFileExporter;
import ru.ibs.pmp.smo.report.export.impl.MekReportFileExporter;
import ru.ibs.pmp.smo.report.export.impl.MekTableReportFileExporter;
import ru.ibs.pmp.smo.report.export.impl.NoHorizontalWithdrawalFileExporter;
import ru.ibs.pmp.smo.report.export.impl.ProtocolMgfomsExporter;
import ru.ibs.pmp.smo.report.export.impl.ProtocolNilExporter;
import ru.ibs.pmp.smo.report.export.impl.ProtocolPdExporter;
import ru.ibs.pmp.smo.report.export.impl.ProtocolPdInExporter;
import ru.ibs.pmp.smo.report.export.impl.ProtocolSmoExporter;
import ru.ibs.pmp.smo.report.export.impl.ProtocolSmpMgfomsDIExporter;
import ru.ibs.pmp.smo.report.export.impl.ProtocolSmpMgfomsExporter;
import ru.ibs.pmp.smo.report.export.impl.ProtocolSmpMgfomsInExporter;
import ru.ibs.pmp.smo.report.export.impl.ProtocolSmpMgfomsNilExporter;
import ru.ibs.pmp.smo.report.export.impl.ProtocolSmpSmoDIExporter;
import ru.ibs.pmp.smo.report.export.impl.ProtocolSmpSmoFiExporter;
import ru.ibs.pmp.smo.report.export.impl.ProtocolSmpSmoPrExporter;
import ru.ibs.pmp.smo.report.export.impl.ProtocolVmpExporter;
import ru.ibs.pmp.smo.report.export.impl.ReExpFileExporter;
import ru.ibs.pmp.smo.report.export.impl.UdFileExporter;
import ru.ibs.pmp.smo.report.export.impl.UpFileExporter;
import ru.ibs.pmp.smo.services.NsiService;
import ru.ibs.pmp.smo.services.ParcelExportService;
import ru.ibs.pmp.smo.services.impl.NsiServiceImpl;
import ru.ibs.pmp.smo.services.impl.ParcelExportServiceImpl;
import ru.ibs.pmp.smo.services.pdf.IntSmoAktMekSmpGenerator;
import ru.ibs.pmp.smo.services.pdf.IntSmoAktPfSmpGenerator;
import ru.ibs.pmp.smo.services.pdf.MekReportGenerator;
import ru.ibs.pmp.smo.services.pdf.MekReportReestrGenerator;
import ru.ibs.pmp.smo.services.pdf.MekReportTableGenerator;
import ru.ibs.pmp.smo.services.pdf.MgfomsProtocol;
import ru.ibs.pmp.smo.services.pdf.NilProtocol;
import ru.ibs.pmp.smo.services.pdf.PdInProtocol;
import ru.ibs.pmp.smo.services.pdf.PdProtocol;
import ru.ibs.pmp.smo.services.pdf.ReExpReportGenerator;
import ru.ibs.pmp.smo.services.pdf.SmoProtocolGenerator;
import ru.ibs.pmp.smo.services.pdf.SmpMgfomsDIProtocol;
import ru.ibs.pmp.smo.services.pdf.SmpMgfomsInProtocol;
import ru.ibs.pmp.smo.services.pdf.SmpMgfomsProtocol;
import ru.ibs.pmp.smo.services.pdf.SmpSmoDIProtocol;
import ru.ibs.pmp.smo.services.pdf.SmpSmoFiProtocol;
import ru.ibs.pmp.smo.services.pdf.SmpSmoPrProtocol;
import ru.ibs.pmp.smo.services.pdf.VmpProtocol;
import ru.ibs.pmp.smo.services.tasks.QueueServiceImpl;
import static ru.ibs.testpumputils.TestPumpUtilsMain.buildAuthSessionFactory;
import static ru.ibs.testpumputils.TestPumpUtilsMain.buildSmoSessionFactory;
import static ru.ibs.testpumputils.TestPumpUtilsMain.getAuthDataSource;
import ru.ibs.pmp.smo.dao.impl.CuredPatientRepositoryImpl;
import ru.ibs.pmp.smo.dao.impl.InvoiceRecordRepositoryImpl;
import ru.ibs.pmp.smo.dao.impl.ServiceRepositoryImpl;
import ru.ibs.testpumputils.interceptors.SQLInterceptor;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler2;
import ru.ibs.testpumputils.utils.FieldUtil;
import static ru.ibs.testpumputils.utils.ObjectUtils.getEntityManagerFactory;
import static ru.ibs.testpumputils.utils.ObjectUtils.getSmoEntityManagerFactory;
import sun.net.www.protocol.http.HttpURLConnection;

/**
 *
 * @author me
 */
public class QueueServiceImplTest {

    SessionFactoryInterface pmpSessionFactory;
    SessionFactory smoSessionFactory;
    SessionFactory authSessionFactory;
    SessionFactory nsiSessionFactoryProxy;

    public static void test() throws Exception {
        new QueueServiceImplTest().init();
    }

    public void init() throws Exception {
        pmpSessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler(TestPumpUtilsMain.buildSessionFactory(), new SqlRewriteInterceptorExt()));
        smoSessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler2(buildSmoSessionFactory(), new SQLInterceptor(sql -> {
            return sql.replaceAll("PMP_PROD", "PMP_SMO");
        })));
        authSessionFactory = (SessionFactoryInterface) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{SessionFactoryInterface.class}, new SessionFactoryInvocationHandler2(buildAuthSessionFactory(), new SQLInterceptor(sql -> {
            return sql.replaceAll("PMP_PROD", "PMP_AUTH");
        })));
        nsiSessionFactoryProxy = TestPumpUtilsMain.buildNsiSessionFactory();
        DataSource authDataSource = getAuthDataSource();
        LocalContainerEntityManagerFactoryBean smoEntityManagerFactory = getSmoEntityManagerFactory();
        EntityManagerFactory smoEntityManagerFactory_ = smoEntityManagerFactory.getNativeEntityManagerFactory();
        EntityManager smoEntityManager = smoEntityManagerFactory_.createEntityManager();
        LocalContainerEntityManagerFactoryBean pmpEntityManagerFactory = getEntityManagerFactory();
        EntityManagerFactory pmpEntityManagerFactory_ = pmpEntityManagerFactory.getNativeEntityManagerFactory();
        EntityManager pmpEntityManager = pmpEntityManagerFactory_.createEntityManager();
        try {
            erzlUrlStr = "http://test.drzsrv.ru:9080/erzlws/policyService/policies.wsdl";
            String pmpReportUrl = "";
            String templateDir = "";
            QueueServiceImpl queueService = new QueueServiceImpl();
            PmpSettings pmpSettings = new PmpSettingsImpl();
            ParcelProtocolRepository parcelProtocolRepository = new ParcelProtocolRepository();
            FieldUtil.setField(parcelProtocolRepository, smoSessionFactory, "smoSessionFactory");
            FieldUtil.setField(queueService, smoSessionFactory, "smoSessionFactory");
            FieldUtil.setField(queueService, authSessionFactory, "authSessionFactory");
            FieldUtil.setField(pmpSettings, "PMP_PROD", "PMP_SCHEMA");
            FieldUtil.setField(pmpSettings, "PMP_NSI_NEW", "NSI_SCHEMA");
            FieldUtil.setField(pmpSettings, "PMP_AUTH", "AUTH_SCHEMA");
            FieldUtil.setField(pmpSettings, "PMP_SMO1", "SMO_SCHEMA");
            FieldUtil.setField(queueService, pmpSettings, "pmpSettings");
            AuditEntryRepositoryImpl auditEntryRepositoryImpl = new AuditEntryRepositoryImpl();
            FieldUtil.setField(auditEntryRepositoryImpl, authDataSource, "dataSource");
            FieldUtil.setField(queueService, auditEntryRepositoryImpl, "auditEntryRepository");
            NsiService nsiService = new NsiServiceImpl();
            ru.ibs.pmp.nsi.service.NsiServiceImpl nsiServiceImpl = new ru.ibs.pmp.nsi.service.NsiServiceImpl() {
                @Override
                protected boolean isUsesInH2() {
                    return false;
                }
            };
            ApplicationContext appContext = (ApplicationContext) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{ApplicationContext.class}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (method.getName().equals("getBean")) {
                        Class cl = (Class) args[0];
                        if (cl.equals(ru.ibs.pmp.nsi.service.NsiService.class)) {
                            return nsiServiceImpl;
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                }
            });
            FieldUtil.setField(nsiServiceImpl, ru.ibs.pmp.nsi.service.NsiServiceImpl.class, appContext, "appContext");
            FindNsiEntries findNsiEntries = new FindNsiEntriesFeature();
            FieldUtil.setField(nsiService, findNsiEntries, "findNsiEntries");

            FieldUtil.setField(findNsiEntries, nsiServiceImpl, "nsiService");
            FieldUtil.setField(nsiServiceImpl, ru.ibs.pmp.nsi.service.NsiServiceImpl.class, nsiSessionFactoryProxy, "sessionFactory");

            SmoReportDaoImpl smoReportDao = new SmoReportDaoImpl();
            FieldUtil.setField(smoReportDao, pmpEntityManager, "entityManager");

            final List<SmoResponseFileExporter> moParcelFileExporters = Arrays.asList( //
                    //                    createEmergencyCtrlFileExporter(smoSessionFactory, findNsiEntries), //
                    //                    createErrorsFileExporter(smoSessionFactory), //
                    //                    createHorizontalWithdrawalFileExporter(),
                    //                    createIntSmoAktMekFileExporter(smoSessionFactory, pmpReportUrl),
                    //                    createIntSmoAktMekSmpFileExporter(smoSessionFactory),
                    //                    createIntSmoAktPfFileExporter(smoSessionFactory, pmpReportUrl),
                    //                    createIntSmoAktPfSmpFileExporter(smoSessionFactory),
                    //                    createIntSmoAktTableFormFileExporter(smoSessionFactory, pmpReportUrl),
                    //                    createIntSmoReestAktFileExporter(smoSessionFactory, pmpReportUrl),
                    //                    createMekReestrFileExporter(smoSessionFactory, smoReportDao, templateDir),
                    //                    createMekReportFileExporter(smoSessionFactory, smoReportDao, templateDir),
                    //                    createMekTableReportFileExporter(smoSessionFactory, smoReportDao, templateDir),
                    createNoHorizontalWithdrawalFileExporter(smoSessionFactory, pmpEntityManager, findNsiEntries) //,
            //                    createProtocolMgfomsExporter(parcelProtocolRepository, smoReportDao),
            //                    createProtocolNilExporter(parcelProtocolRepository, smoReportDao),
            //                    createProtocolPdExporter(parcelProtocolRepository, smoReportDao),
            //                    createProtocolPdInExporter(parcelProtocolRepository, smoReportDao),
            //                    createProtocolSmoExporter(parcelProtocolRepository, smoReportDao),
            //                    createProtocolSmpMgfomsDIExporter(parcelProtocolRepository, smoReportDao),
            //                    createProtocolSmpMgfomsExporter(parcelProtocolRepository, smoReportDao),
            //                    createProtocolSmpMgfomsInExporter(parcelProtocolRepository, smoReportDao),
            //                    createProtocolSmpMgfomsNilExporter(parcelProtocolRepository, smoReportDao),
            //                    createProtocolSmpSmoDIExporter(parcelProtocolRepository, smoReportDao),
            //                    createProtocolSmpSmoFiExporter(parcelProtocolRepository, smoReportDao),
            //                    createProtocolSmpSmoPrExporter(parcelProtocolRepository, smoReportDao),
            //                    createProtocolVmpExporter(parcelProtocolRepository, smoReportDao),
            //                    createReExpFileExporter(smoReportDao, templateDir),
            //                    createUdFileExporter(smoSessionFactory, findNsiEntries),
            //                    createUpFileExporter(smoSessionFactory, findNsiEntries)
            );
            ParcelExportService parcelExportService = new ParcelExportServiceImpl() {
                @Override
                protected List<SmoResponseFileExporter> getMoParcelFileExporters(ReportExportFileType exportType, ReportExportContext context) {
                    return moParcelFileExporters;
                }

                @Override
                protected FutureTask<Boolean> runFutureTask(FutureTask<Boolean> futureTask, ReportExportContext context, SmoResponseFileExporter exporter) {
                    futureTask.run();
                    return futureTask;
                }
            };
            FieldUtil.setField(parcelExportService, ParcelExportServiceImpl.class, nsiService, "nsiService");
            FieldUtil.setField(parcelExportService, ParcelExportServiceImpl.class, moParcelFileExporters, "moParcelFileExporters");
            FieldUtil.setField(parcelExportService, ParcelExportServiceImpl.class, "/home/me/tmp/dbfOutputDir", "dbfOutputDir");
            FieldUtil.setField(parcelExportService, ParcelExportServiceImpl.class, pmpSessionFactory, "pmpSessionFactory");
            FieldUtil.setField(parcelExportService, ParcelExportServiceImpl.class, smoSessionFactory, "smoSessionFactory");
            ERZLStatisticsDAO erzlStatisticsDAO = new ERZLStatisticsDAOImpl();
            ErzlWsGatewayImpl erzlWsGateway = getErzlWsGatewayImpl();
            FieldUtil.setField(erzlStatisticsDAO, erzlWsGateway, "erzlWsGateway");
            GetRepPumpAttachedAgeSexExtFeature getRepPumpAttachedAgeSexExtFeature = new GetRepPumpAttachedAgeSexExtFeatureImpl();
            FieldUtil.setField(parcelExportService, ParcelExportServiceImpl.class, getRepPumpAttachedAgeSexExtFeature, "getRepPumpAttachedAgeSexExtFeature");
            FieldUtil.setField(getRepPumpAttachedAgeSexExtFeature, erzlStatisticsDAO, "dao");
            FieldUtil.setField(queueService, parcelExportService, "parcelExportService");
            FieldUtil.setField(parcelExportService, ParcelExportServiceImpl.class, parcelProtocolRepository, "parcelProtocolRepository");

            SmoSyncRequest smoRequest = (SmoSyncRequest) Db.select(smoSessionFactory, session -> session.get(SmoSyncRequest.class, 816L));
            Runnable task = queueService.toTask(smoRequest);
            task.run();
        } finally {
            pmpEntityManager.close();
            pmpEntityManagerFactory_.close();
            smoEntityManager.close();
            smoEntityManagerFactory_.close();
            nsiSessionFactoryProxy.close();
            authSessionFactory.close();
            smoSessionFactory.close();
            pmpSessionFactory.cleanSessions();
            pmpSessionFactory.close();
        }
    }

    private String erzlUrlStr;

    public ErzlWsGatewayImpl getErzlWsGatewayImpl() {
        return new ErzlWsGatewayImpl() {
            @Override
            public Policies getPolicies() {
                Policies policies = (Policies) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Policies.class}, (Object proxy, Method method, Object[] args) -> {
                    if (method.getName().equals("hashCode")) {
                        return 1;
                    }
                    Object obj = null;
                    try {
                        obj = sendPost(erzlUrlStr, args[0], method.getReturnType());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return obj;
                });
                return policies;
            }
        };
    }

    private String handleHttpResponseString(String obj) {
        Matcher matcher = Pattern.compile("^.+?Body>(.+?)<[^>]+?Body>.+$", Pattern.DOTALL).matcher(obj);
        if (matcher.find()) {
            String group = matcher.group(1);
            return group;
        }
        return null;
    }

    private String handleMarshalledObject(String obj) {
        return obj.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "")
                .replace(" xmlns=\"http://erzl.org/services\"", "")
                .replaceAll("</", "</ser:").replaceAll("<", "<ser:")
                .replaceAll("<ser:/ser:", "</ser:");
    }

    private String marshall(Object obj) throws JAXBException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JAXBContext jaxbContext = JAXBContext.newInstance(obj.getClass());
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
        jaxbMarshaller.marshal(obj, byteArrayOutputStream);
        return byteArrayOutputStream.toString();
    }

    private <T> T unmarshall(byte[] bytes, Class<T> objClass) throws JAXBException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        JAXBContext jaxbContext = JAXBContext.newInstance(objClass);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        T obj = (T) jaxbUnmarshaller.unmarshal(byteArrayInputStream);
        return obj;
    }

    private static final int HTTP_READ_TIMEOUT = 60 * 1000;
    private static final String USER_AGENT = "Apache-HttpClient/4.1.1 (java 1.5)";
    private static Integer readTimeout;

    // HTTP POST request
    @SuppressWarnings("all")
    private <K> K sendPost(String urlStr, Object obj, Class<K> objClass) throws Exception {
        //                try {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setReadTimeout(readTimeout != null ? readTimeout : HTTP_READ_TIMEOUT);
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
//        answerQueue.offer(new H2Bean(kk, response.toString()));
        return kk;
    }

    private org.springframework.cache.CacheManager getCacheManager() {
        org.springframework.cache.CacheManager cacheManager = (org.springframework.cache.CacheManager) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{org.springframework.cache.CacheManager.class}, new InvocationHandler() {
            Map<String, Map<Object, Object>> emulatedCache = new ConcurrentHashMap<>();
            Map<String, org.springframework.cache.Cache> cacheMap = new ConcurrentHashMap<>();

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals("hashCode")) {
                    return 1;
                }
                if (method.getName().equals("getCache") && args.length == 1) {
                    String key = (String) args[0];
                    if (cacheMap.containsKey(key)) {
                        return cacheMap.get(key);
                    } else {
                        org.springframework.cache.Cache cache = (org.springframework.cache.Cache) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{org.springframework.cache.Cache.class}, new InvocationHandler() {
                            String cacheName = (String) args[0];

                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                Object key = args[0];
                                if (method.getName().equals("get") && args.length == 1) {
                                    final Object obj = emulatedCache.get(cacheName).get(key);
                                    Cache.ValueWrapper valueWrapper = (Cache.ValueWrapper) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Cache.ValueWrapper.class}, new InvocationHandler() {
                                        @Override
                                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                            if (method.getName().equals("get")) {
                                                return obj;
                                            } else {
                                                return null;
                                            }
                                        }
                                    });
                                    return valueWrapper;
                                } else if (method.getName().equals("put") && args.length == 2) {
                                    return emulatedCache.get(cacheName).put(key, args[1]);
                                } else {
                                    String errorMessage = "Cache method: " + method.getName() + " is not supported!";
                                    throw new RuntimeException(errorMessage);
                                }
                            }
                        });
                        cacheMap.put(key, cache);
                        emulatedCache.put(key, new HashMap<>());
                        return cache;
                    }
                } else {
                    String errorMessage = "EhCacheCacheManager method: " + method.getName() + " is not supported!";
                    throw new RuntimeException(errorMessage);
                }
            }
        });
        return cacheManager;
    }

    private EmergencyCtrlFileExporter createEmergencyCtrlFileExporter(SessionFactory sessionFactory, FindNsiEntries findNsiEntries) {
        EmergencyCtrlFileExporter emergencyCtrlFileExporter = new EmergencyCtrlFileExporter();
        FieldUtil.setField(emergencyCtrlFileExporter, sessionFactory, "sessionFactory");
        FieldUtil.setField(emergencyCtrlFileExporter, findNsiEntries, "findNsiEntries");
        return emergencyCtrlFileExporter;
    }

    private ErrorsFileExporter createErrorsFileExporter(SessionFactory sessionFactory) {
        ErrorsFileExporter errorsFileExporter = new ErrorsFileExporter();
        FieldUtil.setField(errorsFileExporter, sessionFactory, "sessionFactory");
        return errorsFileExporter;
    }

    private HorizontalWithdrawalFileExporter createHorizontalWithdrawalFileExporter() {
        HorizontalWithdrawalFileExporter horizontalWithdrawalFileExporter = new HorizontalWithdrawalFileExporter();
        return horizontalWithdrawalFileExporter;
    }

    private IntSmoAktMekFileExporter createIntSmoAktMekFileExporter(SessionFactory sessionFactory, String pmpReportUrl) {
        IntSmoAktMekFileExporter intSmoAktMekFileExporter = new IntSmoAktMekFileExporter();
        FieldUtil.setField(intSmoAktMekFileExporter, sessionFactory, "sessionFactory");
        FieldUtil.setField(intSmoAktMekFileExporter, pmpReportUrl, "pmpReportUrl");
        return intSmoAktMekFileExporter;
    }

    private IntSmoAktMekSmpFileExporter createIntSmoAktMekSmpFileExporter(SessionFactory sessionFactory) throws IOException, DocumentException {
        IntSmoAktMekSmpFileExporter intSmoAktMekSmpFileExporter = new IntSmoAktMekSmpFileExporter();
        FieldUtil.setField(intSmoAktMekSmpFileExporter, sessionFactory, "sessionFactory");
        IntSmoAktMekSmpGenerator intSmoAktMekSmpGenerator = new IntSmoAktMekSmpGenerator();
        FieldUtil.setField(intSmoAktMekSmpFileExporter, intSmoAktMekSmpGenerator, "intSmoAktMekSmpGenerator");
        PdfHelper pdf = new PdfHelper();
        FieldUtil.setField(intSmoAktMekSmpGenerator, PdfReportServiceAbstract.class, pdf, "pdf");
        return intSmoAktMekSmpFileExporter;
    }

    private IntSmoAktPfFileExporter createIntSmoAktPfFileExporter(SessionFactory sessionFactory, String pmpReportUrl) {
        IntSmoAktPfFileExporter intSmoAktPfFileExporter = new IntSmoAktPfFileExporter();
        FieldUtil.setField(intSmoAktPfFileExporter, sessionFactory, "sessionFactory");
        FieldUtil.setField(intSmoAktPfFileExporter, pmpReportUrl, "pmpReportUrl");
        return intSmoAktPfFileExporter;
    }

    private IntSmoAktPfSmpFileExporter createIntSmoAktPfSmpFileExporter(SessionFactory sessionFactory) throws IOException, DocumentException {
        IntSmoAktPfSmpFileExporter intSmoAktPfSmpFileExporter = new IntSmoAktPfSmpFileExporter();
        FieldUtil.setField(intSmoAktPfSmpFileExporter, sessionFactory, "sessionFactory");
        IntSmoAktPfSmpGenerator intSmoAktPfSmpGenerator = new IntSmoAktPfSmpGenerator();
        FieldUtil.setField(intSmoAktPfSmpFileExporter, intSmoAktPfSmpGenerator, "intSmoAktPfSmpGenerator");
        PdfHelper pdf = new PdfHelper();
        FieldUtil.setField(intSmoAktPfSmpGenerator, PdfReportServiceAbstract.class, pdf, "pdf");
        return intSmoAktPfSmpFileExporter;
    }

    private IntSmoAktTableFormFileExporter createIntSmoAktTableFormFileExporter(SessionFactory sessionFactory, String pmpReportUrl) {
        IntSmoAktTableFormFileExporter intSmoAktTableFormFileExporter = new IntSmoAktTableFormFileExporter();
        FieldUtil.setField(intSmoAktTableFormFileExporter, sessionFactory, "sessionFactory");
        FieldUtil.setField(intSmoAktTableFormFileExporter, pmpReportUrl, "pmpReportUrl");
        return intSmoAktTableFormFileExporter;
    }

    private IntSmoReestAktFileExporter createIntSmoReestAktFileExporter(SessionFactory sessionFactory, String pmpReportUrl) {
        IntSmoReestAktFileExporter intSmoReestAktFileExporter = new IntSmoReestAktFileExporter();
        FieldUtil.setField(intSmoReestAktFileExporter, sessionFactory, "sessionFactory");
        FieldUtil.setField(intSmoReestAktFileExporter, pmpReportUrl, "pmpReportUrl");
        return intSmoReestAktFileExporter;
    }

    private MekReestrFileExporter createMekReestrFileExporter(SessionFactory sessionFactory, SmoReportDaoImpl smoReportDao, String templateDir) throws IOException, DocumentException {
        MekReestrFileExporter mekReestrFileExporter = new MekReestrFileExporter();
        FieldUtil.setField(mekReestrFileExporter, sessionFactory, "sessionFactory");
        MekReportReestrGenerator mekReportGenerator = new MekReportReestrGenerator();
        FieldUtil.setField(mekReestrFileExporter, mekReportGenerator, "mekReportGenerator");
        FieldUtil.setField(mekReestrFileExporter, smoReportDao, "dataDao");
        FieldUtil.setField(mekReportGenerator, templateDir, "templateDir");
        return mekReestrFileExporter;
    }

    private MekReportFileExporter createMekReportFileExporter(SessionFactory sessionFactory, SmoReportDaoImpl smoReportDao, String templateDir) {
        MekReportFileExporter mekReportFileExporter = new MekReportFileExporter();
        FieldUtil.setField(mekReportFileExporter, sessionFactory, "sessionFactory");
        MekReportGenerator mekReportGenerator = new MekReportGenerator();
        FieldUtil.setField(mekReportFileExporter, mekReportGenerator, "mekReportGenerator");
        FieldUtil.setField(mekReportFileExporter, smoReportDao, "dataDao");
        FieldUtil.setField(mekReportGenerator, templateDir, "templateDir");
        return mekReportFileExporter;
    }

    private MekTableReportFileExporter createMekTableReportFileExporter(SessionFactory sessionFactory, SmoReportDaoImpl smoReportDao, String templateDir) {
        MekTableReportFileExporter mekTableReportFileExporter = new MekTableReportFileExporter();
        FieldUtil.setField(mekTableReportFileExporter, sessionFactory, "sessionFactory");
        MekReportTableGenerator mekReportTableGenerator = new MekReportTableGenerator();
        FieldUtil.setField(mekTableReportFileExporter, mekReportTableGenerator, "mekReportTableGenerator");
        FieldUtil.setField(mekTableReportFileExporter, smoReportDao, "dataDao");
        FieldUtil.setField(mekTableReportFileExporter, templateDir, "templateDir");
        return mekTableReportFileExporter;
    }

    private NoHorizontalWithdrawalFileExporter createNoHorizontalWithdrawalFileExporter(SessionFactory sessionFactory, EntityManager lpuEntityManager, FindNsiEntries findNsiEntries) {
        NoHorizontalWithdrawalFileExporter noHorizontalWithdrawalFileExporter = new NoHorizontalWithdrawalFileExporter();
        GetLpuFeature getLpuFeature = new GetLpuFeatureImpl();
        MoDaoImpl moDaoImpl = new MoDaoImpl();
        GlueServiceImpl glueServiceImpl = new GlueServiceImpl();
        LpuService lpuService = new LpuServiceImpl();
        GetLpuListFeature getLpuListFeature = new GetLpuListFeatureImpl();
        FieldUtil.setField(lpuService, getLpuListFeature, "getLpuListFeature");
        LpuDao lpuRegistry = new LpuDaoImpl();
        NsiHelper nsiHelper = new NsiHelper();
        FieldUtil.setField(nsiHelper, findNsiEntries, "findNsiEntries");
        FieldUtil.setField(lpuRegistry, nsiHelper, "nsiHelper");
        FieldUtil.setField(getLpuListFeature, lpuRegistry, "lpuRegistry");
        FieldUtil.setField(glueServiceImpl, lpuService, "lpuService");
        FieldUtil.setField(lpuRegistry, moDaoImpl, "moDao");
        GetLpuListByIdListFeature getLpuListByIdListFeature = new GetLpuListByIdListFeatureImpl();
        CuredPatientRepository curedPatientRepository = new CuredPatientRepositoryImpl(sessionFactory);
        InvoiceRecordRepository invoiceRecordRepository = new InvoiceRecordRepositoryImpl(sessionFactory);
        ServiceRepository serviceRepository = new ServiceRepositoryImpl(sessionFactory);
        FieldUtil.setField(moDaoImpl, lpuEntityManager, "entityManager");
        FieldUtil.setField(getLpuFeature, lpuRegistry, "lpuRegistry");
        FieldUtil.setField(lpuRegistry, getCacheManager(), "lpuCacheManager");
        FieldUtil.setField(noHorizontalWithdrawalFileExporter, getLpuFeature, "getLpuFeature");
        FieldUtil.setField(noHorizontalWithdrawalFileExporter, getLpuListByIdListFeature, "getLpuListByIdListFeature");
        FieldUtil.setField(noHorizontalWithdrawalFileExporter, curedPatientRepository, "curedPatientRepository");
        FieldUtil.setField(noHorizontalWithdrawalFileExporter, invoiceRecordRepository, "invoiceRecordRepository");
        FieldUtil.setField(noHorizontalWithdrawalFileExporter, serviceRepository, "serviceRepository");
        return noHorizontalWithdrawalFileExporter;
    }

    private ProtocolMgfomsExporter createProtocolMgfomsExporter(ParcelProtocolRepository parcelProtocolRepository, SmoReportDao smoReportDao) throws IOException, DocumentException {
        ProtocolMgfomsExporter protocolMgfomsExporter = new ProtocolMgfomsExporter();
        MgfomsProtocol mgfomsProtocol = new MgfomsProtocol();
        PdfHelper pdf = new PdfHelper();
        FieldUtil.setField(mgfomsProtocol, PdfReportServiceAbstract.class, pdf, "pdf");
        FieldUtil.setField(protocolMgfomsExporter, mgfomsProtocol, "mgfomsProtocol");
        FieldUtil.setField(protocolMgfomsExporter, smoReportDao, "smoReportDao");
        FieldUtil.setField(protocolMgfomsExporter, parcelProtocolRepository, "parcelProtocolRepository");
        return protocolMgfomsExporter;
    }

    private ProtocolNilExporter createProtocolNilExporter(ParcelProtocolRepository parcelProtocolRepository, SmoReportDao smoReportDao) throws IOException, DocumentException {
        ProtocolNilExporter protocolNilExporter = new ProtocolNilExporter();
        NilProtocol nilProtocol = new NilProtocol();
        PdfHelper pdf = new PdfHelper();
        FieldUtil.setField(nilProtocol, PdfReportServiceAbstract.class, pdf, "pdf");
        FieldUtil.setField(protocolNilExporter, nilProtocol, "nilProtocol");
        FieldUtil.setField(protocolNilExporter, smoReportDao, "smoReportDao");
        FieldUtil.setField(protocolNilExporter, parcelProtocolRepository, "parcelProtocolRepository");
        return protocolNilExporter;
    }

    private ProtocolPdExporter createProtocolPdExporter(ParcelProtocolRepository parcelProtocolRepository, SmoReportDao smoReportDao) throws IOException, DocumentException {
        ProtocolPdExporter protocolPdExporter = new ProtocolPdExporter();
        PdProtocol pdProtocol = new PdProtocol();
        PdfHelper pdf = new PdfHelper();
        FieldUtil.setField(pdProtocol, PdfReportServiceAbstract.class, pdf, "pdf");
        FieldUtil.setField(protocolPdExporter, pdProtocol, "pdProtocol");
        FieldUtil.setField(protocolPdExporter, smoReportDao, "smoReportDao");
        FieldUtil.setField(protocolPdExporter, parcelProtocolRepository, "parcelProtocolRepository");
        return protocolPdExporter;
    }

    private ProtocolPdInExporter createProtocolPdInExporter(ParcelProtocolRepository parcelProtocolRepository, SmoReportDao smoReportDao) throws IOException, DocumentException {
        ProtocolPdInExporter protocolPdInExporter = new ProtocolPdInExporter();
        PdInProtocol pdInProtocol = new PdInProtocol();
        PdfHelper pdf = new PdfHelper();
        FieldUtil.setField(pdInProtocol, PdfReportServiceAbstract.class, pdf, "pdf");
        FieldUtil.setField(protocolPdInExporter, pdInProtocol, "pdInProtocol");
        FieldUtil.setField(protocolPdInExporter, smoReportDao, "smoReportDao");
        FieldUtil.setField(protocolPdInExporter, parcelProtocolRepository, "parcelProtocolRepository");
        return protocolPdInExporter;
    }

    private ProtocolSmoExporter createProtocolSmoExporter(ParcelProtocolRepository parcelProtocolRepository, SmoReportDao smoReportDao) throws IOException, DocumentException {
        ProtocolSmoExporter protocolSmoExporter = new ProtocolSmoExporter();
        SmoProtocolGenerator smoProtocolGenerator = new SmoProtocolGenerator();
        PdfHelper pdf = new PdfHelper();
        FieldUtil.setField(smoProtocolGenerator, PdfReportServiceAbstract.class, pdf, "pdf");
        FieldUtil.setField(protocolSmoExporter, smoProtocolGenerator, "smoProtocolGenerator");
        FieldUtil.setField(protocolSmoExporter, smoReportDao, "smoReportDao");
        FieldUtil.setField(protocolSmoExporter, parcelProtocolRepository, "parcelProtocolRepository");
        return protocolSmoExporter;
    }

    private ProtocolSmpMgfomsDIExporter createProtocolSmpMgfomsDIExporter(ParcelProtocolRepository parcelProtocolRepository, SmoReportDao smoReportDao) throws IOException, DocumentException {
        ProtocolSmpMgfomsDIExporter protocolSmpMgfomsDIExporter = new ProtocolSmpMgfomsDIExporter();
        SmpMgfomsDIProtocol smpMgfomsDIProtocol = new SmpMgfomsDIProtocol();
        PdfHelper pdf = new PdfHelper();
        FieldUtil.setField(smpMgfomsDIProtocol, PdfReportServiceAbstract.class, pdf, "pdf");
        FieldUtil.setField(protocolSmpMgfomsDIExporter, smpMgfomsDIProtocol, "smpMgfomsDIProtocol");
        FieldUtil.setField(protocolSmpMgfomsDIExporter, smoReportDao, "smoReportDao");
        FieldUtil.setField(protocolSmpMgfomsDIExporter, parcelProtocolRepository, "parcelProtocolRepository");
        return protocolSmpMgfomsDIExporter;
    }

    private ProtocolSmpMgfomsExporter createProtocolSmpMgfomsExporter(ParcelProtocolRepository parcelProtocolRepository, SmoReportDao smoReportDao) throws IOException, DocumentException {
        ProtocolSmpMgfomsExporter protocolSmpMgfomsExporter = new ProtocolSmpMgfomsExporter();
        SmpMgfomsProtocol smpMgfomsProtocol = new SmpMgfomsProtocol();
        PdfHelper pdf = new PdfHelper();
        FieldUtil.setField(smpMgfomsProtocol, PdfReportServiceAbstract.class, pdf, "pdf");
        FieldUtil.setField(protocolSmpMgfomsExporter, smpMgfomsProtocol, "smpMgfomsProtocol");
        FieldUtil.setField(protocolSmpMgfomsExporter, smoReportDao, "smoReportDao");
        FieldUtil.setField(protocolSmpMgfomsExporter, parcelProtocolRepository, "parcelProtocolRepository");
        return protocolSmpMgfomsExporter;
    }

    private ProtocolSmpMgfomsInExporter createProtocolSmpMgfomsInExporter(ParcelProtocolRepository parcelProtocolRepository, SmoReportDao smoReportDao) throws IOException, DocumentException {
        ProtocolSmpMgfomsInExporter protocolSmpMgfomsInExporter = new ProtocolSmpMgfomsInExporter();
        SmpMgfomsInProtocol smpMgfomsInProtocol = new SmpMgfomsInProtocol();
        PdfHelper pdf = new PdfHelper();
        FieldUtil.setField(smpMgfomsInProtocol, PdfReportServiceAbstract.class, pdf, "pdf");
        FieldUtil.setField(protocolSmpMgfomsInExporter, smpMgfomsInProtocol, "smpMgfomsInProtocol");
        FieldUtil.setField(protocolSmpMgfomsInExporter, smoReportDao, "smoReportDao");
        FieldUtil.setField(protocolSmpMgfomsInExporter, parcelProtocolRepository, "parcelProtocolRepository");
        return protocolSmpMgfomsInExporter;
    }

    private ProtocolSmpMgfomsNilExporter createProtocolSmpMgfomsNilExporter(ParcelProtocolRepository parcelProtocolRepository, SmoReportDao smoReportDao) throws IOException, DocumentException {
        ProtocolSmpMgfomsNilExporter protocolSmpMgfomsNilExporter = new ProtocolSmpMgfomsNilExporter();
        SmpMgfomsInProtocol smpMgfomsInProtocol = new SmpMgfomsInProtocol();
        PdfHelper pdf = new PdfHelper();
        FieldUtil.setField(smpMgfomsInProtocol, PdfReportServiceAbstract.class, pdf, "pdf");
        FieldUtil.setField(protocolSmpMgfomsNilExporter, smpMgfomsInProtocol, "smpMgfomsInProtocol");
        FieldUtil.setField(protocolSmpMgfomsNilExporter, smoReportDao, "smoReportDao");
        FieldUtil.setField(protocolSmpMgfomsNilExporter, parcelProtocolRepository, "parcelProtocolRepository");
        return protocolSmpMgfomsNilExporter;
    }

    private ProtocolSmpSmoDIExporter createProtocolSmpSmoDIExporter(ParcelProtocolRepository parcelProtocolRepository, SmoReportDao smoReportDao) throws IOException, DocumentException {
        ProtocolSmpSmoDIExporter protocolSmpSmoDIExporter = new ProtocolSmpSmoDIExporter();
        SmpSmoDIProtocol smpSmoDIProtocol = new SmpSmoDIProtocol();
        PdfHelper pdf = new PdfHelper();
        FieldUtil.setField(smpSmoDIProtocol, PdfReportServiceAbstract.class, pdf, "pdf");
        FieldUtil.setField(protocolSmpSmoDIExporter, smpSmoDIProtocol, "smpSmoDIProtocol");
        FieldUtil.setField(protocolSmpSmoDIExporter, smoReportDao, "smoReportDao");
        FieldUtil.setField(protocolSmpSmoDIExporter, parcelProtocolRepository, "parcelProtocolRepository");
        return protocolSmpSmoDIExporter;
    }

    private ProtocolSmpSmoFiExporter createProtocolSmpSmoFiExporter(ParcelProtocolRepository parcelProtocolRepository, SmoReportDao smoReportDao) throws IOException, DocumentException {
        ProtocolSmpSmoFiExporter protocolSmpSmoFiExporter = new ProtocolSmpSmoFiExporter();
        SmpSmoFiProtocol smpSmoFiProtocol = new SmpSmoFiProtocol();
        PdfHelper pdf = new PdfHelper();
        FieldUtil.setField(smpSmoFiProtocol, PdfReportServiceAbstract.class, pdf, "pdf");
        FieldUtil.setField(protocolSmpSmoFiExporter, smpSmoFiProtocol, "smpSmoFiProtocol");
        FieldUtil.setField(protocolSmpSmoFiExporter, smoReportDao, "smoReportDao");
        FieldUtil.setField(protocolSmpSmoFiExporter, parcelProtocolRepository, "parcelProtocolRepository");
        return protocolSmpSmoFiExporter;
    }

    private ProtocolSmpSmoPrExporter createProtocolSmpSmoPrExporter(ParcelProtocolRepository parcelProtocolRepository, SmoReportDao smoReportDao) throws IOException, DocumentException {
        ProtocolSmpSmoPrExporter protocolSmpSmoPrExporter = new ProtocolSmpSmoPrExporter();
        SmpSmoPrProtocol smpSmoPrProtocol = new SmpSmoPrProtocol();
        PdfHelper pdf = new PdfHelper();
        FieldUtil.setField(smpSmoPrProtocol, PdfReportServiceAbstract.class, pdf, "pdf");
        FieldUtil.setField(protocolSmpSmoPrExporter, smpSmoPrProtocol, "smpSmoPrProtocol");
        FieldUtil.setField(protocolSmpSmoPrExporter, smoReportDao, "smoReportDao");
        FieldUtil.setField(protocolSmpSmoPrExporter, parcelProtocolRepository, "parcelProtocolRepository");
        return protocolSmpSmoPrExporter;
    }

    private ProtocolVmpExporter createProtocolVmpExporter(ParcelProtocolRepository parcelProtocolRepository, SmoReportDao smoReportDao) throws IOException, DocumentException {
        ProtocolVmpExporter protocolVmpExporter = new ProtocolVmpExporter();
        VmpProtocol vmpProtocol = new VmpProtocol();
        PdfHelper pdf = new PdfHelper();
        FieldUtil.setField(vmpProtocol, PdfReportServiceAbstract.class, pdf, "pdf");
        FieldUtil.setField(protocolVmpExporter, vmpProtocol, "vmpProtocol");
        FieldUtil.setField(protocolVmpExporter, smoReportDao, "smoReportDao");
        FieldUtil.setField(protocolVmpExporter, parcelProtocolRepository, "parcelProtocolRepository");
        return protocolVmpExporter;
    }

    private ReExpFileExporter createReExpFileExporter(SmoReportDao smoReportDao, String templateDir) {
        ReExpFileExporter reExpFileExporter = new ReExpFileExporter();
        ReExpReportGenerator reExpReportGenerator = new ReExpReportGenerator();
        FieldUtil.setField(reExpFileExporter, reExpReportGenerator, "reportGenerator");
        FieldUtil.setField(reExpReportGenerator, templateDir, "templateDir");
        FieldUtil.setField(reExpFileExporter, smoReportDao, "dataDao");
        return reExpFileExporter;
    }

    private UdFileExporter createUdFileExporter(SessionFactory sessionFactory, FindNsiEntries findNsiEntries) {
        UdFileExporter udFileExporter = new UdFileExporter();
        FieldUtil.setField(udFileExporter, sessionFactory, "sessionFactory");
        FieldUtil.setField(udFileExporter, findNsiEntries, "findNsiEntries");
        return udFileExporter;
    }

    private UpFileExporter createUpFileExporter(SessionFactory sessionFactory, FindNsiEntries findNsiEntries) {
        UpFileExporter upFileExporter = new UpFileExporter();
        FieldUtil.setField(upFileExporter, sessionFactory, "sessionFactory");
        FieldUtil.setField(upFileExporter, findNsiEntries, "findNsiEntries");
        return upFileExporter;
    }
}
