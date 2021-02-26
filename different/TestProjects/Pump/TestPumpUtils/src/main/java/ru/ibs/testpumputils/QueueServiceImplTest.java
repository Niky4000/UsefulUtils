/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.erzl.services.Policies;
import org.hibernate.SessionFactory;
import ru.ibs.pmp.api.nsi.interfaces.FindNsiEntries;
import ru.ibs.pmp.api.persons.interfaces.GetRepPumpAttachedAgeSexExtFeature;
import ru.ibs.pmp.api.smo.model.SmoSyncRequest;
import ru.ibs.pmp.auth.reps.AuditEntryRepositoryImpl;
import ru.ibs.pmp.common.lib.Db;
import ru.ibs.pmp.nsi.features.impl.FindNsiEntriesFeature;
import ru.ibs.pmp.persons.features.GetRepPumpAttachedAgeSexExtFeatureImpl;
import ru.ibs.pmp.persons.interfaces.ERZLStatisticsDAO;
import ru.ibs.pmp.persons.ws.ERZLStatisticsDAOImpl;
import ru.ibs.pmp.persons.ws.ErzlWsGatewayImpl;
import ru.ibs.pmp.service.PmpSettings;
import ru.ibs.pmp.service.impl.PmpSettingsImpl;
import ru.ibs.pmp.smo.dto.request.ReportExportFileType;
import ru.ibs.pmp.smo.export.mo.ReportExportContext;
import ru.ibs.pmp.smo.export.mo.SmoResponseFileExporter;
import ru.ibs.pmp.smo.report.export.impl.EmergencyCtrlFileExporter;
import ru.ibs.pmp.smo.services.NsiService;
import ru.ibs.pmp.smo.services.ParcelExportService;
import ru.ibs.pmp.smo.services.impl.NsiServiceImpl;
import ru.ibs.pmp.smo.services.impl.ParcelExportServiceImpl;
import ru.ibs.pmp.smo.services.tasks.QueueServiceImpl;
import static ru.ibs.testpumputils.TestPumpUtilsMain.buildAuthSessionFactory;
import static ru.ibs.testpumputils.TestPumpUtilsMain.buildSmoSessionFactory;
import static ru.ibs.testpumputils.TestPumpUtilsMain.getAuthDataSource;
import ru.ibs.testpumputils.interceptors.SQLInterceptor;
import ru.ibs.testpumputils.interceptors.SqlRewriteInterceptorExt;
import ru.ibs.testpumputils.interfaces.SessionFactoryInterface;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler;
import ru.ibs.testpumputils.interfaces.SessionFactoryInvocationHandler2;
import ru.ibs.testpumputils.utils.FieldUtil;
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
        try {
            erzlUrlStr = "http://test.drzsrv.ru:9080/erzlws/policyService/policies.wsdl";
            QueueServiceImpl queueService = new QueueServiceImpl();
            PmpSettings pmpSettings = new PmpSettingsImpl();
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
            FindNsiEntries findNsiEntries = new FindNsiEntriesFeature();
            FieldUtil.setField(nsiService, findNsiEntries, "findNsiEntries");
            ru.ibs.pmp.nsi.service.NsiServiceImpl nsiServiceImpl = new ru.ibs.pmp.nsi.service.NsiServiceImpl() {
                @Override
                protected boolean isUsesInH2() {
                    return false;
                }
            };
            FieldUtil.setField(findNsiEntries, nsiServiceImpl, "nsiService");
            FieldUtil.setField(nsiServiceImpl, ru.ibs.pmp.nsi.service.NsiServiceImpl.class, nsiSessionFactoryProxy, "sessionFactory");
            final List<SmoResponseFileExporter> moParcelFileExporters = Arrays.asList(createEmergencyCtrlFileExporter(smoSessionFactory, findNsiEntries));
            ParcelExportService parcelExportService = new ParcelExportServiceImpl() {
                @Override
                protected List<SmoResponseFileExporter> getMoParcelFileExporters(ReportExportFileType exportType, ReportExportContext context) {
                    return moParcelFileExporters;
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

            SmoSyncRequest smoRequest = (SmoSyncRequest) Db.select(smoSessionFactory, session -> session.get(SmoSyncRequest.class, 816L));
            Runnable task = queueService.toTask(smoRequest);
            task.run();
        } finally {
            nsiSessionFactoryProxy.close();
            authSessionFactory.close();
            smoSessionFactory.close();
            pmpSessionFactory.cleanSessions();
            pmpSessionFactory.close();
        }
    }

    private static EmergencyCtrlFileExporter createEmergencyCtrlFileExporter(SessionFactory sessionFactory, FindNsiEntries findNsiEntries) {
        EmergencyCtrlFileExporter emergencyCtrlFileExporter = new EmergencyCtrlFileExporter();
        FieldUtil.setField(emergencyCtrlFileExporter, sessionFactory, "sessionFactory");
        FieldUtil.setField(emergencyCtrlFileExporter, findNsiEntries, "findNsiEntries");
        return emergencyCtrlFileExporter;
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
}
