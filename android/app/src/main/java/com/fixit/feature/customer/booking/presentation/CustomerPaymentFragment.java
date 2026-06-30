package com.fixit.feature.customer.booking.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerPaymentBinding;
import com.fixit.feature.customer.booking.domain.usecase.ProcessPaymentUseCase;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CustomerPaymentFragment extends BaseFragment<FragmentCustomerPaymentBinding> {

    @Inject
    ProcessPaymentUseCase processPaymentUseCase;

    private String bookingId;
    private BigDecimal finalPrice;

    @NonNull
    @Override
    protected FragmentCustomerPaymentBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentCustomerPaymentBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        if (getArguments() != null) {
            bookingId = getArguments().getString("bookingId");
            finalPrice = (BigDecimal) getArguments().getSerializable("finalPrice");
        }

        binding.llCustomerTopbar.tvToolbarTitle.setText("Thanh toán");
        binding.llCustomerTopbar.btnBack.setOnClickListener(v -> {
            if (navController != null) navController.popBackStack();
        });

        if (finalPrice != null) {
            NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
            binding.tvFinalPrice.setText(format.format(finalPrice) + " đ");
        }

        binding.btnConfirmPayment.setOnClickListener(v -> processPayment());
    }

    private void processPayment() {
        if (bookingId == null) {
            Toast.makeText(requireContext(), "Lỗi: Không tìm thấy mã đơn hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        if (binding.rgPaymentMethods.getCheckedRadioButtonId() == -1) {
            Toast.makeText(requireContext(), "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy phương thức thanh toán từ UI (mặc định CASH vì flow này chạy qua dialog khác)
        // CustomerPaymentFragment là màn hình cũ, payment method được chọn tại CustomerOrderDetailFragment
        final String selectedPaymentMethod = "CASH";

        binding.btnConfirmPayment.setEnabled(false);
        processPaymentUseCase.execute(bookingId, selectedPaymentMethod, result -> {
            binding.btnConfirmPayment.setEnabled(true);
            if (result != null && result.isSuccess()) {
                Toast.makeText(requireContext(), "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                if (navController != null) {
                    navController.popBackStack();
                }
            } else {
                String error = result != null && result.getError() != null ? result.getError().getMessage() : "Lỗi không xác định";
                Toast.makeText(requireContext(), "Thanh toán thất bại: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        binding.btnConfirmPayment.setEnabled(!loading);
        binding.btnConfirmPayment.setText(loading ? "Đang xử lý..." : "Xác nhận thanh toán");
    }

    @Override
    protected void observeData() {
    }
}
