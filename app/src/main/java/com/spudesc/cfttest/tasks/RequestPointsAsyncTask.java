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
    private RequestPointsInterface mInterface;

    public RequestPointsAsyncTask (RequestPointsInterface mInterface) {
        this.mInterface = mInterface;
    }

    @Override
    protected ServerResponse doInBackground(String... strings) {
        String httpUrl = strings[0];
        String params = strings[1];

        try {
            return sendRequest(httpUrl, params);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (KeyManagementException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            Log.d(TAG, e.toString());
            e.printStackTrace();
            return null;
        }
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

    @Override
    protected void onPostExecute(ServerResponse serverResponse) {
        mInterface.deliverResult(serverResponse);
    }

    public interface RequestPointsInterface {
        void deliverResult(ServerResponse serverResponse);
    }
}
