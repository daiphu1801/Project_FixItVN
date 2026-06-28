package com.fixit.feature.worker.orders.presentation;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.fixit.R;
import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentConfirmPaymentBinding;
import com.fixit.feature.worker.orders.domain.model.WorkerOrder;
import com.fixit.feature.worker.orders.domain.usecase.GetWorkerOrderByIdUseCase;
import com.fixit.feature.worker.orders.domain.usecase.SubmitQuotationUseCase;
import dagger.hilt.android.AndroidEntryPoint;

import java.math.BigDecimal;
import javax.inject.Inject;

@AndroidEntryPoint
public class WorkerConfirmPaymentFragment extends BaseFragment<FragmentConfirmPaymentBinding> {

    @Inject
    SubmitQuotationUseCase submitQuotationUseCase;

    @Inject
    GetWorkerOrderByIdUseCase getWorkerOrderByIdUseCase;

    private String bookingId = "";
    private long laborCostVal = 0;

    @NonNull
    @Override
    protected FragmentConfirmPaymentBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentConfirmPaymentBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        if (getArguments() != null) {
            bookingId = getArguments().getString("bookingId", "");
            laborCostVal = getArguments().getLong("laborCost", 0);
        }

        binding.llWorkerTopbar.tvToolbarTitle.setText("Báo giá sửa chữa");
        binding.llWorkerTopbar.btnBack.setOnClickListener(v -> {
            if (navController != null) navController.popBackStack();
        });

        // Set fixed Labor Cost
        binding.etLaborCost.setText(String.valueOf(laborCostVal));
        binding.etLaborCost.setEnabled(false);
        binding.llLaborSuggestions.setVisibility(View.GONE);

        // Nút Gửi Báo Giá
        binding.btnConfirm.setText("Gửi báo giá cho khách");
        binding.btnConfirm.setOnClickListener(v -> submitQuotation());

        setupSuggestionButtons();
        setupTextWatchers();
        loadOrderDetails();
        calculateTotal();
    }

    private void loadOrderDetails() {
        if (bookingId.isEmpty()) return;
        
        setLoading(true);
        getWorkerOrderByIdUseCase.execute(bookingId, new ResultCallback<WorkerOrder>() {
            @Override
            public void onResult(Result<WorkerOrder> result) {
                setLoading(false);
                if (result.isSuccess() && result.getData() != null) {
                    WorkerOrder order = result.getData();
                    binding.tvOrderId.setText("#" + order.getOrderId());
                    binding.tvServiceTitle.setText(order.getServiceTitle());
                    binding.tvLocation.setText(order.getAddress());
                    binding.tvStatus.setText("ĐANG KHẢO SÁT");
                } else {
                    Toast.makeText(requireContext(), "Không thể tải chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupSuggestionButtons() {
        // Material suggestions
        for (int i = 0; i < binding.llMaterialSuggestions.getChildCount(); i++) {
            com.google.android.material.button.MaterialButton btn = (com.google.android.material.button.MaterialButton) binding.llMaterialSuggestions.getChildAt(i);
            btn.setOnClickListener(v -> {
                String text = btn.getText().toString().replaceAll("[^0-9]", "");
                binding.etMaterialCost.setText(text);
            });
        }
    }

    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                calculateTotal();
            }
        };

        binding.etLaborCost.addTextChangedListener(textWatcher);
        binding.etMaterialCost.addTextChangedListener(textWatcher);
    }

    private void calculateTotal() {
        try {
            String laborStr = binding.etLaborCost.getText().toString();
            String materialStr = binding.etMaterialCost.getText().toString();

            BigDecimal labor = laborStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(laborStr);
            BigDecimal material = materialStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(materialStr);

            BigDecimal total = labor.add(material);
            
            java.text.NumberFormat format = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
            binding.tvTotalAmount.setText(format.format(total) + " đ");
        } catch (Exception e) {
            binding.tvTotalAmount.setText("0 đ");
        }
    }

    private void submitQuotation() {
        try {
            String laborStr = binding.etLaborCost.getText().toString();
            String materialStr = binding.etMaterialCost.getText().toString();

            BigDecimal labor = laborStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(laborStr);
            BigDecimal material = materialStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(materialStr);

            if (material.compareTo(BigDecimal.ZERO) < 0) {
                Toast.makeText(requireContext(), "Tiền vật tư không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            setLoading(true);
            submitQuotationUseCase.execute(bookingId, labor, material, result -> {
                setLoading(false);
                if (result != null && result.isSuccess()) {
                    Toast.makeText(requireContext(), "Đã gửi báo giá cho khách hàng thành công!", Toast.LENGTH_SHORT).show();
                    if (navController != null) navController.popBackStack();
                } else {
                    String errorMsg = (result != null && result.getError() != null) ? result.getError().getMessage() : "Lỗi không xác định";
                    Toast.makeText(requireContext(), "Lỗi: " + errorMsg, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Vui lòng nhập số tiền hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private void setLoading(boolean loading) {
        binding.btnConfirm.setEnabled(!loading);
        binding.btnConfirm.setText(loading ? "Đang xử lý..." : "Gửi báo giá cho khách");
        if (binding.layoutLoading != null) {
            binding.layoutLoading.getRoot().setVisibility(loading ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    protected void observeData() {
    }
}
