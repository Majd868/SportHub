package com.sporthub.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.sporthub.R;
import com.sporthub.data.model.Workout;
import com.sporthub.utils.DateUtils;

public class WorkoutAdapter extends ListAdapter<Workout, WorkoutAdapter.WorkoutViewHolder> {

    public interface OnWorkoutClickListener {
        void onWorkoutClick(Workout workout);
        void onWorkoutLongClick(Workout workout);
    }

    private OnWorkoutClickListener clickListener;

    public WorkoutAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setOnWorkoutClickListener(OnWorkoutClickListener listener) {
        this.clickListener = listener;
    }

    private static final DiffUtil.ItemCallback<Workout> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Workout>() {
                @Override
                public boolean areItemsTheSame(@NonNull Workout oldItem, @NonNull Workout newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Workout oldItem, @NonNull Workout newItem) {
                    return oldItem.getExerciseName().equals(newItem.getExerciseName())
                            && oldItem.getSets() == newItem.getSets()
                            && oldItem.getReps() == newItem.getReps()
                            && oldItem.getCalories() == newItem.getCalories();
                }
            };

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = getItem(position);
        holder.bind(workout, clickListener);
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        private final TextView exerciseName;
        private final TextView workoutDetails;
        private final TextView workoutDate;
        private final TextView workoutCalories;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.exercise_name);
            workoutDetails = itemView.findViewById(R.id.workout_details);
            workoutDate = itemView.findViewById(R.id.workout_date);
            workoutCalories = itemView.findViewById(R.id.workout_calories);
        }

        public void bind(Workout workout, OnWorkoutClickListener listener) {
            exerciseName.setText(workout.getExerciseName());
            workoutDetails.setText(String.format("%d مجموعات × %d تكرارات",
                    workout.getSets(), workout.getReps()));
            workoutDate.setText(DateUtils.getRelativeTime(workout.getDate()));
            workoutCalories.setText(String.format("%d سعرة", workout.getCalories()));

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onWorkoutClick(workout);
            });
            itemView.setOnLongClickListener(v -> {
                if (listener != null) listener.onWorkoutLongClick(workout);
                return true;
            });
        }
    }
}
