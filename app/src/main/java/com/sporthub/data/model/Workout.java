package com.sporthub.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "workouts")
public class Workout {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String userId;
    private String exerciseName;
    private String category;
    private int sets;
    private int reps;
    private double weight;
    private int duration; // in minutes
    private int calories;
    private Date date;
    private String notes;
    private boolean synced;
    
    public Workout() {
        this.date = new Date();
        this.synced = false;
    }
    
    public Workout(String exerciseName, String category, int sets, int reps, double weight) {
        this.exerciseName = exerciseName;
        this.category = category;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.date = new Date();
        this.synced = false;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public int getSets() { return sets; }
    public void setSets(int sets) { this.sets = sets; }
    
    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }
    
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    
    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }
    
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public boolean isSynced() { return synced; }
    public void setSynced(boolean synced) { this.synced = synced; }
}
