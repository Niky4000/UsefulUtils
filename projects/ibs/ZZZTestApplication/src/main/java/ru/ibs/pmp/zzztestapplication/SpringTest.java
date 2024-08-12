/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ibs.pmp.zzztestapplication;

import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author me
 */
public class SpringTest {

    private static final int TIMEOUT = 60 * 1000;

    private static HttpComponentsClientHttpRequestFactory getHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(TIMEOUT);
        httpRequestFactory.setReadTimeout(TIMEOUT);
        return httpRequestFactory;
    }

    public static void testRestTemplate() throws URISyntaxException {
        try {
            URI uri = new URI("http", null, "test.drzsrv.ru", 8088, "/PmpClient", "/PmpClient" + "&user=" + "hello", null);
            RestTemplate restTemplate = new RestTemplate(getHttpRequestFactory());
            Object forObject = restTemplate.getForObject(uri, Object.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
