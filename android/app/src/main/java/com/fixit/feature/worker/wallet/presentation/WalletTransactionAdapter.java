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
                b.tvTransactionStatus.setText("Đã hủy");
                b.tvTransactionStatus.setTextColor(Color.parseColor("#9ca3af"));
            } else if ("PENDING".equals(tx.getStatus())) {
                b.tvTransactionAmount.setTextColor(Color.parseColor("#f59e0b")); // màu cam chờ duyệt
                b.tvTransactionTitle.setTextColor(Color.parseColor("#333333"));
                b.tvTransactionStatus.setText("Đang chờ duyệt");
                b.tvTransactionStatus.setTextColor(Color.parseColor("#f59e0b"));
            } else {
                b.tvTransactionAmount.setTextColor(
                        Color.parseColor(tx.isCredit() ? "#22c55e" : "#ef4444"));
                b.tvTransactionTitle.setTextColor(Color.parseColor("#333333"));
                b.tvTransactionStatus.setText("Thành công");
                b.tvTransactionStatus.setTextColor(Color.parseColor("#64748b"));
            }

            // Gán icon động dựa trên type giao dịch
            int iconRes = com.fixit.R.drawable.ic_lucide_wallet;
            String type = tx.getType();
            if (type != null) {
                switch (type) {
                    case "Deposit":
                        iconRes = com.fixit.R.drawable.ic_deposit_arrow;
                        break;
                    case "Withdraw":
                        iconRes = com.fixit.R.drawable.ic_withdraw_arrow;
                        break;
                    case "Holding":
                        iconRes = com.fixit.R.drawable.ic_lucide_lock;
                        break;
                    case "Release":
                        iconRes = com.fixit.R.drawable.ic_lucide_check_circle;
                        break;
                    case "Fee_Deduction":
                        iconRes = com.fixit.R.drawable.ic_lucide_receipt;
                        break;
                }
            }
            b.ivTransactionIcon.setImageResource(iconRes);

            // Icon màu và background tròn màu theo loại ví
            int iconTint;
            int bgRes;
            
            if ("held".equals(tx.getWalletType())) {
                iconTint = Color.parseColor("#d97706"); // Amber đậm
                bgRes = com.fixit.R.drawable.bg_badge_yellow_light;
            } else if ("debt".equals(tx.getWalletType())) {
                iconTint = Color.parseColor("#dc2626"); // Đỏ đậm
                bgRes = com.fixit.R.drawable.bg_badge_red;
            } else { // available
                if (tx.isCredit()) {
                    iconTint = Color.parseColor("#16a34a"); // Xanh lá
                    bgRes = com.fixit.R.drawable.bg_badge_green_light;
                } else {
                    iconTint = Color.parseColor("#2563eb"); // Xanh dương
                    bgRes = com.fixit.R.drawable.bg_badge_light_blue;
                }
            }
            
            b.ivTransactionIcon.setColorFilter(iconTint);
            b.ivTransactionIcon.setBackgroundResource(bgRes);
        }
    }
}
