package com.inquiet.close2me.model;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class UserManager {
    public static final String USER_PREF_NAME = "muu.user";
    private static UserManager instance = null;
    private Context context = null;

    private UserManager(Context ctx) {
        context = ctx;
    }

    public static void init(Context ctx) {
        instance = new UserManager(ctx);
    }

    public static UserManager getInstance() {
        return instance;
    }

    public boolean isRegistered() {
        return getCurrentUser() != null;
    }

    public void saveUser(String userId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(USER_PREF_NAME, MODE_PRIVATE).edit();
        editor.putString("userId", userId);
        editor.apply();
    }

    public String getCurrentUser() {
        SharedPreferences prefs = context.getSharedPreferences(USER_PREF_NAME, MODE_PRIVATE);
        return prefs.getString("userId", null);
    }

    public void saveToken(String token) {
        SharedPreferences.Editor editor = context.getSharedPreferences(USER_PREF_NAME, MODE_PRIVATE).edit();
        editor.putString("token", token);
        editor.apply();
    }

    public String getToken() {
        SharedPreferences prefs = context.getSharedPreferences(USER_PREF_NAME, MODE_PRIVATE);
        return prefs.getString("token", null);
    }
}
