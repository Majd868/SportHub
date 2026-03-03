package com.sporthub.ui.progress;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.sporthub.data.model.Workout;
import com.sporthub.data.repository.WorkoutRepository;

import java.util.List;

public class ProgressViewModel extends AndroidViewModel {
    private WorkoutRepository repository;
    private LiveData<List<Workout>> allWorkouts;
    
    public ProgressViewModel(@NonNull Application application) {
        super(application);
        repository = new WorkoutRepository(application);
        allWorkouts = repository.getAllWorkouts();
    }
    
    public LiveData<List<Workout>> getAllWorkouts() {
        return allWorkouts;
    }
}
