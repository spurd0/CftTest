package com.spudesc.cfttest.loaders;

import android.content.Context;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.spudesc.cfttest.data.ServerResponse;
import com.spudesc.cfttest.tasks.RequestBuilder;
import com.spudesc.cfttest.tasks.RequestPointsAsyncTask;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Roman Babenko (roman.babenko@sibers.com) on 9/7/2016.
 */
public class PointsLoader extends Loader<ServerResponse> implements RequestPointsAsyncTask.RequestPointsInterface {
    static final String TAG = "PointsLoader";
    public static final String ARGS_COUNT_KEY = "count";
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
        Log.d(TAG, "onCreate " + count);
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        Log.d(TAG, "onForceLoad " + count);
        try {
            RequestPointsAsyncTask task = new RequestPointsAsyncTask(this);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, RequestBuilder.getRequestPointsParams(count));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deliverResult(ServerResponse data) {
        Log.d(TAG, "deliverResult");
        super.deliverResult(data);
    }
}
