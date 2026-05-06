package com.fixit.ui.worker.chat;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.fixit.base.BaseFragment;
import com.fixit.databinding.FragmentWorkerChatBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerChatFragment extends BaseFragment<FragmentWorkerChatBinding> {

    @Override
    protected FragmentWorkerChatBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerChatBinding.inflate(inflater, container, false);
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
