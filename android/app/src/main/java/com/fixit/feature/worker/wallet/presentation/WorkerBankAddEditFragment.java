package com.fixit.feature.worker.wallet.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentWorkerBankAddEditBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerBankAddEditFragment extends BaseFragment<FragmentWorkerBankAddEditBinding> {

    private WorkerBankViewModel viewModel;
    private String editingBankAccountId;
    private final String[] BANKS = {
            "MB Bank", "Vietcombank", "Techcombank", "Agribank", "BIDV", "ACB", "Sacombank"
    };

    @Override
    protected FragmentWorkerBankAddEditBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerBankAddEditBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        binding.btnBack.setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, BANKS);
        binding.autoCompleteBank.setAdapter(adapter);

        readEditArgs();
        binding.btnSaveBank.setOnClickListener(v -> saveBankAccount());
    }

    private void readEditArgs() {
        Bundle args = getArguments();
        if (args == null) {
            return;
        }

        editingBankAccountId = args.getString("bankAccountId", "");
        if (editingBankAccountId == null || editingBankAccountId.trim().isEmpty()) {
            editingBankAccountId = null;
            return;
        }

        binding.tvTitle.setText("Cập nhật tài khoản");
        binding.tvDesc.setText("Bạn có thể đổi ngân hàng, tên chủ tài khoản hoặc nhập số tài khoản mới. Vì bảo mật, app không hiển thị lại số tài khoản đầy đủ.");
        binding.btnSaveBank.setText("Cập nhật tài khoản");
        binding.cbSetDefault.setEnabled(false);
        binding.cbSetDefault.setAlpha(0.4f);

        binding.autoCompleteBank.setText(args.getString("bankName", ""), false);
        binding.etAccountHolder.setText(args.getString("accountHolderName", ""));
        binding.etAccountNumber.setText("");
        binding.tilAccountNumber.setHint("Số tài khoản mới nếu muốn đổi");
    }

    private void saveBankAccount() {
        String bankName = binding.autoCompleteBank.getText() != null
                ? binding.autoCompleteBank.getText().toString().trim()
                : "";
        String accountNumber = binding.etAccountNumber.getText() != null
                ? binding.etAccountNumber.getText().toString().trim()
                : "";
        String accountHolder = binding.etAccountHolder.getText() != null
                ? binding.etAccountHolder.getText().toString().trim()
                : "";
        boolean isDefault = binding.cbSetDefault.isChecked();

        if (bankName.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng chọn ngân hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editingBankAccountId == null && (accountNumber.isEmpty() || accountNumber.length() < 4)) {
            Toast.makeText(requireContext(), "Số tài khoản không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editingBankAccountId != null && !accountNumber.isEmpty() && accountNumber.length() < 4) {
            Toast.makeText(requireContext(), "Số tài khoản mới không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (accountHolder.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng điền tên chủ tài khoản", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editingBankAccountId == null) {
            viewModel.addBank(bankName, accountNumber, accountHolder, isDefault);
        } else {
            viewModel.updateBank(editingBankAccountId, bankName, accountNumber, accountHolder);
        }
    }

    @Override
    protected void observeData() {
        viewModel = new ViewModelProvider(this).get(WorkerBankViewModel.class);

        viewModel.message.observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.trim().isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.saveSuccess.observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                viewModel.clearSaveSuccess();
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });

        viewModel.loading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.layoutLoading.getRoot().setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }
}