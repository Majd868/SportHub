package com.sporthub.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sporthub.data.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private FirebaseFirestore firestore;
    private MutableLiveData<List<Product>> productsLiveData;
    
    public ProductRepository() {
        firestore = FirebaseFirestore.getInstance();
        productsLiveData = new MutableLiveData<>();
    }
    
    public LiveData<List<Product>> getAllProducts() {
        loadProducts(null);
        return productsLiveData;
    }
    
    public LiveData<List<Product>> getProductsByCategory(String category) {
        loadProducts(category);
        return productsLiveData;
    }
    
    private void loadProducts(String category) {
        if (category == null || category.equals("all")) {
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
                    });
        } else {
            firestore.collection("products")
                    .whereEqualTo("category", category)
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
                    });
        }
    }
    
    public void getProductById(String productId, OnProductLoadedListener listener) {
        firestore.collection("products")
                .document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Product product = documentSnapshot.toObject(Product.class);
                        if (product != null) {
                            product.setProductId(documentSnapshot.getId());
                            listener.onProductLoaded(product);
                        }
                    }
                })
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }
    
    public interface OnProductLoadedListener {
        void onProductLoaded(Product product);
        void onError(String error);
    }
}
