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
    private int currentTabPosition = 0;

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
            String tabType = "available";
            switch (currentTabPosition) {
                case 1: tabType = "held"; break;
                case 2: tabType = "debt"; break;
            }
            viewModel.refresh(tabType);
        });

        // RecyclerView lịch sử giao dịch
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

        // Tab phân loại giao dịch
        binding.tabLayoutTx.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabPosition = tab.getPosition();
                filterCurrentTab();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

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

    private void filterCurrentTab() {
        if (viewModel == null) return;
        switch (currentTabPosition) {
            case 0: viewModel.filterByWallet("available"); break;
            case 1: viewModel.filterByWallet("held");      break;
            case 2: viewModel.filterByWallet("debt");      break;
        }
    }

    @Override
    protected void observeData() {
        viewModel = new ViewModelProvider(this).get(WorkerWalletViewModel.class);

        // SwipeRefresh loading spinner
        viewModel.loading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.swipeRefreshLayout.setRefreshing(isLoading);
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

        // Danh sách giao dịch (lọc theo tab)
        viewModel.filteredTransactions.observe(getViewLifecycleOwner(), txList -> {
            adapter.submitList(txList);

            // Hiện/ẩn empty state
            View empty = requireView().findViewById(R.id.layoutEmpty);
            if (empty != null) {
                empty.setVisibility(txList == null || txList.isEmpty()
                        ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            // Làm mới số dư và danh sách giao dịch in-memory
            String tabType = "available";
            switch (currentTabPosition) {
                case 1: tabType = "held"; break;
                case 2: tabType = "debt"; break;
            }
            viewModel.refresh(tabType);
        }
    }
}
