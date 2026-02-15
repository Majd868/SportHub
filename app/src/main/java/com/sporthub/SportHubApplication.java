package com.sporthub;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.sporthub.utils.NotificationHelper;

public class SportHubApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        
        // Create notification channel
        NotificationHelper.createNotificationChannel(this);
    }
}
