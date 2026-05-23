package com.sporthub.ui.seller;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sporthub.R;
import com.sporthub.adapter.SellerProductAdapter;
import com.sporthub.data.model.Product;
import com.sporthub.databinding.ActivitySellerDashboardBinding;
import com.sporthub.utils.FirebaseErrorHelper;

import java.util.ArrayList;
import java.util.List;

public class SellerDashboardActivity extends AppCompatActivity {

    private ActivitySellerDashboardBinding binding;
    private FirebaseFirestore db;
    private String sellerId;
    private SellerProductAdapter adapter;

    private final String[] categoriesAr = {"بروتين", "معدات", "ملابس", "فيتامينات", "إكسسوارات"};
    private final String[] categoriesEn = {"protein", "equipment", "clothes", "vitamins", "accessories"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySellerDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db       = FirebaseFirestore.getInstance();
        sellerId = FirebaseAuth.getInstance().getCurrentUser() != null
                   ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (sellerId == null) { finish(); return; }

        binding.backButton.setOnClickListener(v -> onBackPressed());
        setupRecyclerView();
        loadMyProducts();

        binding.fabAddProduct.setOnClickListener(v -> showAddProductDialog());
    }

    // ─── RecyclerView ─────────────────────────────────────────────────────────
    private void setupRecyclerView() {
        adapter = new SellerProductAdapter();

        adapter.setOnToggleListener(product -> {
            boolean newAvail = !product.isAvailable();
            db.collection("products").document(product.getProductId())
                    .update("available", newAvail)
                    .addOnSuccessListener(v -> {
                        String msg = newAvail ? "✓ أُتيح المنتج للبيع" : "المنتج أُخفي من المتجر";
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                        loadMyProducts();
                    });
        });

        adapter.setOnDeleteListener(product ->
                new AlertDialog.Builder(this)
                        .setTitle("حذف المنتج")
                        .setMessage("هل تريد حذف \"" + product.getName() + "\" نهائياً؟")
                        .setPositiveButton("حذف", (d, w) ->
                                db.collection("products").document(product.getProductId())
                                        .delete()
                                        .addOnSuccessListener(v -> {
                                            Toast.makeText(this, "تم حذف المنتج", Toast.LENGTH_SHORT).show();
                                            loadMyProducts();
                                        }))
                        .setNegativeButton("إلغاء", null)
                        .show());

        binding.sellerProductsRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.sellerProductsRecycler.setAdapter(adapter);
    }

    // ─── Load products ─────────────────────────────────────────────────────────
    private void loadMyProducts() {
        binding.sellerLoading.setVisibility(View.VISIBLE);
        binding.sellerProductsRecycler.setVisibility(View.GONE);
        binding.sellerEmptyView.setVisibility(View.GONE);

        db.collection("products")
                .whereEqualTo("sellerId", sellerId)
                .get()
                .addOnSuccessListener(snap -> {
                    binding.sellerLoading.setVisibility(View.GONE);
                    List<Product> products = new ArrayList<>();
                    int availableCount = 0;
                    for (QueryDocumentSnapshot doc : snap) {
                        Product p = doc.toObject(Product.class);
                        if (p != null) {
                            p.setProductId(doc.getId());
                            products.add(p);
                            if (p.isAvailable()) availableCount++;
                        }
                    }
                    binding.statProductsCount.setText(String.valueOf(products.size()));
                    binding.statAvailableCount.setText(String.valueOf(availableCount));

                    if (products.isEmpty()) {
                        binding.sellerEmptyView.setVisibility(View.VISIBLE);
                    } else {
                        adapter.submitList(products);
                        binding.sellerProductsRecycler.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    binding.sellerLoading.setVisibility(View.GONE);
                    binding.sellerEmptyView.setVisibility(View.VISIBLE);
                    if (FirebaseErrorHelper.isPermissionDenied(e)) {
                        FirebaseErrorHelper.showPermissionFixDialog(this);
                    } else {
                        Toast.makeText(this,
                                "تعذّر التحميل: " + FirebaseErrorHelper.getArabicMessage(e),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    // ─── Add Product Dialog ────────────────────────────────────────────────────
    private void showAddProductDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);

        View v = LayoutInflater.from(this).inflate(R.layout.dialog_add_product, null);
        dialog.setContentView(v);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    (int)(getResources().getDisplayMetrics().widthPixels * 0.92f),
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextInputEditText nameInput  = v.findViewById(R.id.product_name_input);
        TextInputEditText descInput  = v.findViewById(R.id.product_desc_input);
        AutoCompleteTextView catDrop = v.findViewById(R.id.category_dropdown);
        TextInputEditText priceInput = v.findViewById(R.id.product_price_input);
        TextInputEditText stockInput = v.findViewById(R.id.product_stock_input);

        // Category dropdown
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categoriesAr);
        catDrop.setAdapter(catAdapter);
        catDrop.setText(categoriesAr[0], false); // default

        v.findViewById(R.id.cancel_button).setOnClickListener(x -> dialog.dismiss());

        v.findViewById(R.id.publish_button).setOnClickListener(x -> {
            String name  = getText(nameInput);
            String desc  = getText(descInput);
            String catAr = catDrop.getText().toString().trim();
            String priceStr = getText(priceInput);
            String stockStr = getText(stockInput);

            if (name.isEmpty()) { nameInput.setError("أدخل اسم المنتج"); return; }
            if (priceStr.isEmpty()) { priceInput.setError("أدخل السعر"); return; }

            // Map Arabic category to English key
            String catEn = "accessories";
            for (int i = 0; i < categoriesAr.length; i++) {
                if (categoriesAr[i].equals(catAr)) { catEn = categoriesEn[i]; break; }
            }

            double price = 0;
            int stock    = 1;
            try { price = Double.parseDouble(priceStr); } catch (NumberFormatException ignored) {}
            try { stock = Integer.parseInt(stockStr); }   catch (NumberFormatException ignored) {}

            if (price <= 0) { priceInput.setError("يجب أن يكون السعر أكبر من صفر"); return; }

            Product product = new Product(name, desc.isEmpty() ? name : desc, catEn, price);
            product.setStock(stock);
            product.setSellerId(sellerId);
            product.setAvailable(true);

            v.findViewById(R.id.publish_button).setEnabled(false);

            db.collection("products").add(product)
                    .addOnSuccessListener(ref -> {
                        Toast.makeText(this, "✓ تم نشر المنتج بنجاح!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadMyProducts();
                    })
                    .addOnFailureListener(e -> {
                        v.findViewById(R.id.publish_button).setEnabled(true);
                        if (FirebaseErrorHelper.isPermissionDenied(e)) {
                            FirebaseErrorHelper.showPermissionFixDialog(this);
                        } else {
                            Toast.makeText(this,
                                    "فشل النشر: " + FirebaseErrorHelper.getArabicMessage(e),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        dialog.show();
    }

    private String getText(TextInputEditText field) {
        CharSequence cs = field.getText();
        return cs != null ? cs.toString().trim() : "";
    }
}
