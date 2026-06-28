package com.fixit.feature.customer.history.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fixit.R;
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
            String shortId = booking.getBookingId() != null && booking.getBookingId().length() > 8
                    ? booking.getBookingId().substring(0, 8)
                    : booking.getBookingId();
            binding.tvOrderId.setText("#" + shortId);

            // Tên dịch vụ thật từ backend
            String serviceName = booking.getServiceName();
            if (serviceName == null || serviceName.isEmpty()) {
                serviceName = "Dịch vụ sửa chữa";
            }
            binding.tvServiceName.setText(serviceName);

            // Thợ
            if (booking.getWorker() != null && booking.getWorker().getFullName() != null) {
                binding.layoutWorker.setVisibility(View.VISIBLE);
                binding.tvWorkerName.setText("Thợ: " + booking.getWorker().getFullName());
            } else {
                binding.layoutWorker.setVisibility(View.GONE);
            }

            // Giá tiền
            if (booking.getFinalPrice() != null) {
                java.text.DecimalFormat df = new java.text.DecimalFormat("#,###đ");
                binding.tvPrice.setText(df.format(booking.getFinalPrice()));
            } else {
                binding.tvPrice.setText("Đang khảo sát");
            }

            // Lý do hủy
            String status = booking.getStatus();
            if ("CANCELLED".equalsIgnoreCase(status) && booking.getCancellationReason() != null && !booking.getCancellationReason().isEmpty()) {
                binding.tvCancelReason.setVisibility(View.VISIBLE);
                binding.tvCancelReason.setText("Lý do: " + booking.getCancellationReason());
            } else {
                binding.tvCancelReason.setVisibility(View.GONE);
            }

            // Status translation & badge colors
            if ("PENDING".equalsIgnoreCase(status)) {
                binding.tvStatusBadge.setText("ĐANG TÌM THỢ");
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_orange_light);
                binding.tvStatusBadge.setTextColor(android.graphics.Color.parseColor("#F59E0B")); // Amber
            } else if ("ASSIGNED".equalsIgnoreCase(status) || "ACCEPTED".equalsIgnoreCase(status)) {
                binding.tvStatusBadge.setText("ĐÃ NHẬN ĐƠN");
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_light_blue);
                binding.tvStatusBadge.setTextColor(android.graphics.Color.parseColor("#0284C7")); // Blue
            } else if ("SURVEYING".equalsIgnoreCase(status)) {
                binding.tvStatusBadge.setText("ĐANG KHẢO SÁT");
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_light_blue);
                binding.tvStatusBadge.setTextColor(android.graphics.Color.parseColor("#0284C7")); // Blue
            } else if ("WAITING_APPROVAL".equalsIgnoreCase(status)) {
                binding.tvStatusBadge.setText("CHỜ DUYỆT BÁO GIÁ");
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_orange_light);
                binding.tvStatusBadge.setTextColor(android.graphics.Color.parseColor("#F59E0B")); // Amber
            } else if ("WAITING_PAYMENT".equalsIgnoreCase(status)) {
                binding.tvStatusBadge.setText("CHỜ THANH TOÁN");
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_orange_light);
                binding.tvStatusBadge.setTextColor(android.graphics.Color.parseColor("#F59E0B")); // Amber
            } else if ("IN_PROGRESS".equalsIgnoreCase(status)) {
                binding.tvStatusBadge.setText("ĐANG THI CÔNG");
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_light_blue);
                binding.tvStatusBadge.setTextColor(android.graphics.Color.parseColor("#0284C7")); // Blue
            } else if ("CANCELLED".equalsIgnoreCase(status)) {
                binding.tvStatusBadge.setText("ĐÃ HỦY");
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_red);
                binding.tvStatusBadge.setTextColor(android.graphics.Color.parseColor("#EF4444")); // Red
            } else if ("COMPLETED".equalsIgnoreCase(status)) {
                binding.tvStatusBadge.setText("HOÀN THÀNH");
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_green_light);
                binding.tvStatusBadge.setTextColor(android.graphics.Color.parseColor("#10B981")); // Green
            } else {
                binding.tvStatusBadge.setText(status != null ? status.toUpperCase() : "");
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_light_blue);
                binding.tvStatusBadge.setTextColor(android.graphics.Color.parseColor("#0284C7"));
            }

            // Ngày giờ
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

            // Click listener để xem chi tiết
            View.OnClickListener detailsClickListener = v -> {
                androidx.navigation.NavController navController = androidx.navigation.Navigation.findNavController(v);
                android.os.Bundle args = new android.os.Bundle();
                args.putString("orderId", booking.getBookingId());
                if (booking.getWorker() != null) {
                    args.putString("workerId", booking.getWorker().getWorkerId());
                    args.putString("workerName", booking.getWorker().getFullName());
                }
                
                if ("COMPLETED".equalsIgnoreCase(booking.getStatus())) {
                    navController.navigate(R.id.nav_customer_order_detail_finished, args);
                } else {
                    // Mở màn hình chi tiết active
                    navController.navigate(R.id.nav_customer_order_detail, args);
                }
            };
            
            binding.btnPrimary.setOnClickListener(detailsClickListener);
            itemView.setOnClickListener(detailsClickListener);
        }
    }
}
