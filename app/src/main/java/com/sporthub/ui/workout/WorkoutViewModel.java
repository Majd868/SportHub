package com.sporthub.ui.workout;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.sporthub.data.model.Workout;
import com.sporthub.data.repository.WorkoutRepository;

import java.util.List;

public class WorkoutViewModel extends AndroidViewModel {
    private WorkoutRepository repository;
    private LiveData<List<Workout>> allWorkouts;
    
    public WorkoutViewModel(@NonNull Application application) {
        super(application);
        repository = new WorkoutRepository(application);
        allWorkouts = repository.getAllWorkouts();
    }
    
    public LiveData<List<Workout>> getAllWorkouts() {
        return allWorkouts;
    }
    
    public void insert(Workout workout) {
        repository.insert(workout);
    }
    
    public void update(Workout workout) {
        repository.update(workout);
    }
    
    public void delete(Workout workout) {
        repository.delete(workout);
    }
    
    public void syncWorkouts() {
        repository.syncUnsyncedWorkouts();
    }
}
