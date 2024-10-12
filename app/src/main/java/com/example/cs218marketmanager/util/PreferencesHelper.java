package com.example.cs218marketmanager.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {
    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_ID = "userId";

    private final SharedPreferences sharedPreferences;

    public PreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Method to save username
    public void saveUsername(String username) {
        sharedPreferences.edit().putString(KEY_USERNAME, username).apply();
    }

    // Method to get username
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    // Method to save userId
    public void saveUserId(long userId) {
        sharedPreferences.edit().putLong(KEY_USER_ID, userId).apply();
    }

    // Method to get userId
    public long getUserId() {
        return sharedPreferences.getLong(KEY_USER_ID, -1);
    }

    // Optional: Method to clear all stored preferences
    public void clearAll() {
        sharedPreferences.edit().clear().apply();
    }
}
