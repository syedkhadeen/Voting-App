package com.example.onlinevotingsystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

public class CredentialManager {
    private static final String PREFS_NAME = "AdminPrefs";
    private static final String KEY_ADMIN_EMAIL = "AdminEmail";
    private static final String KEY_CREDENTIALS = "Credentials";

    public static void saveAdminEmail(Context context, String email) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_ADMIN_EMAIL, email);
        editor.apply();
    }

    public static String getAdminEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_ADMIN_EMAIL, null);  // Return null if no email found
    }

    public static boolean credentialsExist(Context context, String email) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String storedCredentials = prefs.getString(KEY_CREDENTIALS, "");
        return storedCredentials.contains(email);
    }

    public static void saveCredentials(Context context, String email, String password) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String encodedCredentials = Base64.encodeToString((email + ":" + password).getBytes(), Base64.DEFAULT);
        editor.putString(KEY_CREDENTIALS, encodedCredentials);
        editor.apply();
    }
}
