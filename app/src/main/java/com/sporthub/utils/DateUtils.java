package com.sporthub.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final String DATE_PATTERN = "dd/MM/yyyy";
    private static final String TIME_PATTERN = "HH:mm";
    private static final String DATE_TIME_PATTERN = "dd/MM/yyyy HH:mm";

    private DateUtils() {
    }
    
    public static String formatDate(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat(DATE_PATTERN, Locale.getDefault()).format(date);
    }
    
    public static String formatTime(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat(TIME_PATTERN, Locale.getDefault()).format(date);
    }
    
    public static String formatDateTime(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat(DATE_TIME_PATTERN, Locale.getDefault()).format(date);
    }
    
    public static String getRelativeTime(Date date) {
        if (date == null) return "";
        
        long diff = new Date().getTime() - date.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (seconds < 60) {
            return "الآن";
        } else if (minutes < 60) {
            return minutes + " دقيقة";
        } else if (hours < 24) {
            return hours + " ساعة";
        } else if (days < 7) {
            return days + " يوم";
        } else {
            return formatDate(date);
        }
    }
}
