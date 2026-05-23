package com.sporthub.ui.store;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.sporthub.R;
import com.sporthub.adapter.ProductAdapter;
import com.sporthub.data.local.AppDatabase;
import com.sporthub.data.local.CartDao;
import com.sporthub.data.model.CartItem;
import com.sporthub.databinding.FragmentStoreBinding;

import java.util.concurrent.Executors;

public class StoreFragment extends Fragment {
    private FragmentStoreBinding binding;
    private StoreViewModel viewModel;
    private ProductAdapter adapter;
    private CartDao cartDao;
    private String userId;

    private final String[] categoriesAr = {"الكل", "بروتين", "معدات", "ملابس", "فيتامينات", "إكسسوارات"};
    private final String[] categoriesEn = {"all", "protein", "equipment", "clothes", "vitamins", "accessories"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentStoreBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(StoreViewModel.class);
        cartDao   = AppDatabase.getInstance(requireContext()).cartDao();
        userId    = FirebaseAuth.getInstance().getCurrentUser() != null
                    ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        setupCategoryChips();
        setupRecyclerView();
        observeProducts();
        setupCartButton();
        observeCartBadge();
    }

    // ─── Cart button ────────────────────────────────────────────────────────────
    private void setupCartButton() {
        binding.cartButton.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), CartActivity.class)));
    }

    private void observeCartBadge() {
        if (userId == null) return;
        cartDao.getCartItemCount(userId).observe(getViewLifecycleOwner(), count -> {
            if (count != null && count > 0) {
                binding.cartBadge.setVisibility(View.VISIBLE);
                binding.cartBadge.setText(count > 99 ? "99+" : String.valueOf(count));
            } else {
                binding.cartBadge.setVisibility(View.GONE);
            }
        });
    }

    // ─── Category Chips ─────────────────────────────────────────────────────────
    private void setupCategoryChips() {
        binding.categoryChips.setSingleSelection(true);

        for (int i = 0; i < categoriesAr.length; i++) {
            final String categoryEn = categoriesEn[i];
            Chip chip = new Chip(requireContext());
            chip.setText(categoriesAr[i]);
            chip.setCheckable(true);
            chip.setChecked(i == 0);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) viewModel.setCategory(categoryEn);
            });
            binding.categoryChips.addView(chip);
        }
    }

    // ─── RecyclerView ───────────────────────────────────────────────────────────
    private void setupRecyclerView() {
        adapter = new ProductAdapter();

        // فتح تفاصيل المنتج
        adapter.setOnProductClickListener(product -> {
            Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
            intent.putExtra("product_id",          product.getProductId());
            intent.putExtra("product_name",        product.getName());
            intent.putExtra("product_price",       product.getPrice());
            intent.putExtra("product_image",       product.getImageUrl());
            intent.putExtra("product_description", product.getDescription());
            startActivity(intent);
        });

        // إضافة سريعة للسلة من الكارد مباشرة
        adapter.setOnAddToCartListener(product -> {
            if (userId == null) {
                Toast.makeText(requireContext(), "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show();
                return;
            }
            Executors.newSingleThreadExecutor().execute(() -> {
                // إن كان المنتج موجوداً بالسلة → زد الكمية
                CartItem existing = cartDao.getCartItemByProductId(userId, product.getProductId());
                if (existing != null) {
                    existing.setQuantity(existing.getQuantity() + 1);
                    cartDao.update(existing);
                } else {
                    CartItem item = new CartItem(
                            product.getProductId(), product.getName(),
                            product.getPrice(), 1);
                    item.setUserId(userId);
                    item.setProductImage(product.getImageUrl());
                    cartDao.insert(item);
                }
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "✓ أُضيف إلى السلة", Toast.LENGTH_SHORT).show());
            });
        });

        binding.productsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.productsRecyclerView.setAdapter(adapter);
    }

    // ─── Observe ────────────────────────────────────────────────────────────────
    private void observeProducts() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            if (loading != null && loading) {
                binding.storeLoading.setVisibility(View.VISIBLE);
                binding.productsRecyclerView.setVisibility(View.GONE);
                binding.storeEmptyView.setVisibility(View.GONE);
            }
        });

        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            binding.storeLoading.setVisibility(View.GONE);
            if (products != null && !products.isEmpty()) {
                adapter.submitList(products);
                binding.productsRecyclerView.setVisibility(View.VISIBLE);
                binding.storeEmptyView.setVisibility(View.GONE);
            } else {
                binding.productsRecyclerView.setVisibility(View.GONE);
                binding.storeEmptyView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
