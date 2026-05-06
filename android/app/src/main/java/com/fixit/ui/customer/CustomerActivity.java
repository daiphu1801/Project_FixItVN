package com.fixit.ui.customer;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.fixit.R;
import com.fixit.base.BaseActivity;
import com.fixit.databinding.ActivityCustomerBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CustomerActivity extends BaseActivity<ActivityCustomerBinding> {

    private NavController navController;

    @Override
    protected ActivityCustomerBinding inflateViewBinding(android.view.LayoutInflater inflater) {
        return ActivityCustomerBinding.inflate(inflater);
    }

    @Override
    protected void setupViews() {
        setupNavigation();
        setupFab();
    }

    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navHostFragmentCustomer);
        
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);
            
            MenuItem placeholderItem = binding.bottomNavigationView.getMenu().findItem(R.id.placeholder);
            if (placeholderItem != null) {
                placeholderItem.setEnabled(false);
            }
        }
    }

    private void setupFab() {
        binding.fabFindWorker.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng Tìm thợ quanh đây đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void observeData() {
        // Observe Customer Data
    }
}
