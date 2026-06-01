package com.fixit.feature.customer.workerprofile.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentWorkerPublicProfileBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerPublicProfileFragment extends BaseFragment<FragmentWorkerPublicProfileBinding> {

    private WorkerPublicProfileViewModel viewModel;
    private String workerId;
    private String workerName;
    private ReviewAdapter adapter;

    @NonNull
    @Override
    protected FragmentWorkerPublicProfileBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerPublicProfileBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        if (getArguments() != null) {
            workerId = getArguments().getString("workerId");
            workerName = getArguments().getString("workerName");
        }

        // Cấu hình thanh Toolbar chuẩn
        binding.layoutAppbar.toolbar.setNavigationOnClickListener(v -> {
            if (navController != null) {
                navController.popBackStack();
            }
        });
        binding.layoutAppbar.toolbar.setTitle(workerName);

        // Hiển thị thông tin tên thợ trên header card
        binding.layoutProfileHeader.tvWorkerName.setText(workerName);

        // Ẩn nút sửa chuyên môn (chỉ thợ mới được sửa chuyên môn của họ)
        binding.layoutResume.btnEditSpecialization.setVisibility(View.GONE);

        // Cài đặt danh sách đánh giá
        adapter = new ReviewAdapter();
        binding.rvReviews.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvReviews.setAdapter(adapter);

        // Cài đặt sự kiện chuyển sang khung chat khi ấn "Trò chuyện với thợ"
        binding.btnChatWithWorker.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("workerId", workerId);
            args.putString("workerName", workerName);
            if (navController != null) {
                navController.navigate(R.id.nav_customer_chat, args);
            }
        });
    }

    @Override
    protected void observeData() {
        viewModel = new ViewModelProvider(this).get(WorkerPublicProfileViewModel.class);

        // Tải danh sách đánh giá của thợ
        viewModel.loadReviews(workerId);
        viewModel.getReviews().observe(getViewLifecycleOwner(), reviews -> {
            if (reviews != null && !reviews.isEmpty()) {
                adapter.submitList(reviews);
                binding.tvEmptyReviews.setVisibility(View.GONE);

                // Tính trung bình sao và hiển thị trên giao diện
                double total = 0;
                for (com.fixit.feature.customer.review.domain.model.Review r : reviews) {
                    total += r.getRating();
                }
                double avg = total / reviews.size();
                binding.layoutProfileHeader.tvRating.setText(String.format(java.util.Locale.getDefault(), "%.1f ★", avg));
            } else {
                binding.tvEmptyReviews.setVisibility(View.VISIBLE);
                binding.layoutProfileHeader.tvRating.setText("-- ★");
            }
        });
    }
}
