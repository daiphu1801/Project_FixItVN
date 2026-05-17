package com.fixit.feature.customer.booking.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerLocationPickerBinding;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CustomerLocationPickerFragment extends BaseFragment<FragmentCustomerLocationPickerBinding> {

    public static final String REQUEST_KEY = "location_request";
    public static final String ADDRESS_KEY = "address_text";

    @NonNull
    @Override
    protected FragmentCustomerLocationPickerBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentCustomerLocationPickerBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // Back button
        binding.ivBack.setOnClickListener(v -> {
            if (navController != null) {
                navController.popBackStack();
            }
        });

        // Confirm button
        binding.btnConfirm.setOnClickListener(v -> {
            String address = binding.tvAddressLine1.getText().toString() + ", " + binding.tvAddressLine2.getText().toString();
            Bundle result = new Bundle();
            result.putString(ADDRESS_KEY, address);
            getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);
            
            if (navController != null) {
                navController.popBackStack();
            }
        });
        
        // Search functionality can be added later
    }

    @Override
    protected void observeData() {
    }
}
