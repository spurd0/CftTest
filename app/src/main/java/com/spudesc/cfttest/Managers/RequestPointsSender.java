package com.spudesc.cfttest.Managers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Roman Babenko (rbab@yandex.ru) on 8/24/2016.
 */
public class RequestPointsSender {
    private String httpUrl;
    private SSLContext sc;
    private String params;

    public RequestPointsSender(String httpUrl, String params) {
        try {
            sc = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        TrustManager[] trustAllCerts = new TrustManager[]{ // i trust cft
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };
        try {
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        this.httpUrl = httpUrl;
        this.params = params;
    }


    public InputStream sendRequest() throws IOException, KeyManagementException, NoSuchAlgorithmException {
        URL url = new URL(httpUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setSSLSocketFactory(sc.getSocketFactory());

        conn.setReadTimeout(7000);
        conn.setConnectTimeout(7000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);

        try {
            Writer out = new OutputStreamWriter(conn.getOutputStream());
            out.write(params);
            out.flush();
            out.close();
        } catch (SocketTimeoutException ex) {
            return null;
        }

        conn.connect();
        try {
            return conn.getInputStream();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
