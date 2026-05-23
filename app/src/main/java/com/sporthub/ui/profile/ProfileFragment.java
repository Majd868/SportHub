package com.sporthub.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sporthub.databinding.FragmentProfileBinding;
import com.sporthub.ui.auth.LoginActivity;
import com.sporthub.ui.orders.MyOrdersActivity;
import com.sporthub.ui.premium.PremiumActivity;
import com.sporthub.ui.seller.SellerDashboardActivity;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            binding.userName.setText((name != null && !name.isEmpty()) ? name : "مستخدم");
            binding.userEmail.setText(user.getEmail() != null ? user.getEmail() : "");
        }

        binding.menuOrders.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), MyOrdersActivity.class)));

        binding.menuSeller.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), SellerDashboardActivity.class)));

        binding.menuPremium.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), PremiumActivity.class)));

        binding.menuLogout.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
