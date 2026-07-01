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
import com.fixit.databinding.FragmentWorkerTransactionHistoryBinding;
import com.google.android.material.tabs.TabLayout;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerTransactionHistoryFragment extends BaseFragment<FragmentWorkerTransactionHistoryBinding> {

    private WorkerWalletViewModel viewModel;
    private WalletTransactionAdapter adapter;
    private int currentTabPosition = 0;

    @Override
    protected FragmentWorkerTransactionHistoryBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerTransactionHistoryBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        binding.btnBack.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigateUp()
        );

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            String tabType = "available";
            switch (currentTabPosition) {
                case 1: tabType = "held"; break;
                case 2: tabType = "debt"; break;
            }
            viewModel.refresh(tabType);
        });

        adapter = new WalletTransactionAdapter();
        binding.rvTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTransactions.setAdapter(adapter);

        adapter.setOnTransactionClickListener(tx -> {
            Bundle bundle = new Bundle();
            bundle.putString("transactionId", tx.getId());
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_history_to_tx_detail, bundle);
        });

        binding.tabLayoutTx.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabPosition = tab.getPosition();
                filterCurrentTab();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
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

        viewModel.loading.observe(getViewLifecycleOwner(), isLoading -> {
            if (binding.swipeRefreshLayout.isRefreshing()) {
                if (!isLoading) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                }
            } else {
                binding.layoutLoading.getRoot().setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.filteredTransactions.observe(getViewLifecycleOwner(), txList -> {
            adapter.submitList(txList);
            binding.layoutEmpty.getRoot().setVisibility(txList == null || txList.isEmpty()
                    ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            String tabType = "available";
            switch (currentTabPosition) {
                case 1: tabType = "held"; break;
                case 2: tabType = "debt"; break;
            }
            viewModel.refresh(tabType);
        }
    }
}
