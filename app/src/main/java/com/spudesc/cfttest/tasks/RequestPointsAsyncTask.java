package com.spudesc.cfttest.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.spudesc.cfttest.data.ServerResponse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Roman Babenko (roman.babenko@sibers.com) on 9/7/2016.
 */
public class RequestPointsAsyncTask extends AsyncTask<String, Void, ServerResponse> {
    public final static String TAG = "RequestPointsAsyncTask";

    @Override
    protected ServerResponse doInBackground(String... strings) {
        String httpUrl = strings[0];
        String params = strings[1];
        Log.d(TAG, params);
        SSLContext mSC = null;
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
        URL url = null;
        try {
            url = new URL(httpUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpsURLConnection conn = null;
        try {
            conn = (HttpsURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        conn.setSSLSocketFactory(mSC.getSocketFactory());

        conn.setReadTimeout(7000);
        conn.setConnectTimeout(7000);
        try {
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            conn.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            return new Gson().fromJson(responseToString(conn.getInputStream()),
                    ServerResponse.class);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String responseToString(InputStream is) {
        String result = new String();
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String inputLine;
        try {
            while ((inputLine = in.readLine()) != null) {
                result += inputLine;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
