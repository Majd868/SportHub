package com.sporthub.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.sporthub.data.model.Workout;

import java.util.List;

@Dao
public interface WorkoutDao {
    @Insert
    long insert(Workout workout);
    
    @Update
    void update(Workout workout);
    
    @Delete
    void delete(Workout workout);
    
    @Query("SELECT * FROM workouts WHERE userId = :userId ORDER BY date DESC")
    LiveData<List<Workout>> getAllWorkouts(String userId);
    
    @Query("SELECT * FROM workouts WHERE userId = :userId AND synced = 0")
    List<Workout> getUnsyncedWorkouts(String userId);
    
    @Query("SELECT * FROM workouts WHERE id = :id")
    LiveData<Workout> getWorkoutById(int id);
    
    @Query("DELETE FROM workouts WHERE userId = :userId")
    void deleteAllWorkouts(String userId);
    
    @Query("SELECT * FROM workouts WHERE userId = :userId AND date >= :startDate AND date <= :endDate")
    LiveData<List<Workout>> getWorkoutsByDateRange(String userId, long startDate, long endDate);
}
