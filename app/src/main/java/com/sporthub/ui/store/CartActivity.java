package com.sporthub.ui.store;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.sporthub.adapter.CartAdapter;
import com.sporthub.data.local.AppDatabase;
import com.sporthub.data.local.CartDao;
import com.sporthub.data.model.CartItem;
import com.sporthub.databinding.ActivityCartBinding;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class CartActivity extends AppCompatActivity {

    private ActivityCartBinding binding;
    private CartAdapter adapter;
    private CartDao cartDao;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(this, "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cartDao = AppDatabase.getInstance(this).cartDao();

        binding.backButton.setOnClickListener(v -> onBackPressed());
        setupAdapter();
        loadCartItems();
    }

    private void setupAdapter() {
        adapter = new CartAdapter();

        // حذف المنتج
        adapter.setOnCartItemClickListener(item ->
                Executors.newSingleThreadExecutor().execute(() -> {
                    cartDao.delete(item);
                    loadCartItems();
                }));

        // تغيير الكمية
        adapter.setOnQuantityChangeListener(new CartAdapter.OnQuantityChangeListener() {
            @Override
            public void onIncrease(CartItem item) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    item.setQuantity(item.getQuantity() + 1);
                    cartDao.update(item);
                    loadCartItems();
                });
            }

            @Override
            public void onDecrease(CartItem item) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    if (item.getQuantity() > 1) {
                        item.setQuantity(item.getQuantity() - 1);
                        cartDao.update(item);
                    } else {
                        cartDao.delete(item);
                    }
                    loadCartItems();
                });
            }
        });

        binding.cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.cartRecyclerView.setAdapter(adapter);

        binding.checkoutButton.setOnClickListener(v -> {
            if (adapter.getCurrentList().isEmpty()) {
                Toast.makeText(this, "السلة فارغة", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, CheckoutActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems(); // يُحدَّث عند العودة من الـ checkout
    }

    private void loadCartItems() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<CartItem> items = cartDao.getCartItemsSync(userId);
            double total = 0;
            for (CartItem item : items) total += item.getTotalPrice();
            final double finalTotal = total;

            runOnUiThread(() -> {
                adapter.submitList(new java.util.ArrayList<>(items)); // force DiffUtil
                if (items.isEmpty()) {
                    binding.emptyCartView.setVisibility(View.VISIBLE);
                    binding.cartRecyclerView.setVisibility(View.GONE);
                    binding.cartBottomBar.setVisibility(View.GONE);
                } else {
                    binding.emptyCartView.setVisibility(View.GONE);
                    binding.cartRecyclerView.setVisibility(View.VISIBLE);
                    binding.cartBottomBar.setVisibility(View.VISIBLE);
                    binding.totalPrice.setText(String.format(Locale.US, "%.2f ريال", finalTotal));
                }
            });
        });
    }
}
