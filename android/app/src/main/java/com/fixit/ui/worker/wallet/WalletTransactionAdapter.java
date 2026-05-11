package com.fixit.ui.worker.wallet;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fixit.data.model.WalletTransaction;
import com.fixit.databinding.ItemWalletTransactionBinding;

import java.util.ArrayList;
import java.util.List;

public class WalletTransactionAdapter extends RecyclerView.Adapter<WalletTransactionAdapter.TxViewHolder> {

    private List<WalletTransaction> items = new ArrayList<>();

    public void submitList(List<WalletTransaction> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
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
        holder.bind(items.get(position));
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
            b.tvTransactionAmount.setTextColor(
                    Color.parseColor(tx.isCredit() ? "#22c55e" : "#ef4444"));

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
