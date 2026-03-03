package com.sporthub.ui.store;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sporthub.data.model.Product;
import com.sporthub.data.repository.ProductRepository;

import java.util.List;

public class StoreViewModel extends ViewModel {
    private ProductRepository repository;
    private LiveData<List<Product>> products;
    private MutableLiveData<String> selectedCategory;
    
    public StoreViewModel() {
        repository = new ProductRepository();
        selectedCategory = new MutableLiveData<>("all");
        products = repository.getAllProducts();
    }
    
    public LiveData<List<Product>> getProducts() {
        return products;
    }
    
    public void setCategory(String category) {
        selectedCategory.setValue(category);
        products = repository.getProductsByCategory(category);
    }
    
    public LiveData<String> getSelectedCategory() {
        return selectedCategory;
    }
}
