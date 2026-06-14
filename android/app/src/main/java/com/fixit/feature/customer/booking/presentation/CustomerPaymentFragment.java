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

        binding.llCustomerTopbar.tvTitle.setText("Thanh toán");
        binding.llCustomerTopbar.ivBack.setOnClickListener(v -> {
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

        setLoading(true);
        processPaymentUseCase.execute(bookingId, result -> {
            setLoading(false);
            if (result != null && result.isSuccess()) {
                Toast.makeText(requireContext(), "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                if (navController != null) {
                    navController.popBackStack();
                }
            } else {
                String error = result != null ? result.getError().getMessage() : "Lỗi không xác định";
                Toast.makeText(requireContext(), "Thanh toán thất bại: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void observeData() {
    }
}
