/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.marykaylib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import ru.kiokle.marykaylib.bean.MailBean;
import sun.net.www.protocol.http.HttpURLConnection;

/**
 *
 * @author me
 */
public class MailHandlerWebImpl implements MailHandler {

    private static final String url = "http://kiokle.ru/index2.php";

    @Override
    public void readEmail(Properties properties, String mail, String user, String password) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static final String delimiter = "=> ";

    private List<String> getFileList() {
        Map<String, Object> params = new HashMap<>();
        params.put(GET_FILE_LIST, TRUE_PARAM);
        String sendPost = sendPost(url, params);
        List<String> fileList = Arrays.stream(sendPost.replace("Array(", "").replace(")", "").split("    ")).filter(str -> str.contains(delimiter)).map(str -> str.substring(str.indexOf(delimiter) + delimiter.length())).filter(fileName -> !fileName.equals(".") && !fileName.equals("..")).collect(Collectors.toList());
        return fileList;
    }

    Pattern pattern = Pattern.compile("^cpuId=(.+?)&text=(.+?)&subject=(.+)$", Pattern.DOTALL);

    @Override
    public void readYandexMailbox(String login, String password, String imapServer, String cpuId, Boolean request, Predicate<MailBean> deleteCondition, ArrayBlockingQueue<MailBean> queue) throws MessagingException, IOException {
        if (cpuId == null) {
            List<String> fileList = getFileList();
            fileList.stream().forEach(cpuId_ -> readOneFile(cpuId_, request, deleteCondition, queue));
        } else {
            readOneFile(cpuId, request, deleteCondition, queue);
        }
    }

    private void readOneFile(String cpuId, Boolean request, Predicate<MailBean> deleteCondition, ArrayBlockingQueue<MailBean> queue) {
        Map<String, Object> params = new HashMap<>();
        String type = null;
        if (request != null) {
            params.put(CPU_ID, cpuId);
            type = request ? GET_REQUEST : GET_RESPONSE;
        } else if (cpuId.endsWith(REQUEST)) {
            params.put(CPU_ID, cpuId.replace(REQUEST, ""));
            type = GET_REQUEST;
        } else if (cpuId.endsWith(RESPONSE)) {
            params.put(CPU_ID, cpuId.replace(RESPONSE, ""));
            type = GET_RESPONSE;
        }
        params.put(type, TRUE_PARAM);
        String sendPost = sendPost(url, params);
        if (sendPost != null && sendPost.length() > 0 && !sendPost.contains(NO_DATA)) {
            Matcher matcher = pattern.matcher(sendPost);
            if (matcher.find()) {
                String cpuIdFromPost = matcher.group(1);
                String textFromPost = matcher.group(2);
                String subjectFromPost = matcher.group(3);
                MailBean mailBean = new MailBean(null, null, cpuIdFromPost, request, subjectFromPost, textFromPost);
                if (deleteCondition.test(mailBean)) {
                    deleteMail(params, type);
                }
                queue.offer(mailBean);
            }
        }
    }
    private static final String TRUE_PARAM = "true";
    private static final String CPU_ID = "cpuId";
    private static final String NO_DATA = "No Data!";
    private static final String REQUEST = "_request";
    private static final String RESPONSE = "_response";

    private static final String GET_FILE_LIST = "getFileList";
    private static final String PUT_RESPONSE = "putResponse";
    private static final String GET_RESPONSE = "getResponse";
    private static final String DELETE_RESPONSE = "deleteResponse";
    private static final String PUT_REQUEST = "putRequest";
    private static final String GET_REQUEST = "getRequest";
    private static final String DELETE_REQUEST = "deleteRequest";

    private void deleteMail(Map<String, Object> params, String queryType) {
        params.remove(queryType);
        if (queryType.equals(GET_REQUEST)) {
            params.put(DELETE_REQUEST, TRUE_PARAM);
        } else if (queryType.equals(GET_RESPONSE)) {
            params.put(DELETE_RESPONSE, TRUE_PARAM);
        }
        String sendPost = sendPost(url, params);
    }

    @Override
    public void sendMail(Properties properties, String mail, String user, String password, String sendMail) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendMail2(Properties properties, String mail, String user, String password, String sendMail, MailBean mailBean) {
        Map<String, Object> params = mailBean.toMap();
        params.put(CPU_ID, mailBean.getCpuId());
        params.put(mailBean.isRequest() ? PUT_REQUEST : PUT_RESPONSE, TRUE_PARAM);
        String sendPost = sendPost(url, params);
    }

    private static final int HTTP_READ_TIMEOUT = 60 * 1000;
    private static final String USER_AGENT = "Apache-HttpClient/4.1.1 (java 1.5)";

    // HTTP POST request
    @SuppressWarnings("all")
    private String sendPost(String urlStr, Map<String, Object> params) {
        try {
            URL url = new URL(urlStr);
//        Map<String, Object> params = new LinkedHashMap<>();
//        params.put("value", 5); // All parameters, also easy
//        params.put("id", 17);

            StringBuilder postData = new StringBuilder();
            // POST as urlencoded is basically key-value pairs, as with GET
            // This creates key=value&key=value&... pairs
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) {
                    postData.append('&');
                }
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }

            // Convert string to byte array, as it should be sent
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            // Connect, easy
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            // Tell server that this is POST and in which format is the data

            con.setReadTimeout(HTTP_READ_TIMEOUT);
            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            con.setRequestProperty("User-Agent", USER_AGENT);

//        StringBuilder sb = new StringBuilder("uuuu=SpaceCraft");
//        String urlParameters = sb.toString();
            // Send post request
            con.setDoOutput(true);
            con.getOutputStream().write(postDataBytes);
//        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//        wr.writeBytes(urlParameters);
//        wr.flush();
//        wr.close();
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
            String httpResponse = response.toString();
//        byte[] encodeToUtf8 = httpResponse.getBytes(StandardCharsets.UTF_8);
            return httpResponse;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
