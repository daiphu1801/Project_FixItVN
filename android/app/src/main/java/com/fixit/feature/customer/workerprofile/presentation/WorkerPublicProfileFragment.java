package com.fixit.feature.customer.workerprofile.presentation;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.fixit.R;
import com.fixit.databinding.FragmentWorkerPublicProfileBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerPublicProfileFragment extends Fragment {

    private FragmentWorkerPublicProfileBinding binding;
    private WorkerPublicProfileViewModel viewModel;
    private PublicWorkerSkillAdapter skillAdapter;

    private String workerId;

    public static WorkerPublicProfileFragment newInstance(String workerId) {
        WorkerPublicProfileFragment fragment = new WorkerPublicProfileFragment();
        Bundle args = new Bundle();
        args.putString("workerId", workerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            workerId = getArguments().getString("workerId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentWorkerPublicProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(WorkerPublicProfileViewModel.class);

        setupToolbar();
        setupRecyclerView();
        observeViewModel();

        if (workerId != null) {
            viewModel.loadWorkerProfile(workerId);
        } else {
            Toast.makeText(getContext(), "Không tìm thấy ID Thợ", Toast.LENGTH_SHORT).show();
        }

        binding.btnFavorite.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đã thêm vào mục Yêu thích", Toast.LENGTH_SHORT).show();
            binding.btnFavorite.setIconTint(ColorStateList.valueOf(Color.parseColor("#EF4444")));
            binding.btnFavorite.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#EF4444")));
        });

        binding.btnBookWorker.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tính năng Đặt thợ này đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setupRecyclerView() {
        skillAdapter = new PublicWorkerSkillAdapter();
        binding.rvSkills.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSkills.setAdapter(skillAdapter);
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && binding.layoutLoading != null) {
                binding.layoutLoading.getRoot().setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                binding.tvWorkerName.setText(profile.getFullName() != null ? profile.getFullName() : "Thợ FixIt");
                binding.tvRating.setText(String.format("%s (%d đánh giá)",
                        profile.getReputationScore() != null ? profile.getReputationScore().toString() : "5.0",
                        profile.getTotalReviews() != null ? profile.getTotalReviews() : 0));

                if (profile.getExperienceDescription() != null && !profile.getExperienceDescription().isEmpty()) {
                    binding.tvExperience.setText(profile.getExperienceDescription());
                } else {
                    binding.tvExperience.setText("Chưa có mô tả kinh nghiệm.");
                }

                if (Boolean.TRUE.equals(profile.isAvailable())) {
                    binding.chipStatus.setText("Sẵn sàng nhận việc");
                    binding.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#D1FAE5")));
                    binding.chipStatus.setTextColor(Color.parseColor("#065F46"));
                } else {
                    binding.chipStatus.setText("Đang bận");
                    binding.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FEE2E2")));
                    binding.chipStatus.setTextColor(Color.parseColor("#991B1B"));
                }

                if (profile.getAvatarUrl() != null && !profile.getAvatarUrl().isEmpty()) {
                    Glide.with(this)
                            .load(profile.getAvatarUrl())
                            .placeholder(R.drawable.ic_lucide_user)
                            .error(R.drawable.ic_lucide_user)
                            .into(binding.ivAvatar);
                }
            }
        });

        viewModel.getSkills().observe(getViewLifecycleOwner(), skills -> {
            if (skills != null) {
                skillAdapter.submitList(skills);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
