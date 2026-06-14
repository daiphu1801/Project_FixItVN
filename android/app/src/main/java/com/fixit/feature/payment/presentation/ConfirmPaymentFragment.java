package com.fixit.feature.payment.presentation;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentConfirmPaymentBinding;

import java.text.NumberFormat;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ConfirmPaymentFragment extends BaseFragment<FragmentConfirmPaymentBinding> {

    private boolean isWorker = true; // Placeholder, should be determined by user role
    private long laborCost = 250000;
    private long materialCost = 120000;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isWorker = getArguments().getBoolean("isWorker", true);
            // In a real app, you'd load order data here
        }
    }

    @Override
    protected FragmentConfirmPaymentBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentConfirmPaymentBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        setupHeader();
        setupRoleUI();
        updateTotal();
        setupListeners();
    }

    @Override
    protected void observeData() {
        // Observe order data from ViewModel if needed
    }

    private void setupHeader() {
        binding.llWorkerTopbar.tvToolbarTitle.setText("Xác nhận thanh toán");
        binding.llWorkerTopbar.btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void setupRoleUI() {
        if (isWorker) {
            binding.tvInstruction.setText("Vui lòng nhập chi phí thực tế để hoàn tất đơn hàng. Đảm bảo các con số là chính xác trước khi gửi yêu cầu cho khách hàng.");
            binding.etLaborCost.setEnabled(true);
            binding.etMaterialCost.setEnabled(true);
            binding.llLaborSuggestions.setVisibility(View.VISIBLE);
            binding.llMaterialSuggestions.setVisibility(View.VISIBLE);
            binding.btnConfirm.setText("Gửi báo giá cho khách");
            binding.btnCancel.setVisibility(View.GONE);
        } else {
            binding.tvInstruction.setText("Vui lòng kiểm tra lại các khoản phí bên dưới. Nếu có sai sót, hãy trao đổi lại với thợ trước khi xác nhận.");
            binding.etLaborCost.setEnabled(false);
            binding.etMaterialCost.setEnabled(false);
            binding.llLaborSuggestions.setVisibility(View.GONE);
            binding.llMaterialSuggestions.setVisibility(View.GONE);
            binding.btnConfirm.setText("Xác nhận thanh toán");
            binding.btnCancel.setVisibility(View.VISIBLE);
            binding.btnCancel.setText("Hủy / Khiếu nại");
        }
    }

    private void setupListeners() {
        TextWatcher costWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                updateTotalFromInputs();
            }
        };

        binding.etLaborCost.addTextChangedListener(costWatcher);
        binding.etMaterialCost.addTextChangedListener(costWatcher);

        binding.btnConfirm.setOnClickListener(v -> {
            String message = isWorker ? "Đã gửi báo giá cho khách hàng!" : "Đã xác nhận thanh toán thành công!";
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigateUp();
        });

        binding.btnCancel.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Đã gửi yêu cầu khiếu nại!", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigateUp();
        });
    }

    private void updateTotalFromInputs() {
        try {
            String laborStr = binding.etLaborCost.getText().toString().replaceAll("[^\\d]", "");
            String materialStr = binding.etMaterialCost.getText().toString().replaceAll("[^\\d]", "");
            
            laborCost = laborStr.isEmpty() ? 0 : Long.parseLong(laborStr);
            materialCost = materialStr.isEmpty() ? 0 : Long.parseLong(materialStr);
            
            updateTotal();
        } catch (NumberFormatException ignored) {}
    }

    private void updateTotal() {
        long total = laborCost + materialCost;
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        binding.tvTotalAmount.setText(formatter.format(total) + " đ");
    }
}
