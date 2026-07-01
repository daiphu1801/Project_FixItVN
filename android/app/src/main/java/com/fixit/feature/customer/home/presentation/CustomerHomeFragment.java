package com.fixit.feature.customer.home.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.fixit.R;
import com.fixit.core.common.AutoRefreshHelper;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerHomeBinding;
import com.fixit.feature.customer.service.domain.model.ServiceCategory;
import com.fixit.feature.customer.service.domain.model.ServiceItem;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CustomerHomeFragment extends BaseFragment<FragmentCustomerHomeBinding> {

    private CustomerHomeViewModel viewModel;
    private HomeServiceGridAdapter serviceAdapter;
    private AutoRefreshHelper autoRefreshHelper;

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
                        navigateToFindingWorker(category.getId(), category.getName(), item.getName());
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

        // Tải ảnh avatar từ cache SharedPreferences để hiển thị nhanh
        String savedAvatar = requireContext().getSharedPreferences(com.fixit.core.common.Constants.PREF_NAME, android.content.Context.MODE_PRIVATE)
                .getString("user_avatar", null);
        if (savedAvatar != null && !savedAvatar.isEmpty()) {
            binding.ivUserAvatar.setPadding(0, 0, 0, 0);
            binding.ivUserAvatar.setImageTintList(null);
            Glide.with(this).load(savedAvatar).circleCrop().into(binding.ivUserAvatar);
        } else {
            binding.ivUserAvatar.setImageTintList(android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#42c2ff")
            ));
            int p = (int) (8 * getResources().getDisplayMetrics().density);
            binding.ivUserAvatar.setPadding(p, p, p, p);
            binding.ivUserAvatar.setImageResource(R.drawable.ic_lucide_user);
        }

        // Gọi API tải danh sách dịch vụ khi màn hình vừa mở lên
        viewModel.fetchCategories();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (autoRefreshHelper == null) {
            autoRefreshHelper = new AutoRefreshHelper(
                    requireContext(),
                    0L,
                    () -> viewModel.fetchProfile(),
                    "com.fixit.PROFILE_UPDATE"
            );
        }
        autoRefreshHelper.start();
    }

    @Override
    public void onPause() {
        if (autoRefreshHelper != null) {
            autoRefreshHelper.stop();
        }
        super.onPause();
    }

    private void navigateToFindingWorker(int serviceId, String serviceName, String subServiceName) {
        binding.tvLocationValue.setText(subServiceName);
        if (navController != null) {
            android.os.Bundle args = new android.os.Bundle();
            args.putInt("serviceId", serviceId);
            args.putString("serviceName", serviceName);
            args.putString("subServiceName", subServiceName);
            navController.navigate(R.id.nav_customer_booking, args);
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

        viewModel.profile.observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                if (profile.getFullName() != null && !profile.getFullName().trim().isEmpty()) {
                    binding.tvGreeting.setText("Xin chào, " + profile.getFullName() + " 👋");
                }
                String avatarUrl = profile.getAvatarUrl();
                if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
                    requireContext().getSharedPreferences(com.fixit.core.common.Constants.PREF_NAME, android.content.Context.MODE_PRIVATE)
                            .edit()
                            .putString("user_avatar", avatarUrl)
                            .apply();
                    binding.ivUserAvatar.setPadding(0, 0, 0, 0);
                    binding.ivUserAvatar.setImageTintList(null);
                    Glide.with(this)
                            .load(avatarUrl)
                            .placeholder(R.drawable.ic_lucide_user)
                            .error(R.drawable.ic_lucide_user)
                            .circleCrop()
                            .into(binding.ivUserAvatar);
                } else {
                    requireContext().getSharedPreferences(com.fixit.core.common.Constants.PREF_NAME, android.content.Context.MODE_PRIVATE)
                            .edit()
                            .remove("user_avatar")
                            .apply();
                    binding.ivUserAvatar.setImageTintList(android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor("#42c2ff")
                    ));
                    int p = (int) (8 * getResources().getDisplayMetrics().density);
                    binding.ivUserAvatar.setPadding(p, p, p, p);
                    binding.ivUserAvatar.setImageResource(R.drawable.ic_lucide_user);
                }
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
