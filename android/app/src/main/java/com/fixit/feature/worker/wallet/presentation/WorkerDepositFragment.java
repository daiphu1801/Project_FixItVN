package com.fixit.feature.worker.wallet.presentation;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentWorkerDepositBinding;

import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerDepositFragment extends BaseFragment<FragmentWorkerDepositBinding> {

    private WorkerDepositViewModel viewModel;
    private CountDownTimer timer;
    private androidx.activity.OnBackPressedCallback backPressedCallback;

    @Override
    protected FragmentWorkerDepositBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerDepositBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        viewModel = new ViewModelProvider(this).get(WorkerDepositViewModel.class);

        // Đăng ký bắt sự kiện nút Back hệ thống
        backPressedCallback = new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackPress();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), backPressedCallback);

        binding.btnBack.setOnClickListener(v -> handleBackPress());

        // Gợi ý số tiền nhanh
        binding.btn50k.setOnClickListener(v -> binding.etAmount.setText("50000"));
        binding.btn100k.setOnClickListener(v -> binding.etAmount.setText("100000"));
        binding.btn200k.setOnClickListener(v -> binding.etAmount.setText("200000"));

        // Tạo QR chuyển khoản
        binding.btnGenerateQR.setOnClickListener(v -> generateQrPayment());

        // Nút Kiểm tra trạng thái giao dịch
        binding.btnCheckStatus.setOnClickListener(v -> viewModel.checkDepositStatusManual());

        // Nút Hủy yêu cầu nạp tiền
        binding.btnCancelDeposit.setOnClickListener(v -> handleBackPress());

        // Nút hoàn thành giao dịch
        binding.btnBackToWallet.setOnClickListener(v -> handleBackPress());
    }

    private void generateQrPayment() {
        String amtStr = binding.etAmount.getText() != null ? binding.etAmount.getText().toString().trim() : "";
        if (amtStr.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập số tiền nạp", Toast.LENGTH_SHORT).show();
            return;
        }

        long amount;
        try {
            amount = Long.parseLong(amtStr);
        } catch (Exception ignored) {
            Toast.makeText(requireContext(), "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount < 10000) {
            Toast.makeText(requireContext(), "Số tiền nạp tối thiểu là 10.000 đ", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.generateQr(amount);
    }

    @Override
    protected void observeData() {
        String transactionId = "";
        if (getArguments() != null) {
            transactionId = getArguments().getString("transactionId", "");
        }

        if (!transactionId.isEmpty()) {
            viewModel.loadDeposit(transactionId);
        }

        viewModel.qrCodeUrl.observe(getViewLifecycleOwner(), url -> {
            if (url != null && !url.isEmpty()) {
                binding.scrollStep1.setVisibility(View.GONE);
                binding.scrollStep2.setVisibility(View.VISIBLE);

                binding.pbQrLoading.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(url)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                binding.pbQrLoading.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                binding.pbQrLoading.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(binding.ivQrCode);

                // Điền số tiền
                Long amt = viewModel.amount.getValue();
                if (amt != null) {
                    binding.tvDepositAmount.setText(String.format("%,d đ", amt));
                }

                // Điền mã nội dung chuyển khoản (từ server)
                String content = viewModel.transferContent.getValue();
                if (content != null && !content.isEmpty()) {
                    binding.tvDepositNote.setText(content);
                }

                // Bắt đầu đếm ngược 5 phút
                startCountdownTimer();
            }
        });

        // Observer nội dung chuyển khoản động từ server
        viewModel.transferContent.observe(getViewLifecycleOwner(), content -> {
            if (content != null && !content.isEmpty()) {
                binding.tvDepositNote.setText(content);
            }
        });

        // Observer lỗi
        viewModel.error.observe(getViewLifecycleOwner(), errMsg -> {
            if (errMsg != null && !errMsg.isEmpty()) {
                Toast.makeText(requireContext(), errMsg, Toast.LENGTH_LONG).show();
            }
        });

        // Observer thông báo ngắn (Toast)
        viewModel.toastMessage.observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

        // Observer loading
        viewModel.loading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.layoutLoading.getRoot().setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.status.observe(getViewLifecycleOwner(), status -> {
            if ("Pending".equalsIgnoreCase(status) || "PENDING".equals(status)) {
                // QR observer sẽ xử lý hiển thị
            } else if ("Success".equalsIgnoreCase(status) || "SUCCESS".equals(status)) {
                // Dừng đếm ngược
                if (timer != null) timer.cancel();

                // Hiển thị màn hình thành công
                binding.scrollStep2.setVisibility(View.GONE);
                binding.layoutSuccessState.setVisibility(View.VISIBLE);
                Toast.makeText(requireContext(), "Nạp tiền thành công!", Toast.LENGTH_LONG).show();
            } else if ("Cancelled".equalsIgnoreCase(status) || "CANCELLED".equals(status)) {
                if (timer != null) timer.cancel();
                Toast.makeText(requireContext(), "Yêu cầu nạp tiền đã bị hủy", Toast.LENGTH_SHORT).show();
                if (backPressedCallback != null) {
                    backPressedCallback.setEnabled(false);
                }
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private void startCountdownTimer() {
        if (timer != null) timer.cancel();

        timer = new CountDownTimer(300000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int min = (int) (millisUntilFinished / 1000) / 60;
                int sec = (int) (millisUntilFinished / 1000) % 60;
                binding.tvCountdown.setText(String.format(Locale.getDefault(), "Mã QR hết hạn trong: %02d:%02d", min, sec));
            }

            @Override
            public void onFinish() {
                binding.tvCountdown.setText("Hết hạn");
                Toast.makeText(requireContext(), "Mã QR đã hết hạn giao dịch. Hệ thống đang tự động hủy...", Toast.LENGTH_SHORT).show();
                viewModel.cancelDeposit(() -> {
                    if (backPressedCallback != null) {
                        backPressedCallback.setEnabled(false);
                    }
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                });
            }
        }.start();
    }

    private void handleBackPress() {
        String currentStatus = viewModel.status.getValue();
        if (binding.scrollStep2.getVisibility() == View.VISIBLE &&
                ("PENDING".equalsIgnoreCase(currentStatus) || currentStatus == null)) {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Hủy giao dịch?")
                    .setMessage("Bạn đang có một yêu cầu nạp tiền chưa hoàn tất. Thoát màn hình này sẽ tự động hủy yêu cầu nạp tiền.")
                    .setPositiveButton("Hủy giao dịch và thoát", (dialog, which) -> {
                        viewModel.cancelDeposit(() -> {
                            if (backPressedCallback != null) {
                                backPressedCallback.setEnabled(false);
                            }
                            requireActivity().getOnBackPressedDispatcher().onBackPressed();
                        });
                    })
                    .setNegativeButton("Đóng", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            if (backPressedCallback != null) {
                backPressedCallback.setEnabled(false);
            }
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
        }
    }
}
