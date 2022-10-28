package ru.ibs.testpumputils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.erzl.services.ErzlPump;
import org.erzl.services.GetPersonsInfoRequest;
import org.erzl.services.GetPersonsInfoResponse;
import sun.net.www.protocol.http.HttpURLConnection;

public class Task15072Test {

    private static final String urlStr = "";

    public static void test() throws Exception {
        new Task15072Test().test2();
    }

    public void test2() throws Exception {
        ErzlPump erzlPump = getErzlPump();
        for (int i = 1; i <= 5; i++) {
            GetPersonsInfoRequest getPersonsInfoRequest = deSerializeObject(new File("" + i));
            GetPersonsInfoResponse personsInfo = erzlPump.getPersonsInfo(getPersonsInfoRequest);
        }
    }
    private static final int BUFFER_SIZE = 1024 * 1024;

    private <T> T deSerializeObject(File serfile) {
//		StopwatchBean stopwatch = recreateCommon.createStopWatch();
        if (!serfile.exists()) {
            return null;
        }
        boolean error = false;
        try (ObjectInput input = getInputStream(serfile, false)) {
            T obj = (T) input.readObject();
            return obj;
        } catch (ClassNotFoundException | IOException e) {
//			callBackBeanForDeSerialization.apply(e);
            error = true;
        }
        if (!error) {
//			log("deSerialize", serfile, stopwatch);
        }
        return null;
    }

    private ObjectInput getInputStream(File serfile, boolean gzip) throws FileNotFoundException, IOException {
        ObjectInput input;
        if (gzip) {
            try {
                InputStream file = new FileInputStream(serfile);
                InputStream zip = new GZIPInputStream(file, BUFFER_SIZE); // Use zip!
                input = new ObjectInputStream(zip); // Use zip!
            } catch (IOException ioe) {
                InputStream file = new FileInputStream(serfile);
                InputStream buffer = new BufferedInputStream(file, BUFFER_SIZE); // Do not use zip!
                input = new ObjectInputStream(buffer); // Do not use zip!
            }
        } else {
            InputStream file = new FileInputStream(serfile);
            InputStream buffer = new BufferedInputStream(file, BUFFER_SIZE); // Do not use zip!
            input = new ObjectInputStream(buffer); // Do not use zip!
        }
        return input;
    }

    private static final String USER_AGENT = "Apache-HttpClient/4.1.1 (java 1.5)";

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

    // HTTP POST request
    @SuppressWarnings("all")
    private <K> K sendPost(String urlStr, Object obj, Class<K> objClass) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setReadTimeout(HTTP_READ_TIMEOUT);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
        con.setRequestProperty("SOAPAction", "\"\"");
        con.setRequestProperty("User-Agent", USER_AGENT);
        StringBuilder sb = new StringBuilder("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://erzl.org/services\"><soapenv:Header/><soapenv:Body>\n");
        sb.append(handleMarshalledObject(marshall(obj)));
        sb.append("\n");
        sb.append("</soapenv:Body></soapenv:Envelope>");
        String urlParameters = sb.toString();
        System.out.println("--------------------------------");
        System.out.println("--------------------------------");
        System.out.println(urlParameters);
        System.out.println("--------------------------------");
        System.out.println("--------------------------------");
        con.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.writeBytes(urlParameters);
            wr.flush();
        }
        int responseCode = con.getResponseCode();
        InputStream errorStream = getErrorStream(con);
        InputStream inputStream = (errorStream != null ? errorStream : getInputStream(con));
        StringBuffer response = readResponse(con, inputStream);
        if (responseCode != java.net.HttpURLConnection.HTTP_OK) {
//			answerQueue.offer(new H2Bean(null, urlParameters + "\n\n----\n\n" + response.toString(), responseCode));
            throw new RuntimeException("Response code = " + responseCode + "!");
        }
        String httpResponse = handleHttpResponseString(response.toString());
        byte[] encodeToUtf8 = httpResponse.getBytes(StandardCharsets.UTF_8);
        K kk = unmarshall(encodeToUtf8, objClass);
//		answerQueue.offer(new H2Bean(kk, response.toString(), responseCode));
        return kk;
    }

    private InputStream getInputStream(HttpURLConnection con) {
        try {
            return con.getInputStream();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private InputStream getErrorStream(HttpURLConnection con) {
        return con.getErrorStream();
    }

    private StringBuffer readResponse(HttpURLConnection con, InputStream inputStream) throws IOException {
        StringBuffer response = new StringBuffer();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        return response;
    }

    public ErzlPump getErzlPump() {
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
}
