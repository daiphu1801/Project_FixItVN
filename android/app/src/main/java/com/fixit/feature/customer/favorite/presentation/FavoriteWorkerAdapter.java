package com.fixit.feature.customer.favorite.presentation;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fixit.R;
import com.fixit.databinding.ItemFavoriteWorkerBinding;
import com.fixit.feature.customer.favorite.domain.model.FavoriteWorker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FavoriteWorkerAdapter extends RecyclerView.Adapter<FavoriteWorkerAdapter.ViewHolder> {

    private final List<FavoriteWorker> list = new ArrayList<>();
    private OnFavoriteWorkerClickListener listener;

    public interface OnFavoriteWorkerClickListener {
        void onItemClick(FavoriteWorker worker);

        void onBookClick(FavoriteWorker worker);
    }

    public void setOnFavoriteWorkerClickListener(OnFavoriteWorkerClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<FavoriteWorker> newList) {
        list.clear();
        if (newList != null) {
            list.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFavoriteWorkerBinding binding = ItemFavoriteWorkerBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteWorker worker = list.get(position);

        holder.binding.tvWorkerName.setText(worker.getFullName());
        holder.binding.tvRating.setText(String.format(Locale.getDefault(), "%.1f", worker.getRating()));

        // Gộp kỹ năng bằng dấu phẩy
        if (worker.getSkills() != null && !worker.getSkills().isEmpty()) {
            holder.binding.tvWorkerSkills.setText(String.join(", ", worker.getSkills()));
        } else {
            holder.binding.tvWorkerSkills.setText("Chưa cập nhật chuyên môn");
        }

        // Xử lý trạng thái hoạt động (Online/Offline)
        if (worker.isAvailable()) {
            holder.binding.viewStatusDot.setBackgroundResource(R.drawable.bg_circle_green);
            holder.binding.viewStatusDot.setBackgroundTintList(null); // Reset tint
            holder.binding.tvStatusText.setText("Đang rảnh");
            holder.binding.tvStatusText.setTextColor(Color.parseColor("#10B981"));
        } else {
            holder.binding.viewStatusDot.setBackgroundResource(R.drawable.bg_circle_green);
            holder.binding.viewStatusDot.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#94A3B8")));
            holder.binding.tvStatusText.setText("Bận");
            holder.binding.tvStatusText.setTextColor(Color.parseColor("#94A3B8"));
        }

        // Tải ảnh đại diện bằng Glide
        if (worker.getAvatarUrl() != null && !worker.getAvatarUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(worker.getAvatarUrl())
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(holder.binding.ivWorkerAvatar);
        } else {
            holder.binding.ivWorkerAvatar.setImageResource(R.drawable.ic_person);
        }

        // Bắt sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(worker);
            }
        });

        holder.binding.btnBook.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookClick(worker);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemFavoriteWorkerBinding binding;

        ViewHolder(ItemFavoriteWorkerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
