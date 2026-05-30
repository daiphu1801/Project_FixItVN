package com.fixit.feature.worker.profile.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fixit.R;
import com.fixit.core.ui.ViewUtils;
import com.fixit.feature.worker.profile.domain.model.WorkerSkill;

import java.util.List;

public class WorkerProfileSpecializationAdapter extends RecyclerView.Adapter<WorkerProfileSpecializationAdapter.ViewHolder> {

    private final List<WorkerSkill> items;

    public WorkerProfileSpecializationAdapter(List<WorkerSkill> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_specialization, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkerSkill skill = items.get(position);
        holder.tvServiceName.setText(skill.getServiceName());
        holder.tvBasePrice.setText("Giá cơ bản: " + ViewUtils.formatCurrency((long) skill.getBasePrice()));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName;
        TextView tvBasePrice;

        ViewHolder(View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvBasePrice = itemView.findViewById(R.id.tvBasePrice);
        }
    }
}
