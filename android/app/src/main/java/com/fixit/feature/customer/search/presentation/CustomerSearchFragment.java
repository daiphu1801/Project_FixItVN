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

        // Sự kiện click chọn bộ lọc chip
        binding.chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == -1) {
                // Nếu người dùng bỏ chọn toàn bộ, bắt buộc chọn lại "Tất cả"
                binding.chipAll.setChecked(true);
                return;
            }
            updateChipStyles();

            String filterType = "ALL";
            if (checkedId == R.id.chipAll) {
                filterType = "ALL";
            } else if (checkedId == R.id.chipElectric) {
                filterType = "ELECTRIC";
            } else if (checkedId == R.id.chipAc) {
                filterType = "AC";
            } else if (checkedId == R.id.chipPlumbing) {
                filterType = "PLUMBING";
            } else if (checkedId == R.id.chipCleaning) {
                filterType = "CLEANING";
            }
            viewModel.filterByChip(filterType);
        });

        // Thiết lập giao diện màu sắc ban đầu cho các chip
        updateChipStyles();

        // Sự kiện gõ tìm kiếm
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                viewModel.searchCategories(query);
                binding.ivClearSearch.setVisibility(query.isEmpty() ? android.view.View.GONE : android.view.View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Sự kiện click nút xóa chữ ở ô tìm kiếm
        binding.ivClearSearch.setOnClickListener(v -> {
            binding.etSearch.setText("");
        });

        viewModel.fetchCategories();
    }

    private void updateChipStyles() {
        int checkedId = binding.chipGroupFilter.getCheckedChipId();

        int[][] states = new int[][] {
            new int[] { android.R.attr.state_checked }, // checked
            new int[] { -android.R.attr.state_checked } // unchecked
        };

        // Màu thương hiệu premium (xanh dương đậm và xám nhạt)
        int activeBgColor = android.graphics.Color.parseColor("#0284C7");
        int inactiveBgColor = android.graphics.Color.parseColor("#F1F5F9");

        int activeTextColor = android.graphics.Color.parseColor("#FFFFFF");
        int inactiveTextColor = android.graphics.Color.parseColor("#475569");

        for (int i = 0; i < binding.chipGroupFilter.getChildCount(); i++) {
            android.view.View view = binding.chipGroupFilter.getChildAt(i);
            if (view instanceof com.google.android.material.chip.Chip) {
                com.google.android.material.chip.Chip chip = (com.google.android.material.chip.Chip) view;

                chip.setChipBackgroundColor(new android.content.res.ColorStateList(
                    states,
                    new int[] { activeBgColor, inactiveBgColor }
                ));

                chip.setTextColor(new android.content.res.ColorStateList(
                    states,
                    new int[] { activeTextColor, inactiveTextColor }
                ));

                chip.setTypeface(android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL));
            }
        }
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

            // Cập nhật số lượng và hiển thị empty state
            if (categories != null) {
                binding.tvServiceCount.setText(String.valueOf(categories.size()));
                if (categories.isEmpty()) {
                    binding.layoutEmpty.setVisibility(android.view.View.VISIBLE);
                    binding.rvServices.setVisibility(android.view.View.GONE);
                } else {
                    binding.layoutEmpty.setVisibility(android.view.View.GONE);
                    binding.rvServices.setVisibility(android.view.View.VISIBLE);
                }
            } else {
                binding.tvServiceCount.setText("0");
                binding.layoutEmpty.setVisibility(android.view.View.VISIBLE);
                binding.rvServices.setVisibility(android.view.View.GONE);
            }
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
