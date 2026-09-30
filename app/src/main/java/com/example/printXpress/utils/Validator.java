package com.example.printXpress.utils;

import android.util.Patterns;

public class Validator {

    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^[0-9]{10}$");
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isPositiveNumber(String value) {
        try {
            return Integer.parseInt(value) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isPositivePrice(String value) {
        try {
            return Double.parseDouble(value) > 0;
        } catch (Exception e) {
            return false;
        }
    }
}