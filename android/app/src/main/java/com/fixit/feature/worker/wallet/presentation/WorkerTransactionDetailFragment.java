package com.fixit.feature.worker.wallet.presentation;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.fixit.R;
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

        binding.btnBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        binding.btnCancelWithdraw.setOnClickListener(v -> {
            if (viewModel != null && !transactionId.isEmpty()) {
                viewModel.cancelWithdrawal(transactionId);
                Toast.makeText(requireContext(),
                        "Đã hủy yêu cầu rút tiền thành công, tiền đã hoàn lại ví khả dụng!",
                        Toast.LENGTH_LONG).show();
            }
        });

        binding.btnShowQr.setOnClickListener(v -> {
            if (!transactionId.isEmpty()) {
                Bundle bundle = new Bundle();
                bundle.putString("transactionId", transactionId);
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_tx_detail_to_deposit, bundle);
            }
        });

        binding.btnCancelDeposit.setOnClickListener(v -> {
            if (viewModel != null && !transactionId.isEmpty()) {
                viewModel.cancelDeposit(transactionId);
            }
        });
    }

    @Override
    protected void observeData() {
        viewModel = new ViewModelProvider(this).get(WorkerTransactionDetailViewModel.class);

        if (!transactionId.isEmpty()) {
            viewModel.loadTransaction(transactionId);
        }

        viewModel.message.observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.loading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.layoutLoading.getRoot().setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

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

            String prefix = tx.isCredit() ? "+ " : "- ";
            binding.tvAmount.setText(prefix + tx.getAmount());

            // Wallet type
            String walletTypeStr = "Ví khả dụng";
            if ("held".equalsIgnoreCase(tx.getWalletType())) {
                walletTypeStr = "Ví tạm giữ";
            } else if ("debt".equalsIgnoreCase(tx.getWalletType())) {
                walletTypeStr = "Ví ghi nợ";
            }
            binding.tvWalletType.setText(walletTypeStr);

            if (tx.isCredit()) {
                binding.tvAmount.setTextColor(Color.parseColor("#10b981")); // Green for positive
            } else {
                binding.tvAmount.setTextColor(Color.parseColor("#ef4444")); // Red for negative
            }

            // Status Badge styling
            String status = tx.getStatus();
            if (status == null)
                status = "SUCCESS";

            switch (status) {
                case "PENDING":
                    binding.tvStatusBadge.setText("ĐANG CHỜ DUYỆT");
                    binding.tvStatusBadge.setBackgroundColor(Color.parseColor("#fef3c7"));
                    binding.tvStatusBadge.setTextColor(Color.parseColor("#d97706"));

                    binding.layoutCancelSection.setVisibility(View.VISIBLE);
                    // Show correct buttons based on whether it is a deposit (credit/Top Up) or
                    // withdrawal
                    if (tx.getTitle().contains("Nạp tiền") || tx.isCredit()) {
                        binding.btnCancelWithdraw.setVisibility(View.GONE);
                        binding.btnShowQr.setVisibility(View.VISIBLE);
                        binding.btnCancelDeposit.setVisibility(View.VISIBLE);
                    } else {
                        binding.btnCancelWithdraw.setVisibility(View.VISIBLE);
                        binding.btnShowQr.setVisibility(View.GONE);
                        binding.btnCancelDeposit.setVisibility(View.GONE);
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
