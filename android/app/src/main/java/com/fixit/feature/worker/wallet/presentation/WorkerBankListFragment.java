package com.fixit.feature.worker.wallet.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentWorkerBankListBinding;
import com.fixit.feature.worker.wallet.domain.model.BankAccount;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerBankListFragment extends BaseFragment<FragmentWorkerBankListBinding> {

    private WorkerBankViewModel viewModel;
    private BankAccountAdapter adapter;

    @Override
    protected FragmentWorkerBankListBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerBankListBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        binding.btnBack.setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());

        binding.btnAddBank.setOnClickListener(v ->
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_bank_list_to_add_edit));

        adapter = new BankAccountAdapter(new BankAccountAdapter.OnBankInteractionListener() {
            @Override
            public void onItemClick(BankAccount account) {
                Bundle bundle = new Bundle();
                bundle.putString("bankAccountId", account.getId());
                bundle.putString("bankName", account.getBankName());
                bundle.putString("accountHolderName", account.getAccountHolderName());
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_bank_list_to_add_edit, bundle);
            }

            @Override
            public void onDeleteClick(BankAccount account) {
                viewModel.deleteBank(account.getId());
            }

            @Override
            public void onSetDefaultClick(BankAccount account) {
                viewModel.setDefaultBank(account.getId());
            }
        });

        binding.rvBankAccounts.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvBankAccounts.setAdapter(adapter);
    }

    @Override
    protected void observeData() {
        viewModel = new ViewModelProvider(this).get(WorkerBankViewModel.class);

        viewModel.bankAccounts.observe(getViewLifecycleOwner(), bankList -> {
            if (bankList == null || bankList.isEmpty()) {
                binding.layoutEmptyState.setVisibility(View.VISIBLE);
                binding.rvBankAccounts.setVisibility(View.GONE);
            } else {
                binding.layoutEmptyState.setVisibility(View.GONE);
                binding.rvBankAccounts.setVisibility(View.VISIBLE);
                adapter.submitList(bankList);
            }
        });

        viewModel.message.observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.trim().isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.loadBankAccounts();
        }
    }
}