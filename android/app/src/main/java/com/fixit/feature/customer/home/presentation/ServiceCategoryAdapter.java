package com.fixit.feature.customer.home.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fixit.databinding.ItemServiceCategoryBinding;
import com.fixit.feature.customer.service.domain.model.ServiceCategory;

import java.util.ArrayList;
import java.util.List;

public class ServiceCategoryAdapter extends RecyclerView.Adapter<ServiceCategoryAdapter.ViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(ServiceCategory category);
    }

    private List<ServiceCategory> categories = new ArrayList<>();
    private final OnCategoryClickListener listener;

    public ServiceCategoryAdapter(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<ServiceCategory> newCategories) {
        this.categories = newCategories != null ? newCategories : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemServiceCategoryBinding binding = ItemServiceCategoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceCategory category = categories.get(position);
        holder.binding.tvServiceName.setText(category.getName());
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemServiceCategoryBinding binding;

        ViewHolder(ItemServiceCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
