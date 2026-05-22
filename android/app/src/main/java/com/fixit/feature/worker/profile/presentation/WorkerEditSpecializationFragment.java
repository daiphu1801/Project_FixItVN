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
import com.fixit.feature.worker.profile.data.remote.mapper.WorkerSkillMapper;
import com.fixit.feature.worker.profile.domain.model.WorkerSkill;
import com.fixit.feature.worker.profile.domain.model.ServiceCategory;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WorkerEditSpecializationFragment extends BaseFragment<FragmentWorkerEditSpecializationBinding> {

    private WorkerProfileViewModel viewModel;
    private WorkerSpecializationAdapter adapter;
    private final List<SpecializationItem> myServices = new ArrayList<>();
    private final List<ServiceCategory> availableCategories = new ArrayList<>();
    private Integer selectedCategoryId = null;

    @Override
    protected FragmentWorkerEditSpecializationBinding inflateViewBinding(
            LayoutInflater inflater,
            ViewGroup container
    ) {
        return FragmentWorkerEditSpecializationBinding.inflate(inflater, container, false);
    }

    @Override
    protected void setupViews() {
        binding.toolbarEditSpecialization.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).navigateUp()
        );

        adapter = new WorkerSpecializationAdapter(myServices, position -> {
            myServices.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, myServices.size());
        });

        binding.recyclerSpecializations.setAdapter(adapter);

        binding.btnAddService.setOnClickListener(v -> showAddServiceDialog());

        binding.btnSave.setOnClickListener(v -> saveSkills());
    }

    @Override
    protected void observeData() {
        viewModel = new ViewModelProvider(this).get(WorkerProfileViewModel.class);

        viewModel.skills.observe(getViewLifecycleOwner(), skills -> {
            myServices.clear();

            if (skills != null) {
                for (WorkerSkill skill : skills) {
                    myServices.add(WorkerSkillMapper.toPresentationItem(skill));
                }
            }

            adapter.notifyDataSetChanged();
        });

        viewModel.skillsUpdated.observe(getViewLifecycleOwner(), updated -> {
            if (Boolean.TRUE.equals(updated)) {
                Toast.makeText(requireContext(), "Đã cập nhật danh sách dịch vụ", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

        viewModel.errorMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.trim().isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.categories.observe(getViewLifecycleOwner(), categories -> {
            availableCategories.clear();
            if (categories != null) {
                availableCategories.addAll(categories);
            }
        });

        viewModel.loadSkills();
        viewModel.loadServiceCategories();
    }

    private void saveSkills() {
        List<WorkerSkill> skills = new ArrayList<>();

        for (SpecializationItem item : myServices) {
            if (item.getId() <= 0) {
                Toast.makeText(requireContext(), "serviceId không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            skills.add(WorkerSkillMapper.fromPresentationItem(item));
        }

        viewModel.updateSkills(skills);
    }

    private void showAddServiceDialog() {
        selectedCategoryId = null;
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_service, null);
        EditText edtServiceName = dialogView.findViewById(R.id.edtDialogServiceName);
        EditText edtPrice = dialogView.findViewById(R.id.edtDialogBasePrice);

        edtServiceName.setFocusable(false);
        edtServiceName.setClickable(true);

        edtServiceName.setOnClickListener(v -> {
            if (availableCategories.isEmpty()) {
                Toast.makeText(requireContext(), "Đang tải danh mục dịch vụ từ server...", Toast.LENGTH_SHORT).show();
                viewModel.loadServiceCategories();
                return;
            }

            String[] items = new String[availableCategories.size()];
            for (int i = 0; i < availableCategories.size(); i++) {
                items[i] = availableCategories.get(i).getServiceName();
            }

            new androidx.appcompat.app.AlertDialog.Builder(
                    requireContext(),
                    com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog
            )
                    .setTitle("Chọn dịch vụ từ danh mục")
                    .setItems(items, (dialog, which) -> {
                        ServiceCategory selected = availableCategories.get(which);
                        selectedCategoryId = selected.getId();
                        edtServiceName.setText(selected.getServiceName());
                        edtServiceName.setError(null);
                    })
                    .show();
        });

        new androidx.appcompat.app.AlertDialog.Builder(
                requireContext(),
                com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog
        )
                .setView(dialogView)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String serviceNameText = edtServiceName.getText() != null
                            ? edtServiceName.getText().toString().trim()
                            : "";

                    String priceText = edtPrice.getText() != null
                            ? edtPrice.getText().toString().trim()
                            : "";

                    if (selectedCategoryId == null || serviceNameText.isEmpty()) {
                        Toast.makeText(requireContext(), "Vui lòng chọn dịch vụ từ danh mục", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double price = priceText.isEmpty() ? 0.0 : Double.parseDouble(priceText);

                        myServices.add(new SpecializationItem(
                                selectedCategoryId,
                                serviceNameText,
                                true,
                                price
                        ));

                        adapter.notifyItemInserted(myServices.size() - 1);
                    } catch (NumberFormatException e) {
                        Toast.makeText(requireContext(), "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }
}