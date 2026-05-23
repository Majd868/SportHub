package com.sporthub.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sporthub.data.model.Product;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private final FirebaseFirestore firestore;
    private final MutableLiveData<List<Product>> productsLiveData;
    
    public ProductRepository() {
        firestore = FirebaseFirestore.getInstance();
        productsLiveData = new MutableLiveData<>(new ArrayList<>());
    }
    
    public LiveData<List<Product>> getProducts() {
        return productsLiveData;
    }
    
    public void loadProducts(String category) {
        String normalizedCategory = category == null ? "all" : category;
        
        if ("all".equals(normalizedCategory)) {
            firestore.collection("products")
                    .whereEqualTo("available", true)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Product> products = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Product product = document.toObject(Product.class);
                            if (product != null) {
                                product.setProductId(document.getId());
                                products.add(product);
                            }
                        }
                        productsLiveData.setValue(products);
                    })
                    .addOnFailureListener(e -> productsLiveData.setValue(new ArrayList<>()));
            return;
        }

        firestore.collection("products")
                .whereEqualTo("category", normalizedCategory)
                .whereEqualTo("available", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> products = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Product product = document.toObject(Product.class);
                        if (product != null) {
                            product.setProductId(document.getId());
                            products.add(product);
                        }
                    }
                    productsLiveData.setValue(products);
                })
                .addOnFailureListener(e -> productsLiveData.setValue(new ArrayList<>()));
    }
    
    public void getProductById(String productId, OnProductLoadedListener listener) {
        firestore.collection("products")
                .document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        listener.onError("المنتج غير موجود");
                        return;
                    }
                    
                    Product product = documentSnapshot.toObject(Product.class);
                    if (product == null) {
                        listener.onError("تعذر تحميل بيانات المنتج");
                        return;
                    }
                    
                    product.setProductId(documentSnapshot.getId());
                    listener.onProductLoaded(product);
                })
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }
    
    /** يضيف منتجات تجريبية إلى Firestore إذا كانت المجموعة فارغة */
    public void seedDemoProductsIfEmpty(Runnable onDone) {
        firestore.collection("products")
                .limit(1)
                .get()
                .addOnSuccessListener(snap -> {
                    if (!snap.isEmpty()) {
                        if (onDone != null) onDone.run();
                        return;
                    }
                    // المجموعة فارغة → نضيف المنتجات التجريبية
                    List<Object[]> demos = new ArrayList<>();
                    // {name, description, category, price, stock}
                    demos.add(new Object[]{"بروتين واي جولد ستاندرد",
                            "بروتين مصل اللبن عالي الجودة، 24g بروتين لكل وجبة. مناسب لبناء العضلات وتعافي ما بعد التمرين.",
                            "protein", 189.99, 50});
                    demos.add(new Object[]{"كرياتين مونوهيدرات",
                            "كرياتين نقي 100% يرفع الأداء الرياضي ويزيد القوة والقدرة على التحمل.",
                            "protein", 79.99, 80});
                    demos.add(new Object[]{"دمبل قابلة للتعديل 20kg",
                            "دمبل متعددة الأوزان من 2kg إلى 20kg، مثالية للتمرين المنزلي.",
                            "equipment", 299.99, 20});
                    demos.add(new Object[]{"حبل تمرين رياضي",
                            "حبل قفز رياضي احترافي مناسب للكارديو وفقدان الوزن، يتحمل الاستخدام اليومي.",
                            "equipment", 49.99, 100});
                    demos.add(new Object[]{"تيشيرت رياضي دراي فيت",
                            "قماش مسامي خفيف الوزن يمتص العرق بسرعة، مريح خلال التمارين الشاقة.",
                            "clothes", 89.99, 60});
                    demos.add(new Object[]{"فيتامين د3 + ك2",
                            "مكمل غذائي يدعم صحة العظام والمناعة. 60 كبسولة تكفي لشهرين.",
                            "vitamins", 59.99, 120});
                    demos.add(new Object[]{"حزام ظهر رياضي",
                            "حزام دعم أسفل الظهر أثناء رفع الأثقال، يحمي العمود الفقري.",
                            "accessories", 69.99, 40});

                    final int[] count = {0};
                    for (Object[] d : demos) {
                        Product p = new Product(
                                (String) d[0], (String) d[1],
                                (String) d[2], (double) d[3]);
                        p.setStock((int) d[4]);
                        p.setAvailable(true);
                        p.setRating(4.0 + Math.random() * 0.9);
                        firestore.collection("products").add(p)
                                .addOnCompleteListener(task -> {
                                    count[0]++;
                                    if (count[0] == demos.size() && onDone != null) {
                                        onDone.run();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProductRepo", "seedDemoProducts failed: " + e.getMessage());
                    if (onDone != null) onDone.run();
                });
    }

    public interface OnProductLoadedListener {
        void onProductLoaded(Product product);
        void onError(String error);
    }
}
