/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.testpumputils;

import static com.mysema.query.types.ExpressionUtils.path;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import static org.apache.http.client.methods.RequestBuilder.options;
import sun.net.www.protocol.http.HttpURLConnection;
import static sun.security.krb5.Confounder.bytes;

/**
 *
 * @author me
 */
public class HttpTest {

    private static File file = new File("/home/me/tmp/report");

    private static final String USER_AGENT = "Apache-HttpClient/4.1.1 (java 1.5)";
    // HTTP POST request

    private static String sendGet(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
            return response.toString();
        } else {
            System.out.println("GET request not worked");
            return null;
        }
    }

    private static byte[] sendPost(String url, String params) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "UTF-8");

        con.setDoOutput(true);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.getOutputStream());
        outputStreamWriter.write(params.toString());
        outputStreamWriter.flush();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
//        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

//        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//        String inputLine;
//        StringBuffer response = new StringBuffer();
//
//        while ((inputLine = in.readLine()) != null) {
//            response.append(inputLine);
//        }
//        in.close();
//
//        System.out.println(response.toString());
//        return response.toString();
        return readInputStream(new BufferedInputStream(con.getInputStream()));
    }

    private static final int BUFFER_SIZE = 1024 * 1024;
    private static final int SMALL_BUFFER_SIZE = 1024 * 1024;

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        byte[] bytes3 = null;
        int previousIndex = 0;
        try (BufferedInputStream in = new BufferedInputStream(inputStream)) {
            Integer read = 0;
            Integer available = 0;
            do {
                available = in.available();
                byte[] bytes2 = new byte[available > SMALL_BUFFER_SIZE ? available : SMALL_BUFFER_SIZE];
                read = in.read(bytes2);
                if (read > 0 && read + previousIndex > bytes.length) {
                    int newBufferSize = Math.max(bytes2.length, BUFFER_SIZE + bytes.length + previousIndex);
                    bytes3 = new byte[newBufferSize];
                    System.arraycopy(bytes, 0, bytes3, 0, bytes.length);
                    bytes = new byte[newBufferSize];
                    System.arraycopy(bytes3, 0, bytes, 0, bytes3.length);
                    System.arraycopy(bytes2, 0, bytes, previousIndex, read);
                    previousIndex += read;
                } else if (read > 0) {
                    System.arraycopy(bytes2, 0, bytes, previousIndex, read);
                    previousIndex += read;
                }
            } while (read > 0);
        }
        bytes3 = new byte[previousIndex];
        System.arraycopy(bytes, 0, bytes3, 0, previousIndex);
        return bytes3;
    }

    public static void test() throws Exception {
//        String sendResult = new HttpTest().sendGet("http://test.drzsrv.ru:8085/module-reports-pmp/api/reportsInternal/Hello?id=8&str=something");
//        String sendResult = new HttpTest().sendGet("http://test.drzsrv.ru:8085/module-reports-pmp/api/reportsInternal/Hello?id=8&str=0");
//        String sendResult = new HttpTest().sendPost("http://test.drzsrv.ru:8085/module-reports-pmp/api/reportsInternal/testPost2",
//                new StringBuilder("id=").append(URLEncoder.encode("1000088", "UTF-8"))
//                        .append("&user=").append(URLEncoder.encode("soapuser", "UTF-8"))
//                        .append("&password=").append(URLEncoder.encode("123", "UTF-8")).toString());
//        String sendResult = new HttpTest().sendPost("http://test.drzsrv.ru:8085/module-reports-pmp/api/reportsInternal/templates/user_action_mo/reports",
//                new StringBuilder("__format=").append(URLEncoder.encode("PDF", "UTF-8"))
//                        .append("&user=").append(URLEncoder.encode("soapuser", "UTF-8"))
//                        .append("&password=").append(URLEncoder.encode("123", "UTF-8"))
//                        .append("&startDate=").append(URLEncoder.encode("02.10.2020", "UTF-8"))
//                        .append("&endDate=").append(URLEncoder.encode("01.10.2020", "UTF-8"))
//                        .append("&login=").append(URLEncoder.encode("IBS4", "UTF-8"))
//                        .append("&moId=").append(URLEncoder.encode("1863", "UTF-8"))
//                        .toString());
        byte[] sendResult = new HttpTest().sendPost("http://test.drzsrv.ru:8085/module-reports-pmp/api/reportsInternal/templates/rpt_pract_sertificat/reports",
                new StringBuilder("__format=").append(URLEncoder.encode("XLSX", "UTF-8"))
                        .append("&user=").append(URLEncoder.encode("soapuser", "UTF-8"))
                        .append("&password=").append(URLEncoder.encode("123", "UTF-8"))
                        .append("&moId=").append(URLEncoder.encode("4102", "UTF-8"))
                        .toString());
        if (sendResult != null && sendResult.length > 0) {
            if (!file.exists()) {
                file.createNewFile();
            }
            Files.write(file.toPath(), sendResult, StandardOpenOption.TRUNCATE_EXISTING);
        }
//        System.out.println(sendResult);
    }
}
