package com.sporthub.ui.orders;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.sporthub.databinding.ActivityMyOrdersBinding;

public class MyOrdersActivity extends AppCompatActivity {
    private ActivityMyOrdersBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Implementation for orders
    }
}
