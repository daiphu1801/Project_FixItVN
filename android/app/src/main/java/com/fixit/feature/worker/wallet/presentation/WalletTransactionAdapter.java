package com.fixit.feature.worker.wallet.presentation;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fixit.feature.worker.wallet.domain.model.WalletTransaction;
import com.fixit.databinding.ItemWalletTransactionBinding;

import java.util.ArrayList;
import java.util.List;

public class WalletTransactionAdapter extends RecyclerView.Adapter<WalletTransactionAdapter.TxViewHolder> {

    public interface OnTransactionClickListener {
        void onTransactionClick(WalletTransaction tx);
    }

    private List<WalletTransaction> items = new ArrayList<>();
    private OnTransactionClickListener listener;

    public void submitList(List<WalletTransaction> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnTransactionClickListener(OnTransactionClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWalletTransactionBinding b = ItemWalletTransactionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TxViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull TxViewHolder holder, int position) {
        WalletTransaction tx = items.get(position);
        holder.bind(tx);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTransactionClick(tx);
            }
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class TxViewHolder extends RecyclerView.ViewHolder {
        private final ItemWalletTransactionBinding b;

        TxViewHolder(ItemWalletTransactionBinding b) {
            super(b.getRoot());
            this.b = b;
        }

        void bind(WalletTransaction tx) {
            b.tvTransactionTitle.setText(tx.getTitle());
            b.tvTransactionDate.setText(tx.getDate());

            String prefix = tx.isCredit() ? "+ " : "- ";
            b.tvTransactionAmount.setText(prefix + tx.getAmount());
            
            // Màu sắc theo trạng thái giao dịch
            if ("CANCELLED".equals(tx.getStatus())) {
                b.tvTransactionAmount.setTextColor(Color.parseColor("#9ca3af"));
                b.tvTransactionTitle.setTextColor(Color.parseColor("#9ca3af"));
            } else if ("PENDING".equals(tx.getStatus())) {
                b.tvTransactionAmount.setTextColor(Color.parseColor("#f59e0b")); // màu cam chờ duyệt
                b.tvTransactionTitle.setTextColor(Color.parseColor("#333333"));
            } else {
                b.tvTransactionAmount.setTextColor(
                        Color.parseColor(tx.isCredit() ? "#22c55e" : "#ef4444"));
                b.tvTransactionTitle.setTextColor(Color.parseColor("#333333"));
            }

            // Icon màu theo loại ví
            int iconTint;
            switch (tx.getWalletType()) {
                case "held": iconTint = Color.parseColor("#f59e0b"); break;
                case "debt": iconTint = Color.parseColor("#ef4444"); break;
                default:     iconTint = Color.parseColor("#42c2ff"); break;
            }
            b.ivTransactionIcon.setColorFilter(iconTint);
        }
    }
}
