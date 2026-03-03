package com.sporthub.ui.progress;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sporthub.data.model.Workout;
import com.sporthub.databinding.FragmentProgressBinding;

import java.util.ArrayList;
import java.util.List;

public class ProgressFragment extends Fragment {
    private FragmentProgressBinding binding;
    private ProgressViewModel viewModel;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProgressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(ProgressViewModel.class);
        
        // Observe workouts and update chart
        viewModel.getAllWorkouts().observe(getViewLifecycleOwner(), workouts -> {
            if (workouts != null && !workouts.isEmpty()) {
                updateChart(workouts);
                updateStats(workouts);
            }
        });
        
        setupChart();
    }
    
    private void setupChart() {
        LineChart chart = binding.workoutChart;
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setTextColor(Color.WHITE);
    }
    
    private void updateChart(List<Workout> workouts) {
        List<Entry> entries = new ArrayList<>();
        
        for (int i = 0; i < Math.min(workouts.size(), 10); i++) {
            Workout workout = workouts.get(i);
            entries.add(new Entry(i, workout.getCalories()));
        }
        
        LineDataSet dataSet = new LineDataSet(entries, "السعرات الحرارية");
        dataSet.setColor(Color.parseColor("#00D9FF"));
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(Color.parseColor("#00D9FF"));
        dataSet.setCircleRadius(4f);
        
        LineData lineData = new LineData(dataSet);
        binding.workoutChart.setData(lineData);
        binding.workoutChart.invalidate();
    }
    
    private void updateStats(List<Workout> workouts) {
        int totalWorkouts = workouts.size();
        int totalCalories = 0;
        
        for (Workout workout : workouts) {
            totalCalories += workout.getCalories();
        }
        
        binding.totalWorkouts.setText(String.valueOf(totalWorkouts));
        binding.totalCalories.setText(String.valueOf(totalCalories));
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
