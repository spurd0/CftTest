package com.spudesc.cfttest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.spudesc.cfttest.Tasks.RequestBuilder;
import com.spudesc.cfttest.Interfaces.RequestInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity implements RequestInterface{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPoints(10);
    }

    @Override
    public void requestPoints(final int count) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Log.d("Response =", RequestBuilder.getRequestedPoints(count));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

    }
}
