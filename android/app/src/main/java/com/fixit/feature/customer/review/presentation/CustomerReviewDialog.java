package com.fixit.feature.customer.review.presentation;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.fixit.databinding.DialogCustomerReviewBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CustomerReviewDialog extends DialogFragment {

    private DialogCustomerReviewBinding binding;
    private CustomerReviewViewModel viewModel;
    private String bookingId;

    public static CustomerReviewDialog newInstance(String bookingId) {
        CustomerReviewDialog fragment = new CustomerReviewDialog();
        Bundle args = new Bundle();
        args.putString("bookingId", bookingId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookingId = getArguments().getString("bookingId");
        }
        viewModel = new ViewModelProvider(this).get(CustomerReviewViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogCustomerReviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bo góc Dialog
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        binding.btnCancel.setOnClickListener(v -> dismiss());

        binding.btnSubmitReview.setOnClickListener(v -> {
            int rating = (int) binding.ratingBar.getRating();
            String comment = binding.etComment.getText().toString().trim();

            if (rating == 0) {
                Toast.makeText(requireContext(), "Vui lòng chọn số sao đánh giá!", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.progressBar.setVisibility(View.VISIBLE);
            binding.btnSubmitReview.setEnabled(false);

            viewModel.submitReview(bookingId, rating, comment, result -> {
                if (isAdded()) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSubmitReview.setEnabled(true);
                    if (result.isSuccess()) {
                        Toast.makeText(requireContext(), "Đánh giá thành công!", Toast.LENGTH_SHORT).show();
                        if (getParentFragment() instanceof OnReviewSubmittedListener) {
                            ((OnReviewSubmittedListener) getParentFragment()).onReviewSubmitted();
                        } else if (getActivity() instanceof OnReviewSubmittedListener) {
                            ((OnReviewSubmittedListener) getActivity()).onReviewSubmitted();
                        }
                        dismiss();
                    } else {
                        Toast.makeText(requireContext(), "Lỗi: " + result.getError().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    public interface OnReviewSubmittedListener {
        void onReviewSubmitted();
    }
}
