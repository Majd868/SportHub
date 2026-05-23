package com.sporthub.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sporthub.data.model.User;

public class UserRepository {
    private static final String TAG = "UserRepository";

    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;
    private final MutableLiveData<User> currentUserLiveData;

    public interface OnSuccessListener {
        void onSuccess();
    }

    public interface OnErrorListener {
        void onError(String error);
    }

    public UserRepository() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        currentUserLiveData = new MutableLiveData<>();
    }

    public LiveData<User> getCurrentUser() {
        if (auth.getCurrentUser() != null) {
            loadUserData(auth.getCurrentUser().getUid());
        }
        return currentUserLiveData;
    }

    public void loadUserData(String userId) {
        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        currentUserLiveData.setValue(user);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "فشل تحميل بيانات المستخدم: " + e.getMessage()));
    }

    public void createUser(User user, OnSuccessListener onSuccess, OnErrorListener onError) {
        firestore.collection("users")
                .document(user.getUserId())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    if (onSuccess != null) onSuccess.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "فشل إنشاء المستخدم: " + e.getMessage());
                    if (onError != null) onError.onError(e.getMessage());
                });
    }

    public void updateUser(User user) {
        firestore.collection("users")
                .document(user.getUserId())
                .set(user)
                .addOnFailureListener(e -> Log.e(TAG, "فشل تحديث المستخدم: " + e.getMessage()));
    }

    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public String getCurrentUserId() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
    }
}
