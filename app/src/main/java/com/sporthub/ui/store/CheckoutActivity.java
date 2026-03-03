package com.sporthub.ui.store;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.sporthub.databinding.ActivityCheckoutBinding;

public class CheckoutActivity extends AppCompatActivity {
    private ActivityCheckoutBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Implementation for checkout
    }
}
