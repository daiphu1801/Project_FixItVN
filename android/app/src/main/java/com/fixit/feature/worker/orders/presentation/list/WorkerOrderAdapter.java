package com.fixit.feature.worker.orders.presentation.list;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fixit.feature.worker.orders.domain.model.WorkerOrder;
import com.fixit.databinding.ItemWorkerOrderCardBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter hiển thị danh sách Đơn hàng của Thợ.
 * Mỗi item dùng layout item_worker_order_card.xml với ViewBinding.
 */
public class WorkerOrderAdapter extends RecyclerView.Adapter<WorkerOrderAdapter.OrderViewHolder> {

    private List<WorkerOrder> orders = new ArrayList<>();

    /** Cập nhật toàn bộ danh sách và refresh RecyclerView */
    public void submitList(List<WorkerOrder> newOrders) {
        this.orders = newOrders != null ? newOrders : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWorkerOrderCardBinding binding = ItemWorkerOrderCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderViewHolder(binding);
    }

    public interface OnOrderClickListener {
        void onDetailClick(WorkerOrder order);
    }

    private OnOrderClickListener listener;

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        WorkerOrder order = orders.get(position);
        holder.bind(order);
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDetailClick(order);
            }
        });

        holder.binding.btnOrderDetail.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDetailClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // ViewHolder
    // ──────────────────────────────────────────────────────────────────────────
    static class OrderViewHolder extends RecyclerView.ViewHolder {

        private final ItemWorkerOrderCardBinding binding;

        OrderViewHolder(ItemWorkerOrderCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(WorkerOrder order) {
            binding.tvOrderService.setText(order.getServiceTitle());
            binding.tvOrderAddress.setText(order.getAddress());
            binding.tvOrderTime.setText(order.getTimeSlot());
            binding.tvOrderPrice.setText(order.getPrice());

            // Bind customer name
            binding.tvCustomerName.setText(order.getCustomerName() != null ? order.getCustomerName() : "Khách hàng");

            // Format and bind payment method
            String paymentMethodText = "Tiền mặt";
            String pm = order.getPaymentMethod();
            if (pm != null) {
                if ("CASH".equalsIgnoreCase(pm) || "TIEN_MAT".equalsIgnoreCase(pm)) {
                    paymentMethodText = "Tiền mặt";
                } else if ("WALLET".equalsIgnoreCase(pm)) {
                    paymentMethodText = "Ví FixIt";
                } else if ("sepay".equalsIgnoreCase(pm)) {
                    paymentMethodText = "Chuyển khoản QR";
                } else {
                    paymentMethodText = pm;
                }
            }
            binding.tvPaymentMethod.setText("Thanh toán: " + paymentMethodText);

            // Bind issue description with dynamic visibility
            String desc = order.getIssueDescription();
            if (desc != null && !desc.trim().isEmpty()) {
                binding.cardIssue.setVisibility(android.view.View.VISIBLE);
                binding.tvOrderIssue.setText("Mô tả: " + desc);
            } else {
                binding.cardIssue.setVisibility(android.view.View.GONE);
            }

            // Hiển thị nhãn trạng thái với màu tương ứng
            applyStatusStyle(order.getStatus());

            // Hiển thị badge khiếu nại nếu có
            if (order.getComplaintStatus() != null && !order.getComplaintStatus().equals("none")) {
                binding.tvComplaintBadge.setVisibility(android.view.View.VISIBLE);
                if (order.getComplaintStatus().equals("pending")) {
                    binding.tvComplaintBadge.setText("Có khiếu nại");
                } else if (order.getComplaintStatus().equals("responded")) {
                    binding.tvComplaintBadge.setText("Đã phản hồi");
                }
            } else {
                binding.tvComplaintBadge.setVisibility(android.view.View.GONE);
            }
        }

        private void applyStatusStyle(String status) {
            String label;
            String textColor;
            int bgResId;

            switch (status) {
                case "ongoing":
                    label     = "Đang làm";
                    textColor = "#c2410c";   // dark amber/orange
                    bgResId   = com.fixit.R.drawable.bg_badge_orange_light;
                    break;
                case "completed":
                    label     = "Hoàn thành";
                    textColor = "#15803d";   // dark green
                    bgResId   = com.fixit.R.drawable.bg_badge_green_light;
                    break;
                case "cancelled":
                    label     = "Đã huỷ";
                    textColor = "#b91c1c";   // dark red
                    bgResId   = com.fixit.R.drawable.bg_badge_red;
                    break;
                default: // "pending"
                    label     = "Chờ làm";
                    textColor = "#1d4ed8";   // dark blue
                    bgResId   = com.fixit.R.drawable.bg_badge_light_blue;
                    break;
            }

            binding.tvOrderStatus.setText(label);
            binding.tvOrderStatus.setTextColor(Color.parseColor(textColor));
            binding.tvOrderStatus.setBackgroundResource(bgResId);
        }
    }
}
