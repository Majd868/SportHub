package com.sporthub.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.sporthub.R;
import com.sporthub.data.model.Product;

public class ProductAdapter extends ListAdapter<Product, ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public interface OnAddToCartListener {
        void onAddToCart(Product product);
    }

    private OnProductClickListener clickListener;
    private OnAddToCartListener addToCartListener;

    public ProductAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnAddToCartListener(OnAddToCartListener listener) {
        this.addToCartListener = listener;
    }

    private static final DiffUtil.ItemCallback<Product> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Product>() {
                @Override
                public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
                    return oldItem.getProductId() != null
                            && oldItem.getProductId().equals(newItem.getProductId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
                    return oldItem.getName().equals(newItem.getName())
                            && oldItem.getPrice() == newItem.getPrice()
                            && oldItem.isAvailable() == newItem.isAvailable();
                }
            };

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = getItem(position);
        holder.bind(product, clickListener, addToCartListener);
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView productName;
        private final TextView productPrice;
        private final TextView categoryBadge;
        private final MaterialButton addToCartQuick;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage   = itemView.findViewById(R.id.product_image);
            productName    = itemView.findViewById(R.id.product_name);
            productPrice   = itemView.findViewById(R.id.product_price);
            categoryBadge  = itemView.findViewById(R.id.product_category_badge);
            addToCartQuick = itemView.findViewById(R.id.add_to_cart_quick);
        }

        public void bind(Product product,
                         OnProductClickListener clickListener,
                         OnAddToCartListener addListener) {

            productName.setText(product.getName());
            productPrice.setText(String.format("%.2f ريال", product.getPrice()));

            // Category badge
            categoryBadge.setText(getCategoryLabel(product.getCategory()));

            // Image — تحميل من الإنترنت مع تأثير fade-in أنيق
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_placeholder)
                        .centerCrop()
                        .transition(com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade(300))
                        .into(productImage);
            } else {
                Glide.with(itemView.getContext())
                        .load(R.drawable.ic_image_placeholder)
                        .centerCrop()
                        .into(productImage);
            }

            // Click → detail page
            itemView.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onProductClick(product);
            });

            // Quick add to cart
            addToCartQuick.setOnClickListener(v -> {
                if (addListener != null) addListener.onAddToCart(product);
            });
        }

        private String getCategoryLabel(String category) {
            if (category == null) return "منتج";
            switch (category) {
                case "protein":     return "بروتين";
                case "equipment":   return "معدات";
                case "clothes":     return "ملابس";
                case "vitamins":    return "فيتامينات";
                case "accessories": return "إكسسوارات";
                default:            return category;
            }
        }
    }
}
