package com.fixit.feature.worker.profile.presentation;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fixit.R;
import com.google.android.material.card.MaterialCardView;

import java.text.DecimalFormat;
import java.util.List;

public class CatalogServiceAdapter extends RecyclerView.Adapter<CatalogServiceAdapter.ViewHolder> {

    public interface OnSelectionChangedListener {
        void onSelectionChanged();
    }

    private final List<CatalogServiceItem> items;
    private final OnSelectionChangedListener selectionListener;

    public CatalogServiceAdapter(List<CatalogServiceItem> items, OnSelectionChangedListener selectionListener) {
        this.items = items;
        this.selectionListener = selectionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalog_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CatalogServiceItem item = items.get(position);

        holder.tvTitle.setText(item.getServiceName());
        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.tvSuggestedPrice.setText("Giá gợi ý: " + formatter.format(item.getSuggestedPrice()) + " VND");

        // Clear listeners to prevent recursion/bugs during view recycling
        holder.cbSelect.setOnCheckedChangeListener(null);
        holder.edtPrice.removeTextChangedListener(holder.textWatcher);

        // Bind states
        holder.cbSelect.setChecked(item.isSelected());
        holder.layoutPrice.setVisibility(item.isSelected() ? View.VISIBLE : View.GONE);

        if (item.getCustomPrice() != null) {
            holder.edtPrice.setText(String.valueOf(item.getCustomPrice().intValue()));
        } else {
            holder.edtPrice.setText("");
        }

        // Apply state styling to card
        if (item.isSelected()) {
            holder.cardView.setStrokeColor(Color.parseColor("#0ea5e9"));
            holder.cardView.setCardBackgroundColor(Color.parseColor("#f0faff")); // light blue tint
        } else {
            holder.cardView.setStrokeColor(Color.parseColor("#e2e8f0"));
            holder.cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
        }

        // Handle Checkbox clicks
        holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
            if (isChecked) {
                if (item.getCustomPrice() == null) {
                    item.setCustomPrice(item.getSuggestedPrice());
                }
                holder.layoutPrice.setVisibility(View.VISIBLE);
                holder.edtPrice.setText(String.valueOf(item.getCustomPrice().intValue()));
                holder.cardView.setStrokeColor(Color.parseColor("#0ea5e9"));
                holder.cardView.setCardBackgroundColor(Color.parseColor("#f0faff"));
            } else {
                item.setCustomPrice(null);
                holder.layoutPrice.setVisibility(View.GONE);
                holder.edtPrice.setText("");
                holder.cardView.setStrokeColor(Color.parseColor("#e2e8f0"));
                holder.cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
            }
            if (selectionListener != null) {
                selectionListener.onSelectionChanged();
            }
        });

        // Setup TextWatcher to update price
        holder.textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    try {
                        item.setCustomPrice(Double.parseDouble(s.toString()));
                    } catch (NumberFormatException e) {
                        item.setCustomPrice(item.getSuggestedPrice());
                    }
                } else {
                    item.setCustomPrice(0.0);
                }
            }
        };
        holder.edtPrice.addTextChangedListener(holder.textWatcher);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        CheckBox cbSelect;
        TextView tvTitle;
        TextView tvSuggestedPrice;
        View layoutPrice;
        EditText edtPrice;
        TextWatcher textWatcher;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_service);
            cbSelect = itemView.findViewById(R.id.cb_service_select);
            tvTitle = itemView.findViewById(R.id.tv_service_title);
            tvSuggestedPrice = itemView.findViewById(R.id.tv_suggested_price);
            layoutPrice = itemView.findViewById(R.id.layout_price_expand);
            edtPrice = itemView.findViewById(R.id.edt_price_input);
        }
    }
}
