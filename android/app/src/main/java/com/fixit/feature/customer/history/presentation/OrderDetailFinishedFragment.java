package com.fixit.feature.customer.history.presentation;

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
import com.fixit.databinding.FragmentOrderDetailFinishedBinding;
import com.fixit.feature.customer.booking.domain.model.CustomerBooking;
import com.fixit.feature.customer.booking.domain.usecase.GetBookingDetailUseCase;
import com.fixit.feature.customer.review.presentation.CustomerReviewDialog;
import com.fixit.feature.customer.favorite.domain.usecase.CheckFavoriteStatusUseCase;
import com.fixit.feature.customer.favorite.domain.usecase.AddFavoriteWorkerUseCase;
import com.fixit.feature.customer.favorite.domain.usecase.RemoveFavoriteWorkerUseCase;
import com.fixit.feature.customer.complaint.presentation.CustomerComplaintViewModel;
import android.graphics.Color;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * FILE ĐIỀU KHIỂN CHI TIẾT ĐƠN HÀNG ĐÃ HOÀN THÀNH
 * Mục đích: Hiển thị thông tin chi tiết thực tế của một đơn hàng sau khi thợ đã sửa xong.
 */
@AndroidEntryPoint
public class OrderDetailFinishedFragment extends BaseFragment<FragmentOrderDetailFinishedBinding>
        implements CustomerReviewDialog.OnReviewSubmittedListener {

    private String orderId;
    private String workerId;
    private String workerName;
    private CustomerComplaintViewModel complaintViewModel;

    @Inject
    CheckFavoriteStatusUseCase checkFavoriteStatusUseCase;

    @Inject
    AddFavoriteWorkerUseCase addFavoriteWorkerUseCase;

    @Inject
    RemoveFavoriteWorkerUseCase removeFavoriteWorkerUseCase;

    @Inject
    GetBookingDetailUseCase getBookingDetailUseCase;

    @NonNull
    @Override
    protected FragmentOrderDetailFinishedBinding inflateViewBinding(@NonNull LayoutInflater inflater,
            ViewGroup container) {
        return FragmentOrderDetailFinishedBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        if (getArguments() != null) {
            orderId = getArguments().getString("orderId");
            workerId = getArguments().getString("workerId");
            workerName = getArguments().getString("workerName", "Thợ");
        }

        // Cài đặt nút quay lại
        binding.ivBack.setOnClickListener(v -> {
            if (navController != null) {
                navController.popBackStack();
            }
        });

        // Hiển thị mặc định ban đầu
        binding.tvWorkerName.setText(workerName);

        // Gọi API tải chi tiết đơn hàng
        if (orderId != null) {
            getBookingDetailUseCase.execute(orderId, result -> {
                if (result.isSuccess() && result.getData() != null) {
                    bindOrderDetails(result.getData());
                } else {
                    Toast.makeText(requireContext(), "Lỗi tải chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Sự kiện khi bấm nút "Đánh giá ngay"
        binding.btnRate.setOnClickListener(v -> {
            if (orderId != null) {
                CustomerReviewDialog dialog = CustomerReviewDialog.newInstance(orderId);
                dialog.show(getChildFragmentManager(), "CustomerReviewDialog");
            } else {
                Toast.makeText(requireContext(), "Không tìm thấy thông tin mã đơn hàng!", Toast.LENGTH_SHORT).show();
            }
        });

        // Sự kiện khi bấm vào thẻ thông tin thợ để xem Hồ sơ công khai
        binding.cardWorkerInfo.setOnClickListener(v -> {
            if (workerId != null) {
                Bundle args = new Bundle();
                args.putString("workerId", workerId);
                args.putString("workerName", workerName);
                if (navController != null) {
                    navController.navigate(R.id.nav_customer_worker_public_profile, args);
                }
            } else {
                Toast.makeText(requireContext(), "Không tìm thấy thông tin thợ sửa chữa!", Toast.LENGTH_SHORT).show();
            }
        });

        // ================= KẾT NỐI TRÁI TIM YÊU THÍCH =================
        if (workerId != null) {
            checkFavoriteStatusUseCase.execute(workerId, result -> {
                if (result.isSuccess() && result.getData() != null) {
                    binding.cbFavorite.setChecked(result.getData());
                }
            });
        }

        binding.cbFavorite.setOnClickListener(v -> {
            boolean isChecked = binding.cbFavorite.isChecked();
            if (workerId == null) {
                binding.cbFavorite.setChecked(!isChecked);
                Toast.makeText(requireContext(), "Không tìm thấy thông tin thợ!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isChecked) {
                addFavoriteWorkerUseCase.execute(workerId, result -> {
                    if (result.isSuccess()) {
                        Toast.makeText(requireContext(), "Đã thêm vào danh sách thợ quen", Toast.LENGTH_SHORT).show();
                    } else {
                        binding.cbFavorite.setChecked(false);
                        Toast.makeText(requireContext(), "Lỗi khi thêm thợ quen", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                removeFavoriteWorkerUseCase.execute(workerId, result -> {
                    if (result.isSuccess()) {
                        Toast.makeText(requireContext(), "Đã xóa khỏi danh sách thợ quen", Toast.LENGTH_SHORT).show();
                    } else {
                        binding.cbFavorite.setChecked(true);
                        Toast.makeText(requireContext(), "Lỗi khi xóa thợ quen", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Khởi tạo ViewModel khiếu nại và cài đặt click nút Khiếu nại
        complaintViewModel = new ViewModelProvider(this).get(CustomerComplaintViewModel.class);
        binding.btnComplaint.setOnClickListener(v -> {
            if (orderId != null) {
                Bundle args = new Bundle();
                args.putString("bookingId", orderId);
                if (complaintViewModel.complaint.getValue() == null) {
                    if (navController != null) {
                        navController.navigate(R.id.nav_customer_complaint_create, args);
                    }
                } else {
                    if (navController != null) {
                        navController.navigate(R.id.nav_customer_complaint_detail, args);
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Không tìm thấy thông tin đơn hàng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindOrderDetails(CustomerBooking booking) {
        if (booking == null) return;

        // Cập nhật lại ID thợ thật nếu ban đầu truyền từ args là rỗng
        if (booking.getWorker() != null) {
            workerId = booking.getWorker().getWorkerId();
            workerName = booking.getWorker().getFullName();
            binding.tvWorkerName.setText(workerName);

            Glide.with(this)
                    .load(booking.getWorker().getAvatarUrl())
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(binding.ivAvatar);
        }

        // Tên dịch vụ
        String serviceName = booking.getServiceName();
        if (serviceName == null || serviceName.isEmpty()) {
            serviceName = "Dịch vụ sửa chữa";
        }
        binding.tvServiceName.setText(serviceName);

        // Địa chỉ & Thời gian
        binding.tvJobLocation.setText(booking.getAddress());
        if (booking.getCreatedAt() != null) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault());
                java.util.Date date = inputFormat.parse(booking.getCreatedAt());
                binding.tvJobTime.setText(outputFormat.format(date));
            } catch (Exception e) {
                binding.tvJobTime.setText(booking.getCreatedAt());
            }
        }

        // Tổng tiền
        DecimalFormat df = new DecimalFormat("#,###đ");
        if (booking.getFinalPrice() != null) {
            binding.tvTotalPrice.setText(df.format(booking.getFinalPrice()));
        } else {
            binding.tvTotalPrice.setText("Chưa có giá");
        }

        // Trạng thái hiển thị
        String status = booking.getStatus();
        if ("CANCELLED".equalsIgnoreCase(status)) {
            binding.tvStatus.setText("● Đã hủy");
            binding.tvStatus.setTextColor(Color.parseColor("#EF4444"));
            binding.tvStatus.setBackgroundResource(R.drawable.bg_badge_red);
        } else if ("COMPLETED".equalsIgnoreCase(status)) {
            binding.tvStatus.setText("● Hoàn thành");
            binding.tvStatus.setTextColor(Color.parseColor("#10B981"));
            binding.tvStatus.setBackgroundResource(R.drawable.bg_badge_green_light);
        } else {
            binding.tvStatus.setText("● " + status);
            binding.tvStatus.setTextColor(Color.parseColor("#0284C7"));
            binding.tvStatus.setBackgroundResource(R.drawable.bg_badge_light_blue);
        }

        // Bảng chi phí chi tiết (Tiền công / Vật tư)
        if (booking.getLaborCost() != null || booking.getMaterialCost() != null) {
            binding.layoutPriceList.setVisibility(View.VISIBLE);

            if (booking.getLaborCost() != null) {
                binding.tvItem1Title.setVisibility(View.VISIBLE);
                binding.tvItem1Price.setVisibility(View.VISIBLE);
                binding.tvItem1Title.setText("Tiền công sửa chữa");
                binding.tvItem1Price.setText(df.format(booking.getLaborCost()));
            } else {
                binding.tvItem1Title.setVisibility(View.GONE);
                binding.tvItem1Price.setVisibility(View.GONE);
            }

            if (booking.getMaterialCost() != null) {
                binding.tvItem2Title.setVisibility(View.VISIBLE);
                binding.tvItem2Price.setVisibility(View.VISIBLE);
                binding.tvItem2Title.setText("Tiền vật tư / linh kiện");
                binding.tvItem2Price.setText(df.format(booking.getMaterialCost()));
            } else {
                binding.tvItem2Title.setVisibility(View.GONE);
                binding.tvItem2Price.setVisibility(View.GONE);
            }
        } else {
            binding.layoutPriceList.setVisibility(View.GONE);
        }

        // Đặt lại dịch vụ nhanh
        final String finalServiceName = serviceName;
        binding.btnReorder.setOnClickListener(v -> {
            if (navController != null) {
                Bundle args = new Bundle();
                args.putInt("serviceId", booking.getServiceId());
                args.putString("serviceName", finalServiceName);
                navController.navigate(R.id.nav_customer_booking, args);
            }
        });
    }

    @Override
    protected void observeData() {
        if (complaintViewModel != null) {
            complaintViewModel.complaint.observe(getViewLifecycleOwner(), complaint -> {
                if (complaint == null) {
                    binding.btnComplaint.setText("⚠️ Khiếu nại");
                } else {
                    binding.btnComplaint.setText("👁️ Xem khiếu nại");
                }
            });

            if (orderId != null) {
                complaintViewModel.loadBookingComplaint(orderId);
            }
        }
    }

    @Override
    public void onReviewSubmitted() {
        binding.btnRate.setEnabled(false);
        binding.btnRate.setText("✓ Đã đánh giá");
    }
}
