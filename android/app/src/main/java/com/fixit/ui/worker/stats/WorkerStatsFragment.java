package com.fixit.ui.worker.stats;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.fixit.base.BaseFragment;
import com.fixit.databinding.FragmentWorkerStatsBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerStatsFragment extends BaseFragment<FragmentWorkerStatsBinding> {

    @Override
    protected FragmentWorkerStatsBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerStatsBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // TODO: Setup stats charts
    }

    @Override
    protected void observeData() {
        // TODO: Observe stats data
    }
}
