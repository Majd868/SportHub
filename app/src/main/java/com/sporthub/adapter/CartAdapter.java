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
import com.sporthub.R;
import com.sporthub.data.model.CartItem;

import java.util.Locale;

public class CartAdapter extends ListAdapter<CartItem, CartAdapter.CartViewHolder> {

    public interface OnCartItemClickListener {
        void onRemoveClick(CartItem item);
    }

    public interface OnQuantityChangeListener {
        void onIncrease(CartItem item);
        void onDecrease(CartItem item);
    }

    private OnCartItemClickListener removeListener;
    private OnQuantityChangeListener quantityListener;

    public CartAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setOnCartItemClickListener(OnCartItemClickListener listener) {
        this.removeListener = listener;
    }

    public void setOnQuantityChangeListener(OnQuantityChangeListener listener) {
        this.quantityListener = listener;
    }

    private static final DiffUtil.ItemCallback<CartItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<CartItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull CartItem a, @NonNull CartItem b) {
                    return a.getId() == b.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull CartItem a, @NonNull CartItem b) {
                    return a.getProductName().equals(b.getProductName())
                            && a.getQuantity() == b.getQuantity()
                            && Double.compare(a.getPrice(), b.getPrice()) == 0;
                }
            };

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = getItem(position);
        holder.bind(item, removeListener, quantityListener);
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;
        private final TextView name;
        private final TextView unitPrice;
        private final TextView quantity;
        private final TextView total;
        private final ImageView btnIncrease;
        private final ImageView btnDecrease;
        private final ImageView btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            image       = itemView.findViewById(R.id.cart_item_image);
            name        = itemView.findViewById(R.id.cart_item_name);
            unitPrice   = itemView.findViewById(R.id.cart_item_unit_price);
            quantity    = itemView.findViewById(R.id.cart_item_quantity);
            total       = itemView.findViewById(R.id.cart_item_total);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            btnRemove   = itemView.findViewById(R.id.btn_remove);
        }

        public void bind(CartItem item,
                         OnCartItemClickListener removeListener,
                         OnQuantityChangeListener quantityListener) {

            name.setText(item.getProductName());
            unitPrice.setText(String.format(Locale.US, "%.2f ريال / قطعة", item.getPrice()));
            quantity.setText(String.valueOf(item.getQuantity()));
            total.setText(String.format(Locale.US, "%.2f ريال", item.getTotalPrice()));

            // صورة المنتج
            if (item.getProductImage() != null && !item.getProductImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(item.getProductImage())
                        .placeholder(R.drawable.ic_image_placeholder)
                        .centerCrop()
                        .into(image);
            } else {
                image.setImageResource(R.drawable.ic_image_placeholder);
            }

            btnIncrease.setOnClickListener(v -> {
                if (quantityListener != null) quantityListener.onIncrease(item);
            });

            btnDecrease.setOnClickListener(v -> {
                if (item.getQuantity() <= 1) {
                    // الكمية 1 → نحذف المنتج
                    if (removeListener != null) removeListener.onRemoveClick(item);
                } else {
                    if (quantityListener != null) quantityListener.onDecrease(item);
                }
            });

            btnRemove.setOnClickListener(v -> {
                if (removeListener != null) removeListener.onRemoveClick(item);
            });
        }
    }
}
