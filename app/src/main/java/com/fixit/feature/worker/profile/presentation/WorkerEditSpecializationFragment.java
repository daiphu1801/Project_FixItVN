package com.fixit.feature.worker.profile.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.fixit.R;
import com.fixit.core.ui.BaseFragment;
import com.fixit.databinding.FragmentWorkerEditSpecializationBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerEditSpecializationFragment extends BaseFragment<FragmentWorkerEditSpecializationBinding> {

    private WorkerProfileViewModel viewModel;
    private WorkerSpecializationAdapter adapter;
    private java.util.List<SpecializationItem> myServices = new java.util.ArrayList<>();

    @Override
    protected FragmentWorkerEditSpecializationBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentWorkerEditSpecializationBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        // Xử lý nút Back trên Toolbar
        binding.toolbarEditSpecialization.setNavigationOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });

        // Mock data khởi tạo (sau này sẽ lấy từ ViewModel/DB)
        myServices.add(new SpecializationItem(1, "Sửa chữa Điện - Nước", true, 150000.0));
        myServices.add(new SpecializationItem(2, "Sửa chữa Điện lạnh", true, 200000.0));

        adapter = new WorkerSpecializationAdapter(myServices, position -> {
            myServices.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, myServices.size());
        });
        binding.recyclerSpecializations.setAdapter(adapter);

        // Nút thêm dịch vụ mới
        binding.btnAddService.setOnClickListener(v -> showAddServiceDialog());

        // Xử lý nút Lưu thay đổi
        binding.btnSave.setOnClickListener(v -> {
            // TODO: Gửi danh sách myServices lên Server
            Toast.makeText(requireContext(), "Đã cập nhật danh sách dịch vụ", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigateUp();
        });
    }

    private void showAddServiceDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_service, null);
        EditText edtName = dialogView.findViewById(R.id.edtDialogServiceName);
        EditText edtPrice = dialogView.findViewById(R.id.edtDialogBasePrice);

        new androidx.appcompat.app.AlertDialog.Builder(requireContext(), com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setView(dialogView)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String name = edtName.getText().toString().trim();
                    String priceStr = edtPrice.getText().toString().trim();

                    if (!name.isEmpty()) {
                        Double price = priceStr.isEmpty() ? 0.0 : Double.parseDouble(priceStr);
                        // Tạo ID giả cho bản mock
                        int newId = myServices.size() + 1;
                        myServices.add(new SpecializationItem(newId, name, true, price));
                        adapter.notifyItemInserted(myServices.size() - 1);
                    } else {
                        Toast.makeText(requireContext(), "Vui lòng nhập tên dịch vụ", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    @Override
    protected void observeData() {
        viewModel = new ViewModelProvider(this).get(WorkerProfileViewModel.class);
    }
}
