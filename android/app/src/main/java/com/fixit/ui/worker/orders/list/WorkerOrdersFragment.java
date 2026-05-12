package com.fixit.ui.worker.orders.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fixit.base.BaseFragment;
import com.fixit.databinding.FragmentWorkerOrdersBinding;
import com.fixit.ui.worker.orders.WorkerOrdersViewModel;
import com.google.android.material.tabs.TabLayout;

import com.fixit.base.BaseFragment;
import com.fixit.databinding.FragmentWorkerOrdersBinding;
import com.google.android.material.tabs.TabLayout;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerOrdersFragment extends BaseFragment<FragmentWorkerOrdersBinding> {

    private WorkerOrdersViewModel viewModel;
    private WorkerOrderAdapter adapter;

    // ──────────────────────────────────────────────────────────────────────────
    // 1. Inflate
    // ──────────────────────────────────────────────────────────────────────────
    @Override
    protected FragmentWorkerOrdersBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerOrdersBinding.inflate(inflater, container, false);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 2. Setup Views
    // ──────────────────────────────────────────────────────────────────────────
    @Override
    protected void setupViews() {
        // Ẩn nút back vì đây là top-level destination
        View btnBack = requireView().findViewById(com.fixit.R.id.btnBack);
        if (btnBack != null) btnBack.setVisibility(View.GONE);

        android.widget.TextView tvTitle = requireView().findViewById(com.fixit.R.id.tvToolbarTitle);
        if (tvTitle != null) tvTitle.setText("Đơn hàng");

        // Setup RecyclerView
        adapter = new WorkerOrderAdapter();
        adapter.setOnOrderClickListener(order -> {
            android.os.Bundle bundle = new android.os.Bundle();
            bundle.putString("orderId", order.getOrderId());
            
            // Nếu đơn hàng đang có khiếu nại, chuyển đến màn hình giải trình khiếu nại
            if (order.getComplaintStatus() != null && !order.getComplaintStatus().equals("none")) {
                androidx.navigation.Navigation.findNavController(requireView())
                        .navigate(com.fixit.R.id.workerComplaintFragment, bundle);
            } else {
                androidx.navigation.Navigation.findNavController(requireView())
                        .navigate(com.fixit.R.id.workerOrderDetailFragment, bundle);
            }
        });
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvOrders.setAdapter(adapter);

        // Setup Tab listener → lọc dữ liệu theo tab
        binding.tabLayoutOrders.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: viewModel.filterByStatus("pending");  break;
                    case 1: viewModel.filterByStatus("ongoing");  break;
                    case 2: viewModel.filterByStatus("history");  break;
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Restore tab position
        String currentStatus = viewModel.getCurrentFilterStatus();
        if ("pending".equals(currentStatus)) binding.tabLayoutOrders.getTabAt(0).select();
        else if ("ongoing".equals(currentStatus)) binding.tabLayoutOrders.getTabAt(1).select();
        else if ("history".equals(currentStatus)) binding.tabLayoutOrders.getTabAt(2).select();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 3. Observe Data
    // ──────────────────────────────────────────────────────────────────────────
    @Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(WorkerOrdersViewModel.class);
    }

    @Override
    protected void observeData() {
        // Lắng nghe danh sách đơn đã lọc → cập nhật adapter + hiển thị empty state
        viewModel.filteredOrders.observe(getViewLifecycleOwner(), orders -> {
            adapter.submitList(orders);

            // Hiện/ẩn trạng thái rỗng (layout_empty_state → id: layoutEmpty)
            View emptyState = requireView().findViewById(com.fixit.R.id.layoutEmpty);
            if (emptyState != null) {
                emptyState.setVisibility(orders == null || orders.isEmpty()
                        ? View.VISIBLE : View.GONE);
            }
        });
    }
}
