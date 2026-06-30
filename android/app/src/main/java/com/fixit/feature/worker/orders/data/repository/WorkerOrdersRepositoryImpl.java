package com.fixit.feature.worker.orders.data.repository;

import com.fixit.core.common.AppError;
import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.network.ApiResponse;
import com.fixit.feature.worker.orders.data.remote.api.WorkerOrdersApi;
import com.fixit.feature.worker.orders.data.remote.dto.BookingActionResponseDto;
import com.fixit.feature.worker.orders.data.remote.dto.WorkerBookingDetailResponseDto;
import com.fixit.feature.worker.orders.data.remote.dto.WorkerHistoryResponseDto;
import com.fixit.feature.worker.orders.data.remote.dto.WorkerScheduleResponseDto;
import com.fixit.feature.worker.orders.data.remote.mapper.WorkerOrdersMapper;
import com.fixit.feature.worker.orders.domain.model.ExtraCostItem;
import com.fixit.feature.worker.orders.domain.model.JobStatus;
import com.fixit.feature.worker.orders.domain.model.WorkerOrder;
import com.fixit.feature.worker.orders.domain.repository.WorkerOrdersRepository;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class WorkerOrdersRepositoryImpl implements WorkerOrdersRepository {
    private static final String ADMIN_BANK_ID = "MB";
    private static final String ADMIN_ACCOUNT_NO = "0859226688";
    private static final String ADMIN_ACCOUNT_NAME = "CONG TY FIXIT VN";

    private final WorkerOrdersApi api;
    private List<ExtraCostItem> extraCosts = new ArrayList<>();

    @Inject
    public WorkerOrdersRepositoryImpl(WorkerOrdersApi api) {
        this.api = api;
    }

    @Override
    public void getOrders(ResultCallback<List<WorkerOrder>> callback) {
        api.getSchedule().enqueue(new Callback<ApiResponse<WorkerScheduleResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<WorkerScheduleResponseDto>> call,
                    Response<ApiResponse<WorkerScheduleResponseDto>> response) {
                if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                    callback.onResult(Result.error(new AppError("Không tải được danh sách đơn hàng")));
                    return;
                }

                List<WorkerOrder> orders = new ArrayList<>();
                if (response.body().getData() != null && response.body().getData().getItems() != null) {
                    orders = response.body().getData().getItems().stream()
                            .map(WorkerOrdersMapper::map)
                            .collect(Collectors.toList());
                }
                callback.onResult(Result.success(orders));
            }

            @Override
            public void onFailure(Call<ApiResponse<WorkerScheduleResponseDto>> call, Throwable t) {
                callback.onResult(Result.error(new AppError("Lỗi kết nối: " + t.getMessage(), t)));
            }
        });
    }

    @Override
    public void filterOrders(String status, ResultCallback<List<WorkerOrder>> callback) {
        if ("history".equals(status)) {
            api.getHistory(null).enqueue(new Callback<ApiResponse<WorkerHistoryResponseDto>>() {
                @Override
                public void onResponse(Call<ApiResponse<WorkerHistoryResponseDto>> call,
                        Response<ApiResponse<WorkerHistoryResponseDto>> response) {
                    if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                        callback.onResult(Result.error(new AppError("Không tải được lịch sử đơn hàng")));
                        return;
                    }

                    List<WorkerOrder> orders = new ArrayList<>();
                    if (response.body().getData() != null && response.body().getData().getItems() != null) {
                        orders = response.body().getData().getItems().stream()
                                .map(WorkerOrdersMapper::map)
                                .collect(Collectors.toList());
                    }
                    callback.onResult(Result.success(orders));
                }

                @Override
                public void onFailure(Call<ApiResponse<WorkerHistoryResponseDto>> call, Throwable t) {
                    callback.onResult(Result.error(new AppError("Lỗi kết nối: " + t.getMessage(), t)));
                }
            });
        } else {
            getOrders(new ResultCallback<List<WorkerOrder>>() {
                @Override
                public void onResult(Result<List<WorkerOrder>> result) {
                    if (result.isSuccess()) {
                        List<WorkerOrder> filtered = result.getData().stream()
                                .filter(order -> status.equals(order.getStatus()))
                                .collect(Collectors.toList());
                        callback.onResult(Result.success(filtered));
                    } else {
                        callback.onResult(result);
                    }
                }
            });
        }
    }

    @Override
    public void getOrderById(String orderId, ResultCallback<WorkerOrder> callback) {
        api.getBookingDetails(orderId).enqueue(new Callback<ApiResponse<WorkerBookingDetailResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<WorkerBookingDetailResponseDto>> call,
                    Response<ApiResponse<WorkerBookingDetailResponseDto>> response) {
                if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                    callback.onResult(Result.error(new AppError("Không lấy được chi tiết đơn hàng")));
                    return;
                }

                WorkerOrder order = WorkerOrdersMapper.map(response.body().getData());
                if (order == null) {
                    callback.onResult(Result.error(new AppError("Dữ liệu đơn hàng không hợp lệ")));
                } else {
                    callback.onResult(Result.success(order));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<WorkerBookingDetailResponseDto>> call, Throwable t) {
                callback.onResult(Result.error(new AppError("Lỗi kết nối: " + t.getMessage(), t)));
            }
        });
    }

    @Override
    public JobStatus getInitialStatus(String orderStatus) {
        if ("ongoing".equals(orderStatus)) {
            return JobStatus.SURVEYING;
        } else if ("completed".equals(orderStatus) || "cancelled".equals(orderStatus)) {
            return JobStatus.COMPLETED;
        }
        return JobStatus.ACCEPTED;
    }

    @Override
    public void advanceStatus(String orderId, JobStatus currentStatus, ResultCallback<JobStatus> callback) {
        if (currentStatus == null) {
            callback.onResult(Result.error(new AppError("Trạng thái hiện tại không hợp lệ")));
            return;
        }

        switch (currentStatus) {
            case ACCEPTED:
                api.startMoving(orderId).enqueue(new Callback<ApiResponse<BookingActionResponseDto>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<BookingActionResponseDto>> call,
                            Response<ApiResponse<BookingActionResponseDto>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            callback.onResult(Result.success(JobStatus.ARRIVING));
                        } else {
                            callback.onResult(Result.error(new AppError("Không thể bắt đầu di chuyển")));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<BookingActionResponseDto>> call, Throwable t) {
                        callback.onResult(Result.error(new AppError("Lỗi kết nối: " + t.getMessage(), t)));
                    }
                });
                break;

            case ARRIVING:
                api.arrive(orderId).enqueue(new Callback<ApiResponse<BookingActionResponseDto>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<BookingActionResponseDto>> call,
                            Response<ApiResponse<BookingActionResponseDto>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            // Automatically transition to start survey
                            api.startSurvey(orderId).enqueue(new Callback<ApiResponse<BookingActionResponseDto>>() {
                                @Override
                                public void onResponse(Call<ApiResponse<BookingActionResponseDto>> call,
                                        Response<ApiResponse<BookingActionResponseDto>> res) {
                                    if (res.isSuccessful() && res.body() != null && res.body().isSuccess()) {
                                        callback.onResult(Result.success(JobStatus.SURVEYING));
                                    } else {
                                        callback.onResult(Result
                                                .error(new AppError("Đã đến nơi nhưng không thể bắt đầu khảo sát")));
                                    }
                                }

                                @Override
                                public void onFailure(Call<ApiResponse<BookingActionResponseDto>> call, Throwable t) {
                                    callback.onResult(
                                            Result.error(new AppError("Lỗi kết nối khảo sát: " + t.getMessage(), t)));
                                }
                            });
                        } else {
                            callback.onResult(Result.error(new AppError("Không thể xác nhận đã đến nơi")));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<BookingActionResponseDto>> call, Throwable t) {
                        callback.onResult(Result.error(new AppError("Lỗi kết nối: " + t.getMessage(), t)));
                    }
                });
                break;

            case SURVEYING:
                api.startRepair(orderId).enqueue(new Callback<ApiResponse<BookingActionResponseDto>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<BookingActionResponseDto>> call,
                            Response<ApiResponse<BookingActionResponseDto>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            callback.onResult(Result.success(JobStatus.REPAIRING));
                        } else {
                            callback.onResult(Result.error(new AppError("Không thể bắt đầu sửa chữa")));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<BookingActionResponseDto>> call, Throwable t) {
                        callback.onResult(Result.error(new AppError("Lỗi kết nối: " + t.getMessage(), t)));
                    }
                });
                break;

            case REPAIRING:
                api.workerComplete(orderId).enqueue(new Callback<ApiResponse<BookingActionResponseDto>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<BookingActionResponseDto>> call,
                            Response<ApiResponse<BookingActionResponseDto>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            callback.onResult(Result.success(JobStatus.COMPLETED));
                        } else {
                            callback.onResult(Result.error(new AppError("Không thể hoàn thành đơn hàng")));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<BookingActionResponseDto>> call, Throwable t) {
                        callback.onResult(Result.error(new AppError("Lỗi kết nối: " + t.getMessage(), t)));
                    }
                });
                break;

            default:
                callback.onResult(Result.success(currentStatus));
                break;
        }
    }

    @Override
    public void saveExtraCosts(List<ExtraCostItem> items) {
        extraCosts = items == null ? new ArrayList<>() : new ArrayList<>(items);
    }

    @Override
    public List<ExtraCostItem> getExtraCosts() {
        return extraCosts;
    }

    @Override
    public long calculateTotalExtra() {
        long total = 0;
        for (ExtraCostItem item : extraCosts) {
            total += item.getTotal();
        }
        return total;
    }

    @Override
    public String generatePaymentQrUrl(String orderId, long amount) {
        String template = "qr_only";
        String description = "FIXIT ORD " + orderId;

        try {
            return String.format("https://img.vietqr.io/image/%s-%s-%s.png?amount=%d&addInfo=%s&accountName=%s",
                    ADMIN_BANK_ID,
                    ADMIN_ACCOUNT_NO,
                    template,
                    amount,
                    URLEncoder.encode(description, "UTF-8"),
                    URLEncoder.encode(ADMIN_ACCOUNT_NAME, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
}
