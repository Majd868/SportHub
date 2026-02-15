package com.sporthub.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sporthub.R;
import com.sporthub.data.model.Workout;
import com.sporthub.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    private List<Workout> workouts = new ArrayList<>();
    
    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false);
        return new WorkoutViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = workouts.get(position);
        holder.bind(workout);
    }
    
    @Override
    public int getItemCount() {
        return workouts.size();
    }
    
    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        private TextView exerciseName;
        private TextView workoutDetails;
        private TextView workoutDate;
        private TextView workoutCalories;
        
        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exercise_name);
            workoutDetails = itemView.findViewById(R.id.workout_details);
            workoutDate = itemView.findViewById(R.id.workout_date);
            workoutCalories = itemView.findViewById(R.id.workout_calories);
        }
        
        public void bind(Workout workout) {
            exerciseName.setText(workout.getExerciseName());
            workoutDetails.setText(String.format("%d مجموعات × %d تكرارات", 
                    workout.getSets(), workout.getReps()));
            workoutDate.setText(DateUtils.getRelativeTime(workout.getDate()));
            workoutCalories.setText(String.format("%d سعرة", workout.getCalories()));
        }
    }
}
