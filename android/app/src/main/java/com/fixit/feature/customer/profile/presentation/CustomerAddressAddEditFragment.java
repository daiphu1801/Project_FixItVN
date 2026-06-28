package com.fixit.feature.customer.profile.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerAddressAddEditBinding;
import com.fixit.feature.customer.profile.domain.model.CustomerAddress;
import com.google.android.material.chip.Chip;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CustomerAddressAddEditFragment extends BaseFragment<FragmentCustomerAddressAddEditBinding> {

    private AddressViewModel viewModel;
    private String addressId = "";
    private double selectedLatitude = 21.0285;
    private double selectedLongitude = 105.8542;

    @NonNull
    @Override
    protected FragmentCustomerAddressAddEditBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentCustomerAddressAddEditBinding.inflate(inflater, container, false);
    }

    @Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParentFragmentManager().setFragmentResultListener(
            com.fixit.feature.customer.booking.presentation.CustomerLocationPickerFragment.REQUEST_KEY,
            this,
            (requestKey, bundle) -> {
                String address = bundle.getString(com.fixit.feature.customer.booking.presentation.CustomerLocationPickerFragment.ADDRESS_KEY);
                if (address != null) {
                    binding.etAddress.setText(address);
                    selectedLatitude = bundle.getDouble(com.fixit.feature.customer.booking.presentation.CustomerLocationPickerFragment.LATITUDE_KEY, 21.0285);
                    selectedLongitude = bundle.getDouble(com.fixit.feature.customer.booking.presentation.CustomerLocationPickerFragment.LONGITUDE_KEY, 105.8542);
                }
            }
        );
    }

    @Override
    protected void setupViews() {
        // Lấy thông tin truyền vào từ Bundle
        Bundle args = getArguments();
        if (args != null) {
            addressId = args.getString("addressId", "");
            String label = args.getString("label", "");
            String address = args.getString("address", "");
            boolean defaultAddress = args.getBoolean("defaultAddress", false);
            selectedLatitude = args.getDouble("latitude", 21.0285);
            selectedLongitude = args.getDouble("longitude", 105.8542);

            binding.etLabel.setText(label);
            binding.etAddress.setText(address);
            binding.cbSetDefault.setChecked(defaultAddress);
        }

        // Cập nhật tiêu đề màn hình
        if (addressId == null || addressId.trim().isEmpty()) {
            binding.tvTitle.setText("Thêm địa chỉ mới");
        } else {
            binding.tvTitle.setText("Cập nhật địa chỉ");
        }

        // Xử lý nút quay lại
        binding.btnBack.setOnClickListener(v -> {
            if (navController != null) {
                navController.popBackStack();
            }
        });

        // Chọn vị trí từ bản đồ
        binding.tilAddress.setEndIconOnClickListener(v -> {
            if (navController != null) {
                navController.navigate(com.fixit.R.id.nav_customer_location_picker);
            }
        });

        // Xử lý khi click vào các gợi ý nhãn (Home, Office, ...)
        binding.chipHome.setOnClickListener(v -> binding.etLabel.setText(binding.chipHome.getText().toString()));
        binding.chipOffice.setOnClickListener(v -> binding.etLabel.setText(binding.chipOffice.getText().toString()));
        binding.chipParent.setOnClickListener(v -> binding.etLabel.setText(binding.chipParent.getText().toString()));
        binding.chipOther.setOnClickListener(v -> binding.etLabel.setText(binding.chipOther.getText().toString()));

        // Xử lý nút lưu
        binding.btnSaveAddress.setOnClickListener(v -> {
            String label = binding.etLabel.getText().toString().trim();
            String addressText = binding.etAddress.getText().toString().trim();
            boolean isDefault = binding.cbSetDefault.isChecked();

            if (addressText.isEmpty()) {
                binding.tilAddress.setError("Vui lòng nhập địa chỉ chi tiết");
                return;
            }
            binding.tilAddress.setError(null);

            if (label.isEmpty()) {
                label = "Địa chỉ";
            }

            CustomerAddress addressObj = new CustomerAddress(
                    addressId,
                    label,
                    addressText,
                    selectedLatitude,
                    selectedLongitude,
                    isDefault
            );

            if (addressId == null || addressId.trim().isEmpty()) {
                viewModel.addAddress(addressObj);
            } else {
                viewModel.updateAddress(addressId, addressObj);
            }
        });
    }

    @Override
    protected void observeData() {
        viewModel = new ViewModelProvider(this).get(AddressViewModel.class);

        // Theo dõi trạng thái loading
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            if (binding.layoutLoading != null) {
                binding.layoutLoading.getRoot().setVisibility(loading ? android.view.View.VISIBLE : android.view.View.GONE);
            }
        });

        // Theo dõi thông báo lỗi
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.trim().isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Theo dõi thông báo thành công
        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.trim().isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                if (navController != null) {
                    navController.popBackStack();
                }
            }
        });
    }
}
