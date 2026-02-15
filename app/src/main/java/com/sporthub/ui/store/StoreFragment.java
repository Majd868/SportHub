package com.sporthub.ui.store;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android:view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.chip.Chip;
import com.sporthub.R;
import com.sporthub.adapter.ProductAdapter;
import com.sporthub.databinding.FragmentStoreBinding;

public class StoreFragment extends Fragment {
    private FragmentStoreBinding binding;
    private StoreViewModel viewModel;
    private ProductAdapter adapter;
    
    private final String[] categories = {"الكل", "بروتين", "معدات", "ملابس", "فيتامينات", "إكسسوارات"};
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentStoreBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(StoreViewModel.class);
        
        // Set up category chips
        for (String category : categories) {
            Chip chip = new Chip(requireContext());
            chip.setText(category);
            chip.setCheckable(true);
            chip.setOnClickListener(v -> {
                String categoryEng = getCategoryKey(category);
                viewModel.setCategory(categoryEng);
            });
            binding.categoryChips.addView(chip);
        }
        
        // Select first chip by default
        ((Chip) binding.categoryChips.getChildAt(0)).setChecked(true);
        
        // Set up RecyclerView
        adapter = new ProductAdapter();
        binding.productsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.productsRecyclerView.setAdapter(adapter);
        
        // Observe products
        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                adapter.setProducts(products);
            }
        });
    }
    
    private String getCategoryKey(String categoryArabic) {
        switch (categoryArabic) {
            case "الكل": return "all";
            case "بروتين": return "protein";
            case "معدات": return "equipment";
            case "ملابس": return "clothes";
            case "فيتامينات": return "vitamins";
            case "إكسسوارات": return "accessories";
            default: return "all";
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
