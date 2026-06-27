package com.fixit.feature.customer.order.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerCancelOrderBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CustomerCancelOrderFragment extends BaseFragment<FragmentCustomerCancelOrderBinding> {

    private CustomerOrderViewModel orderViewModel;
    private boolean isCancelling = false;
    private boolean isWorkerFault = false;

    @NonNull
    @Override
    protected FragmentCustomerCancelOrderBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentCustomerCancelOrderBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        orderViewModel = new ViewModelProvider(requireActivity()).get(CustomerOrderViewModel.class);

        binding.ivBack.setOnClickListener(v -> {
            if (navController != null) {
                navController.popBackStack();
            }
        });

        binding.btnKeepOrder.setOnClickListener(v -> {
            if (navController != null) {
                navController.popBackStack();
            }
        });

        // Clear worker reason if customer reason is picked
        binding.rgReasonCustomer.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                binding.rgReasonWorker.clearCheck();
            }
        });

        // Clear customer reason if worker reason is picked
        binding.rgReasonWorker.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                binding.rgReasonCustomer.clearCheck();
            }
        });

        binding.btnConfirmCancel.setOnClickListener(v -> {
            int customerCheckedId = binding.rgReasonCustomer.getCheckedRadioButtonId();
            int workerCheckedId = binding.rgReasonWorker.getCheckedRadioButtonId();

            if (customerCheckedId != -1) {
                isCancelling = true;
                isWorkerFault = false;
                orderViewModel.cancelCurrentBooking("Lý do cá nhân", false);
            } else if (workerCheckedId != -1) {
                isCancelling = true;
                isWorkerFault = true;
                orderViewModel.cancelCurrentBooking("Lỗi do thợ", true);
            } else {
                Toast.makeText(requireContext(), "Vui lòng chọn lý do hủy", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void observeData() {
        if (orderViewModel != null) {
            orderViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
                if (isLoading != null && binding.layoutLoading != null) {
                    binding.layoutLoading.getRoot().setVisibility(isLoading ? android.view.View.VISIBLE : android.view.View.GONE);
                }
            });

            orderViewModel.orderStatus.observe(getViewLifecycleOwner(), status -> {
                if (isCancelling) {
                    if (!isWorkerFault && status == 0) {
                        isCancelling = false;
                        Toast.makeText(requireContext(), "Đã hủy đơn thành công", Toast.LENGTH_SHORT).show();
                        if (navController != null) {
                            navController.navigate(R.id.nav_customer_home);
                        }
                    } else if (isWorkerFault && status == 1) {
                        isCancelling = false;
                        Toast.makeText(requireContext(), "Đang tìm thợ khác cho bạn", Toast.LENGTH_SHORT).show();
                        if (navController != null) {
                            navController.navigate(R.id.nav_customer_order);
                        }
                    }
                }
            });

            orderViewModel.error.observe(getViewLifecycleOwner(), error -> {
                if (isCancelling && error != null && !error.isEmpty()) {
                    isCancelling = false;
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
