package com.fixit.feature.worker.profile.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fixit.R;
import com.fixit.feature.worker.profile.domain.model.ServiceCategory;
import com.google.android.material.chip.Chip;

import java.util.List;

public class CatalogCategoryChipAdapter extends RecyclerView.Adapter<CatalogCategoryChipAdapter.ViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Integer categoryId);
    }

    private final List<ServiceCategory> categories;
    private final OnCategoryClickListener listener;
    private Integer selectedCategoryId = -1; // -1 represents "Tất cả"

    public CatalogCategoryChipAdapter(List<ServiceCategory> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalog_category_chip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name;
        Integer id;
        if (position == 0) {
            name = "Tất cả";
            id = -1;
        } else {
            ServiceCategory cat = categories.get(position - 1);
            name = cat.getServiceName();
            id = cat.getId();
        }

        holder.chip.setText(name);
        holder.chip.setChecked(id.equals(selectedCategoryId));

        holder.chip.setOnClickListener(v -> {
            selectedCategoryId = id;
            notifyDataSetChanged();
            if (listener != null) {
                listener.onCategoryClick(selectedCategoryId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories == null ? 1 : categories.size() + 1;
    }

    public void setSelectedCategoryId(Integer categoryId) {
        this.selectedCategoryId = categoryId;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        Chip chip;

        ViewHolder(View itemView) {
            super(itemView);
            chip = (Chip) itemView;
        }
    }
}
