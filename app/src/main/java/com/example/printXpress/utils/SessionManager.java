package com.example.printXpress.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "PrintXpressSession";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void createSession(String name, String email, String role) {
        editor.putBoolean("isLoggedIn", true);
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("role", role);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    public String getName() {
        return sharedPreferences.getString("name", "");
    }

    public String getEmail() {
        return sharedPreferences.getString("email", "");
    }

    public String getRole() {
        return sharedPreferences.getString("role", "");
    }

    public void updateAddress(String address) {
        editor.putString("address", address);
        editor.apply();
    }

    public void updateProfile(String name, String phone) {
        editor.putString("name", name);
        editor.putString("phone", phone);
        editor.apply();
    }

    public String getPhone() {
        return sharedPreferences.getString("phone", "");
    }

    public String getAddress() {
        return sharedPreferences.getString("address", "");
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
