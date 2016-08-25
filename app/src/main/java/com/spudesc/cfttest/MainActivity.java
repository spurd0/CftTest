package com.spudesc.cfttest;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.spudesc.cfttest.Data.ServerResponse;
import com.spudesc.cfttest.Fragments.RequestFragment;
import com.spudesc.cfttest.Fragments.ResponseFragment;
import com.spudesc.cfttest.Interfaces.ResponseInterface;
import com.spudesc.cfttest.Tasks.RequestBuilder;
import com.spudesc.cfttest.Interfaces.RequestInterface;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity implements RequestInterface, ResponseInterface{
    Thread requestThread;
    RequestFragment requestFragment;
    ServerResponse response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showRequestFragment();
    }

    @Override
    public void requestPoints(final int count) {
        if (isNetworkConnected()) {
            if (count < 1) {
                requestFragment.requestPerformed = false;
                requestFragment.showCountError(getResources().getString(R.string.wrong_params));
                return;
            }
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
            requestFragment.setViews(true);
            requestThread.start();
        } else {
            showToast(getResources().getString(R.string.network_error));
        }

    }

    @Override
    public void cancelRequest() {
        if (requestThread != null && requestThread.isAlive()) requestThread.interrupt();
    }

    @Override
    public void successServerResponse(ServerResponse response) {
        if (requestFragment != null) {
            requestFragment.requestPerformed = false;
            requestFragment.setViews(false);
        }
        this.response = response;
        }

    @Override
    public void busyErrorServerResponse(ServerResponse response) {
        if (requestFragment != null) {
            requestFragment.requestPerformed = false;
            requestFragment.setViews(false);
        }
        showAd(getResources().getString(R.string.server_busy));
    }

    @Override
    public void wrongParamsServerResponse(final ServerResponse response) {
        if (requestFragment != null) {
            requestFragment.requestPerformed = false;
            requestFragment.setViews(false);
        }
        if (requestFragment != null && requestFragment.isVisible()) {
            requestFragment.showCountError(getResources().getString(R.string.wrong_params));
        }
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

    public void showAd(final String text) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getResources().getString(R.string.title_text))
                .setMessage(text)
                .setCancelable(true)
                .setNegativeButton(getResources().getString(R.string.ok_button),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                builder.show();
            }
        });
    }

    private void showRequestFragment(){
        if (requestFragment == null) {
            requestFragment = new RequestFragment();
            requestFragment.setRequestInterface(this);
        }
        getFragmentManager().beginTransaction()
                .replace(R.id.main_layout, requestFragment)
                .commit();


    }

    private void showResponseFragment(){
        ResponseFragment responseFragment = new ResponseFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("pointsArray", response.response.points);
        responseFragment.setArguments(bundle);

        getFragmentManager().beginTransaction()
                .addToBackStack("request")
                .replace(R.id.main_layout, responseFragment)
                .commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
