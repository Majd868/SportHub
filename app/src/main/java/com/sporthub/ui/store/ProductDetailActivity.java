package com.sporthub.ui.store;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.sporthub.data.local.AppDatabase;
import com.sporthub.data.local.CartDao;
import com.sporthub.data.model.CartItem;
import com.sporthub.databinding.ActivityProductDetailBinding;

import java.util.Locale;
import java.util.concurrent.Executors;

public class ProductDetailActivity extends AppCompatActivity {

    private ActivityProductDetailBinding binding;
    private CartDao cartDao;
    private int quantity = 1;
    private double productPrice = 0;
    private String productId, productName, productImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cartDao = AppDatabase.getInstance(this).cartDao();

        productId    = getIntent().getStringExtra("product_id");
        productName  = getIntent().getStringExtra("product_name");
        productPrice = getIntent().getDoubleExtra("product_price", 0.0);
        productImage = getIntent().getStringExtra("product_image");
        String desc  = getIntent().getStringExtra("product_description");

        if (productName == null) {
            Toast.makeText(this, "تعذر تحميل بيانات المنتج", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Toolbar back
        if (binding.toolbar != null) {
            setSupportActionBar(binding.toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("");
            }
        }

        binding.productName.setText(productName);
        binding.productPrice.setText(String.format(Locale.US, "%.2f ريال", productPrice));

        if (desc != null && !desc.isEmpty()) {
            binding.productDescription.setText(desc);
        } else {
            binding.productDescription.setVisibility(View.GONE);
        }

        if (productImage != null && !productImage.isEmpty()) {
            Glide.with(this)
                    .load(productImage)
                    .placeholder(com.sporthub.R.drawable.ic_image_placeholder)
                    .centerCrop()
                    .into(binding.productImage);
        }

        updateQuantityUI();

        // +/-
        binding.btnIncrease.setOnClickListener(v -> {
            quantity++;
            updateQuantityUI();
        });
        binding.btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantityUI();
            }
        });

        // Add to cart
        binding.addToCartButton.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                    ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
            if (userId == null) {
                Toast.makeText(this, "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show();
                return;
            }

            final String uid = userId;
            final int qty    = quantity;

            Executors.newSingleThreadExecutor().execute(() -> {
                CartItem existing = cartDao.getCartItemByProductId(uid, productId);
                if (existing != null) {
                    existing.setQuantity(existing.getQuantity() + qty);
                    cartDao.update(existing);
                } else {
                    CartItem item = new CartItem(productId, productName, productPrice, qty);
                    item.setUserId(uid);
                    item.setProductImage(productImage);
                    cartDao.insert(item);
                }
                runOnUiThread(() -> {
                    Toast.makeText(this, "✓ أُضيف " + qty + " للسلة", Toast.LENGTH_SHORT).show();
                    // فتح السلة مباشرة بعد الإضافة
                    startActivity(new Intent(this, CartActivity.class));
                });
            });
        });
    }

    private void updateQuantityUI() {
        binding.quantityText.setText(String.valueOf(quantity));
        double total = productPrice * quantity;
        binding.totalPricePreview.setText(
                String.format(Locale.US, "الإجمالي: %.2f ريال", total));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
