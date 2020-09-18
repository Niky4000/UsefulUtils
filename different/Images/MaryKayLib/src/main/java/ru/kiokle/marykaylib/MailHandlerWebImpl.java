/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.marykaylib;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Predicate;
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

    @Override
    public void readYandexMailbox(String login, String password, String imapServer, Predicate<MailBean> deleteCondition, ArrayBlockingQueue<MailBean> queue) throws MessagingException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendMail(Properties properties, String mail, String user, String password, String sendMail) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendMail2(Properties properties, String mail, String user, String password, String sendMail, MailBean mailBean) {
        sendPost(url, mailBean.toMap());
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
