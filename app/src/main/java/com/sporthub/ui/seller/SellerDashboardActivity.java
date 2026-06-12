package com.sporthub.ui.seller;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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

    // ─── Image picker ──────────────────────────────────────────────────────────
    private Uri selectedImageUri = null;
    private ImageView dialogImagePreview;   // مرجع لـ ImageView داخل الـ dialog
    private View dialogImagePlaceholder;    // مرجع للـ placeholder (أيقونة + نص)
    private View dialogChangeBtnView;       // زر "تغيير"

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri == null) return;
                        selectedImageUri = uri;
                        if (dialogImagePreview != null) {
                            dialogImagePlaceholder.setVisibility(View.GONE);
                            dialogChangeBtnView.setVisibility(View.VISIBLE);
                            dialogImagePreview.setVisibility(View.VISIBLE);
                            Glide.with(this)
                                    .load(uri)
                                    .centerCrop()
                                    .into(dialogImagePreview);
                        }
                    });

    // ─── Categories ───────────────────────────────────────────────────────────
    private final String[] categoriesAr = {"بروتين", "معدات", "ملابس", "فيتامينات", "إكسسوارات"};
    private final String[] categoriesEn = {"protein", "equipment", "clothes", "vitamins", "accessories"};

    // ──────────────────────────────────────────────────────────────────────────
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

    // ─── RecyclerView ──────────────────────────────────────────────────────────
    private void setupRecyclerView() {
        adapter = new SellerProductAdapter();

        adapter.setOnToggleListener(product -> {
            boolean newAvail = !product.isAvailable();
            db.collection("products").document(product.getProductId())
                    .update("available", newAvail)
                    .addOnSuccessListener(v -> {
                        Toast.makeText(this,
                                newAvail ? "✓ أُتيح المنتج للبيع" : "المنتج أُخفي من المتجر",
                                Toast.LENGTH_SHORT).show();
                        loadMyProducts();
                    })
                    .addOnFailureListener(e -> handleError("تحديث المنتج", e));
        });

        adapter.setOnDeleteListener(product ->
                new AlertDialog.Builder(this)
                        .setTitle("حذف المنتج")
                        .setMessage("هل تريد حذف \"" + product.getName() + "\" نهائياً؟")
                        .setPositiveButton("حذف", (d, w) ->
                                db.collection("products").document(product.getProductId())
                                        .delete()
                                        .addOnSuccessListener(v -> {
                                            Toast.makeText(this, "تم الحذف", Toast.LENGTH_SHORT).show();
                                            loadMyProducts();
                                        })
                                        .addOnFailureListener(e -> handleError("حذف المنتج", e)))
                        .setNegativeButton("إلغاء", null)
                        .show());

        binding.sellerProductsRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.sellerProductsRecycler.setAdapter(adapter);
    }

    // ─── Load ─────────────────────────────────────────────────────────────────
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
                    int available = 0;
                    for (QueryDocumentSnapshot doc : snap) {
                        Product p = doc.toObject(Product.class);
                        if (p != null) {
                            p.setProductId(doc.getId());
                            products.add(p);
                            if (p.isAvailable()) available++;
                        }
                    }
                    binding.statProductsCount.setText(String.valueOf(products.size()));
                    binding.statAvailableCount.setText(String.valueOf(available));

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
                    handleError("تحميل المنتجات", e);
                });
    }

    // ─── Add Product Dialog ────────────────────────────────────────────────────
    private void showAddProductDialog() {
        selectedImageUri = null; // reset image selection

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

        // ─── Image picker setup ───────────────────────────────────────────────
        View imagePicker      = v.findViewById(R.id.image_picker_area);
        dialogImagePreview    = v.findViewById(R.id.image_preview);
        dialogImagePlaceholder = v.findViewById(R.id.image_placeholder);
        dialogChangeBtnView   = v.findViewById(R.id.change_image_btn);
        ProgressBar uploadBar = v.findViewById(R.id.upload_progress);

        imagePicker.setOnClickListener(x ->
                imagePickerLauncher.launch("image/*"));

        // ─── Form fields ──────────────────────────────────────────────────────
        TextInputEditText nameInput  = v.findViewById(R.id.product_name_input);
        TextInputEditText descInput  = v.findViewById(R.id.product_desc_input);
        AutoCompleteTextView catDrop = v.findViewById(R.id.category_dropdown);
        TextInputEditText priceInput = v.findViewById(R.id.product_price_input);
        TextInputEditText stockInput = v.findViewById(R.id.product_stock_input);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categoriesAr);
        catDrop.setAdapter(catAdapter);
        catDrop.setText(categoriesAr[0], false);

        v.findViewById(R.id.cancel_button).setOnClickListener(x -> dialog.dismiss());

        v.findViewById(R.id.publish_button).setOnClickListener(x -> {
            // ─── Validate ─────────────────────────────────────────────────────
            String name     = getText(nameInput);
            String desc     = getText(descInput);
            String catAr    = catDrop.getText().toString().trim();
            String priceStr = getText(priceInput);
            String stockStr = getText(stockInput);

            if (name.isEmpty()) { nameInput.setError("أدخل اسم المنتج"); return; }
            if (priceStr.isEmpty()) { priceInput.setError("أدخل السعر"); return; }

            String catEn = "accessories";
            for (int i = 0; i < categoriesAr.length; i++) {
                if (categoriesAr[i].equals(catAr)) { catEn = categoriesEn[i]; break; }
            }

            double price = 0;
            int stock    = 1;
            try { price = Double.parseDouble(priceStr); } catch (NumberFormatException ignored) {}
            try { stock = Integer.parseInt(stockStr);   } catch (NumberFormatException ignored) {}
            if (price <= 0) { priceInput.setError("يجب أن يكون السعر أكبر من صفر"); return; }

            Product product = new Product(name, desc.isEmpty() ? name : desc, catEn, price);
            product.setStock(stock);
            product.setSellerId(sellerId);
            product.setAvailable(true);

            // ─── Upload image if selected, then publish ────────────────────────
            v.findViewById(R.id.publish_button).setEnabled(false);
            if (selectedImageUri != null) {
                uploadImageThenPublish(selectedImageUri, product, dialog, uploadBar, v);
            } else {
                publishProduct(product, dialog);
            }
        });

        dialog.show();
    }

    // ─── Upload image → get URL → publish ─────────────────────────────────────
    private void uploadImageThenPublish(Uri imageUri, Product product,
                                        Dialog dialog, ProgressBar bar, View dialogView) {
        bar.setVisibility(View.VISIBLE);

        String path = "products/" + sellerId + "/" + System.currentTimeMillis() + ".jpg";
        StorageReference ref = FirebaseStorage.getInstance().getReference(path);

        ref.putFile(imageUri)
                .addOnProgressListener(snap -> {
                    // اجعل الشريط يعكس التقدم
                    int pct = (int)((100.0 * snap.getBytesTransferred()) / snap.getTotalByteCount());
                    bar.setIndeterminate(pct < 5);  // indeterminate فقط في البداية
                })
                .addOnSuccessListener(snap ->
                        ref.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    product.setImageUrl(uri.toString());
                                    bar.setVisibility(View.GONE);
                                    publishProduct(product, dialog);
                                })
                                .addOnFailureListener(e -> {
                                    bar.setVisibility(View.GONE);
                                    // نشر بدون صورة إذا فشل جلب الرابط
                                    Toast.makeText(this, "لم يُتمكّن من جلب رابط الصورة، سيُنشر بدون صورة",
                                            Toast.LENGTH_SHORT).show();
                                    dialogView.findViewById(R.id.publish_button).setEnabled(true);
                                    publishProduct(product, dialog);
                                })
                )
                .addOnFailureListener(e -> {
                    bar.setVisibility(View.GONE);
                    dialogView.findViewById(R.id.publish_button).setEnabled(true);
                    if (FirebaseErrorHelper.isPermissionDenied(e)) {
                        showStorageRulesDialog();
                    } else {
                        Toast.makeText(this,
                                "فشل رفع الصورة: " + FirebaseErrorHelper.getArabicMessage(e) +
                                "\nسيُنشر المنتج بدون صورة",
                                Toast.LENGTH_LONG).show();
                        publishProduct(product, dialog);
                    }
                });
    }

    // ─── Publish product to Firestore ─────────────────────────────────────────
    private void publishProduct(Product product, Dialog dialog) {
        db.collection("products").add(product)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(this, "✓ تم نشر المنتج بنجاح!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadMyProducts();
                })
                .addOnFailureListener(e -> handleError("نشر المنتج", e));
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private void handleError(String action, Exception e) {
        if (FirebaseErrorHelper.isPermissionDenied(e)) {
            FirebaseErrorHelper.showPermissionFixDialog(this);
        } else {
            Toast.makeText(this,
                    "فشل " + action + ": " + FirebaseErrorHelper.getArabicMessage(e),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void showStorageRulesDialog() {
        new AlertDialog.Builder(this)
                .setTitle("⛔ Firebase Storage مقيّد")
                .setMessage(
                    "لتفعيل رفع الصور:\n\n" +
                    "1️⃣  Firebase Console → Storage → Rules\n\n" +
                    "2️⃣  الصق هذه القواعد:\n\n" +
                    "rules_version = '2';\n" +
                    "service firebase.storage {\n" +
                    "  match /b/{bucket}/o {\n" +
                    "    match /products/{allPaths=**} {\n" +
                    "      allow read: if true;\n" +
                    "      allow write: if request.auth != null;\n" +
                    "    }\n" +
                    "  }\n" +
                    "}\n\n" +
                    "3️⃣  اضغط Publish"
                )
                .setPositiveButton("تم", null)
                .show();
    }

    private String getText(TextInputEditText field) {
        CharSequence cs = field.getText();
        return cs != null ? cs.toString().trim() : "";
    }
}
