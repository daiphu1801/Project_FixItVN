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

        // Nút Rút tiền (Ví khả dụng)
        binding.btnWithdraw.setOnClickListener(v ->
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_wallet_to_withdraw)
        );

        // Nút Nạp tiền (Ví ghi nợ)
        binding.btnTopUp.setOnClickListener(v ->
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_wallet_to_deposit)
        );

        // Nút Quản lý ngân hàng liên kết
        binding.btnManageBank.setOnClickListener(v ->
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_wallet_to_bank_list)
        );

        // Nút Xem chi tiết (Ví tạm giữ)
        binding.btnHeldDetail.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("orderId", "ORD004"); // Mock trỏ đến đơn có khiếu nại
            Navigation.findNavController(requireView())
                    .navigate(R.id.workerComplaintFragment, bundle);
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

        // Số dư 3 loại ví
        viewModel.availableBalance.observe(getViewLifecycleOwner(),
                bal -> binding.tvAvailableBalance.setText(bal));
        viewModel.heldBalance.observe(getViewLifecycleOwner(),
                bal -> binding.tvHeldBalance.setText(bal));
        viewModel.debtBalance.observe(getViewLifecycleOwner(),
                bal -> binding.tvDebtBalance.setText(bal));

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
