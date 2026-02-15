package com.sporthub.ui.premium;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.sporthub.databinding.ActivityPremiumBinding;

public class PremiumActivity extends AppCompatActivity {
    private ActivityPremiumBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPremiumBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Implementation for premium subscription
    }
}
