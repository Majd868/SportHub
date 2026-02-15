package com.sporthub.ui.store;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.sporthub.databinding.ActivityProductDetailBinding;

public class ProductDetailActivity extends AppCompatActivity {
    private ActivityProductDetailBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Implementation for product details
    }
}
