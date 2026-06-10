package com.fixit.feature.customer.history.presentation;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerOrderHistoryBinding;
import com.fixit.databinding.ItemOrderHistoryBinding;

import dagger.hilt.android.AndroidEntryPoint;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FILE ĐIỀU KHIỂN MÀN HÌNH LỊCH SỬ ĐƠN HÀNG
 * Mục đích: Hiển thị danh sách các đơn hàng của khách hàng.
 */
@AndroidEntryPoint
public class OrderHistoryFragment extends BaseFragment<FragmentCustomerOrderHistoryBinding> {

    private final List<MockHistoryOrder> allOrders = new ArrayList<>();
    private HistoryOrderAdapter adapter;

    @NonNull
    @Override
    protected FragmentCustomerOrderHistoryBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        // Kết nối file giao diện fragment_customer_order_history.xml với code Java này
        return FragmentCustomerOrderHistoryBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // Cài đặt nút quay lại (Back)
        binding.ivBack.setOnClickListener(v -> {
            if (navController != null) {
                navController.popBackStack();
            }
        });

        initMockData();

        // Cài đặt RecyclerView để render danh sách đơn hàng
        adapter = new HistoryOrderAdapter(new ArrayList<>(allOrders), this::onOrderDetailClick);
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvOrders.setAdapter(adapter);

        setupFilterChips();
    }

    private void initMockData() {
        allOrders.clear();
        allOrders.add(new MockHistoryOrder("ORD003", "Sửa điều hoa không mát", "ĐANG ĐẾN", "Hôm nay, 14:00", "Thợ: Phạm Thị D", "350.000đ", "worker_d_id"));
        allOrders.add(new MockHistoryOrder("ORD004", "Thông tắc bồn rửa bát", "HOÀN THÀNH", "Hôm qua, 09:00", "Thợ: Nguyễn Văn E", "120.000đ", "worker_e_id"));
        allOrders.add(new MockHistoryOrder("ORD006", "Lắp đèn phòng ngủ", "ĐÃ HỦY", "2 ngày trước, 11:00", "Thợ: Trịnh Văn G", "80.000đ", "worker_g_id"));
    }

    private void onOrderDetailClick(MockHistoryOrder order) {
        if ("HOÀN THÀNH".equals(order.getStatus())) {
            Bundle args = new Bundle();
            args.putString("orderId", order.getOrderId());
            args.putString("workerId", order.getWorkerId());
            args.putString("workerName", order.getWorkerName().replace("Thợ: ", ""));
            if (navController != null) {
                navController.navigate(R.id.nav_customer_order_detail_finished, args);
            }
        }
    }

    private void setupFilterChips() {
        List<TextView> chips = new ArrayList<>();
        chips.add(binding.chipAll);
        chips.add(binding.chipComing);
        chips.add(binding.chipDone);
        chips.add(binding.chipCanceled);

        for (TextView chip : chips) {
            chip.setOnClickListener(v -> {
                updateChipsState(chips, chip);
                filterData(chip.getText().toString());
            });
        }
    }

    private void updateChipsState(List<TextView> allChips, TextView selectedChip) {
        for (TextView chip : allChips) {
            if (chip == selectedChip) {
                chip.setBackgroundResource(R.drawable.bg_chip_active);
                chip.setTextColor(Color.WHITE);
            } else {
                chip.setBackgroundResource(R.drawable.bg_chip_inactive);
                chip.setTextColor(Color.parseColor("#64748B"));
            }
        }
    }

    private void filterData(String statusText) {
        List<MockHistoryOrder> filtered;
        if ("Tất cả".equals(statusText)) {
            filtered = new ArrayList<>(allOrders);
        } else if ("Đang đến".equals(statusText)) {
            filtered = allOrders.stream().filter(o -> "ĐANG ĐẾN".equals(o.getStatus())).collect(Collectors.toList());
        } else if ("Hoàn thành".equals(statusText)) {
            filtered = allOrders.stream().filter(o -> "HOÀN THÀNH".equals(o.getStatus())).collect(Collectors.toList());
        } else {
            filtered = allOrders.stream().filter(o -> "ĐÃ HỦY".equals(o.getStatus())).collect(Collectors.toList());
        }
        adapter.updateData(filtered);
    }

    @Override
    protected void observeData() {
        // ViewModel data observation
    }

    // --- LỚP DỮ LIỆU MOCK ---
    public static class MockHistoryOrder {
        private final String orderId;
        private final String serviceName;
        private final String status;
        private final String dateTime;
        private final String workerName;
        private final String price;
        private final String workerId;

        public MockHistoryOrder(String orderId, String serviceName, String status, String dateTime, String workerName, String price, String workerId) {
            this.orderId = orderId;
            this.serviceName = serviceName;
            this.status = status;
            this.dateTime = dateTime;
            this.workerName = workerName;
            this.price = price;
            this.workerId = workerId;
        }

        public String getOrderId() { return orderId; }
        public String getServiceName() { return serviceName; }
        public String getStatus() { return status; }
        public String getDateTime() { return dateTime; }
        public String getWorkerName() { return workerName; }
        public String getPrice() { return price; }
        public String getWorkerId() { return workerId; }
    }

    // --- ADAPTER RENDER DANH SÁCH ---
    private static class HistoryOrderAdapter extends RecyclerView.Adapter<HistoryOrderAdapter.ViewHolder> {
        private final List<MockHistoryOrder> list;
        private final OnItemClickListener listener;

        interface OnItemClickListener {
            void onItemClick(MockHistoryOrder order);
        }

        public HistoryOrderAdapter(List<MockHistoryOrder> list, OnItemClickListener listener) {
            this.list = list;
            this.listener = listener;
        }

        public void updateData(List<MockHistoryOrder> newList) {
            this.list.clear();
            this.list.addAll(newList);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemOrderHistoryBinding binding = ItemOrderHistoryBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            MockHistoryOrder item = list.get(position);
            holder.binding.tvServiceName.setText(item.getServiceName());
            holder.binding.tvOrderId.setText("#" + item.getOrderId());
            holder.binding.tvStatusBadge.setText(item.getStatus());
            holder.binding.tvDateTime.setText(item.getDateTime());
            holder.binding.tvWorkerName.setText(item.getWorkerName());
            holder.binding.tvPrice.setText(item.getPrice());

            // Đổi màu badge theo trạng thái
            if ("HOÀN THÀNH".equals(item.getStatus())) {
                holder.binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_green_light);
                holder.binding.tvStatusBadge.setTextColor(Color.parseColor("#10B981"));
            } else if ("ĐÃ HỦY".equals(item.getStatus())) {
                holder.binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_gray);
                holder.binding.tvStatusBadge.setTextColor(Color.parseColor("#64748B"));
            } else {
                holder.binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_light_blue);
                holder.binding.tvStatusBadge.setTextColor(Color.parseColor("#0284C7"));
            }

            holder.binding.btnPrimary.setOnClickListener(v -> listener.onItemClick(item));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final ItemOrderHistoryBinding binding;
            ViewHolder(ItemOrderHistoryBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
