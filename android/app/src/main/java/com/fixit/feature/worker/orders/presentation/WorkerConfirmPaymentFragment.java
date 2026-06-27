package com.fixit.feature.worker.orders.presentation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentConfirmPaymentBinding;
import com.fixit.feature.worker.orders.domain.usecase.SubmitQuotationUseCase;
import dagger.hilt.android.AndroidEntryPoint;

import java.math.BigDecimal;
import javax.inject.Inject;

@AndroidEntryPoint
public class WorkerConfirmPaymentFragment extends BaseFragment<FragmentConfirmPaymentBinding> {

    @Inject
    SubmitQuotationUseCase submitQuotationUseCase;

    private String bookingId = "12345678-1234-1234-1234-123456789abc"; // TODO: Lấy từ arguments

    @NonNull
    @Override
    protected FragmentConfirmPaymentBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentConfirmPaymentBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        setupSuggestionButtons();
        setupTextWatchers();

        binding.btnConfirm.setOnClickListener(v -> submitQuotation());
        
        binding.llWorkerTopbar.btnBack.setOnClickListener(v -> {
            if (navController != null) navController.popBackStack();
        });
        
        binding.llWorkerTopbar.tvToolbarTitle.setText("Xác nhận thanh toán");
        
        calculateTotal();
    }

    private void setupSuggestionButtons() {
        // Labor suggestions
        for (int i = 0; i < binding.llLaborSuggestions.getChildCount(); i++) {
            com.google.android.material.button.MaterialButton btn = (com.google.android.material.button.MaterialButton) binding.llLaborSuggestions.getChildAt(i);
            btn.setOnClickListener(v -> {
                String text = btn.getText().toString().replaceAll("[^0-9]", "");
                binding.etLaborCost.setText(text);
            });
        }

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

            binding.btnConfirm.setEnabled(false);
            submitQuotationUseCase.execute(bookingId, labor, material, result -> {
                binding.btnConfirm.setEnabled(true);
                if (result != null && result.isSuccess()) {
                    Toast.makeText(requireContext(), "Đã gửi báo giá cho khách hàng", Toast.LENGTH_SHORT).show();
                    if (navController != null) navController.popBackStack();
                } else if (result != null) {
                    Toast.makeText(requireContext(), "Lỗi: " + result.getError().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Vui lòng nhập số tiền hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void observeData() {
    }
}
