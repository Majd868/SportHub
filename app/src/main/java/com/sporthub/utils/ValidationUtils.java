package com.sporthub.utils;

import android.util.Patterns;

public class ValidationUtils {
    
    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
    
    public static boolean isValidName(String name) {
        return name != null && name.trim().length() >= 2;
    }
    
    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.length() >= 10;
    }
    
    public static String getPasswordError(String password) {
        if (password == null || password.isEmpty()) {
            return "كلمة المرور مطلوبة";
        }
        if (password.length() < 6) {
            return "كلمة المرور يجب أن تكون 6 أحرف على الأقل";
        }
        return null;
    }
    
    public static String getEmailError(String email) {
        if (email == null || email.isEmpty()) {
            return "البريد الإلكتروني مطلوب";
        }
        if (!isValidEmail(email)) {
            return "البريد الإلكتروني غير صحيح";
        }
        return null;
    }
}
