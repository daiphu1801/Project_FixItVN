package com.fixit.feature.customer.booking.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fixit.R;
import com.fixit.databinding.DialogAddressSelectionBinding;
import com.fixit.feature.customer.profile.domain.model.CustomerAddress;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class CustomerAddressSelectionBottomSheet extends BottomSheetDialogFragment {

    public interface OnAddressSelectedListener {
        void onAddressSelected(CustomerAddress address);
        void onSelectFromMapSelected();
        void onAddNewAddressSelected();
    }

    private DialogAddressSelectionBinding binding;
    private OnAddressSelectedListener listener;
    private List<CustomerAddress> addresses = new ArrayList<>();
    private String selectedAddressId;

    public static CustomerAddressSelectionBottomSheet newInstance(List<CustomerAddress> addresses, String selectedAddressId) {
        CustomerAddressSelectionBottomSheet fragment = new CustomerAddressSelectionBottomSheet();
        fragment.setAddresses(addresses);
        fragment.setSelectedAddressId(selectedAddressId);
        return fragment;
    }

    public void setListener(OnAddressSelectedListener listener) {
        this.listener = listener;
    }

    private void setAddresses(List<CustomerAddress> addresses) {
        this.addresses = addresses != null ? addresses : new ArrayList<>();
    }

    private void setSelectedAddressId(String selectedAddressId) {
        this.selectedAddressId = selectedAddressId;
    }

    @Override
    public int getTheme() {
        // Use a nice theme that supports transparent background for rounded corners
        return R.style.Theme_FixIt_BottomSheetDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddressSelectionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup RecyclerView
        CustomerAddressSelectAdapter adapter = new CustomerAddressSelectAdapter(address -> {
            if (listener != null) {
                listener.onAddressSelected(address);
            }
            dismiss();
        });
        
        binding.rvAddresses.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAddresses.setAdapter(adapter);
        
        adapter.submitList(addresses);
        adapter.setSelectedAddressId(selectedAddressId);

        // Map button
        binding.btnSelectFromMap.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSelectFromMapSelected();
            }
            dismiss();
        });

        // Add new address button
        binding.btnAddAddress.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddNewAddressSelected();
            }
            dismiss();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
