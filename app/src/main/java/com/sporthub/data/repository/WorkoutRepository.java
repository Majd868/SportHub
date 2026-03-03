package com.sporthub.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sporthub.data.local.AppDatabase;
import com.sporthub.data.local.WorkoutDao;
import com.sporthub.data.model.Workout;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorkoutRepository {
    private WorkoutDao workoutDao;
    private LiveData<List<Workout>> allWorkouts;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private ExecutorService executorService;
    
    public WorkoutRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        workoutDao = database.workoutDao();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        executorService = Executors.newSingleThreadExecutor();
        
        if (auth.getCurrentUser() != null) {
            allWorkouts = workoutDao.getAllWorkouts(auth.getCurrentUser().getUid());
        }
    }
    
    public void insert(Workout workout) {
        executorService.execute(() -> {
            workout.setUserId(auth.getCurrentUser().getUid());
            workoutDao.insert(workout);
            syncToFirebase(workout);
        });
    }
    
    public void update(Workout workout) {
        executorService.execute(() -> {
            workoutDao.update(workout);
            syncToFirebase(workout);
        });
    }
    
    public void delete(Workout workout) {
        executorService.execute(() -> {
            workoutDao.delete(workout);
            if (workout.isSynced()) {
                firestore.collection("workouts")
                        .document(String.valueOf(workout.getId()))
                        .delete();
            }
        });
    }
    
    public LiveData<List<Workout>> getAllWorkouts() {
        return allWorkouts;
    }
    
    private void syncToFirebase(Workout workout) {
        if (auth.getCurrentUser() != null) {
            firestore.collection("workouts")
                    .document(String.valueOf(workout.getId()))
                    .set(workout)
                    .addOnSuccessListener(aVoid -> {
                        workout.setSynced(true);
                        workoutDao.update(workout);
                    });
        }
    }
    
    public void syncUnsyncedWorkouts() {
        executorService.execute(() -> {
            if (auth.getCurrentUser() != null) {
                List<Workout> unsyncedWorkouts = workoutDao.getUnsyncedWorkouts(
                        auth.getCurrentUser().getUid()
                );
                for (Workout workout : unsyncedWorkouts) {
                    syncToFirebase(workout);
                }
            }
        });
    }
}
