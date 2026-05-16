package com.fixit.feature.worker.wallet.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentWorkerWalletBinding;
import com.google.android.material.tabs.TabLayout;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerWalletFragment extends BaseFragment<FragmentWorkerWalletBinding> {

    private WorkerWalletViewModel viewModel;
    private WalletTransactionAdapter adapter;

    // ──────────────────────────────────────────────────────────────────────────
    // 1. Inflate
    // ──────────────────────────────────────────────────────────────────────────
    @Override
    protected FragmentWorkerWalletBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerWalletBinding.inflate(inflater, container, false);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 2. Setup Views
    // ──────────────────────────────────────────────────────────────────────────
    @Override
    protected void setupViews() {
        // ← Nút back về màn hình Tài khoản
        View btnBack = requireView().findViewById(com.fixit.R.id.btnBack);
        if (btnBack != null) {
            btnBack.setVisibility(View.VISIBLE);
            btnBack.setOnClickListener(v ->
                    requireActivity().getOnBackPressedDispatcher().onBackPressed());
        }

        android.widget.TextView tvTitle = requireView().findViewById(com.fixit.R.id.tvToolbarTitle);
        if (tvTitle != null) tvTitle.setText("Ví tiền");

        // RecyclerView lịch sử giao dịch
        adapter = new WalletTransactionAdapter();
        binding.rvWalletTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvWalletTransactions.setAdapter(adapter);

        // Tab phân loại giao dịch
        binding.tabLayoutTx.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: viewModel.filterByWallet("available"); break;
                    case 1: viewModel.filterByWallet("held");      break;
                    case 2: viewModel.filterByWallet("debt");      break;
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Nút Rút tiền (Ví khả dụng)
        binding.btnWithdraw.setOnClickListener(v ->
                android.widget.Toast.makeText(requireContext(),
                        "Tính năng Rút tiền đang phát triển", android.widget.Toast.LENGTH_SHORT).show()
        );

        // Nút Nạp tiền (Ví ghi nợ)
        binding.btnTopUp.setOnClickListener(v ->
                android.widget.Toast.makeText(requireContext(),
                        "Tính năng Nạp tiền đang phát triển", android.widget.Toast.LENGTH_SHORT).show()
        );

        // Nút Xem chi tiết (Ví tạm giữ)
        binding.btnHeldDetail.setOnClickListener(v -> {
            android.os.Bundle bundle = new android.os.Bundle();
            bundle.putString("orderId", "ORD004"); // Mock trỏ đến đơn có khiếu nại
            androidx.navigation.Navigation.findNavController(requireView())
                    .navigate(com.fixit.R.id.workerComplaintFragment, bundle);
        });
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 3. Observe Data
    // ──────────────────────────────────────────────────────────────────────────
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
            View empty = requireView().findViewById(com.fixit.R.id.layoutEmpty);
            if (empty != null) {
                empty.setVisibility(txList == null || txList.isEmpty()
                        ? View.VISIBLE : View.GONE);
            }
        });
    }
}
