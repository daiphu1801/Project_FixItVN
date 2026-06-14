package com.fixit.feature.customer.history.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fixit.databinding.ItemOrderHistoryBinding;
import com.fixit.feature.customer.booking.domain.model.CustomerBooking;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private final List<CustomerBooking> bookings = new ArrayList<>();

    public void submitList(List<CustomerBooking> list) {
        bookings.clear();
        if (list != null) {
            bookings.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderHistoryBinding binding = ItemOrderHistoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(bookings.get(position));
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderHistoryBinding binding;

        public OrderViewHolder(ItemOrderHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CustomerBooking booking) {
            binding.tvOrderId.setText("#" + booking.getBookingId().substring(0, 8));
            
            // Map service ID to name (for now hardcode or lookup)
            binding.tvServiceName.setText("Dịch vụ sửa chữa"); 

            if (booking.getWorker() != null && booking.getWorker().getFullName() != null) {
                binding.layoutWorker.setVisibility(View.VISIBLE);
                binding.tvWorkerName.setText("Thợ: " + booking.getWorker().getFullName());
            } else {
                binding.layoutWorker.setVisibility(View.GONE);
            }

            // Status translation
            String status = booking.getStatus();
            if ("PENDING".equalsIgnoreCase(status)) {
                binding.tvStatusBadge.setText("ĐANG TÌM THỢ");
                binding.tvStatusBadge.setTextColor(android.graphics.Color.parseColor("#F59E0B")); // Amber
            } else if ("ASSIGNED".equalsIgnoreCase(status) || "ACCEPTED".equalsIgnoreCase(status)) {
                binding.tvStatusBadge.setText("ĐÃ NHẬN ĐƠN");
                binding.tvStatusBadge.setTextColor(android.graphics.Color.parseColor("#0284C7")); // Blue
            } else if ("CANCELLED".equalsIgnoreCase(status)) {
                binding.tvStatusBadge.setText("ĐÃ HỦY");
                binding.tvStatusBadge.setTextColor(android.graphics.Color.parseColor("#EF4444")); // Red
            } else if ("COMPLETED".equalsIgnoreCase(status)) {
                binding.tvStatusBadge.setText("HOÀN THÀNH");
                binding.tvStatusBadge.setTextColor(android.graphics.Color.parseColor("#10B981")); // Green
            } else {
                binding.tvStatusBadge.setText(status);
            }

            if (booking.getCreatedAt() != null) {
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault());
                    java.util.Date date = inputFormat.parse(booking.getCreatedAt());
                    binding.tvDateTime.setText(outputFormat.format(date));
                } catch (Exception e) {
                    binding.tvDateTime.setText(booking.getCreatedAt());
                }
            }
        }
    }
}
