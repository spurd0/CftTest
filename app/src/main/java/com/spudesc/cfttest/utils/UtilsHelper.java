package com.spudesc.cfttest.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Roman Babenko (roman.babenko@sibers.com) on 9/7/2016.
 */
public class UtilsHelper {

    public static boolean checkPermission(String permission, Activity activity) {
        int res = activity.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public static void showToast(final String text, Activity activity) {
        final Toast toast = Toast.makeText(activity,
                text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toast.show();
            }
        });
    }

    public static boolean isNetworkConnected(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
