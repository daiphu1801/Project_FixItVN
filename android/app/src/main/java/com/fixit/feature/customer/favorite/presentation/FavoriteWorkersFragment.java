package com.fixit.feature.customer.favorite.presentation;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentFavoriteWorkersBinding;
import com.fixit.feature.customer.favorite.domain.model.FavoriteWorker;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FavoriteWorkersFragment extends BaseFragment<FragmentFavoriteWorkersBinding> {

    private FavoriteWorkersViewModel viewModel;
    private FavoriteWorkerAdapter adapter;

    @NonNull
    @Override
    protected FragmentFavoriteWorkersBinding inflateViewBinding(@NonNull LayoutInflater inflater, ViewGroup container) {
        return FragmentFavoriteWorkersBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {


        // Thiết lập RecyclerView & Adapter
        adapter = new FavoriteWorkerAdapter();
        binding.rvFavoriteWorkers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvFavoriteWorkers.setAdapter(adapter);

        // Cài đặt sự kiện click trong danh sách thợ quen
        adapter.setOnFavoriteWorkerClickListener(new FavoriteWorkerAdapter.OnFavoriteWorkerClickListener() {
            @Override
            public void onItemClick(FavoriteWorker worker) {
                // Chuyển sang màn hình xem hồ sơ chi tiết của thợ sửa chữa
                Bundle args = new Bundle();
                args.putString("workerId", worker.getWorkerId());
                args.putString("workerName", worker.getFullName());
                if (navController != null) {
                    navController.navigate(R.id.nav_customer_worker_public_profile, args);
                }
            }

            @Override
            public void onBookClick(FavoriteWorker worker) {
                // Chuyển sang màn hình đặt lịch dịch vụ
                if (navController != null) {
                    navController.navigate(R.id.nav_customer_booking);
                }
            }
        });

        // Xử lý ô tìm kiếm tên thợ quen
        binding.etSearchWorker.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (viewModel != null) {
                    viewModel.filterWorkers(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void observeData() {
        // Khởi tạo ViewModel liên kết Hilt
        viewModel = new ViewModelProvider(this).get(FavoriteWorkersViewModel.class);

        // Đăng ký quan sát danh sách thợ yêu thích để submit lên Adapter
        viewModel.favoriteWorkers.observe(getViewLifecycleOwner(), workers -> {
            if (workers != null) {
                adapter.submitList(workers);
            }
        });

        // Đăng ký quan sát thông báo lỗi
        viewModel.errorMessage.observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && binding.layoutLoading != null) {
                binding.layoutLoading.getRoot().setVisibility(isLoading ? android.view.View.VISIBLE : android.view.View.GONE);
            }
        });

        // Gọi API tải danh sách thợ quen khi Fragment được hiển thị
        viewModel.fetchFavoriteWorkers();
    }
}
