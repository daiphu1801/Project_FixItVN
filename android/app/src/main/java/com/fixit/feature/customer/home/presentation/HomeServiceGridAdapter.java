package com.fixit.feature.customer.home.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fixit.databinding.ItemServiceGridBinding;
import com.fixit.feature.customer.service.domain.model.ServiceCategory;

import java.util.ArrayList;
import java.util.List;

public class HomeServiceGridAdapter extends RecyclerView.Adapter<HomeServiceGridAdapter.ViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(ServiceCategory category, boolean isSeeAll);
    }

    private List<ServiceCategory> categories = new ArrayList<>();
    private final OnCategoryClickListener listener;

    public HomeServiceGridAdapter(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<ServiceCategory> newCategories) {
        this.categories = newCategories != null ? newCategories : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemServiceGridBinding binding = ItemServiceGridBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceCategory category = categories.get(position);
        
        holder.binding.tvServiceName.setText(category.getName());
        
        if (category.getId() == -1) {
            holder.binding.cardIconBg.setCardBackgroundColor(0xFFF3F4F6); // Xám nhạt
            holder.binding.ivServiceIcon.setImageResource(com.fixit.R.drawable.ic_lucide_menu); 
        } else {
            holder.binding.cardIconBg.setCardBackgroundColor(0xFFF0FAFF); // Xanh nhạt
            
            // Dùng Glide để load ảnh từ mạng
            if (category.getIconUrl() != null && !category.getIconUrl().isEmpty()) {
                com.bumptech.glide.Glide.with(holder.itemView.getContext())
                        .load(category.getIconUrl())
                        .placeholder(com.fixit.R.drawable.ic_home_repair)
                        .error(com.fixit.R.drawable.ic_home_repair)
                        .into(holder.binding.ivServiceIcon);
            } else {
                holder.binding.ivServiceIcon.setImageResource(com.fixit.R.drawable.ic_home_repair);
            }
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category, category.getId() == -1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemServiceGridBinding binding;

        ViewHolder(ItemServiceGridBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
