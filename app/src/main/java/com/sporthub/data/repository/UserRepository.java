package com.sporthub.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sporthub.data.model.User;

public class UserRepository {
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private MutableLiveData<User> currentUserLiveData;
    
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
                });
    }
    
    public void createUser(User user) {
        firestore.collection("users")
                .document(user.getUserId())
                .set(user);
    }
    
    public void updateUser(User user) {
        firestore.collection("users")
                .document(user.getUserId())
                .set(user);
    }
    
    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }
    
    public String getCurrentUserId() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
    }
}
