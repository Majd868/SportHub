package com.sporthub;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.sporthub.databinding.ActivityMainBinding;
import com.sporthub.ui.auth.LoginActivity;
import com.sporthub.ui.profile.ProfileFragment;
import com.sporthub.ui.progress.ProgressFragment;
import com.sporthub.ui.store.StoreFragment;
import com.sporthub.ui.workout.WorkoutFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth auth;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        auth = FirebaseAuth.getInstance();
        
        // Check if user is logged in
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Set up bottom navigation
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            
            if (itemId == R.id.navigation_workouts) {
                selectedFragment = new WorkoutFragment();
            } else if (itemId == R.id.navigation_store) {
                selectedFragment = new StoreFragment();
            } else if (itemId == R.id.navigation_progress) {
                selectedFragment = new ProgressFragment();
            } else if (itemId == R.id.navigation_profile) {
                selectedFragment = new ProfileFragment();
            }
            
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });
        
        // Load default fragment
        if (savedInstanceState == null) {
            binding.bottomNavigation.setSelectedItemId(R.id.navigation_workouts);
        }
    }
}
