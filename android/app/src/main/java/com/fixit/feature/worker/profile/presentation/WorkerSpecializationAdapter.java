package com.fixit.feature.worker.profile.presentation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fixit.R;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.List;

public class WorkerSpecializationAdapter extends RecyclerView.Adapter<WorkerSpecializationAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    private List<SpecializationItem> items;
    private OnItemClickListener deleteListener;

    public WorkerSpecializationAdapter(List<SpecializationItem> items, OnItemClickListener deleteListener) {
        this.items = items;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_worker_specialization, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SpecializationItem item = items.get(position);
        holder.tvServiceName.setText(item.getName());

        // Remove old text watcher before setting text to avoid triggering it
        if (holder.textWatcher != null) {
            holder.edtBasePrice.removeTextChangedListener(holder.textWatcher);
        }

        if (item.getBasePrice() != null) {
            holder.edtBasePrice.setText(String.valueOf(item.getBasePrice().intValue()));
        } else {
            holder.edtBasePrice.setText("");
        }

        holder.btnDeleteService.setOnClickListener(v -> {
            if (deleteListener != null) {
                int pos = holder.getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    deleteListener.onDeleteClick(pos);
                }
            }
        });

        holder.textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    try {
                        item.setBasePrice(Double.parseDouble(s.toString()));
                    } catch (NumberFormatException e) {
                        item.setBasePrice(null);
                    }
                } else {
                    item.setBasePrice(null);
                }
            }
        };
        holder.edtBasePrice.addTextChangedListener(holder.textWatcher);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public List<SpecializationItem> getItems() {
        return items;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName;
        ImageView btnDeleteService;
        EditText edtBasePrice;
        TextWatcher textWatcher;

        ViewHolder(View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            btnDeleteService = itemView.findViewById(R.id.btnDeleteService);
            edtBasePrice = itemView.findViewById(R.id.edtBasePrice);
        }
    }
}
