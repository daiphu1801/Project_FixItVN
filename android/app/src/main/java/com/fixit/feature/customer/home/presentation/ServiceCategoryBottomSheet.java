package com.fixit.feature.customer.home.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fixit.R;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.common.Result;
import com.fixit.feature.customer.service.domain.model.ServiceItem;
import com.fixit.feature.customer.service.domain.repository.ServiceRepository;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ServiceCategoryBottomSheet extends BottomSheetDialogFragment {

    public interface OnServiceItemSelectedListener {
        void onServiceItemSelected(ServiceItem item);
    }

    private static final String ARG_CATEGORY_ID = "category_id";
    private static final String ARG_CATEGORY_NAME = "category_name";

    private OnServiceItemSelectedListener listener;
    private Integer categoryId;
    private String categoryName;

    private ServiceItemAdapter adapter;
    private ProgressBar progressBar;

    @Inject
    ServiceRepository serviceRepository;

    public static ServiceCategoryBottomSheet newInstance(Integer categoryId, String categoryName) {
        ServiceCategoryBottomSheet fragment = new ServiceCategoryBottomSheet();
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY_ID, categoryId);
        args.putString(ARG_CATEGORY_NAME, categoryName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getInt(ARG_CATEGORY_ID);
            categoryName = getArguments().getString(ARG_CATEGORY_NAME);
        }
    }

    public void setOnServiceItemSelectedListener(OnServiceItemSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_service_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        TextView tvTitle = view.findViewById(R.id.tv_title);
        if (tvTitle != null) {
            tvTitle.setText(categoryName);
        }

        progressBar = view.findViewById(R.id.progressBar);
        RecyclerView rvServiceItems = view.findViewById(R.id.rv_service_items);

        if (rvServiceItems != null) {
            adapter = new ServiceItemAdapter(item -> {
                if (listener != null) {
                    listener.onServiceItemSelected(item);
                    dismiss();
                }
            });
            rvServiceItems.setLayoutManager(new LinearLayoutManager(getContext()));
            rvServiceItems.setAdapter(adapter);
        }

        loadServiceItems();
    }

    private void loadServiceItems() {
        if (categoryId == null || serviceRepository == null) return;

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        serviceRepository.getItemsByCategoryId(categoryId, new ResultCallback<List<ServiceItem>>() {
            @Override
            public void onResult(Result<List<ServiceItem>> result) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                
                if (result.isSuccess()) {
                    if (adapter != null) {
                        adapter.submitList(result.getData());
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi tải dịch vụ: " + result.getError().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
