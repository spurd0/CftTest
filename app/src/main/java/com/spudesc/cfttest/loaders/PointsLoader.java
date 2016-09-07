package com.spudesc.cfttest.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.spudesc.cfttest.data.ServerResponse;
import com.spudesc.cfttest.tasks.RequestBuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
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
public class PointsLoader extends AsyncTaskLoader<ServerResponse> {
    static final String TAG = "PointsLoader";
    public static final String ARGS_COUNT_KEY = "count";
    private String[] mString;
    int count;
    /**
     * Stores away the application context associated with context.
     * Since Loaders can be used across multiple activities it's dangerous to
     * store the context directly; always use {@link #getContext()} to retrieve
     * the Loader's Context, don't use the constructor argument directly.
     * The Context returned by {@link #getContext} is safe to use across
     * Activity instances.
     *
     * @param context used to retrieve the application context.
     */
    public PointsLoader(Context context, Bundle args) {
        super(context);

        if (args != null) {
            count = args.getInt(ARGS_COUNT_KEY);
        }
        try {
            mString = RequestBuilder.getRequestPointsParams(count);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "onCreate " + count);
    }

    @Override
    public ServerResponse loadInBackground() {
        String httpUrl = mString[0];
        String params = mString[1];
        try {
            return sendRequest(httpUrl, params);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private ServerResponse sendRequest(String httpUrl, String params) throws NoSuchAlgorithmException,
            KeyManagementException, IOException {
        SSLContext mSC = null;
        mSC = SSLContext.getInstance("SSL");

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

        mSC.init(null, trustAllCerts, new java.security.SecureRandom());

        URL url;
        url = new URL(httpUrl);

        HttpsURLConnection conn;
        conn = (HttpsURLConnection) url.openConnection();
        conn.setSSLSocketFactory(mSC.getSocketFactory());
        conn.setReadTimeout(7000);
        conn.setConnectTimeout(7000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);

        Writer out = new OutputStreamWriter(conn.getOutputStream());
        out.write(params);
        out.flush();
        out.close();
        conn.connect();

        try {
            InputStream responseStream = conn.getInputStream();
            return new Gson().fromJson(responseToString(responseStream),
                    ServerResponse.class);
        } catch (FileNotFoundException ex) {
            return null;
        }
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
