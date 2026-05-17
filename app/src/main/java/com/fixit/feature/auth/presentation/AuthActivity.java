package com.fixit.feature.auth.presentation;

import android.view.LayoutInflater;

import com.fixit.core.ui.BaseActivity;
import com.fixit.databinding.ActivityAuthBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AuthActivity extends BaseActivity<ActivityAuthBinding> {

    @Override
    protected ActivityAuthBinding inflateViewBinding(LayoutInflater inflater) {
        return ActivityAuthBinding.inflate(inflater);
    }

    @Override
    protected void setupViews() {
        // Navigation is handled by NavHostFragment
    }

    @Override
    protected void observeData() {
        // Observe any global auth state if needed
    }
}
