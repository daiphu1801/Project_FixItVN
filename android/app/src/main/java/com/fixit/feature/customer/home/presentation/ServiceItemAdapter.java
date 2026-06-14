package com.fixit.feature.customer.home.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fixit.databinding.ItemServiceSubCategoryBinding;
import com.fixit.feature.customer.service.domain.model.ServiceItem;

import java.util.ArrayList;
import java.util.List;

public class ServiceItemAdapter extends RecyclerView.Adapter<ServiceItemAdapter.ViewHolder> {

    public interface OnServiceItemSelectedListener {
        void onItemSelected(ServiceItem item);
    }

    private List<ServiceItem> items = new ArrayList<>();
    private final OnServiceItemSelectedListener listener;

    public ServiceItemAdapter(OnServiceItemSelectedListener listener) {
        this.listener = listener;
    }

    public void submitList(List<ServiceItem> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemServiceSubCategoryBinding binding = ItemServiceSubCategoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceItem item = items.get(position);
        holder.binding.tvItemName.setText(item.getName());
        
        // Format price (e.g. 150000 -> 150k)
        if (item.getPrice() != null) {
            long price = item.getPrice();
            if (price >= 1000) {
                holder.binding.tvPrice.setText((price / 1000) + "k");
            } else {
                holder.binding.tvPrice.setText(price + "đ");
            }
        } else {
            holder.binding.tvPrice.setText("Thỏa thuận");
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemSelected(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemServiceSubCategoryBinding binding;

        ViewHolder(ItemServiceSubCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
