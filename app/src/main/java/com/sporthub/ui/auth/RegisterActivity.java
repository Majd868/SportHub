package com.sporthub.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sporthub.MainActivity;
import com.sporthub.data.model.User;
import com.sporthub.data.repository.UserRepository;
import com.sporthub.databinding.ActivityRegisterBinding;
import com.sporthub.utils.ValidationUtils;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private FirebaseAuth auth;
    private UserRepository userRepository;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        auth = FirebaseAuth.getInstance();
        userRepository = new UserRepository();
        
        binding.registerButton.setOnClickListener(v -> register());
        binding.loginText.setOnClickListener(v -> finish());
    }
    
    private void register() {
        String name = binding.nameInput.getText().toString().trim();
        String email = binding.emailInput.getText().toString().trim();
        String password = binding.passwordInput.getText().toString().trim();
        
        // Validate inputs
        if (!ValidationUtils.isValidName(name)) {
            binding.nameLayout.setError("الاسم مطلوب");
            return;
        }
        
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
        binding.nameLayout.setError(null);
        binding.emailLayout.setError(null);
        binding.passwordLayout.setError(null);
        
        // Show progress
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.registerButton.setEnabled(false);
        
        // Create account with Firebase
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.registerButton.setEnabled(true);
                    
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Create user in Firestore
                            User user = new User(firebaseUser.getUid(), email, name);
                            userRepository.createUser(user);
                            
                            Toast.makeText(this, "تم إنشاء الحساب بنجاح", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "فشل إنشاء الحساب: " + task.getException().getMessage(), 
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
