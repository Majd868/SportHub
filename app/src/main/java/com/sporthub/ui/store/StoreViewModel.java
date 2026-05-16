package com.sporthub.ui.store;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sporthub.data.model.Product;
import com.sporthub.data.repository.ProductRepository;

import java.util.List;

public class StoreViewModel extends ViewModel {
    private final ProductRepository repository;
    private final LiveData<List<Product>> products;
    private final MutableLiveData<String> selectedCategory;
    
    public StoreViewModel() {
        repository = new ProductRepository();
        products = repository.getProducts();
        selectedCategory = new MutableLiveData<>("all");
        repository.loadProducts("all");
    }
    
    public LiveData<List<Product>> getProducts() {
        return products;
    }
    
    public void setCategory(String category) {
        String normalizedCategory = category == null ? "all" : category;
        selectedCategory.setValue(normalizedCategory);
        repository.loadProducts(normalizedCategory);
    }
    
    public LiveData<String> getSelectedCategory() {
        return selectedCategory;
    }
}
