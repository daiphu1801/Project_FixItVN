package com.fixit.feature.customer.order.presentation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentCustomerOrderDetailBinding;
import com.fixit.feature.customer.booking.domain.model.CustomerBooking;
import java.text.SimpleDateFormat;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CustomerOrderDetailFragment extends BaseFragment<FragmentCustomerOrderDetailBinding> {

    private CustomerOrderViewModel viewModel;

    @NonNull
    @Override
    protected FragmentCustomerOrderDetailBinding inflateViewBinding(@NonNull LayoutInflater inflater,
            ViewGroup container) {
        return FragmentCustomerOrderDetailBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        viewModel = new ViewModelProvider(requireActivity()).get(CustomerOrderViewModel.class);

        binding.ivClose.setOnClickListener(v -> {
            if (navController != null) {
                navController.popBackStack();
            }
        });

        binding.btnCancelOrder.setOnClickListener(v -> {
            if (navController != null) {
                navController.navigate(R.id.nav_customer_cancel_order);
            }
        });
    }

    @Override
    protected void observeData() {
        if (viewModel != null) {
            viewModel.currentBooking.observe(getViewLifecycleOwner(), this::bindBookingData);
        }
    }

    private void bindBookingData(CustomerBooking booking) {
        if (booking == null) return;

        // Mã đơn hàng
        String shortId = booking.getBookingId() != null && booking.getBookingId().length() > 8
                ? booking.getBookingId().substring(0, 8)
                : booking.getBookingId();
        binding.tvOrderCode.setText("#" + shortId);

        // Tên dịch vụ
        String serviceName = booking.getServiceName();
        if (serviceName == null || serviceName.isEmpty()) {
            serviceName = "Dịch vụ sửa chữa";
        }
        binding.tvServiceName.setText(serviceName);

        // Mô tả vấn đề
        binding.tvNote.setText(booking.getIssueDescription());

        // Địa chỉ
        binding.tvAddress.setText(booking.getAddress());

        // Giá tiền dự kiến / chính thức
        if (booking.getFinalPrice() != null) {
            java.text.DecimalFormat df = new java.text.DecimalFormat("#,###đ");
            binding.tvEstimatedCost.setText(df.format(booking.getFinalPrice()));
        } else {
            binding.tvEstimatedCost.setText("Khảo sát báo giá sau");
        }

        // Thời gian
        if (booking.getCreatedAt() != null) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault());
                java.util.Date date = inputFormat.parse(booking.getCreatedAt());
                binding.tvSchedule.setText(outputFormat.format(date));
            } catch (Exception e) {
                binding.tvSchedule.setText(booking.getCreatedAt());
            }
        }

        // Thông tin thợ
        if (booking.getWorker() != null) {
            binding.tvTechnicianName.setVisibility(View.VISIBLE);
            binding.ivTechnicianAvatar.setVisibility(View.VISIBLE);
            binding.tvTechnicianDesc.setVisibility(View.VISIBLE);
            binding.layoutRating.setVisibility(View.VISIBLE);
            binding.btnCall.setVisibility(View.VISIBLE);
            binding.btnMessage.setVisibility(View.VISIBLE);

            binding.tvTechnicianName.setText(booking.getWorker().getFullName());

            Glide.with(this)
                    .load(booking.getWorker().getAvatarUrl())
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(binding.ivTechnicianAvatar);

            // Nút nhắn tin
            binding.btnMessage.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("workerId", booking.getWorker().getWorkerId());
                args.putString("workerName", booking.getWorker().getFullName());
                if (navController != null) {
                    navController.navigate(R.id.nav_customer_chat, args);
                }
            });

            // Nút gọi điện
            binding.btnCall.setOnClickListener(v -> {
                String phone = booking.getWorker().getPhoneNumber();
                if (phone != null && !phone.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phone));
                    startActivity(intent);
                } else {
                    Toast.makeText(requireContext(), "Không tìm thấy số điện thoại thợ", Toast.LENGTH_SHORT).show();
                }
            });

            // Click xem profile thợ
            binding.ivTechnicianAvatar.setOnClickListener(v -> navigateToWorkerProfile(booking.getWorker().getWorkerId()));
            binding.tvTechnicianName.setOnClickListener(v -> navigateToWorkerProfile(booking.getWorker().getWorkerId()));
        } else {
            binding.tvTechnicianName.setVisibility(View.GONE);
            binding.ivTechnicianAvatar.setVisibility(View.GONE);
            binding.tvTechnicianDesc.setVisibility(View.GONE);
            binding.layoutRating.setVisibility(View.GONE);
            binding.btnCall.setVisibility(View.GONE);
            binding.btnMessage.setVisibility(View.GONE);
        }
    }

    private void navigateToWorkerProfile(String workerId) {
        if (workerId == null || workerId.isEmpty()) return;
        if (navController != null) {
            Bundle args = new Bundle();
            args.putString("workerId", workerId);
            navController.navigate(R.id.nav_worker_public_profile, args);
        }
    }
}
