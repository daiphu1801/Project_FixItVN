package com.fixit.feature.customer.complaint.data.repository;

import android.os.Handler;
import android.os.Looper;

import com.fixit.core.common.AppError;
import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.network.ApiResponse;
import com.fixit.feature.customer.complaint.data.remote.api.ComplaintApi;
import com.fixit.feature.customer.complaint.data.remote.dto.ComplaintRequestDto;
import com.fixit.feature.customer.complaint.data.remote.dto.ComplaintResponseDto;
import com.fixit.feature.customer.complaint.data.remote.mapper.ComplaintMapper;
import com.fixit.feature.customer.complaint.domain.model.Complaint;
import com.fixit.feature.customer.complaint.domain.repository.ComplaintRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class ComplaintRepositoryImpl implements ComplaintRepository {
    private static final boolean USE_MOCK = true; // Bật true để chạy giao diện offline bằng Mock Data

    private final ComplaintApi complaintApi;
    private final Map<String, Complaint> mockComplaints = new HashMap<>();

    @Inject
    public ComplaintRepositoryImpl(ComplaintApi complaintApi) {
        this.complaintApi = complaintApi;
        initMockData();
    }

    private void initMockData() {
        // Mock đơn hàng đã có khiếu nại (Chờ thợ phản hồi)
        List<String> customerEvidences1 = new ArrayList<>();
        customerEvidences1.add("https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg");
        Complaint complaintPending = new Complaint(
                "COMP_001",
                "33333333-3333-3333-3333-333333333333",
                "Sửa vòi nước bị rò rỉ nhưng hôm sau lại tiếp tục bị rỉ nước mạnh hơn.",
                null,
                customerEvidences1,
                new ArrayList<>(),
                "Pending",
                "15:30, Ngày mai",
                "2026-06-22T10:00:00Z"
        );
        mockComplaints.put("33333333-3333-3333-3333-333333333333", complaintPending);

        // Mock đơn hàng đã có giải trình của thợ
        List<String> customerEvidences2 = new ArrayList<>();
        customerEvidences2.add("https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg");
        List<String> workerEvidences2 = new ArrayList<>();
        workerEvidences2.add("https://res.cloudinary.com/demo/image/upload/w_200,c_fill,g_face,r_max/v1312461204/sample.jpg");
        Complaint complaintResponded = new Complaint(
                "COMP_002",
                "44444444-4444-4444-4444-444444444444",
                "Lắp đặt điều hòa không mát, yêu cầu kiểm tra lại ga.",
                "Tôi đã kiểm tra kỹ và nạp ga đầy đủ, máy không mát do block cũ của khách hàng bị hỏng từ trước, không nằm trong bảo hành lắp đặt.",
                customerEvidences2,
                workerEvidences2,
                "Worker_Responded",
                "Hết hạn",
                "2026-06-21T08:30:00Z"
        );
        mockComplaints.put("44444444-4444-4444-4444-444444444444", complaintResponded);
    }

    @Override
    public void createComplaint(String bookingId, String reason, List<String> evidenceUrls, ResultCallback<Complaint> callback) {
        if (USE_MOCK) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Complaint newComplaint = new Complaint(
                        "COMP_" + System.currentTimeMillis(),
                        bookingId,
                        reason,
                        null,
                        evidenceUrls != null ? evidenceUrls : new ArrayList<>(),
                        new ArrayList<>(),
                        "Pending",
                        "24 giờ sau",
                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(new Date())
                );
                mockComplaints.put(bookingId, newComplaint);
                callback.onResult(Result.success(newComplaint));
            }, 1000);
            return;
        }

        ComplaintRequestDto req = new ComplaintRequestDto(reason, evidenceUrls);
        complaintApi.createComplaint(bookingId, req).enqueue(new Callback<ApiResponse<ComplaintResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<ComplaintResponseDto>> call, Response<ApiResponse<ComplaintResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onResult(Result.success(ComplaintMapper.toDomain(response.body().getData())));
                } else {
                    callback.onResult(Result.error(new AppError("Lỗi gửi khiếu nại lên máy chủ")));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ComplaintResponseDto>> call, Throwable t) {
                callback.onResult(Result.error(new AppError(t.getMessage(), t)));
            }
        });
    }

    @Override
    public void getBookingComplaint(String bookingId, ResultCallback<Complaint> callback) {
        if (USE_MOCK) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Complaint complaint = mockComplaints.get(bookingId);
                callback.onResult(Result.success(complaint)); // Trả về null nếu chưa có khiếu nại
            }, 600);
            return;
        }

        complaintApi.getBookingComplaint(bookingId).enqueue(new Callback<ApiResponse<ComplaintResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<ComplaintResponseDto>> call, Response<ApiResponse<ComplaintResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onResult(Result.success(ComplaintMapper.toDomain(response.body().getData())));
                } else {
                    callback.onResult(Result.success(null)); // Không có khiếu nại
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ComplaintResponseDto>> call, Throwable t) {
                callback.onResult(Result.error(new AppError(t.getMessage(), t)));
            }
        });
    }

    @Override
    public void cancelComplaint(String bookingId, String complaintId, ResultCallback<Void> callback) {
        if (USE_MOCK) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                mockComplaints.remove(bookingId);
                callback.onResult(Result.success(null));
            }, 1000);
            return;
        }

        complaintApi.cancelComplaint(bookingId, complaintId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onResult(Result.success(null));
                } else {
                    callback.onResult(Result.error(new AppError("Lỗi hủy khiếu nại từ server")));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onResult(Result.error(new AppError(t.getMessage(), t)));
            }
        });
    }
}
