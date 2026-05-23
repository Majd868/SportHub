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
    private final MutableLiveData<Boolean> isLoading;

    public StoreViewModel() {
        repository = new ProductRepository();
        products = repository.getProducts();
        selectedCategory = new MutableLiveData<>("all");
        isLoading = new MutableLiveData<>(true);

        // أضف المنتجات التجريبية إن كانت Firestore فارغة ثم حمّل
        repository.seedDemoProductsIfEmpty(() -> {
            repository.loadProducts("all");
            isLoading.postValue(false);
        });
    }

    public LiveData<List<Product>> getProducts() {
        return products;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void setCategory(String category) {
        String normalizedCategory = category == null ? "all" : category;
        selectedCategory.setValue(normalizedCategory);
        isLoading.setValue(true);
        repository.loadProducts(normalizedCategory);
        isLoading.setValue(false);
    }

    public LiveData<String> getSelectedCategory() {
        return selectedCategory;
    }
}
