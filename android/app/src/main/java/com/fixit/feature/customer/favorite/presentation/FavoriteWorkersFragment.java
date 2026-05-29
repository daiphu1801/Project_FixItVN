package com.fixit.feature.customer.favorite.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentFavoriteWorkersBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FavoriteWorkersFragment extends BaseFragment<FragmentFavoriteWorkersBinding> {

    @NonNull
    @Override
    protected FragmentFavoriteWorkersBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentFavoriteWorkersBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // Nút quay lại (Back)
        binding.btnBack.setOnClickListener(v -> {
            if (navController != null) {
                navController.popBackStack();
            }
        });
    }

    @Override
    protected void observeData() {
        // Observe viewmodel data here
    }
}
