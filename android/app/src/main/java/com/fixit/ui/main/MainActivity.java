package com.fixit.ui.main;

import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.fixit.base.BaseActivity;
import com.fixit.databinding.ActivityWorkerBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends BaseActivity<ActivityWorkerBinding> {

    @Override
    protected ActivityWorkerBinding inflateViewBinding(android.view.LayoutInflater inflater) {
        return ActivityWorkerBinding.inflate(inflater);
    }

    @Override
    protected void setupViews() {
        // Lấy NavController từ NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(binding.navHostFragment.getId());

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

            // Vô hiệu hóa nút placeholder ở giữa
            android.view.MenuItem placeholderItem = binding.bottomNavigationView.getMenu().findItem(com.fixit.R.id.placeholder_worker);
            if (placeholderItem != null) {
                placeholderItem.setEnabled(false);
            }
        }

        binding.fabWorkerOnline.setOnClickListener(v -> {
            android.widget.Toast.makeText(this, "Chuyển trạng thái Nhận việc", android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void observeData() {
        // TODO: Observe role-based navigation logic here in the future
    }
}
