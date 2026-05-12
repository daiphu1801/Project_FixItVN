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
            String bgColor;

            switch (status) {
                case "ongoing":
                    label     = "Đang làm";
                    textColor = "#f59e0b";   // amber
                    bgColor   = "#fef3c7";
                    break;
                case "completed":
                    label     = "Hoàn thành";
                    textColor = "#22c55e";   // green
                    bgColor   = "#dcfce7";
                    break;
                case "cancelled":
                    label     = "Đã huỷ";
                    textColor = "#ef4444";   // red
                    bgColor   = "#fee2e2";
                    break;
                default: // "pending"
                    label     = "Chờ làm";
                    textColor = "#42c2ff";   // brand blue
                    bgColor   = "#e0f5ff";
                    break;
            }

            binding.tvOrderStatus.setText(label);
            binding.tvOrderStatus.setTextColor(Color.parseColor(textColor));
            binding.tvOrderStatus.setBackgroundColor(Color.parseColor(bgColor));
        }
    }
}
