package com.fixit.ui.worker.wallet;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.fixit.base.BaseFragment;
import com.fixit.databinding.FragmentWorkerWalletBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerWalletFragment extends BaseFragment<FragmentWorkerWalletBinding> {

    @Override
    protected FragmentWorkerWalletBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerWalletBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        android.view.View btnBack = requireView().findViewById(com.fixit.R.id.btnBack);
        if (btnBack != null) {
            btnBack.setVisibility(android.view.View.GONE);
        }
        
        android.widget.TextView tvTitle = requireView().findViewById(com.fixit.R.id.tvToolbarTitle);
        if (tvTitle != null) {
            tvTitle.setText("Ví");
        }
    }

    @Override
    protected void observeData() {
        // TODO: Observe wallet balance
    }
}
