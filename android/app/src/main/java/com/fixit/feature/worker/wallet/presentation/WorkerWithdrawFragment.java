package com.fixit.feature.worker.wallet.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentWorkerWithdrawBinding;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerWithdrawFragment extends BaseFragment<FragmentWorkerWithdrawBinding> {

    private WorkerWithdrawViewModel viewModel;
    private long maxAvailable = 0;

    @Override
    protected FragmentWorkerWithdrawBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerWithdrawBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        binding.btnBack.setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());

        binding.cardBankAccount.setOnClickListener(v -> {
            // Nếu chưa có ngân hàng nào, click để liên kết
            if (viewModel.defaultBankAccount.getValue() == null) {
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_wallet_to_bank_list);
            }
        });

        // Setup các nút gợi ý tiền nhanh
        binding.btn100k.setOnClickListener(v -> binding.etAmount.setText("100000"));
        binding.btn200k.setOnClickListener(v -> binding.etAmount.setText("200000"));
        binding.btn500k.setOnClickListener(v -> binding.etAmount.setText("500000"));
        binding.btnMax.setOnClickListener(v -> binding.etAmount.setText(String.valueOf(maxAvailable)));

        binding.btnConfirmWithdraw.setOnClickListener(v -> performWithdrawal());
    }

    private void performWithdrawal() {
        String amtStr = binding.etAmount.getText() != null ? binding.etAmount.getText().toString().trim() : "";
        if (amtStr.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập số tiền rút", Toast.LENGTH_SHORT).show();
            return;
        }

        long amount;
        try {
            amount = Long.parseLong(amtStr);
        } catch (Exception ignored) {
            Toast.makeText(requireContext(), "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount < 50000) {
            Toast.makeText(requireContext(), "Số tiền rút tối thiểu là 50.000 đ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount > maxAvailable) {
            Toast.makeText(requireContext(), "Số tiền rút vượt quá số dư khả dụng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi async — kết quả sẽ được xử lý qua observer message
        viewModel.submitWithdrawal(amount);
    }

    @Override
    protected void observeData() {
        viewModel = new ViewModelProvider(this).get(WorkerWithdrawViewModel.class);

        viewModel.availableBalanceStr.observe(getViewLifecycleOwner(), balStr ->
                binding.tvAvailableBalance.setText(balStr));

        viewModel.availableAmount.observe(getViewLifecycleOwner(), amt -> {
            maxAvailable = amt != null ? amt : 0;
        });

        viewModel.defaultBankAccount.observe(getViewLifecycleOwner(), acc -> {
            if (acc == null) {
                binding.layoutBankDetails.setVisibility(View.GONE);
                binding.layoutNoBank.setVisibility(View.VISIBLE);
                binding.btnConfirmWithdraw.setEnabled(false);
                binding.btnConfirmWithdraw.setAlpha(0.5f);
            } else {
                binding.layoutBankDetails.setVisibility(View.VISIBLE);
                binding.layoutNoBank.setVisibility(View.GONE);
                binding.btnConfirmWithdraw.setEnabled(true);
                binding.btnConfirmWithdraw.setAlpha(1.0f);

                binding.tvBankName.setText(acc.getBankName());
                binding.tvAccountNumber.setText(acc.getAccountNumber());
                binding.tvAccountHolder.setText(acc.getAccountHolderName());
            }
        });
        viewModel.message.observe(getViewLifecycleOwner(), msg -> {
            if (msg == null || msg.isEmpty()) return;
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            // Nếu thành công, quay về
            if (msg.contains("thành công") || msg.contains("đã được gửi")) {
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.loadData();
        }
    }
}
