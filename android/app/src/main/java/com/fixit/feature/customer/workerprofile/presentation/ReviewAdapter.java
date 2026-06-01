package com.fixit.feature.customer.workerprofile.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fixit.R;
import com.fixit.databinding.ItemReviewBinding;
import com.fixit.feature.customer.review.domain.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private final List<Review> reviews = new ArrayList<>();

    public void submitList(List<Review> newList) {
        reviews.clear();
        if (newList != null) {
            reviews.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReviewBinding binding = ItemReviewBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.binding.tvCustomerName.setText(review.getCustomerName());
        holder.binding.tvComment.setText(review.getComment());
        holder.binding.itemRatingBar.setRating(review.getRating());

        // Định dạng ngày hiển thị đơn giản
        String dateStr = review.getCreatedAt();
        if (dateStr != null && dateStr.contains("T")) {
            dateStr = dateStr.split("T")[0]; // Cắt lấy "YYYY-MM-DD"
        }
        holder.binding.tvReviewDate.setText(dateStr);

        // Load avatar bằng Glide
        if (review.getCustomerAvatar() != null && !review.getCustomerAvatar().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(review.getCustomerAvatar())
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(holder.binding.ivCustomerAvatar);
        } else {
            holder.binding.ivCustomerAvatar.setImageResource(R.drawable.ic_person);
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemReviewBinding binding;
        ViewHolder(ItemReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
