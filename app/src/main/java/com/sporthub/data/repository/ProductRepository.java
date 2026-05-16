package com.sporthub.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sporthub.data.model.Product;

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
                            product.setProductId(document.getId());
                            products.add(product);
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
                        product.setProductId(document.getId());
                        products.add(product);
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
    
    public interface OnProductLoadedListener {
        void onProductLoaded(Product product);
        void onError(String error);
    }
}
