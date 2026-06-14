package com.fixit.feature.customer.home.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerHomeBinding;
import com.fixit.feature.customer.service.domain.model.ServiceCategory;
import com.fixit.feature.customer.service.domain.model.ServiceItem;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CustomerHomeFragment extends BaseFragment<FragmentCustomerHomeBinding> {

    private CustomerHomeViewModel viewModel;
    private HomeServiceGridAdapter serviceAdapter;

    @NonNull
    @Override
    protected FragmentCustomerHomeBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentCustomerHomeBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        viewModel = new ViewModelProvider(this).get(CustomerHomeViewModel.class);

        // Thiết lập Adapter cho RecyclerView
        serviceAdapter = new HomeServiceGridAdapter(new HomeServiceGridAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(ServiceCategory category, boolean isSeeAll) {
                if (isSeeAll) {
                    if (navController != null) {
                        navController.navigate(R.id.nav_customer_search);
                    }
                } else {
                    // Hiện BottomSheet để chọn dịch vụ con
                    ServiceCategoryBottomSheet bottomSheet = ServiceCategoryBottomSheet.newInstance(category.getId(),
                            category.getName());
                    bottomSheet.setOnServiceItemSelectedListener(item -> {
                        // Khi chọn xong 1 dịch vụ con -> Chuyển sang màn hình Đặt thợ
                        navigateToFindingWorker(item.getName());
                    });
                    bottomSheet.show(getParentFragmentManager(), "ServiceCategoryBottomSheet");
                }
            }
        });
        binding.rvServices.setAdapter(serviceAdapter);

        // --- XỬ LÝ SỰ KIỆN KHÁC ---

        // Ô chọn vị trí phía trên
        binding.cardLocation.setOnClickListener(v -> {
            if (navController != null) {
                navController.navigate(R.id.nav_customer_search);
            }
        });

        // Icon Chat ở góc phải header
        binding.ivChatIcon.setOnClickListener(v -> {
            if (navController != null) {
                navController.navigate(R.id.nav_customer_list_msg);
            }
        });

        // Avatar người dùng → mở tab Cá nhân qua BottomNavigationView
        binding.ivUserAvatar.setOnClickListener(v -> {
            com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = requireActivity()
                    .findViewById(R.id.bottomNavigationView);
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.nav_customer_profile);
            } else {
                if (navController != null) {
                    navController.navigate(R.id.nav_customer_profile);
                }
            }
        });

        binding.tvSeeAll.setOnClickListener(v -> {
            if (navController != null) {
                navController.navigate(R.id.nav_customer_search);
            }
        });

        // Gọi API tải danh sách dịch vụ khi màn hình vừa mở lên
        viewModel.fetchCategories();
    }

    private void navigateToFindingWorker(String serviceName) {
        binding.tvLocationValue.setText(serviceName);
        if (navController != null) {
            navController.navigate(R.id.nav_customer_booking);
        }
    }

    @Override
    protected void observeData() {
        viewModel.categories.observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && !categories.isEmpty()) {
                java.util.List<ServiceCategory> displayList = new java.util.ArrayList<>();
                int limit = Math.min(categories.size(), 7);
                for (int i = 0; i < limit; i++) {
                    displayList.add(categories.get(i));
                }
                displayList.add(new ServiceCategory(-1, "Xem tất cả"));
                serviceAdapter.submitList(displayList);
            } else {
                serviceAdapter.submitList(categories);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Có thể hiển thị ProgressBar nếu muốn
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                showToast(error);
            }
        });
    }
}
