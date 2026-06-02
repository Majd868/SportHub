package com.sporthub.ui.splash;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.sporthub.MainActivity;
import com.sporthub.databinding.ActivitySplashBinding;
import com.sporthub.ui.auth.LoginActivity;

 public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // إخفاء شريط الحالة لتجربة immersive
        getWindow().getDecorView().setSystemUiVisibility(
                 View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        startAnimation();
    }

    private void startAnimation() {
        // انيميشن اللوجو — يظهر مع scale من الأصغر
        ObjectAnimator logoFade = ObjectAnimator.ofFloat(binding.splashLogo, "alpha", 0f, 1f);
        ObjectAnimator logoScale = ObjectAnimator.ofFloat(binding.splashLogo, "scaleX", 0.5f, 1f);
        ObjectAnimator logoScaleY = ObjectAnimator.ofFloat(binding.splashLogo, "scaleY", 0.5f, 1f);

         AnimatorSet logoAnim = new AnimatorSet();
        logoAnim.playTogether(logoFade, logoScale, logoScaleY);
        logoAnim.setDuration(700);
        logoAnim.setInterpolator(new DecelerateInterpolator());

        // انيميشن النص — يظهر بعد اللوجو
        ObjectAnimator nameFade = ObjectAnimator.ofFloat(binding.splashAppName, "alpha", 0f, 1f);
        ObjectAnimator nameTranslate = ObjectAnimator.ofFloat(binding.splashAppName, "translationY", 30f, 0f);

        AnimatorSet nameAnim = new AnimatorSet();
        nameAnim.playTogether(nameFade, nameTranslate);
        nameAnim.setDuration(500);
        nameAnim.setStartDelay(400);

        // انيميشن الـ tagline
        ObjectAnimator tagFade = ObjectAnimator.ofFloat(binding.splashTagline, "alpha", 0f, 1f);
        tagFade.setDuration(400);
        tagFade.setStartDelay(700);

        // انيميشن شريط التحميل
        ObjectAnimator progressFade = ObjectAnimator.ofFloat(binding.splashProgress, "alpha", 0f, 1f);
        progressFade.setDuration(400);
        progressFade.setStartDelay(900);

        // شغّل كل الانيميشنات
        AnimatorSet allAnim = new AnimatorSet();
        allAnim.playTogether(logoAnim, nameAnim, tagFade, progressFade);
        allAnim.start();

        // الانتقال بعد 2.5 ثانية
        binding.splashLogo.postDelayed(this::navigateNext, 2500);
    }

    private void navigateNext() {
        Intent intent;
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
