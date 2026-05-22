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
        EditText edtItemName = dialogView.findViewById(R.id.edtDialogItemName);
        EditText edtPrice = dialogView.findViewById(R.id.edtDialogBasePrice);

        edtServiceName.setFocusable(false);
        edtServiceName.setClickable(true);

        edtItemName.setFocusable(false);
        edtItemName.setClickable(true);

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
                    .setTitle("Chọn danh mục dịch vụ")
                    .setItems(items, (dialog, which) -> {
                        ServiceCategory selected = availableCategories.get(which);
                        selectedCategoryId = selected.getId();
                        edtServiceName.setText(selected.getServiceName());
                        edtServiceName.setError(null);
                        
                        // Clear the item selection and price
                        edtItemName.setText("");
                        edtPrice.setText("");
                    })
                    .show();
        });

        edtItemName.setOnClickListener(v -> {
            if (selectedCategoryId == null || edtServiceName.getText().toString().trim().isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng chọn danh mục dịch vụ trước!", Toast.LENGTH_SHORT).show();
                return;
            }

            String categoryName = edtServiceName.getText().toString().trim();
            List<LocalServiceItem> subItems = getLocalServiceItems(categoryName);
            if (subItems.isEmpty()) {
                Toast.makeText(requireContext(), "Không tìm thấy chi tiết dịch vụ", Toast.LENGTH_SHORT).show();
                return;
            }

            String[] subItemNames = new String[subItems.size()];
            for (int i = 0; i < subItems.size(); i++) {
                subItemNames[i] = subItems.get(i).itemName + " (Đề xuất: " + String.format("%,.0f", subItems.get(i).suggestedPrice) + "đ)";
            }

            new androidx.appcompat.app.AlertDialog.Builder(
                    requireContext(),
                    com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog
            )
                    .setTitle("Chọn dịch vụ chi tiết (item_name)")
                    .setItems(subItemNames, (dialog, which) -> {
                        LocalServiceItem selectedSub = subItems.get(which);
                        edtItemName.setText(selectedSub.itemName);
                        edtItemName.setError(null);
                        
                        // Auto-populate recommended price
                        edtPrice.setText(String.valueOf((int) selectedSub.suggestedPrice));
                        edtPrice.setError(null);
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
                    String itemNameText = edtItemName.getText() != null
                            ? edtItemName.getText().toString().trim()
                            : "";
                    String priceText = edtPrice.getText() != null
                            ? edtPrice.getText().toString().trim()
                            : "";

                    if (selectedCategoryId == null || serviceNameText.isEmpty()) {
                        Toast.makeText(requireContext(), "Vui lòng chọn danh mục dịch vụ", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (itemNameText.isEmpty()) {
                        Toast.makeText(requireContext(), "Vui lòng chọn dịch vụ chi tiết", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double price = priceText.isEmpty() ? 0.0 : Double.parseDouble(priceText);

                        String finalDisplayName = serviceNameText + " - " + itemNameText;

                        myServices.add(new SpecializationItem(
                                selectedCategoryId,
                                finalDisplayName,
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

     private static class LocalServiceItem {
         String itemName;
         double suggestedPrice;

         LocalServiceItem(String itemName, double suggestedPrice) {
             this.itemName = itemName;
             this.suggestedPrice = suggestedPrice;
         }
     }

     private List<LocalServiceItem> getLocalServiceItems(String categoryName) {
         List<LocalServiceItem> items = new ArrayList<>();
         if (categoryName == null) return items;

         switch (categoryName.trim()) {
             case "Sửa điện lạnh":
                 items.add(new LocalServiceItem("Vệ sinh & Bảo dưỡng điều hoà", 180000));
                 items.add(new LocalServiceItem("Bơm ga điều hoà công suất nhỏ", 200000));
                 items.add(new LocalServiceItem("Sửa tủ lạnh không làm đá", 350000));
                 items.add(new LocalServiceItem("Sửa tủ lạnh chảy nước", 250000));
                 items.add(new LocalServiceItem("Vệ sinh lồng giặt máy giặt", 220000));
                 items.add(new LocalServiceItem("Sửa máy giặt không vắt", 400000));
                 break;
             case "Sửa điện nước":
                 items.add(new LocalServiceItem("Lắp đặt & Sửa vòi nước", 150000));
                 items.add(new LocalServiceItem("Thông tắc chậu rửa bát/Lavabo", 200000));
                 items.add(new LocalServiceItem("Sửa đường ống nước rò rỉ", 250000));
                 items.add(new LocalServiceItem("Lắp đặt bồn cầu mới", 400000));
                 items.add(new LocalServiceItem("Đi đường dây điện âm/nổi", 300000));
                 items.add(new LocalServiceItem("Lắp đặt bóng đèn/Quạt trần", 120000));
                 break;
             case "Thi công xây dựng":
                 items.add(new LocalServiceItem("Sơn nước nội thất / Ngoại thất", 80000));
                 items.add(new LocalServiceItem("Chống thấm trần & Tường nhà", 500000));
                 items.add(new LocalServiceItem("Khoan tường treo tranh/Kệ sách", 50000));
                 items.add(new LocalServiceItem("Lát gạch nền nhà vệ sinh", 350000));
                 items.add(new LocalServiceItem("Xây trát tường gạch ngăn phòng", 450000));
                 break;
             case "Sửa khóa & Cửa":
                 items.add(new LocalServiceItem("Thay ổ khóa tròn tay gạt", 150000));
                 items.add(new LocalServiceItem("Làm mới chìa khóa cơ", 50000));
                 items.add(new LocalServiceItem("Sửa chữa motor cửa cuốn", 800000));
                 items.add(new LocalServiceItem("Thay thế bản lề cửa gỗ", 100000));
                 break;
             default:
                 items.add(new LocalServiceItem("Kiểm tra & Khảo sát lỗi tận nơi", 50000));
                 items.add(new LocalServiceItem("Sửa chữa thiết bị theo yêu cầu", 150000));
                 items.add(new LocalServiceItem("Lắp ráp thiết bị mới", 200000));
                 break;
         }
         return items;
     }
}