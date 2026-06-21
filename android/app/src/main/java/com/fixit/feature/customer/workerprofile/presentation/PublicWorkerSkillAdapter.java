package com.fixit.feature.customer.workerprofile.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fixit.R;
import com.fixit.databinding.ItemPublicWorkerSkillBinding;
import com.fixit.feature.customer.workerprofile.domain.model.PublicWorkerSkill;

import java.util.ArrayList;
import java.util.List;

public class PublicWorkerSkillAdapter extends RecyclerView.Adapter<PublicWorkerSkillAdapter.ViewHolder> {

    private List<PublicWorkerSkill> skills = new ArrayList<>();

    public void submitList(List<PublicWorkerSkill> newSkills) {
        this.skills = newSkills != null ? newSkills : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPublicWorkerSkillBinding binding = ItemPublicWorkerSkillBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PublicWorkerSkill skill = skills.get(position);
        
        holder.binding.tvServiceName.setText(skill.getServiceName());
        
        if (skill.getBasePrice() != null) {
            long price = skill.getBasePrice().longValue();
            if (price >= 1000) {
                holder.binding.tvBasePrice.setText("Từ " + (price / 1000) + "k");
            } else {
                holder.binding.tvBasePrice.setText("Từ " + price + "đ");
            }
        } else {
            holder.binding.tvBasePrice.setText("Thỏa thuận");
        }

        if (skill.getIconUrl() != null && !skill.getIconUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(skill.getIconUrl())
                    .placeholder(R.drawable.ic_home_repair)
                    .error(R.drawable.ic_home_repair)
                    .into(holder.binding.ivServiceIcon);
        } else {
            holder.binding.ivServiceIcon.setImageResource(R.drawable.ic_home_repair);
        }
    }

    @Override
    public int getItemCount() {
        return skills.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemPublicWorkerSkillBinding binding;

        ViewHolder(ItemPublicWorkerSkillBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
