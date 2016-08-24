package com.spudesc.cfttest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.spudesc.cfttest.Data.ServerResponse;
import com.spudesc.cfttest.Interfaces.ResponseInterface;
import com.spudesc.cfttest.Tasks.RequestBuilder;
import com.spudesc.cfttest.Interfaces.RequestInterface;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity implements RequestInterface, ResponseInterface{
    Thread requestThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPoints(-1);
    }

    @Override
    public void requestPoints(final int count) {
        if (isNetworkConnected()) {
            if (requestThread != null && requestThread.isAlive()) {
                requestThread.interrupt();
            }
            final ResponseInterface ri = this;
            requestThread = new Thread() {
                @Override
                public void run() {
                    try {
                        RequestBuilder.getRequestedPoints(count, ri);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (KeyManagementException e) {
                        e.printStackTrace();
                    }
                }
            };
            requestThread.start();
        } else {
            showToast(getResources().getString(R.string.network_error));
        }

    }

    @Override
    public void successServerResponse(ServerResponse response) {
        Log.d("serverResponse is", String.valueOf(response.result));
    }

    @Override
    public void busyErrorServerResponse(ServerResponse response) {
        //todo show alerdialog
    }

    @Override
    public void wrongParamsServerResponse(ServerResponse response) {
        //todo show alerdialog
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void showToast(final String text) {
        final Toast toast = Toast.makeText(getApplicationContext(),
                text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toast.show();
            }
        });
    }

}
