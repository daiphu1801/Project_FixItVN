package com.fixit.feature.customer.search.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fixit.databinding.ItemSearchServiceBinding;
import com.fixit.feature.customer.home.domain.model.ServiceCategory;

import java.util.ArrayList;
import java.util.List;

public class SearchServiceAdapter extends RecyclerView.Adapter<SearchServiceAdapter.ViewHolder> {

    public interface OnServiceClickListener {
        void onServiceClick(ServiceCategory service);
    }

    // Nơi chứa danh sách dữ liệu
    private List<ServiceCategory> items = new ArrayList<>();
    private final OnServiceClickListener listener;

    public SearchServiceAdapter(OnServiceClickListener listener) {
        this.listener = listener;
    }

    // Hàm này được Fragment gọi (adapter.submitList) để bơm dữ liệu vào
    public void submitList(List<ServiceCategory> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged(); // Báo cho giao diện biết là có hàng mới, vẽ lại đi!
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Gọi cái khuôn giao diện (item_search_service.xml) ra
        ItemSearchServiceBinding binding = ItemSearchServiceBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Lấy từng dòng dữ liệu ra
        ServiceCategory item = items.get(position);

        // Nhét tên dịch vụ vào chữ trên màn hình
        holder.binding.tvServiceName.setText(item.getName());

        // Xử lý sự kiện khi người dùng nhấn vào một dịch vụ
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onServiceClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Lớp nội bộ để giữ cái khuôn giao diện
    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemSearchServiceBinding binding;
        ViewHolder(ItemSearchServiceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}