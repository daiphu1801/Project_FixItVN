package com.fixit.feature.worker.kyc.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.navigation.Navigation;

import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentWorkerKycPendingBinding;

public class WorkerKycPendingFragment extends BaseFragment<FragmentWorkerKycPendingBinding> {

    @Override
    protected FragmentWorkerKycPendingBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerKycPendingBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // Nút back trên toolbar
        binding.appBarLayout.toolbar.setTitle("Trạng thái hồ sơ");
        binding.appBarLayout.toolbar.setNavigationOnClickListener(v -> 
                Navigation.findNavController(v).navigateUp());

        // Nút khám phá việc làm (Tạm thời là chuyển sang tab Tìm việc hoặc hiện Toast)
        binding.cardExploreJobs.setOnClickListener(v -> 
                Toast.makeText(requireContext(), "Sẽ điều hướng sang tab Tìm việc", Toast.LENGTH_SHORT).show()
        );

        // Nút Về trang chủ
        binding.btnBackToHome.setOnClickListener(v -> 
                Navigation.findNavController(v).navigate(com.fixit.R.id.workerHomeFragment)
        );
    }

    @Override
    protected void observeData() {
        // Không cần tải data ở đây
    }
}
