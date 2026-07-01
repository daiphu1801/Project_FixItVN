package com.fixit.feature.worker.wallet.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentWorkerWalletBinding;
import com.google.android.material.tabs.TabLayout;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerWalletFragment extends BaseFragment<FragmentWorkerWalletBinding> {

    private WorkerWalletViewModel viewModel;
    private WalletTransactionAdapter adapter;

    @Override
    protected FragmentWorkerWalletBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerWalletBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // ← Nút back về màn hình Tài khoản
        View btnBack = requireView().findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setVisibility(View.VISIBLE);
            btnBack.setOnClickListener(v ->
                    requireActivity().getOnBackPressedDispatcher().onBackPressed());
        }

        android.widget.TextView tvTitle = requireView().findViewById(R.id.tvToolbarTitle);
        if (tvTitle != null) tvTitle.setText("Ví tiền thợ");

        // Pull-to-refresh
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.refresh("available");
        });

        // RecyclerView lịch sử giao dịch gần đây
        adapter = new WalletTransactionAdapter();
        binding.rvWalletTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvWalletTransactions.setAdapter(adapter);

        // Click giao dịch để xem chi tiết
        adapter.setOnTransactionClickListener(tx -> {
            Bundle bundle = new Bundle();
            bundle.putString("transactionId", tx.getId());
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_wallet_to_tx_detail, bundle);
        });

        // Nút Xem tất cả lịch sử giao dịch
        binding.btnViewAllTransactions.setOnClickListener(v ->
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_wallet_to_tx_history)
        );

        // Quick Action: Nạp tiền
        binding.btnActionTopUp.setOnClickListener(v ->
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_wallet_to_deposit)
        );

        // Quick Action: Rút tiền
        binding.btnActionWithdraw.setOnClickListener(v ->
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_wallet_to_withdraw)
        );

        // Quick Action: Liên kết ngân hàng
        binding.btnActionBank.setOnClickListener(v ->
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_wallet_to_bank_list)
        );

        // Nút Nạp tiền nhanh ở Ví ghi nợ
        binding.btnTopUp.setOnClickListener(v ->
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_wallet_to_deposit)
        );

        // Nút Quản lý ngân hàng liên kết ở phía dưới
        binding.btnManageBank.setOnClickListener(v ->
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_wallet_to_bank_list)
        );

        // Nút Xem chi tiết khiếu nại của Ví tạm giữ
        binding.btnHeldDetail.setOnClickListener(v -> {
            String bookingId = viewModel.heldBookingId.getValue();
            if (bookingId != null && !bookingId.isEmpty()) {
                Bundle bundle = new Bundle();
                bundle.putString("orderId", bookingId);
                Navigation.findNavController(requireView())
                        .navigate(R.id.workerComplaintFragment, bundle);
            } else {
                android.widget.Toast.makeText(requireContext(), 
                        "Không tìm thấy đơn hàng đang tạm giữ bảo hành", 
                        android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void observeData() {
        viewModel = new ViewModelProvider(this).get(WorkerWalletViewModel.class);

        // SwipeRefresh và loading overlay
        viewModel.loading.observe(getViewLifecycleOwner(), isLoading -> {
            if (binding.swipeRefreshLayout.isRefreshing()) {
                if (!isLoading) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                }
            } else {
                binding.layoutLoading.getRoot().setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        // Số dư 3 loại ví
        viewModel.availableBalance.observe(getViewLifecycleOwner(),
                bal -> binding.tvAvailableBalance.setText(bal));
        viewModel.heldBalance.observe(getViewLifecycleOwner(),
                bal -> binding.tvHeldBalance.setText(bal));
        viewModel.debtBalance.observe(getViewLifecycleOwner(),
                bal -> binding.tvDebtBalance.setText(bal));

        // Thống kê thu nhập
        viewModel.incomeThisWeek.observe(getViewLifecycleOwner(),
                week -> binding.tvIncomeThisWeek.setText(week));
        viewModel.incomeThisMonth.observe(getViewLifecycleOwner(),
                month -> binding.tvIncomeThisMonth.setText(month));

        // Danh sách giao dịch gần đây (lấy top 3)
        viewModel.filteredTransactions.observe(getViewLifecycleOwner(), txList -> {
            java.util.List<com.fixit.feature.worker.wallet.domain.model.WalletTransaction> recentList = new java.util.ArrayList<>();
            if (txList != null) {
                for (int i = 0; i < Math.min(3, txList.size()); i++) {
                    recentList.add(txList.get(i));
                }
            }
            adapter.submitList(recentList);

            // Hiện/ẩn empty state
            View empty = requireView().findViewById(R.id.layoutEmptyState);
            if (empty != null) {
                empty.setVisibility(recentList.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            // Làm mới số dư và danh sách giao dịch in-memory
            viewModel.refresh("available");
        }
    }
}
