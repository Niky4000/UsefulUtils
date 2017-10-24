/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ibs.featuretest;

import java.net.URI;
import java.util.concurrent.Callable;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ibs.pmp.common.ex.PmpTransportException;
import ru.ibs.pmp.registry.ModuleToken;
import ru.ibs.pmp.utils.KeyValue;

/**
 *
 * @author NAnishhenko
 */
public final class HttpHelperExt {

    private HttpHelperExt() {

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpHelperExt.class);
    private static final int CONNECTION_TIMEOUT = 300000;
    private static final int SO_TIMEOUT = 300000;

    public static final String PARAM_SEPARATOR = ";;;";
    public static final String CONTENT_TYPE = "text/html;charset=UTF-8";
    public static final String PMP_HEADER = "PMP_SUPER_HEADER";

    private static HttpClient client;

    private static ThreadLocal<Boolean> disableTimeout = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    public static void init() {
        RequestConfig requestConfig = RequestConfig.custom().
                setSocketTimeout(SO_TIMEOUT).
                setConnectTimeout(CONNECTION_TIMEOUT).
                build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(10000);
        connectionManager.setDefaultMaxPerRoute(1000);

        BasicCookieStore cookieStore = new BasicCookieStore();
        BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", "72F95B3144B899EFAAABDDDBC05363B6");
        cookie.setDomain("172.29.4.26");
        cookie.setPath("/");
        cookieStore.addCookie(cookie);

        client = HttpClients.custom().
                setDefaultRequestConfig(requestConfig).
                setConnectionManager(connectionManager).
                //setDefaultCookieStore(cookieStore).
                build();
    }

    /**
     * Execute action with no timeout in {@link #sendGetRequest} and
     * {@link #sendPostRequest}
     *
     * @param action Action to execute
     * @param <T> Action result type
     * @return Action result
     * @throws Exception Action exception
     */
    public static <T> T executeNoTimeout(Callable<T> action) throws Exception {
        try {
            disableTimeout.set(true);
            return action.call();
        } finally {
            disableTimeout.set(false);
        }
    }

    public static String sendPostRequest(String request, ModuleToken token) throws PmpTransportException {

        LOGGER.debug("HTTP POST Client for Feature {} was created", token.toUrl());

        HttpPost httppost = new HttpPost(token.toUrl());
        checkDisabledTimeout(httppost);
        httppost.setHeader(PMP_HEADER, PMP_HEADER);

        HttpResponse response = null;
        String content = null;

        try {

            StringEntity se = new StringEntity(request, "UTF-8");
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE));

            httppost.setEntity(se);

            response = client.execute(httppost);

            HttpEntity responseEntity = response.getEntity();
            content = EntityUtils.toString(responseEntity);

        } catch (Exception ae) {

            throw new PmpTransportException(ae);

        } finally {

            httppost.releaseConnection();
        }

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

            return content;

        } else {

            String format
                    = String.format("Could not get response from module %s with httpcode %d error message: \n %s \n",
                            token.toUrl(), response.getStatusLine().getStatusCode(), content);

            LOGGER.error(format);
            throw new PmpTransportException(format);
        }
    }

    public static String sendGetRequest(KeyValue[] pairs, String host, String module, int port)
            throws PmpTransportException {

        LOGGER.debug(" HTTP GET Client for  Feature {} was created", host);

        URIBuilder builder = new URIBuilder();
        URIBuilder http = builder.setScheme("http").setHost(host).setPath(String.format("/%s", module)).setPort(port);

        for (KeyValue keyValue : pairs) {
            http.setParameter(keyValue.getKey(), keyValue.getValue());
        }

        HttpResponse response = null;
        String content = null;
        HttpGet httpget = null;

        try {
            URI uri = builder.build();
            httpget = new HttpGet(uri);
            checkDisabledTimeout(httpget);
            httpget.setHeader(PMP_HEADER, PMP_HEADER);

            response = client.execute(httpget);

            HttpEntity responseEntity = response.getEntity();
            content = EntityUtils.toString(responseEntity);
        } catch (Exception ae) {
            throw new PmpTransportException(ae);
        } finally {
            if (httpget != null) {
                httpget.releaseConnection();
            }
        }

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return content;
        } else {
            String format
                    = String.format("Could not get response from module %s : %d with httpcode %d error message: \n %s \n", host, port, response.getStatusLine().getStatusCode(), content);

            LOGGER.error(format);
            throw new PmpTransportException(format);
        }
    }

    public static void shutdown() {
        client.getConnectionManager().shutdown();
    }

    /**
     * Recofigure request wuthout timeout, if requested
     *
     * @param request Request to reconfigure
     */
    private static void checkDisabledTimeout(HttpRequestBase request) {
        if (disableTimeout.get()) {
            RequestConfig requestConfig = RequestConfig.custom().
                    setSocketTimeout(0).
                    setConnectTimeout(0).
                    build();
            request.setConfig(requestConfig);
        }
    }
}
