package com.spudesc.cfttest;

import android.Manifest;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.spudesc.cfttest.data.Point;
import com.spudesc.cfttest.data.Response;
import com.spudesc.cfttest.data.ServerResponse;
import com.spudesc.cfttest.fragments.RequestFragment;
import com.spudesc.cfttest.fragments.ResponseFragment;
import com.spudesc.cfttest.interfaces.ChartInterface;
import com.spudesc.cfttest.loaders.PointsLoader;
import com.spudesc.cfttest.interfaces.RequestInterface;
import com.spudesc.cfttest.utils.UtilsHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements RequestInterface,
        ChartInterface, LoaderManager.LoaderCallbacks<ServerResponse>  {
    private RequestFragment requestFragment;
    private ResponseFragment responseFragment;

    static final String TAG = "MainActivity";

    static final int WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 1;
    static final int LOADER_POINTS_ID = 1;
    private View mChart;
    private String mImagePath;
    private boolean mCapturingGraph;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            showRequestFragment();
        }
        getLoaderManager().initLoader(LOADER_POINTS_ID, null, this);
        mImagePath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getPath() + File.separatorChar +
                getResources().getString(R.string.app_name) + File.separatorChar;
    }

    @Override
    public void requestPoints(final int count) {
        if (UtilsHelper.isNetworkConnected(this)) {
            if (count < 1) {
                requestFragment.showParamsError(getResources().getString(R.string.wrong_params));
                return;
            }
            sendRequest(count);
            requestFragment.setViews(true);
        } else {
            UtilsHelper.showToast(getResources().getString(R.string.network_error), this);
        }
    }

    private void sendRequest(int count) {
        Bundle args = new Bundle();
        args.putInt(PointsLoader.ARGS_COUNT_KEY, count);
        Loader<ServerResponse> loader = getLoaderManager().restartLoader(LOADER_POINTS_ID, args,
                this);
        loader.forceLoad();
    }


    public void successServerResponse(ServerResponse serverResponse) {
        if (requestFragment != null) {
            requestFragment.setViews(false);
        }
        showResponseFragment(serverResponse.response);
    }


    public void serverIsBusyResponse(ServerResponse response) {
        if (requestFragment != null) {
            requestFragment.setViews(false);
        }
        showAd(getResources().getString(R.string.server_busy));
    }


    public void wrongParamsServerResponse(final ServerResponse response) {
        if (requestFragment != null) {
            requestFragment.setViews(false);
        }
        if (requestFragment != null && requestFragment.isVisible()) {
            requestFragment.showParamsError(getResources().getString(R.string.wrong_params));
        }
    }


    public void serverErrorResponse(ServerResponse serverResponse) {
        if (requestFragment != null) {
            requestFragment.setViews(false);
        }
        if (serverResponse == null) {
            showAd(getResources().getString(R.string.server_error));
        } else {
            byte[] data = Base64.decode(serverResponse.response.message, Base64.DEFAULT);
            try {
                showAd(new String(data, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            };
        }
    }

    private void handleServerAnswer(ServerResponse serverResponse) {
        if (serverResponse != null) {
            int result = serverResponse.result;
            switch (result) {
                case 0: {
                    successServerResponse(serverResponse);
                    break;
                }
                case -1: {
                    serverIsBusyResponse(serverResponse);
                    break;
                }
                case -100: {
                    wrongParamsServerResponse(serverResponse);
                    break;
                }
                default: {
                    serverErrorResponse(serverResponse);
                    break;
                }
            }
        } else {
            serverErrorResponse(null);
        }
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

    private void showRequestFragment() {
        requestFragment = new RequestFragment();

        getFragmentManager().beginTransaction()
                .replace(R.id.main_layout, requestFragment)
                .commit();
    }

    private void showResponseFragment(Response response) {
        responseFragment = new ResponseFragment();

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
        Arrays.sort(data);
        return data;
    }

    @Override
    public void saveScreenshotIntent(View chart) {
        this.mChart = chart;
        if (UtilsHelper.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, this)) {
            saveScreenshot();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
            }
        }
    }

    private void saveScreenshot() {
        if (!mCapturingGraph) {
            mCapturingGraph = true;
            Log.d("saveScreenshot", "Is accelerated " + mChart.isHardwareAccelerated());
            Bitmap bitmap = null;
            mChart.setDrawingCacheEnabled(true);
            try {
                bitmap = Bitmap.createBitmap(mChart.getDrawingCache()); // canvas in graphview doesn`t support hardware acceleration
            } catch (IllegalStateException iex) {
                UtilsHelper.showToast(getResources().getString(R.string.error), this);
                mCapturingGraph = false;
                return;
            }
            finally {
                mChart.setDrawingCacheEnabled(false);
            }
            SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");
            Date myDate = new Date();
            String date = timeStampFormat.format(myDate);
            File tempPhoto = new File(mImagePath +
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
            mCapturingGraph = false;
            UtilsHelper.showToast(getResources().getString(R.string.graph_saved), this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: { /// WRITE_EXTERNAL_STORAGE_CODE
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveScreenshot();
                } else {
                    UtilsHelper.showToast(getResources().getString(R.string.permission_strogage_err), this);
                }

            }
        }
    }

    @Override
    public Loader<ServerResponse> onCreateLoader(int id, Bundle args) {
        Loader<ServerResponse> loader = null;
        if (id == LOADER_POINTS_ID) {
            loader = new PointsLoader(this, args);
            Log.d("MainActivity", "onCreateLoader: " + loader.hashCode());
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader loader, ServerResponse data) {
        Log.d(TAG, "onLoadFinished");
        handleServerAnswer(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}

