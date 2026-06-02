package com.sporthub.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.sporthub.R;
import com.sporthub.data.model.Product;

import java.util.Locale;

public class SellerProductAdapter extends ListAdapter<Product, SellerProductAdapter.ViewHolder> {

    public interface OnToggleListener  { void onToggle(Product product); }
    public interface OnDeleteListener  { void onDelete(Product product); }

    private OnToggleListener toggleListener;
    private OnDeleteListener deleteListener;

    public SellerProductAdapter() { super(DIFF); }

    public void setOnToggleListener(OnToggleListener l)  { this.toggleListener = l; }
    public void setOnDeleteListener(OnDeleteListener l)  { this.deleteListener = l; }

    private static final DiffUtil.ItemCallback<Product> DIFF =
            new DiffUtil.ItemCallback<Product>() {
                @Override public boolean areItemsTheSame(@NonNull Product a, @NonNull Product b) {
                    return a.getProductId() != null && a.getProductId().equals(b.getProductId());
                }
                @Override public boolean areContentsTheSame(@NonNull Product a, @NonNull Product b) {
                    return a.getName().equals(b.getName())
                            && a.getPrice() == b.getPrice()
                            && a.isAvailable() == b.isAvailable()
                            && a.getStock() == b.getStock();
                }
            };

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_seller_product, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), toggleListener, deleteListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, price, stock, category;
        final ImageView btnToggle, btnDelete;

        ViewHolder(@NonNull View v) {
            super(v);
            name      = v.findViewById(R.id.seller_product_name);
            price     = v.findViewById(R.id.seller_product_price);
            stock     = v.findViewById(R.id.seller_product_stock);
            category  = v.findViewById(R.id.seller_product_category);
            btnToggle = v.findViewById(R.id.btn_toggle_availability);
            btnDelete = v.findViewById(R.id.btn_delete_product);
        }

        void bind(Product p, OnToggleListener toggleL, OnDeleteListener deleteL) {
            name.setText(p.getName());
            price.setText(String.format(Locale.US, "%.2f ريال", p.getPrice()));
            stock.setText("مخزون: " + p.getStock());
            category.setText(getCategoryLabel(p.getCategory()));

            // Toggle icon changes based on availability
            if (p.isAvailable()) {
                btnToggle.setImageResource(R.drawable.ic_star); // eye open = visible
                btnToggle.setColorFilter(
                        ContextCompat.getColor(itemView.getContext(), R.color.success));
                btnToggle.setContentDescription("إخفاء المنتج");
            } else {
                btnToggle.setImageResource(R.drawable.ic_image_placeholder);
                btnToggle.setColorFilter(
                        ContextCompat.getColor(itemView.getContext(), R.color.text_hint));
                btnToggle.setContentDescription("إتاحة المنتج");
            }

            btnToggle.setOnClickListener(v -> { if (toggleL != null) toggleL.onToggle(p); });
            btnDelete.setOnClickListener(v -> { if (deleteL != null) deleteL.onDelete(p); });
        }

        private String getCategoryLabel(String cat) {
            if (cat == null) return "أخرى";
            switch (cat) {
                case "protein":     return "بروتين";
                case "equipment":   return "معدات";
                case "clothes":     return "ملابس";
                case "vitamins":    return "فيتامينات";
                case "accessories": return "إكسسوارات";
                default: return cat;
            }
        }
    }
}
