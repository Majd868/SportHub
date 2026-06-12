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
                    // المجموعة فارغة → نضيف المنتجات التجريبية مع صورها
                    List<Object[]> demos = buildDemoProducts();

                    final int[] count = {0};
                    for (Object[] d : demos) {
                        Product p = new Product(
                                (String) d[0], (String) d[1],
                                (String) d[2], (double) d[3]);
                        p.setStock((int) d[4]);
                        p.setImageUrl((String) d[5]);
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

    // ─── بيانات المنتجات مع الصور ─────────────────────────────────────────────
    // {name, description, category, price, stock, imageUrl}
    private static final String IMG = "https://images.unsplash.com/photo-";
    private static final String P   = "?auto=format&fit=crop&w=500&h=400&q=80";

    private List<Object[]> buildDemoProducts() {
        List<Object[]> list = new ArrayList<>();
        list.add(new Object[]{
            "بروتين واي جولد ستاندرد",
            "بروتين مصل اللبن عالي الجودة، 24g بروتين لكل وجبة. مناسب لبناء العضلات وتعافي ما بعد التمرين.",
            "protein", 189.99, 50,
            IMG + "1593095948071-474c5cc2989d" + P   // protein powder
        });
        list.add(new Object[]{
            "كرياتين مونوهيدرات",
            "كرياتين نقي 100% يرفع الأداء الرياضي ويزيد القوة والقدرة على التحمل.",
            "protein", 79.99, 80,
            IMG + "1526256262350-7da7584cf5eb" + P   // supplement capsules
        });
        list.add(new Object[]{
            "دمبل قابلة للتعديل 20kg",
            "دمبل متعددة الأوزان من 2kg إلى 20kg، مثالية للتمرين المنزلي.",
            "equipment", 299.99, 20,
            IMG + "1571019613454-1cb2f99b2d8b" + P   // gym dumbbells
        });
        list.add(new Object[]{
            "حبل تمرين رياضي",
            "حبل قفز رياضي احترافي مناسب للكارديو وفقدان الوزن، يتحمل الاستخدام اليومي.",
            "equipment", 49.99, 100,
            IMG + "1598289431512-b97b0917affc" + P   // jump rope
        });
        list.add(new Object[]{
            "تيشيرت رياضي دراي فيت",
            "قماش مسامي خفيف الوزن يمتص العرق بسرعة، مريح خلال التمارين الشاقة.",
            "clothes", 89.99, 60,
            IMG + "1521572163474-6864f9cf17ab" + P   // sports clothing
        });
        list.add(new Object[]{
            "فيتامين د3 + ك2",
            "مكمل غذائي يدعم صحة العظام والمناعة. 60 كبسولة تكفي لشهرين.",
            "vitamins", 59.99, 120,
            IMG + "1550572017-edd951b72e7a" + P      // vitamins/supplements
        });
        list.add(new Object[]{
            "حزام ظهر رياضي",
            "حزام دعم أسفل الظهر أثناء رفع الأثقال، يحمي العمود الفقري.",
            "accessories", 69.99, 40,
            IMG + "1534258936925-c58bed479fcb" + P   // gym accessories
        });
        list.add(new Object[]{
            "شاكر بروتين 700ml",
            "شاكر عملي محكم الغلق، مناسب لتحضير البروتين والمشروبات الرياضية أينما كنت.",
            "accessories", 34.99, 200,
            IMG + "1554344728-577cf8f84f9b" + P      // shaker bottle
        });
        list.add(new Object[]{
            "قفازات رفع الأثقال",
            "قفازات جلدية مبطنة تحمي راحة يدك أثناء التمرين وتمنع الانزلاق.",
            "accessories", 44.99, 80,
            IMG + "1517836357463-d25dfeac3438" + P   // gym workout
        });
        list.add(new Object[]{
            "تايت رياضي ضغط",
            "بنطال ضغط مرن يدعم العضلات ويقلل التعب أثناء الجري والتمارين.",
            "clothes", 109.99, 45,
            IMG + "1539710827142-7f82ac17fcef" + P   // sports pants
        });
        return list;
    }

    /**
     * يُحدّث المنتجات الموجودة في Firestore التي ليس لها صورة
     * (لإصلاح المنتجات المُبذورة قبل إضافة الصور)
     */
    public void addImagesToExistingProducts() {
        // خريطة: اسم المنتج → رابط الصورة
        java.util.Map<String, String> nameToImage = new java.util.HashMap<>();
        for (Object[] d : buildDemoProducts()) {
            nameToImage.put((String) d[0], (String) d[5]);
        }

        for (java.util.Map.Entry<String, String> entry : nameToImage.entrySet()) {
            firestore.collection("products")
                    .whereEqualTo("name", entry.getKey())
                    .get()
                    .addOnSuccessListener(snap -> {
                        for (QueryDocumentSnapshot doc : snap) {
                            String existingImg = doc.getString("imageUrl");
                            if (existingImg == null || existingImg.isEmpty()) {
                                doc.getReference()
                                   .update("imageUrl", entry.getValue())
                                   .addOnSuccessListener(v ->
                                           Log.d("ProductRepo", "✓ صورة أُضيفت: " + doc.getString("name")));
                            }
                        }
                    });
        }
    }

    public interface OnProductLoadedListener {
        void onProductLoaded(Product product);
        void onError(String error);
    }
}
