package com.sporthub.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sporthub.data.local.AppDatabase;
import com.sporthub.data.local.WorkoutDao;
import com.sporthub.data.model.Workout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkoutRepository {
    private final WorkoutDao workoutDao;
    private final LiveData<List<Workout>> allWorkouts;
    private final FirebaseFirestore firestore;
    private final FirebaseAuth auth;
    private final ExecutorService executorService;
    
    public WorkoutRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        workoutDao = database.workoutDao();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        executorService = Executors.newSingleThreadExecutor();
        
        String userId = getCurrentUserId();
        if (userId != null) {
            allWorkouts = workoutDao.getAllWorkouts(userId);
        } else {
            allWorkouts = new MutableLiveData<>(new ArrayList<>());
        }
    }
    
    public void insert(Workout workout) {
        String userId = getCurrentUserId();
        if (userId == null || workout == null) {
            return;
        }
        
        executorService.execute(() -> {
            workout.setUserId(userId);
            long insertedId = workoutDao.insert(workout);
            workout.setId((int) insertedId);
            syncToFirebase(workout);
        });
    }
    
    public void update(Workout workout) {
        String userId = getCurrentUserId();
        if (userId == null || workout == null) {
            return;
        }
        
        executorService.execute(() -> {
            workout.setUserId(userId);
            workoutDao.update(workout);
            syncToFirebase(workout);
        });
    }
    
    public void delete(Workout workout) {
        if (workout == null) {
            return;
        }
        
        executorService.execute(() -> {
            workoutDao.delete(workout);
            if (workout.isSynced()) {
                firestore.collection("workouts")
                        .document(getWorkoutDocumentId(workout))
                        .delete();
            }
        });
    }
    
    public LiveData<List<Workout>> getAllWorkouts() {
        return allWorkouts;
    }
    
    private void syncToFirebase(Workout workout) {
        String userId = getCurrentUserId();
        if (userId == null || workout == null) {
            return;
        }
        
        firestore.collection("workouts")
                .document(getWorkoutDocumentId(workout))
                .set(workout)
                .addOnSuccessListener(aVoid -> executorService.execute(() -> {
                    workout.setSynced(true);
                    workoutDao.update(workout);
                }));
    }
    
    public void syncUnsyncedWorkouts() {
        String userId = getCurrentUserId();
        if (userId == null) {
            return;
        }
        
        executorService.execute(() -> {
            List<Workout> unsyncedWorkouts = workoutDao.getUnsyncedWorkouts(userId);
            for (Workout workout : unsyncedWorkouts) {
                syncToFirebase(workout);
            }
        });
    }
    
    private String getWorkoutDocumentId(Workout workout) {
        String userId = workout.getUserId();
        if (userId == null || userId.trim().isEmpty()) {
            userId = getCurrentUserId();
        }
        if (userId == null || userId.trim().isEmpty()) {
            userId = "unknown_user";
        }
        return userId + "_" + workout.getId();
    }
    
    private String getCurrentUserId() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
    }
}
