package com.fixit.feature.customer.profile.presentation;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerAddressListBinding;
import com.fixit.feature.customer.profile.domain.model.CustomerAddress;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddressFragment extends BaseFragment<FragmentCustomerAddressListBinding> implements CustomerAddressAdapter.OnAddressInteractionListener {

    private AddressViewModel viewModel;
    private CustomerAddressAdapter adapter;

    @NonNull
    @Override
    protected FragmentCustomerAddressListBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentCustomerAddressListBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // Cài đặt RecyclerView
        adapter = new CustomerAddressAdapter(this);
        binding.rvAddresses.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvAddresses.setAdapter(adapter);

        // Nút quay lại
        binding.btnBack.setOnClickListener(v -> {
            if (navController != null) {
                navController.popBackStack();
            }
        });

        // Nút thêm địa chỉ mới
        binding.btnAddAddress.setOnClickListener(v -> {
            if (navController != null) {
                navController.navigate(R.id.nav_customer_address_add_edit);
            }
        });
    }

    @Override
    protected void observeData() {
        viewModel = new ViewModelProvider(this).get(AddressViewModel.class);

        // Tự động tải danh sách địa chỉ khi vào màn hình
        viewModel.loadAddresses();

        // Quan sát danh sách địa chỉ
        viewModel.getAddressesData().observe(getViewLifecycleOwner(), addresses -> {
            if (addresses == null || addresses.isEmpty()) {
                binding.layoutEmptyState.setVisibility(android.view.View.VISIBLE);
                binding.rvAddresses.setVisibility(android.view.View.GONE);
            } else {
                binding.layoutEmptyState.setVisibility(android.view.View.GONE);
                binding.rvAddresses.setVisibility(android.view.View.VISIBLE);
                adapter.submitList(addresses);
            }
        });

        // Quan sát trạng thái tải dữ liệu
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            if (binding.layoutLoading != null) {
                binding.layoutLoading.getRoot().setVisibility(loading ? android.view.View.VISIBLE : android.view.View.GONE);
            }
        });

        // Quan sát tin nhắn lỗi
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.trim().isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Quan sát tin nhắn thành công
        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), success -> {
            if (success != null && !success.trim().isEmpty()) {
                Toast.makeText(requireContext(), success, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(CustomerAddress address) {
        // Có thể mở chỉnh sửa khi click trực tiếp
        onEditClick(address);
    }

    @Override
    public void onEditClick(CustomerAddress address) {
        if (navController != null) {
            Bundle args = new Bundle();
            args.putString("addressId", address.getId());
            args.putString("label", address.getLabel());
            args.putString("address", address.getAddress());
            args.putBoolean("defaultAddress", address.getDefaultAddress() != null && address.getDefaultAddress());
            args.putDouble("latitude", address.getLatitude() != null ? address.getLatitude() : 21.0285);
            args.putDouble("longitude", address.getLongitude() != null ? address.getLongitude() : 105.8542);
            navController.navigate(R.id.nav_customer_address_add_edit, args);
        }
    }

    @Override
    public void onDeleteClick(CustomerAddress address) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa địa chỉ")
                .setMessage("Bạn có chắc chắn muốn xóa địa chỉ này?")
                .setPositiveButton("Xóa", (dialog, which) -> viewModel.deleteAddress(address.getId()))
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onSetDefaultClick(CustomerAddress address) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Đặt làm mặc định")
                .setMessage("Bạn có muốn đặt địa chỉ này làm địa chỉ mặc định không?")
                .setPositiveButton("Đặt mặc định", (dialog, which) -> viewModel.setDefaultAddress(address.getId()))
                .setNegativeButton("Hủy", null)
                .show();
    }
}
