package com.sporthub.ui.orders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sporthub.adapter.OrderAdapter;
import com.sporthub.data.model.Order;
import com.sporthub.databinding.ActivityMyOrdersBinding;
import com.sporthub.utils.FirebaseErrorHelper;

import java.util.ArrayList;
import java.util.List;

public class MyOrdersActivity extends AppCompatActivity {

    private ActivityMyOrdersBinding binding;
    private OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupRecyclerView();
        loadOrders();
    }

    private void setupToolbar() {
        binding.backButton.setOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        adapter = new OrderAdapter();
        adapter.setOnOrderClickListener(order -> {
            // يمكن فتح تفاصيل الطلب لاحقاً
            String status = order.getStatus() != null ? order.getStatus() : "غير محدد";
            Toast.makeText(this, "الطلب: " + status, Toast.LENGTH_SHORT).show();
        });

        binding.ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.ordersRecyclerView.setAdapter(adapter);
    }

    private void loadOrders() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            showEmptyState();
            return;
        }

        binding.loadingProgress.setVisibility(View.VISIBLE);
        binding.emptyView.setVisibility(View.GONE);

        FirebaseFirestore.getInstance()
                .collection("orders")
                .whereEqualTo("userId", user.getUid())
                .orderBy("orderDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    binding.loadingProgress.setVisibility(View.GONE);
                    List<Order> orders = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Order order = doc.toObject(Order.class);
                        if (order != null) {
                            order.setOrderId(doc.getId());
                            orders.add(order);
                        }
                    }
                    if (orders.isEmpty()) {
                        showEmptyState();
                    } else {
                        binding.emptyView.setVisibility(View.GONE);
                        binding.ordersRecyclerView.setVisibility(View.VISIBLE);
                        adapter.submitList(orders);
                    }
                })
                .addOnFailureListener(e -> {
                    binding.loadingProgress.setVisibility(View.GONE);
                    showEmptyState();
                    if (FirebaseErrorHelper.isPermissionDenied(e)) {
                        FirebaseErrorHelper.showPermissionFixDialog(this);
                    } else {
                        Toast.makeText(this,
                                "تعذّر تحميل الطلبات: " + FirebaseErrorHelper.getArabicMessage(e),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showEmptyState() {
        binding.emptyView.setVisibility(View.VISIBLE);
        binding.ordersRecyclerView.setVisibility(View.GONE);

        // زر الانتقال إلى المتجر
        binding.goToStoreButton.setOnClickListener(v -> {
            onBackPressed(); // يعود إلى الشاشة الرئيسية حيث يوجد تبويب المتجر
        });
    }
}
