package com.sporthub.ui.seller;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.sporthub.databinding.ActivitySellerDashboardBinding;

public class SellerDashboardActivity extends AppCompatActivity {
    private ActivitySellerDashboardBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySellerDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Implementation for seller dashboard
    }
}
