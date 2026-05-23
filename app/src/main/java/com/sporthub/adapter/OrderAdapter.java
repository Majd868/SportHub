package com.sporthub.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.sporthub.R;
import com.sporthub.data.model.Order;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class OrderAdapter extends ListAdapter<Order, OrderAdapter.OrderViewHolder> {

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    private OnOrderClickListener clickListener;

    public OrderAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.clickListener = listener;
    }

    private static final DiffUtil.ItemCallback<Order> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Order>() {
                @Override
                public boolean areItemsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
                    return oldItem.getOrderId() != null
                            && oldItem.getOrderId().equals(newItem.getOrderId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
                    String s1 = oldItem.getStatus() != null ? oldItem.getStatus() : "";
                    String s2 = newItem.getStatus() != null ? newItem.getStatus() : "";
                    return s1.equals(s2)
                            && Double.compare(oldItem.getTotalAmount(), newItem.getTotalAmount()) == 0;
                }
            };

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = getItem(position);
        holder.bind(order, clickListener);
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView orderId;
        private final TextView statusBadge;
        private final TextView itemsCount;
        private final TextView totalText;
        private final TextView dateText;

        private static final SimpleDateFormat DATE_FORMAT =
                new SimpleDateFormat("dd/MM/yyyy  HH:mm", new Locale("ar"));

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId     = itemView.findViewById(R.id.order_id_text);
            statusBadge = itemView.findViewById(R.id.order_status_badge);
            itemsCount  = itemView.findViewById(R.id.order_items_count);
            totalText   = itemView.findViewById(R.id.order_total_text);
            dateText    = itemView.findViewById(R.id.order_date_text);
        }

        public void bind(Order order, OnOrderClickListener listener) {
            // Order ID — show first 8 chars
            String id = order.getOrderId();
            if (id != null && id.length() > 8) id = id.substring(0, 8);
            orderId.setText(String.format("طلب #%s", id != null ? id : "------"));

            // Status badge
            String status = order.getStatus();
            StatusInfo info = getStatusInfo(status);
            statusBadge.setText(info.label);
            statusBadge.setTextColor(Color.parseColor(info.textColor));
            statusBadge.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(info.bgColor)));

            // Items count
            int count = order.getItems() != null ? order.getItems().size() : 0;
            itemsCount.setText(count + " منتج");

            // Total
            totalText.setText(String.format(Locale.US, "%.2f ريال", order.getTotalAmount()));

            // Date
            if (order.getOrderDate() != null) {
                dateText.setText("📅 " + DATE_FORMAT.format(order.getOrderDate()));
            } else {
                dateText.setText("📅 غير محدد");
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onOrderClick(order);
            });
        }

        private static class StatusInfo {
            final String label;
            final String textColor;
            final String bgColor;

            StatusInfo(String label, String textColor, String bgColor) {
                this.label = label;
                this.textColor = textColor;
                this.bgColor = bgColor;
            }
        }

        private StatusInfo getStatusInfo(String status) {
            if (status == null) return new StatusInfo("غير معروف", "#B3B3B3", "#22B3B3B3");
            switch (status) {
                case "pending":
                    return new StatusInfo("⏳ قيد الانتظار", "#FF9800", "#33FF9800");
                case "confirmed":
                    return new StatusInfo("✅ مؤكد",         "#00D9FF", "#3300D9FF");
                case "shipped":
                    return new StatusInfo("🚚 تم الشحن",     "#9C27B0", "#339C27B0");
                case "delivered":
                    return new StatusInfo("📦 تم التسليم",   "#4CAF50", "#334CAF50");
                case "cancelled":
                    return new StatusInfo("❌ ملغي",          "#F44336", "#33F44336");
                default:
                    return new StatusInfo(status,             "#B3B3B3", "#22B3B3B3");
            }
        }
    }
}
