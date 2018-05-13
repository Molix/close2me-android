package com.inquiet.close2me.utils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.inquiet.close2me.R;
import com.inquiet.close2me.model.UserManager;

import static android.content.Context.MODE_PRIVATE;

public class PermissionHelper {

    public static final int MILKAAPP_PERMISSION_LOCATION_REQUEST = 102;
    private static final boolean EXPLAIN_PERMISSIONS = true;

    // Location
    public static boolean needsToRequestLocationPermissions(Activity activity) {
        if (Build.VERSION.SDK_INT < 23) {
            return false;
        } else {
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        }
    }

    public static void requestLocationPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, MILKAAPP_PERMISSION_LOCATION_REQUEST);
    }

    public static boolean shouldShowLocationRequestPermissionRationale (Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    // Explain permissions and handle stop asking for request

    public static void notifyUserMustGrantPermissionsFromSettingsIfNeeded(final Activity activity, int requestCode) {
        if(!EXPLAIN_PERMISSIONS) {
            return;
        }
        if(Build.VERSION.SDK_INT >= 23) {
            switch(requestCode) {
                case MILKAAPP_PERMISSION_LOCATION_REQUEST:
                    PermissionHelper.notifyForLocationPermissions(activity);
                    break;
                default:
                    break;
            }
        }
    }

    // Location

    private static void notifyForLocationPermissions(final Activity activity) {

        SharedPreferences prefs = activity.getSharedPreferences(UserManager.USER_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        boolean locationPermissionWasRejectedAtLeastOnce = prefs.getBoolean("locationPermissionWasRejectedAtLeastOnce", false);

        if(!locationPermissionWasRejectedAtLeastOnce) {
            editor.putBoolean("locationPermissionWasRejectedAtLeastOnce", true);
            editor.apply();
        }

        final boolean userCheckStopAsking = !PermissionHelper.shouldShowLocationRequestPermissionRationale(activity) && locationPermissionWasRejectedAtLeastOnce;

        if(userCheckStopAsking ||locationPermissionWasRejectedAtLeastOnce ) {
            String message = userCheckStopAsking ? activity.getString(R.string.location_permissions_explained_stop_ask) : activity.getString(R.string.location_permissions_explained);
            String buttonTitle = userCheckStopAsking ? activity.getString(R.string.accept) : activity.getString(R.string.retry);
            Utils.showMessage(activity, activity.getString(R.string.permissions_needed_title), message, buttonTitle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(!userCheckStopAsking) {
                        requestLocationPermission(activity);
                    }
                }
            }, true);
        }
    }
}

