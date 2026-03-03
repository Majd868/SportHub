package com.sporthub.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.sporthub.MainActivity;
import com.sporthub.databinding.ActivityLoginBinding;
import com.sporthub.utils.ValidationUtils;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth auth;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        auth = FirebaseAuth.getInstance();
        
        binding.loginButton.setOnClickListener(v -> login());
        binding.registerText.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
    
    private void login() {
        String email = binding.emailInput.getText().toString().trim();
        String password = binding.passwordInput.getText().toString().trim();
        
        // Validate inputs
        String emailError = ValidationUtils.getEmailError(email);
        if (emailError != null) {
            binding.emailLayout.setError(emailError);
            return;
        }
        
        String passwordError = ValidationUtils.getPasswordError(password);
        if (passwordError != null) {
            binding.passwordLayout.setError(passwordError);
            return;
        }
        
        // Clear errors
        binding.emailLayout.setError(null);
        binding.passwordLayout.setError(null);
        
        // Show progress
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.loginButton.setEnabled(false);
        
        // Sign in with Firebase
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.loginButton.setEnabled(true);
                    
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "فشل تسجيل الدخول: " + task.getException().getMessage(), 
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
