package com.fixit.ui.customer.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.fixit.base.BaseFragment;
import com.fixit.databinding.FragmentCustomerHomeBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CustomerHomeFragment extends BaseFragment<FragmentCustomerHomeBinding> {

    @NonNull
    @Override
    protected FragmentCustomerHomeBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentCustomerHomeBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // Setup initial static mock data views if necessary
    }

    @Override
    protected void observeData() {
        // Observe viewmodel
    }
}
