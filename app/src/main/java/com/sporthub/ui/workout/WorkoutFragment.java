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
        adapter.setOnWorkoutClickListener(new WorkoutAdapter.OnWorkoutClickListener() {
            @Override
            public void onWorkoutClick(com.sporthub.data.model.Workout workout) {
                // يمكن فتح تفاصيل التمرين لاحقاً
            }

            @Override
            public void onWorkoutLongClick(com.sporthub.data.model.Workout workout) {
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("حذف التمرين")
                        .setMessage("هل تريد حذف تمرين \"" + workout.getExerciseName() + "\"؟")
                        .setPositiveButton("حذف", (dialog, which) -> viewModel.delete(workout))
                        .setNegativeButton("إلغاء", null)
                        .show();
            }
        });
        binding.workoutsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.workoutsRecyclerView.setAdapter(adapter);

        // Observe workouts
        viewModel.getAllWorkouts().observe(getViewLifecycleOwner(), workouts -> {
            if (workouts != null && !workouts.isEmpty()) {
                adapter.submitList(workouts);
                binding.emptyView.setVisibility(View.GONE);
                binding.workoutsRecyclerView.setVisibility(View.VISIBLE);
            } else {
                adapter.submitList(null);
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
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_workout, null);
        dialog.setContentView(dialogView);

        // Wider dialog
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.92f),
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextInputEditText exerciseName = dialogView.findViewById(R.id.exercise_name_input);
        TextInputEditText sets         = dialogView.findViewById(R.id.sets_input);
        TextInputEditText reps         = dialogView.findViewById(R.id.reps_input);
        TextInputEditText weight       = dialogView.findViewById(R.id.weight_input);
        TextInputEditText notes        = dialogView.findViewById(R.id.notes_input);

        dialogView.findViewById(R.id.save_button).setOnClickListener(v -> {
            CharSequence nameCs   = exerciseName.getText();
            CharSequence setsCs   = sets.getText();
            CharSequence repsCs   = reps.getText();
            CharSequence weightCs = weight.getText();
            CharSequence notesCs  = notes.getText();

            String name      = nameCs   != null ? nameCs.toString().trim()   : "";
            String setsStr   = setsCs   != null ? setsCs.toString().trim()   : "";
            String repsStr   = repsCs   != null ? repsCs.toString().trim()   : "";
            String weightStr = weightCs != null ? weightCs.toString().trim() : "";
            String notesStr  = notesCs  != null ? notesCs.toString().trim()  : "";

            if (name.isEmpty()) {
                exerciseName.setError("أدخل اسم التمرين");
                exerciseName.requestFocus();
                return;
            }
            if (setsStr.isEmpty() || repsStr.isEmpty()) {
                Toast.makeText(requireContext(), "يرجى ملء المجموعات والتكرارات", Toast.LENGTH_SHORT).show();
                return;
            }

            int setsCount, repsCount;
            double weightVal = 0;
            try {
                setsCount = Integer.parseInt(setsStr);
                repsCount = Integer.parseInt(repsStr);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "يرجى إدخال أرقام صحيحة للمجموعات والتكرارات", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!weightStr.isEmpty()) {
                try {
                    weightVal = Double.parseDouble(weightStr);
                } catch (NumberFormatException ignored) { /* نتجاهل إذا كانت قيمة غير صالحة */ }
            }

            if (setsCount <= 0 || repsCount <= 0) {
                Toast.makeText(requireContext(), "يجب أن تكون المجموعات والتكرارات أكبر من صفر", Toast.LENGTH_SHORT).show();
                return;
            }

            Workout workout = new Workout(name, "عام", setsCount, repsCount, weightVal);
            workout.setCalories(setsCount * repsCount * 5);
            if (!notesStr.isEmpty()) {
                workout.setNotes(notesStr);
            }

            viewModel.insert(workout);
            dialog.dismiss();
            Toast.makeText(requireContext(), "✓ تم إضافة التمرين بنجاح", Toast.LENGTH_SHORT).show();
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
