package com.fixit.feature.worker.wallet.presentation;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentWorkerTransactionDetailBinding;
import com.fixit.feature.worker.wallet.domain.model.WalletTransaction;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerTransactionDetailFragment extends BaseFragment<FragmentWorkerTransactionDetailBinding> {

    private WorkerTransactionDetailViewModel viewModel;
    private String transactionId = "";

    @Override
    protected FragmentWorkerTransactionDetailBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerTransactionDetailBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        if (getArguments() != null) {
            transactionId = getArguments().getString("transactionId", "");
        }

        binding.btnBack.setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());

        binding.btnCancelWithdraw.setOnClickListener(v -> {
            if (viewModel != null && !transactionId.isEmpty()) {
                viewModel.cancelWithdrawal(transactionId);
                Toast.makeText(requireContext(), 
                        "Đã hủy yêu cầu rút tiền thành công, tiền đã hoàn lại ví khả dụng!", 
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void observeData() {
        viewModel = new ViewModelProvider(this).get(WorkerTransactionDetailViewModel.class);

        if (!transactionId.isEmpty()) {
            viewModel.loadTransaction(transactionId);
        }

        viewModel.transaction.observe(getViewLifecycleOwner(), tx -> {
            if (tx == null) {
                Toast.makeText(requireContext(), "Không tìm thấy thông tin giao dịch", Toast.LENGTH_SHORT).show();
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
                return;
            }

            // Bind values
            binding.tvTxId.setText(tx.getId());
            binding.tvTxDate.setText(tx.getDate());
            binding.tvTitle.setText(tx.getTitle());
            binding.tvAmount.setText(tx.getAmount());

            // Wallet type
            String walletType = "Ví khả dụng";
            if (tx.getAmount().contains("+") || tx.getTitle().contains("Nạp")) {
                binding.tvAmount.setTextColor(Color.parseColor("#10b981")); // Green for positive
            } else {
                binding.tvAmount.setTextColor(Color.parseColor("#ef4444")); // Red for negative
            }

            // Status Badge styling
            String status = tx.getStatus();
            if (status == null) status = "SUCCESS";

            switch (status) {
                case "PENDING":
                    binding.tvStatusBadge.setText("ĐANG CHỜ DUYỆT");
                    binding.tvStatusBadge.setBackgroundColor(Color.parseColor("#fef3c7"));
                    binding.tvStatusBadge.setTextColor(Color.parseColor("#d97706"));
                    
                    // Show cancel section if it is a pending withdrawal
                    if (tx.getTitle().contains("Rút tiền")) {
                        binding.layoutCancelSection.setVisibility(View.VISIBLE);
                    } else {
                        binding.layoutCancelSection.setVisibility(View.GONE);
                    }
                    break;

                case "CANCELLED":
                    binding.tvStatusBadge.setText("ĐÃ HỦY");
                    binding.tvStatusBadge.setBackgroundColor(Color.parseColor("#fee2e2"));
                    binding.tvStatusBadge.setTextColor(Color.parseColor("#ef4444"));
                    binding.layoutCancelSection.setVisibility(View.GONE);
                    binding.tvAmount.setTextColor(Color.parseColor("#64748b")); // Grey out cancelled amount
                    break;

                case "SUCCESS":
                default:
                    binding.tvStatusBadge.setText("THÀNH CÔNG");
                    binding.tvStatusBadge.setBackgroundColor(Color.parseColor("#d1fae5"));
                    binding.tvStatusBadge.setTextColor(Color.parseColor("#059669"));
                    binding.layoutCancelSection.setVisibility(View.GONE);
                    break;
            }
        });
    }
}
