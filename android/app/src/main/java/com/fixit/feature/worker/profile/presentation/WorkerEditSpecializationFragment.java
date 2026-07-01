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

        binding.btnAddService.setOnClickListener(v -> {
            WorkerSelectServiceBottomSheet bottomSheet = new WorkerSelectServiceBottomSheet();
            bottomSheet.setCategoriesAndSelected(availableCategories, myServices);
            bottomSheet.setOnServicesSelectedListener(selectedList -> {
                if (selectedList != null && !selectedList.isEmpty()) {
                    for (SpecializationItem newItem : selectedList) {
                        boolean exists = false;
                        for (SpecializationItem existing : myServices) {
                            if ((existing.getId() != null && existing.getId().equals(newItem.getId()))
                                    || existing.getName().equalsIgnoreCase(newItem.getName())) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            myServices.add(newItem);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    Toast.makeText(requireContext(), "Đã chọn dịch vụ mới. Nhớ nhấn nút \"Lưu thay đổi\" ở góc dưới màn hình nhé!", Toast.LENGTH_LONG).show();
                }
            });
            bottomSheet.show(getChildFragmentManager(), "SelectServiceBottomSheet");
        });

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

        viewModel.isLoading.observe(getViewLifecycleOwner(), loading -> {
            if (binding.layoutLoading != null) {
                binding.layoutLoading.getRoot().setVisibility(loading ? android.view.View.VISIBLE : android.view.View.GONE);
            }
        });

        viewModel.loadSkills();
        viewModel.loadServiceCategories();
    }

    private void saveSkills() {
        List<WorkerSkill> skills = new ArrayList<>();

        for (SpecializationItem item : myServices) {
            boolean hasValidId = item.getId() != null && item.getId() > 0;
            boolean hasValidCustomName = item.getCustomServiceName() != null && !item.getCustomServiceName().trim().isEmpty();

            if (!hasValidId && !hasValidCustomName) {
                Toast.makeText(requireContext(), "Danh mục dịch vụ không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            skills.add(WorkerSkillMapper.fromPresentationItem(item));
        }

        viewModel.updateSkills(skills);
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