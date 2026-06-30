package com.fixit.feature.worker.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.fixit.core.ui.BaseActivity;
import com.fixit.databinding.ActivityWorkerBinding;

import dagger.hilt.android.AndroidEntryPoint;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.fixit.feature.notification.domain.usecase.RegisterDeviceTokenUseCase;
import com.fixit.feature.worker.orders.domain.usecase.GetPendingAssignmentsUseCase;
import com.fixit.feature.worker.orders.domain.usecase.AcceptAssignmentUseCase;
import com.fixit.feature.worker.orders.domain.usecase.RejectAssignmentUseCase;
import com.google.firebase.messaging.FirebaseMessaging;
import javax.inject.Inject;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;

@AndroidEntryPoint
public class WorkerActivity extends BaseActivity<ActivityWorkerBinding> {

    private static final int PERMISSION_REQUEST_CODE = 1001;

    @Inject
    RegisterDeviceTokenUseCase registerDeviceTokenUseCase;

    @Inject
    GetPendingAssignmentsUseCase getPendingAssignmentsUseCase;

    @Inject
    AcceptAssignmentUseCase acceptAssignmentUseCase;

    @Inject
    RejectAssignmentUseCase rejectAssignmentUseCase;

    @Override
    protected ActivityWorkerBinding inflateViewBinding(LayoutInflater inflater) {
        return ActivityWorkerBinding.inflate(inflater);
    }

