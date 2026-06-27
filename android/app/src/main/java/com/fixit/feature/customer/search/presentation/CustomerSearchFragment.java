package com.fixit.feature.customer.search.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerSearchBinding;
import dagger.hilt.android.AndroidEntryPoint;

import androidx.lifecycle.ViewModelProvider;
import android.text.Editable;
import android.text.TextWatcher;
import com.fixit.R;
import com.fixit.feature.customer.home.presentation.ServiceCategoryAdapter;
import com.fixit.feature.customer.home.presentation.ServiceCategoryBottomSheet;
import com.fixit.feature.customer.service.domain.model.ServiceCategory;

/**
 * FILE ĐIỀU KHIỂN GIAO DIỆN TÌM KIẾM (CUSTOMER SEARCH FRAGMENT)
 * Mục đích: Quản lý các hành động của người dùng tại màn hình tìm kiếm.
 */
@AndroidEntryPoint
public class CustomerSearchFragment extends BaseFragment<FragmentCustomerSearchBinding> {

    private CustomerSearchViewModel viewModel;
    private ServiceCategoryAdapter serviceAdapter;

    // Hàm này giúp kết nối file giao diện XML (fragment_customer_search.xml) với code Java này
    @NonNull
    @Override
    protected FragmentCustomerSearchBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentCustomerSearchBinding.inflate(inflater, container, false);
    }

    // Nơi thực hiện các cài đặt ban đầu cho giao diện Tìm kiếm 
    // Ví dụ: Bắt sự kiện khi người dùng gõ từ khóa vào thanh tìm kiếm.
    @Override
    protected void setupViews() {
        viewModel = new ViewModelProvider(this).get(CustomerSearchViewModel.class);

        // Sự kiện khi nhấn nút Quay lại (Back)
        binding.btnBack.setOnClickListener(v -> {
            if (navController != null) {
                navController.popBackStack();
            }
        });

        // Thiết lập Adapter cho RecyclerView (Dùng lại ServiceCategoryAdapter)
        serviceAdapter = new ServiceCategoryAdapter(new ServiceCategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(ServiceCategory category) {
                // Hiện BottomSheet để chọn dịch vụ con
                ServiceCategoryBottomSheet bottomSheet = ServiceCategoryBottomSheet.newInstance(category.getId(),
                        category.getName());
                bottomSheet.setOnServiceItemSelectedListener(item -> {
                    // Khi chọn xong 1 dịch vụ con -> Chuyển sang màn hình Đặt thợ
                    navigateToFindingWorker(category.getId(), category.getName(), item.getName());
                });
                bottomSheet.show(getParentFragmentManager(), "ServiceCategoryBottomSheet");
            }
        });
        binding.rvServices.setAdapter(serviceAdapter);

        // Sự kiện gõ tìm kiếm
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.searchCategories(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        viewModel.fetchCategories();
    }

    private void navigateToFindingWorker(int serviceId, String serviceName, String subServiceName) {
        if (navController != null) {
            android.os.Bundle args = new android.os.Bundle();
            args.putInt("serviceId", serviceId);
            args.putString("serviceName", serviceName);
            args.putString("subServiceName", subServiceName);
            navController.navigate(R.id.nav_customer_booking, args);
        }
    }

    // Nơi nhận dữ liệu từ ViewModel để cập nhật lên màn hình
    // Ví dụ: Hiển thị danh sách các thợ sửa chữa tìm thấy được.
    @Override
    protected void observeData() {
        viewModel.categories.observe(getViewLifecycleOwner(), categories -> {
            serviceAdapter.submitList(categories);
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && binding.layoutLoading != null) {
                binding.layoutLoading.getRoot().setVisibility(isLoading ? android.view.View.VISIBLE : android.view.View.GONE);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                showToast(error);
            }
        });
    }
}
