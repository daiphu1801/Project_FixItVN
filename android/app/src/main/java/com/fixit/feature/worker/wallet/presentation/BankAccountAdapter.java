package com.fixit.feature.worker.wallet.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fixit.databinding.ItemBankAccountBinding;
import com.fixit.feature.worker.wallet.domain.model.BankAccount;

import java.util.ArrayList;
import java.util.List;

public class BankAccountAdapter extends RecyclerView.Adapter<BankAccountAdapter.BankViewHolder> {

    public interface OnBankInteractionListener {
        void onItemClick(BankAccount account);

        void onDeleteClick(BankAccount account);

        void onSetDefaultClick(BankAccount account);
    }

    private List<BankAccount> items = new ArrayList<>();
    private final OnBankInteractionListener listener;

    public BankAccountAdapter(OnBankInteractionListener listener) {
        this.listener = listener;
    }

    public void submitList(List<BankAccount> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBankAccountBinding b = ItemBankAccountBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new BankViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull BankViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class BankViewHolder extends RecyclerView.ViewHolder {
        private final ItemBankAccountBinding b;

        BankViewHolder(ItemBankAccountBinding b) {
            super(b.getRoot());
            this.b = b;
        }

        void bind(BankAccount acc) {
            b.tvBankName.setText(acc.getBankName());
            b.tvAccountNumber.setText(acc.getAccountNumber());
            b.tvAccountHolder.setText(acc.getAccountHolderName());

            if (acc.isDefault()) {
                b.tvDefaultBadge.setVisibility(View.VISIBLE);
                b.btnSetDefault.setVisibility(View.GONE);
            } else {
                b.tvDefaultBadge.setVisibility(View.GONE);
                b.btnSetDefault.setVisibility(View.VISIBLE);
            }

            b.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(acc);
            });

            b.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteClick(acc);
            });

            b.btnSetDefault.setOnClickListener(v -> {
                if (listener != null) listener.onSetDefaultClick(acc);
            });
        }
    }
}