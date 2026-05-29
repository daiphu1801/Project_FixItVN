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

    @Override
    protected FragmentWorkerDepositBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerDepositBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        binding.btnBack.setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());

        // Gợi ý số tiền nhanh
        binding.btn50k.setOnClickListener(v -> binding.etAmount.setText("50000"));
        binding.btn100k.setOnClickListener(v -> binding.etAmount.setText("100000"));
        binding.btn200k.setOnClickListener(v -> binding.etAmount.setText("200000"));

        // Tạo QR chuyển khoản
        binding.btnGenerateQR.setOnClickListener(v -> generateQrPayment());

        // Giả lập nạp thành công
        binding.btnSimulateSuccess.setOnClickListener(v -> {
            if (viewModel != null) {
                viewModel.simulateSuccess();
            }
        });

        // Nút hoàn thành giao dịch
        binding.btnBackToWallet.setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());
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
        viewModel = new ViewModelProvider(this).get(WorkerDepositViewModel.class);

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

                // Điền mã nội dung chuyển khoản
                String txId = viewModel.transactionId.getValue();
                if (txId != null) {
                    binding.tvDepositNote.setText(txId);
                }

                // Bắt đầu đếm ngược 5 phút
                startCountdownTimer();
            }
        });

        viewModel.status.observe(getViewLifecycleOwner(), status -> {
            if ("SUCCESS".equals(status)) {
                // Dừng đếm ngược
                if (timer != null) timer.cancel();

                // Hiển thị màn hình thành công cực đẹp
                binding.scrollStep2.setVisibility(View.GONE);
                binding.layoutSuccessState.setVisibility(View.VISIBLE);
                binding.btnSimulateSuccess.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Nạp tiền thành công!", Toast.LENGTH_LONG).show();
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
                binding.btnSimulateSuccess.setEnabled(false);
                binding.btnSimulateSuccess.setAlpha(0.5f);
                Toast.makeText(requireContext(), "Mã QR đã hết hạn giao dịch", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
        }
    }
}
