package com.spudesc.cfttest;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.spudesc.cfttest.Data.Point;
import com.spudesc.cfttest.Data.Response;
import com.spudesc.cfttest.Data.ServerResponse;
import com.spudesc.cfttest.Fragments.RequestFragment;
import com.spudesc.cfttest.Fragments.ResponseFragment;
import com.spudesc.cfttest.Interfaces.ChartInterface;
import com.spudesc.cfttest.Interfaces.ServerResponseInterface;
import com.spudesc.cfttest.Tasks.RequestBuilder;
import com.spudesc.cfttest.Interfaces.RequestInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements RequestInterface,
        ServerResponseInterface, ChartInterface {
    Thread requestThread;
    RequestFragment requestFragment;
    int WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 1;
    View chart;
    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showRequestFragment();
        imagePath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getPath() + File.separatorChar +
                getResources().getString(R.string.app_name) + File.separatorChar;
    }

    @Override
    public void requestPoints(final int count) {
        if (isNetworkConnected()) {
            if (count < 1) {
                requestFragment.requestPerformed = false;
                requestFragment.showParamsError(getResources().getString(R.string.wrong_params));
                return;
            }
            if (requestThread != null && requestThread.isAlive()) {
                requestThread.interrupt();
            }
            final ServerResponseInterface ri = this;
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
    public void successServerResponse(ServerResponse serverResponse) {
        if (requestFragment != null) {
            requestFragment.requestPerformed = false;
            requestFragment.setViews(false);
        }
        showResponseFragment(serverResponse.response);
        }

    @Override
    public void busyErrorServerResponse(ServerResponse response) { // Check response, may be @null
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
            requestFragment.showParamsError(getResources().getString(R.string.wrong_params));
        }
    }

    @Override
    public void serverErrorResponse(ServerResponse response) {
        if (requestFragment != null) {
            requestFragment.requestPerformed = false;
            requestFragment.setViews(false);
        }
        if (response == null) {
            showAd(getResources().getString(R.string.server_error));
        } else {
            showAd(response.response.message); //TODO handle russian
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
                .add(R.id.main_layout, requestFragment)
                .commit();
    }

    private void showResponseFragment(Response response){
        ResponseFragment responseFragment = new ResponseFragment();
        responseFragment.setChartInterface(this);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(getResources().getString(R.string.points_array),
                response.points);
        bundle.putParcelableArray(getResources().getString(R.string.sorted_points_array),
                prepareArrayForGraph(response.points));
        responseFragment.setArguments(bundle);

        getFragmentManager().beginTransaction()
                .addToBackStack("request")
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.main_layout, responseFragment)
                .commit();
    }

    private Point[] prepareArrayForGraph(ArrayList<Point> input) {
        Point[] data = new Point[input.size()];
        for (int i = 0; i < input.size(); i++) {
            data[i] = input.get(i);
        }
        quickSorting(data, 0, data.length - 1);
        return data;
    }

    private void quickSorting(Point[] points, int start, int end) {
        if (start >= end)
            return;
        int i = start, j = end;
        int cur = i - (i - j) / 2;
        while (i < j) {
            while (i < cur && (points[i].getX() <= points[cur].getX())) {
                i++;
            }
            while (j > cur && (points[cur].getX() <= points[j].getX())) {
                j--;
            }
            if (i < j) {
                Point temp = points[i];
                points[i] = points[j];
                points[j] = temp;
                if (i == cur)
                    cur = j;
                else if (j == cur)
                    cur = i;
            }
        }
        quickSorting(points, start, cur);
        quickSorting(points, cur+1, end);
    }

    @Override
    public void saveScreenshotIntent(View chart) {
        this.chart = chart;
        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            saveScreenshot();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
            }
        }
    }

    private void saveScreenshot() {
        Log.d("TAG", "Is accelerated " + chart.isHardwareAccelerated());
        Bitmap bitmap;
        chart.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(chart.getDrawingCache());
        chart.setDrawingCacheEnabled(false);
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");
        Date myDate = new Date();
        String date = timeStampFormat.format(myDate);
        File tempPhoto = new File(imagePath +
                date + "." + Bitmap.CompressFormat.JPEG);
        FileOutputStream out = null;
        try {
            new File(tempPhoto.getParent()).mkdirs();
            tempPhoto.createNewFile();
            out = new FileOutputStream(tempPhoto.getPath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        showToast(getResources().getString(R.string.graph_saved));
    }

    boolean checkPermission(String permission)
    {
        int res = checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: { /// WRITE_EXTERNAL_STORAGE_CODE
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveScreenshot();
                } else {
                    showToast(getResources().getString(R.string.permission_strogage_err));
                }

            }
        }
    }

}
