package com.fixit.ui.worker.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fixit.base.BaseFragment;
import com.fixit.databinding.FragmentWorkerOrdersBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerOrdersFragment extends BaseFragment<FragmentWorkerOrdersBinding> {

    @Override
    protected FragmentWorkerOrdersBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerOrdersBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        android.view.View btnBack = requireView().findViewById(com.fixit.R.id.btnBack);
        if (btnBack != null) {
            btnBack.setVisibility(android.view.View.GONE);
        }
        
        android.widget.TextView tvTitle = requireView().findViewById(com.fixit.R.id.tvToolbarTitle);
        if (tvTitle != null) {
            tvTitle.setText("Đơn hàng");
        }
    }

    @Override
    protected void observeData() {
        // TODO: Observe orders from ViewModel
    }
}
