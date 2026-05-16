package com.fixit.feature.worker.chat.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentChatWorkerBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerChatFragment extends BaseFragment<FragmentChatWorkerBinding> {

    @Override
    protected FragmentChatWorkerBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentChatWorkerBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // TODO: Setup chat list
    }

    @Override
    protected void observeData() {
        // TODO: Observe chat messages
    }
}
