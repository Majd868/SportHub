package com.sporthub.ui.workout;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.textfield.TextInputEditText;
import com.sporthub.R;
import com.sporthub.adapter.WorkoutAdapter;
import com.sporthub.data.model.Workout;
import com.sporthub.databinding.FragmentWorkoutBinding;

public class WorkoutFragment extends Fragment {
    private FragmentWorkoutBinding binding;
    private WorkoutViewModel viewModel;
    private WorkoutAdapter adapter;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentWorkoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(WorkoutViewModel.class);
        
        // Set up RecyclerView
        adapter = new WorkoutAdapter();
        binding.workoutsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.workoutsRecyclerView.setAdapter(adapter);
        
        // Observe workouts
        viewModel.getAllWorkouts().observe(getViewLifecycleOwner(), workouts -> {
            if (workouts != null && !workouts.isEmpty()) {
                adapter.setWorkouts(workouts);
                binding.emptyView.setVisibility(View.GONE);
                binding.workoutsRecyclerView.setVisibility(View.VISIBLE);
            } else {
                binding.emptyView.setVisibility(View.VISIBLE);
                binding.workoutsRecyclerView.setVisibility(View.GONE);
            }
        });
        
        // FAB click listener
        binding.fabAddWorkout.setOnClickListener(v -> showAddWorkoutDialog());
        
        // Sync workouts
        viewModel.syncWorkouts();
    }
    
    private void showAddWorkoutDialog() {
        Dialog dialog = new Dialog(requireContext());
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_workout, null);
        dialog.setContentView(dialogView);
        
        TextInputEditText exerciseName = dialogView.findViewById(R.id.exercise_name_input);
        TextInputEditText sets = dialogView.findViewById(R.id.sets_input);
        TextInputEditText reps = dialogView.findViewById(R.id.reps_input);
        
        dialogView.findViewById(R.id.save_button).setOnClickListener(v -> {
            String name = exerciseName.getText().toString().trim();
            String setsStr = sets.getText().toString().trim();
            String repsStr = reps.getText().toString().trim();
            
            if (name.isEmpty() || setsStr.isEmpty() || repsStr.isEmpty()) {
                Toast.makeText(requireContext(), "يرجى ملء جميع الحقول", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Workout workout = new Workout(
                    name, 
                    "عام",
                    Integer.parseInt(setsStr),
                    Integer.parseInt(repsStr),
                    0
            );
            workout.setCalories(100); // Calculate based on exercise
            
            viewModel.insert(workout);
            dialog.dismiss();
            Toast.makeText(requireContext(), "تم إضافة التمرين", Toast.LENGTH_SHORT).show();
        });
        
        dialogView.findViewById(R.id.cancel_button).setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
