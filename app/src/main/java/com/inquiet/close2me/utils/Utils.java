package com.inquiet.close2me.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.inquiet.close2me.R;

public class Utils {

    public static boolean LOG_ENABLE = false;

    public static void log(String text) {
        Utils.log("MilkApp", text);
    }

    public static void log(String tag, String text) {
        if(LOG_ENABLE) {
            Log.d(tag, text);
        }
    }

    public static boolean isNetworkAvailable(Context context) {

        boolean HaveConnectedWifi = false;
        boolean HaveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();

        for (NetworkInfo ni : netInfo) {

            if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
                if (ni.isConnected()) {
                    HaveConnectedWifi = true;
                }
            }
            if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (ni.isConnected()) {
                    HaveConnectedMobile = true;
                }
            }

        }

        return HaveConnectedWifi || HaveConnectedMobile;

    }

    public static void showMessage(Context context, String title, String message) {
        Utils.showMessage(context, title, message, context.getString(R.string.accept), null, false);
    }

    public static void showMessage(Context context, String title, String message, String positiveButtonTitle, DialogInterface.OnClickListener listener, boolean cancelable) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setTitle(title)
                .setMessage(message)
                .setCancelable(cancelable)
                .setPositiveButton(positiveButtonTitle, listener);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

}