    @Override
    protected void setupViews() {
        checkNotificationPermission();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(binding.navHostFragment.getId());

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            // Wire bottom nav (5 items: Trang chủ, Đơn hàng, Tìm việc, Chat, Tài khoản)
            NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

            // Xử lý khi nhấn vào các mục trên Bottom Navigation để đồng bộ trải nghiệm
            binding.bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == com.fixit.R.id.workerHomeFragment) {
                    boolean popped = navController.popBackStack(com.fixit.R.id.workerHomeFragment, false);
                    if (!popped) {
                        navController.navigate(com.fixit.R.id.workerHomeFragment);
                    }
                    return true;
                }
                return NavigationUI.onNavDestinationSelected(item, navController);
            });

            // FAB ở giữa → simulate click item "Tìm việc" trong bottom nav
            // Để NavigationUI tự xử lý popUpTo đúng cách (tránh conflict back stack)
            binding.fabWorkerOnline.setOnClickListener(v ->
                    binding.bottomNavigationView.setSelectedItemId(com.fixit.R.id.workerJobFragment));

            // Đổi màu FAB khi item Tìm Việc được chọn
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == com.fixit.R.id.workerJobFragment) {
                    binding.fabWorkerOnline.setBackgroundTintList(
                            android.content.res.ColorStateList.valueOf(
                                    android.graphics.Color.parseColor("#22c55e")));
                } else {
                    binding.fabWorkerOnline.setBackgroundTintList(
                            android.content.res.ColorStateList.valueOf(
                                    android.graphics.Color.parseColor("#42c2ff")));
                }
            });
        }
    }

    @Override
    protected void observeData() {
        // TODO: Observe role-based navigation logic here in the future
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        PERMISSION_REQUEST_CODE);
            } else {
                fetchAndRegisterFcmToken();
            }
        } else {
            fetchAndRegisterFcmToken();
        }
    }

    private void fetchAndRegisterFcmToken() {
        try {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w("WorkerActivity", "Fetching FCM registration token failed", task.getException());
                    return;
                }

                String token = task.getResult();
                Log.d("WorkerActivity", "FCM Token: " + token);

                registerDeviceTokenUseCase.execute(token, "Android", result -> {
                    if (result.isSuccess()) {
                        Log.d("WorkerActivity", "Register FCM token success");
                    } else {
                        Log.e("WorkerActivity", "Register FCM token error: " + result.getError().getMessage());
                    }
                });
            });
        } catch (Exception e) {
            Log.e("WorkerActivity", "Error initializing Firebase Messaging: " + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchAndRegisterFcmToken();
            } else {
                Log.w("WorkerActivity", "POST_NOTIFICATIONS permission denied by user");
            }
        }
    }

    private androidx.appcompat.app.AlertDialog incomingOrderDialog;
    private android.os.CountDownTimer countDownTimer;
    private final android.os.Handler pollHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private final Runnable pollRunnable = new Runnable() {
        @Override
        public void run() {
            pollPendingAssignments();
            pollHandler.postDelayed(this, 5000L);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        pollHandler.post(pollRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pollHandler.removeCallbacks(pollRunnable);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (incomingOrderDialog != null && incomingOrderDialog.isShowing()) {
            incomingOrderDialog.dismiss();
        }
    }

    private void pollPendingAssignments() {
        if (incomingOrderDialog != null && incomingOrderDialog.isShowing()) {
            return;
        }
        getPendingAssignmentsUseCase.execute(result -> {
            if (result.isSuccess() && result.getData() != null && !result.getData().isEmpty()) {
                com.fixit.feature.worker.orders.domain.model.WorkerAssignment assignment = result.getData().get(0);
                if (assignment.getRemainingSeconds() > 0) {
                    showIncomingOrderDialog(assignment);
                }
            }
        });
    }

    private void showIncomingOrderDialog(com.fixit.feature.worker.orders.domain.model.WorkerAssignment assignment) {
        if (incomingOrderDialog != null && incomingOrderDialog.isShowing()) {
            return;
        }

        long durationMs = assignment.getRemainingSeconds() * 1000L;
        if (durationMs <= 0) {
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(com.fixit.R.layout.dialog_incoming_order, null);
        
        TextView tvCountdownTimer = dialogView.findViewById(com.fixit.R.id.tvCountdownTimer);
        TextView tvIncomingService = dialogView.findViewById(com.fixit.R.id.tvIncomingService);
        TextView tvIncomingDistance = dialogView.findViewById(com.fixit.R.id.tvIncomingDistance);
        TextView tvIncomingAddress = dialogView.findViewById(com.fixit.R.id.tvIncomingAddress);
        View btnReject = dialogView.findViewById(com.fixit.R.id.btnReject);
        View btnAccept = dialogView.findViewById(com.fixit.R.id.btnAccept);

        tvIncomingService.setText(assignment.getServiceName());
        tvIncomingAddress.setText(assignment.getAddressPreview());
        tvIncomingDistance.setText(String.format(Locale.US, "Giá: %,.0f đ - %s", assignment.getFinalPrice(), assignment.getPaymentMethod()));

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new android.os.CountDownTimer(durationMs, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                long sec = millisUntilFinished / 1000;
                long min = sec / 60;
                sec = sec % 60;
                tvCountdownTimer.setText(String.format(Locale.US, "%02d:%02d", min, sec));
            }

            @Override
            public void onFinish() {
                if (incomingOrderDialog != null && incomingOrderDialog.isShowing()) {
                    incomingOrderDialog.dismiss();
                }
            }
        }.start();

        incomingOrderDialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        if (incomingOrderDialog.getWindow() != null) {
            incomingOrderDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnReject.setOnClickListener(v -> {
            countDownTimer.cancel();
            incomingOrderDialog.dismiss();
            rejectAssignment(assignment.getBookingId(), assignment.getAssignmentId());
        });

        btnAccept.setOnClickListener(v -> {
            countDownTimer.cancel();
            incomingOrderDialog.dismiss();
            acceptAssignment(assignment.getBookingId(), assignment.getAssignmentId());
        });

        incomingOrderDialog.show();
    }

    private void acceptAssignment(String bookingId, String assignmentId) {
        acceptAssignmentUseCase.execute(bookingId, assignmentId, result -> {
            if (result.isSuccess()) {
                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(binding.navHostFragment.getId());
                if (navHostFragment != null) {
                    NavController navController = navHostFragment.getNavController();
                    Bundle bundle = new Bundle();
                    bundle.putString("orderId", bookingId);
                    navController.navigate(com.fixit.R.id.workerOrderDetailFragment, bundle);
                }
            } else {
                Toast.makeText(this, "Không thể chấp nhận đơn: " + result.getError().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void rejectAssignment(String bookingId, String assignmentId) {
        rejectAssignmentUseCase.execute(bookingId, assignmentId, result -> {
            if (!result.isSuccess()) {
                Toast.makeText(this, "Không thể từ chối đơn: " + result.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
