package com.spudesc.cfttest.managers;

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
    private String mHttpUrl;
    private SSLContext mSC;
    private String mParams;

    public RequestPointsSender(String httpUrl, String mParams) {
        try {
            mSC = SSLContext.getInstance("SSL");
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
            mSC.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        this.mHttpUrl = httpUrl;
        this.mParams = mParams;
    }


    public InputStream sendRequest() throws IOException, KeyManagementException, NoSuchAlgorithmException {
        URL url = new URL(mHttpUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setSSLSocketFactory(mSC.getSocketFactory());

        conn.setReadTimeout(7000);
        conn.setConnectTimeout(7000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);

        try {
            Writer out = new OutputStreamWriter(conn.getOutputStream());
            out.write(mParams);
            out.flush();
            out.close();
        } catch (SocketTimeoutException ex) {
            return null;
        }

        conn.connect();
        try {
            return conn.getInputStream();
        } catch (FileNotFoundException ex) {
            return null;
        }
    }
}
