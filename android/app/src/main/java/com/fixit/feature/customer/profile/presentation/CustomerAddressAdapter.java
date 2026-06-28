package com.fixit.feature.customer.profile.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fixit.R;
import com.fixit.databinding.ItemCustomerAddressBinding;
import com.fixit.feature.customer.profile.domain.model.CustomerAddress;

import java.util.ArrayList;
import java.util.List;

public class CustomerAddressAdapter extends RecyclerView.Adapter<CustomerAddressAdapter.AddressViewHolder> {

    public interface OnAddressInteractionListener {
        void onItemClick(CustomerAddress address);
        void onEditClick(CustomerAddress address);
        void onDeleteClick(CustomerAddress address);
        void onSetDefaultClick(CustomerAddress address);
    }

    private List<CustomerAddress> items = new ArrayList<>();
    private final OnAddressInteractionListener listener;

    public CustomerAddressAdapter(OnAddressInteractionListener listener) {
        this.listener = listener;
    }

    public void submitList(List<CustomerAddress> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCustomerAddressBinding b = ItemCustomerAddressBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new AddressViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class AddressViewHolder extends RecyclerView.ViewHolder {
        private final ItemCustomerAddressBinding b;

        AddressViewHolder(ItemCustomerAddressBinding b) {
            super(b.getRoot());
            this.b = b;
        }

        void bind(CustomerAddress address) {
            b.tvAddressLabel.setText(address.getLabel());
            b.tvAddressDetails.setText(address.getAddress());

            // Bind icons based on label name (e.g. Home, Work)
            String label = address.getLabel() != null ? address.getLabel().toLowerCase().trim() : "";
            if (label.contains("nhà") || label.contains("home")) {
                b.ivAddressIcon.setImageResource(R.drawable.ic_lucide_home);
            } else if (label.contains("văn phòng") || label.contains("công ty") || label.contains("work") || label.contains("office")) {
                b.ivAddressIcon.setImageResource(R.drawable.ic_lucide_briefcase);
            } else {
                b.ivAddressIcon.setImageResource(R.drawable.ic_lucide_map_pin);
            }

            if (address.getDefaultAddress() != null && address.getDefaultAddress()) {
                b.tvDefaultBadge.setVisibility(View.VISIBLE);
            } else {
                b.tvDefaultBadge.setVisibility(View.GONE);
            }

            b.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(address);
                }
            });

            b.btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(address);
                }
            });

            b.btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(address);
                }
            });
            
            // Long click to set default address if it's not already default
            b.getRoot().setOnLongClickListener(v -> {
                if (listener != null && (address.getDefaultAddress() == null || !address.getDefaultAddress())) {
                    listener.onSetDefaultClick(address);
                    return true;
                }
                return false;
            });
        }
    }
}
