package com.fixit.feature.customer.booking.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fixit.databinding.BottomSheetQuotationBinding;
import com.fixit.feature.customer.booking.domain.usecase.AcceptQuotationUseCase;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class QuotationBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetQuotationBinding binding;

    @Inject
    AcceptQuotationUseCase acceptQuotationUseCase;

    private String bookingId;
    private String quotationId;
    private BigDecimal laborCost;
    private BigDecimal materialCost;

    public static QuotationBottomSheet newInstance(String bookingId, String quotationId, BigDecimal laborCost, BigDecimal materialCost) {
        QuotationBottomSheet fragment = new QuotationBottomSheet();
        Bundle args = new Bundle();
        args.putString("bookingId", bookingId);
        args.putString("quotationId", quotationId);
        args.putSerializable("laborCost", laborCost);
        args.putSerializable("materialCost", materialCost);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookingId = getArguments().getString("bookingId");
            quotationId = getArguments().getString("quotationId");
            laborCost = (BigDecimal) getArguments().getSerializable("laborCost");
            materialCost = (BigDecimal) getArguments().getSerializable("materialCost");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetQuotationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
        
        if (laborCost != null) {
            binding.tvLaborCost.setText(format.format(laborCost) + " đ");
        }
        if (materialCost != null) {
            binding.tvMaterialCost.setText(format.format(materialCost) + " đ");
        }
        if (laborCost != null && materialCost != null) {
            BigDecimal total = laborCost.add(materialCost);
            binding.tvTotalCost.setText(format.format(total) + " đ");
        }

        binding.btnAccept.setOnClickListener(v -> acceptQuotation());
        
        binding.btnReject.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Bạn đã từ chối báo giá", Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }

    private void acceptQuotation() {
        binding.btnAccept.setEnabled(false);
        binding.btnAccept.setText("Đang xử lý...");

        acceptQuotationUseCase.execute(bookingId, quotationId, result -> {
            if (result != null && result.isSuccess()) {
                Toast.makeText(requireContext(), "Đã đồng ý sửa chữa", Toast.LENGTH_SHORT).show();
                dismiss();
            } else {
                binding.btnAccept.setEnabled(true);
                binding.btnAccept.setText("Đồng ý sửa chữa");
                String error = result != null ? result.getError().getMessage() : "Lỗi";
                Toast.makeText(requireContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
