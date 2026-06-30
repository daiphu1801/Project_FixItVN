package com.fixit.feature.customer.booking.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fixit.R;
import com.fixit.databinding.ItemCustomerAddressSelectBinding;
import com.fixit.feature.customer.profile.domain.model.CustomerAddress;

import java.util.ArrayList;
import java.util.List;

public class CustomerAddressSelectAdapter extends RecyclerView.Adapter<CustomerAddressSelectAdapter.ViewHolder> {

    public interface OnAddressClickListener {
        void onAddressClick(CustomerAddress address);
    }

    private List<CustomerAddress> items = new ArrayList<>();
    private String selectedAddressId = null;
    private final OnAddressClickListener listener;

    public CustomerAddressSelectAdapter(OnAddressClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<CustomerAddress> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setSelectedAddressId(String selectedAddressId) {
        this.selectedAddressId = selectedAddressId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCustomerAddressSelectBinding binding = ItemCustomerAddressSelectBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCustomerAddressSelectBinding binding;

        ViewHolder(ItemCustomerAddressSelectBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CustomerAddress address) {
            binding.tvAddressLabel.setText(address.getLabel());
            binding.tvAddressDetails.setText(address.getAddress());

            // Default Badge
            if (address.getDefaultAddress() != null && address.getDefaultAddress()) {
                binding.tvDefaultBadge.setVisibility(View.VISIBLE);
            } else {
                binding.tvDefaultBadge.setVisibility(View.GONE);
            }

            // Bind icon based on label
            String label = address.getLabel() != null ? address.getLabel().toLowerCase().trim() : "";
            if (label.contains("nhà") || label.contains("home")) {
                binding.ivAddressIcon.setImageResource(R.drawable.ic_lucide_home);
            } else if (label.contains("văn phòng") || label.contains("công ty") || label.contains("work") || label.contains("office")) {
                binding.ivAddressIcon.setImageResource(R.drawable.ic_lucide_briefcase);
            } else {
                binding.ivAddressIcon.setImageResource(R.drawable.ic_lucide_map_pin);
            }

            // Selection state indicator
            boolean isSelected = address.getId() != null && address.getId().equals(selectedAddressId);
            if (isSelected) {
                binding.cardAddress.setStrokeColor(android.graphics.Color.parseColor("#42C2FF"));
                binding.cardAddress.setStrokeWidth(4); // 2dp equivalent
                binding.ivCheck.setVisibility(View.VISIBLE);
                binding.iconContainer.setBackgroundResource(R.drawable.bg_circle_blue);
                binding.ivAddressIcon.setColorFilter(android.graphics.Color.WHITE);
            } else {
                binding.cardAddress.setStrokeColor(android.graphics.Color.parseColor("#E2E8F0"));
                binding.cardAddress.setStrokeWidth(2); // 1dp equivalent
                binding.ivCheck.setVisibility(View.GONE);
                binding.iconContainer.setBackgroundResource(R.drawable.bg_circle_light_blue);
                binding.ivAddressIcon.setColorFilter(android.graphics.Color.parseColor("#0284c7"));
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddressClick(address);
                }
            });
        }
    }
}
