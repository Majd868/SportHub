package com.sporthub.ui.premium;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.sporthub.R;
import com.sporthub.databinding.ActivityPremiumBinding;

public class PremiumActivity extends AppCompatActivity {

    private ActivityPremiumBinding binding;
    private boolean isAnnual = false;

    // بيانات الميزات
    private static final String[][] FEATURES = {
        {"🏋️", "خطط تمرين احترافية", "خطط مخصصة حسب هدفك من محترفين رياضيين"},
        {"🥗", "جداول تغذية ذكية",    "نظام غذائي محسوب حسب وزنك وهدفك"},
        {"📊", "إحصائيات تفصيلية",    "تقارير أسبوعية وشهرية لتقدمك الرياضي"},
        {"🔔", "تنبيهات تمرين ذكية", "يذكّرك بمواعيد تمرينك ويتابع التزامك"},
        {"🎯", "أهداف ومتابعة يومية", "حدد أهدافك وتابع إنجازاتها يوماً بيوم"},
        {"💰", "خصم 15% على المتجر",  "على جميع منتجات المتجر لمشتركي Premium"},
        {"🚫", "بدون إعلانات",         "تجربة نظيفة خالية من أي إعلانات"},
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPremiumBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backButton.setOnClickListener(v -> onBackPressed());

        setupFeatures();
        setupPlanSelection();
        setupSubscribeButton();
    }

    private void setupFeatures() {
        // نربط كل include بمحتواه
        int[] featureIds = {
            R.id.feat1, R.id.feat2, R.id.feat3,
            R.id.feat4, R.id.feat5, R.id.feat6, R.id.feat7
        };
        for (int i = 0; i < featureIds.length && i < FEATURES.length; i++) {
            View featureView = binding.getRoot().findViewById(featureIds[i]);
            if (featureView == null) continue;
            TextView icon  = featureView.findViewById(R.id.feature_icon);
            TextView title = featureView.findViewById(R.id.feature_title);
            TextView desc  = featureView.findViewById(R.id.feature_desc);
            if (icon  != null) icon.setText(FEATURES[i][0]);
            if (title != null) title.setText(FEATURES[i][1]);
            if (desc  != null) desc.setText(FEATURES[i][2]);
        }
    }

    private void setupPlanSelection() {
        // اختيار الخطة
        binding.planMonthly.setOnClickListener(v -> {
            isAnnual = false;
            binding.radioMonthly.setChecked(true);
            binding.radioAnnual.setChecked(false);
        });
        binding.planAnnual.setOnClickListener(v -> {
            isAnnual = true;
            binding.radioAnnual.setChecked(true);
            binding.radioMonthly.setChecked(false);
        });
        binding.radioMonthly.setOnClickListener(v -> binding.planMonthly.performClick());
        binding.radioAnnual.setOnClickListener(v  -> binding.planAnnual.performClick());
    }

    private void setupSubscribeButton() {
        binding.subscribeButton.setOnClickListener(v -> {
            String plan  = isAnnual ? "السنوية (199.99 ريال)" : "الشهرية (29.99 ريال)";
            new AlertDialog.Builder(this)
                    .setTitle("👑 تأكيد الاشتراك")
                    .setMessage("ستشترك في الخطة " + plan + ".\n\nهذه ميزة قيد التطوير حالياً وستُطلق قريباً!")
                    .setPositiveButton("حسناً، سأنتظر!", (d, w) ->
                            Toast.makeText(this,
                                    "شكراً لاهتمامك! سيُفعَّل Premium قريباً 🚀",
                                    Toast.LENGTH_LONG).show())
                    .setNegativeButton("إلغاء", null)
                    .show();
        });
    }
}
