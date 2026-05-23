package com.sporthub.ui.store;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sporthub.MainActivity;
import com.sporthub.data.local.AppDatabase;
import com.sporthub.utils.FirebaseErrorHelper;
import com.sporthub.data.local.CartDao;
import com.sporthub.data.model.CartItem;
import com.sporthub.data.model.Order;
import com.sporthub.databinding.ActivityCheckoutBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class CheckoutActivity extends AppCompatActivity {

    private ActivityCheckoutBinding binding;
    private CartDao cartDao;
    private String userId;
    private List<CartItem> cartItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId  = FirebaseAuth.getInstance().getCurrentUser() != null
                  ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        cartDao = AppDatabase.getInstance(this).cartDao();

        if (userId == null) { finish(); return; }

        binding.backButton.setOnClickListener(v -> onBackPressed());
        loadSummary();
        binding.confirmOrderButton.setOnClickListener(v -> validateAndConfirm());
    }

    private void loadSummary() {
        Executors.newSingleThreadExecutor().execute(() -> {
            cartItems = cartDao.getCartItemsSync(userId);
            double total = 0;
            for (CartItem item : cartItems) total += item.getTotalPrice();
            final double finalTotal = total;
            final int count = cartItems.size();
            runOnUiThread(() -> {
                binding.summaryItemsCount.setText(count + " منتج");
                binding.summaryTotal.setText(String.format(Locale.US, "%.2f ريال", finalTotal));
            });
        });
    }

    private void validateAndConfirm() {
        String fullName = getText(binding.fullNameInput);
        String phone    = getText(binding.phoneInput);
        String address  = getText(binding.addressInput);

        if (fullName.isEmpty()) {
            binding.fullNameInput.setError("أدخل اسمك الكامل");
            binding.fullNameInput.requestFocus();
            return;
        }
        if (phone.isEmpty() || phone.length() < 9) {
            binding.phoneInput.setError("أدخل رقم جوال صحيح");
            binding.phoneInput.requestFocus();
            return;
        }
        if (address.isEmpty()) {
            binding.addressInput.setError("أدخل عنوان التوصيل");
            binding.addressInput.requestFocus();
            return;
        }
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "السلة فارغة", Toast.LENGTH_SHORT).show();
            return;
        }

        placeOrder(fullName, phone, address);
    }

    private void placeOrder(String fullName, String phone, String address) {
        binding.loadingOverlay.setVisibility(View.VISIBLE);
        binding.confirmOrderButton.setEnabled(false);

        double total = 0;
        for (CartItem item : cartItems) total += item.getTotalPrice();

        Order order = new Order(userId, new ArrayList<>(), total);
        order.setShippingAddress(address + " | " + phone + " | " + fullName);
        order.setPaymentMethod("cash_on_delivery");

        FirebaseFirestore.getInstance()
                .collection("orders")
                .add(order)
                .addOnSuccessListener(ref -> {
                    // احذف السلة من Room بعد إتمام الطلب
                    Executors.newSingleThreadExecutor().execute(() -> {
                        cartDao.clearCart(userId);
                        runOnUiThread(this::showSuccessDialog);
                    });
                })
                .addOnFailureListener(e -> {
                    binding.loadingOverlay.setVisibility(View.GONE);
                    binding.confirmOrderButton.setEnabled(true);
                    if (FirebaseErrorHelper.isPermissionDenied(e)) {
                        FirebaseErrorHelper.showPermissionFixDialog(this);
                    } else {
                        Toast.makeText(this,
                                "فشل إرسال الطلب: " + FirebaseErrorHelper.getArabicMessage(e),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showSuccessDialog() {
        binding.loadingOverlay.setVisibility(View.GONE);
        new AlertDialog.Builder(this)
                .setTitle("🎉 تم تأكيد طلبك!")
                .setMessage("شكراً لطلبك! سيتم التواصل معك قريباً لتأكيد موعد التوصيل.")
                .setCancelable(false)
                .setPositiveButton("العودة للرئيسية", (dialog, which) -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .show();
    }

    private String getText(com.google.android.material.textfield.TextInputEditText field) {
        CharSequence cs = field.getText();
        return cs != null ? cs.toString().trim() : "";
    }
}
