package com.spudesc.cfttest;

import android.Manifest;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.spudesc.cfttest.data.Point;
import com.spudesc.cfttest.data.Response;
import com.spudesc.cfttest.data.ServerResponse;
import com.spudesc.cfttest.fragments.DialogFragment;
import com.spudesc.cfttest.fragments.RequestFragment;
import com.spudesc.cfttest.fragments.ResponseFragment;
import com.spudesc.cfttest.interfaces.ChartInterface;
import com.spudesc.cfttest.loaders.PointsAsyncLoader;
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

    private static final String TAG = "MainActivity";
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 1;
    private static final int LOADER_POINTS_ID = 1;
    private static final String RESULT_HANDLED_KEY = "keyHandled";
    private static final String DIALOG_TAG = "dialog_tag";
    private static final int SERVER_RESPONSE_ID = 1;
    private View mChart;
    private String mImagePath;
    private boolean mCapturingGraph;
    private boolean mResultHandled;
    private Handler mHandler;
    private ServerResponse mServerResponse;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            Log.d(TAG, "savedInstanceState == null");
            showRequestFragment();
        } else {
            mResultHandled = savedInstanceState.getBoolean(RESULT_HANDLED_KEY);
        }
        getLoaderManager().initLoader(LOADER_POINTS_ID, null, this);

        mImagePath = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getPath() + File.separatorChar +
                getResources().getString(R.string.app_name) + File.separatorChar;

        mHandler = new Handler()  {
            @Override
            public void handleMessage (Message msg) {
                if (msg.what == SERVER_RESPONSE_ID) {
                    handleServerAnswer();
                }
            }
        };
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

    @Override
    public void requestFragmentCreated(int id) {
        requestFragment = (RequestFragment) getFragmentManager().findFragmentById(id);
    }

    private void sendRequest(int count) {
        mResultHandled = false;
        Bundle args = new Bundle();
        args.putInt(PointsAsyncLoader.ARGS_COUNT_KEY, count);
        Loader<ServerResponse> loader = getLoaderManager().restartLoader(LOADER_POINTS_ID, args,
                this);
        loader.forceLoad();
    }


    public void successServerResponse() {
        if (requestFragment != null) {
            requestFragment.setViews(false);
        }
        showResponseFragment(mServerResponse.response);
    }


    public void serverIsBusyResponse() {
        if (requestFragment != null) {
            requestFragment.setViews(false);
        }
        showAd(getResources().getString(R.string.server_busy));
    }


    public void wrongParamsServerResponse() {
        if (requestFragment != null) {
            requestFragment.setViews(false);
        }
        if (requestFragment != null && requestFragment.isVisible()) {
            requestFragment.showParamsError(getResources().getString(R.string.wrong_params));
        }
    }


    public void serverErrorResponse() {
        if (requestFragment != null) {
            requestFragment.setViews(false);
        }
        if (mServerResponse == null) {
            showAd(getResources().getString(R.string.server_error));
        } else {
            byte[] data = Base64.decode(mServerResponse.response.message, Base64.DEFAULT);
            try {
                showAd(new String(data, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            };
        }
    }

    private void handleServerAnswer() {
        mResultHandled = true;
        if (mServerResponse != null) {
            int result = mServerResponse.result;
            switch (result) {
                case 0: {
                    successServerResponse();
                    break;
                }
                case -1: {
                    serverIsBusyResponse();
                    break;
                }
                case -100: {
                    wrongParamsServerResponse();
                    break;
                }
                default: {
                    serverErrorResponse();
                    break;
                }
            }
        } else {
            serverErrorResponse();
        }
    }

    public void showAd(final String text) {
        DialogFragment dialogFragment = DialogFragment.newInstance(text);
        dialogFragment.show(getFragmentManager(), DIALOG_TAG);
    }

    private void showRequestFragment() {
        requestFragment = new RequestFragment();

        getFragmentManager().beginTransaction()
                .replace(R.id.main_layout, requestFragment)
                .commit();
    }

    private void showResponseFragment(Response response) {
        ResponseFragment responseFragment;
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
        Log.d(TAG, "onCreateLoader");
        Loader<ServerResponse> loader = null;
        if (id == LOADER_POINTS_ID) {
            loader = new PointsAsyncLoader(this, args);
            Log.d(TAG, "onCreateLoader: " + loader.hashCode());
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader loader, ServerResponse data) {
        Log.d(TAG, "onLoadFinished");
        mServerResponse = data;
        if (!mResultHandled) {
            mHandler.sendEmptyMessage(SERVER_RESPONSE_ID);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(RESULT_HANDLED_KEY, mResultHandled);
    }
}

