package com.fixit.feature.worker.profile.presentation;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fixit.R;
import com.fixit.feature.worker.profile.domain.model.ServiceCategory;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class WorkerSelectServiceBottomSheet extends BottomSheetDialogFragment {

    public interface OnServicesSelectedListener {
        void onServicesSelected(List<SpecializationItem> selectedList);
    }

    private OnServicesSelectedListener listener;
    private final List<ServiceCategory> categories = new ArrayList<>();
    private final List<CatalogServiceItem> allCatalogItems = new ArrayList<>();
    private final List<CatalogServiceItem> filteredCatalogItems = new ArrayList<>();

    private final List<ServiceCategory> originalCategories = new ArrayList<>();
    private final List<Integer> alreadySelectedIds = new ArrayList<>();

    private CatalogCategoryChipAdapter categoryAdapter;
    private CatalogServiceAdapter serviceAdapter;

    private Integer currentSelectedCategoryId = -1; // -1 = Tất cả
    private String currentSearchQuery = "";

    // Views
    private View layoutCatalogContainer;
    private View layoutCustomContainer;
    private TextView tvSelectedCount;
    private AutoCompleteTextView edtSelectCategory;
    private Integer selectedCustomCategoryId = null;

    public void setOnServicesSelectedListener(OnServicesSelectedListener listener) {
        this.listener = listener;
    }

    public void setCategoriesAndSelected(List<ServiceCategory> availableCategories, List<SpecializationItem> myServices) {
        this.originalCategories.clear();
        if (availableCategories != null) {
            this.originalCategories.addAll(availableCategories);
        }
        this.alreadySelectedIds.clear();
        if (myServices != null) {
            for (SpecializationItem item : myServices) {
                if (item.getId() != null) {
                    this.alreadySelectedIds.add(item.getId());
                }
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, com.google.android.material.R.style.Theme_Design_Light_BottomSheetDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_select_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Catalog Items using the passed categories
        initCatalogItems();

        // Bind Views
        layoutCatalogContainer = view.findViewById(R.id.layout_catalog_container);
        layoutCustomContainer = view.findViewById(R.id.layout_custom_container);
        tvSelectedCount = view.findViewById(R.id.tv_selected_count);

        EditText edtSearch = view.findViewById(R.id.edt_search);
        androidx.recyclerview.widget.RecyclerView recyclerCategories = view.findViewById(R.id.recycler_catalog_categories);
        androidx.recyclerview.widget.RecyclerView recyclerServices = view.findViewById(R.id.recycler_catalog_services);
        MaterialButton btnConfirm = view.findViewById(R.id.btn_confirm);

        // Custom Service Creator Views
        MaterialButton btnBackToCatalog = view.findViewById(R.id.btn_back_to_catalog);
        SwitchMaterial switchCustomCategory = view.findViewById(R.id.switch_custom_category);
        TextInputLayout tilCustomCategory = view.findViewById(R.id.til_custom_category);
        TextInputLayout tilSelectCategory = view.findViewById(R.id.til_select_category);
        edtSelectCategory = view.findViewById(R.id.edt_select_category);
        EditText edtCustomCategory = view.findViewById(R.id.edt_custom_category);
        EditText edtCustomItem = view.findViewById(R.id.edt_custom_item);
        EditText edtCustomPrice = view.findViewById(R.id.edt_custom_price);
        MaterialButton btnAddCustomService = view.findViewById(R.id.btn_add_custom_service);

        // Setup Adapters
        categoryAdapter = new CatalogCategoryChipAdapter(categories, categoryId -> {
            currentSelectedCategoryId = categoryId;
            filterItems();
        });
        recyclerCategories.setAdapter(categoryAdapter);
        recyclerCategories.setVisibility(View.GONE);

        serviceAdapter = new CatalogServiceAdapter(filteredCatalogItems, this::updateSelectedCount);
        recyclerServices.setAdapter(serviceAdapter);

        // Filter initially
        filterItems();
        updateSelectedCount();

        // Setup Search Listener
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                currentSearchQuery = s.toString().trim();
                filterItems();
            }
        });

        // Setup Sticky Bottom Confirm Click
        btnConfirm.setOnClickListener(v -> {
            if (listener != null) {
                List<SpecializationItem> selectedList = new ArrayList<>();
                for (CatalogServiceItem item : allCatalogItems) {
                    if (item.isSelected()) {
                        String displayName;
                        if (item.isCustom()) {
                            displayName = item.getServiceName();
                        } else if (item.getCategoryName().equals(item.getServiceName())) {
                            displayName = item.getCategoryName();
                        } else {
                            displayName = item.getCategoryName() + " - " + item.getServiceName();
                        }

                        double price = item.getCustomPrice() != null ? item.getCustomPrice() : item.getSuggestedPrice();

                        selectedList.add(new SpecializationItem(
                                item.getServiceId(),
                                displayName,
                                true,
                                price,
                                item.isCustom() ? item.getCategoryName() : null
                        ));
                    }
                }
                listener.onServicesSelected(selectedList);
            }
            dismiss();
        });

        // Setup Card Custom Trigger click listener
        View cardCustomTrigger = view.findViewById(R.id.card_custom_trigger);
        if (cardCustomTrigger != null) {
            cardCustomTrigger.setVisibility(View.GONE);
        }

        // Setup Back to Catalog button
        btnBackToCatalog.setOnClickListener(v -> {
            layoutCustomContainer.setVisibility(View.GONE);
            layoutCatalogContainer.setVisibility(View.VISIBLE);
        });

        // Setup Switch Custom Category toggling
        switchCustomCategory.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tilCustomCategory.setVisibility(View.VISIBLE);
                tilSelectCategory.setVisibility(View.GONE);
                selectedCustomCategoryId = null;
            } else {
                tilCustomCategory.setVisibility(View.GONE);
                tilSelectCategory.setVisibility(View.VISIBLE);
            }
        });

        // Setup Custom Dropdown Adapter
        String[] catNames = new String[categories.size()];
        for (int i = 0; i < categories.size(); i++) {
            catNames[i] = categories.get(i).getServiceName();
        }
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, catNames);
        edtSelectCategory.setAdapter(dropdownAdapter);
        edtSelectCategory.setOnItemClickListener((parent, view1, position, id) -> {
            selectedCustomCategoryId = categories.get(position).getId();
        });

        // Setup Add Custom Service logic
        btnAddCustomService.setOnClickListener(v -> {
            boolean isNewCategory = switchCustomCategory.isChecked();
            String categoryName = "";
            Integer catId = null;

            if (isNewCategory) {
                categoryName = edtCustomCategory.getText().toString().trim();
                if (categoryName.isEmpty()) {
                    edtCustomCategory.setError("Vui lòng nhập tên danh mục");
                    return;
                }
            } else {
                categoryName = edtSelectCategory.getText().toString().trim();
                if (categoryName.isEmpty() || selectedCustomCategoryId == null) {
                    edtSelectCategory.setError("Vui lòng chọn danh mục");
                    return;
                }
                catId = selectedCustomCategoryId;
            }

            String itemName = edtCustomItem.getText().toString().trim();
            if (itemName.isEmpty()) {
                edtCustomItem.setError("Vui lòng nhập tên dịch vụ");
                return;
            }

            String priceStr = edtCustomPrice.getText().toString().trim();
            if (priceStr.isEmpty()) {
                edtCustomPrice.setError("Vui lòng nhập giá");
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                edtCustomPrice.setError("Giá không hợp lệ");
                return;
            }

            // Create new Custom Catalog item
            String fullServiceName = itemName;
            CatalogServiceItem customItem = new CatalogServiceItem(
                    null, // null ID represents custom item
                    catId,
                    categoryName,
                    fullServiceName,
                    price,
                    true // isCustom = true
            );
            customItem.setSelected(true);
            customItem.setCustomPrice(price);

            // Add to lists at the top for maximum visibility
            allCatalogItems.add(0, customItem);
            
            // Clear inputs
            edtCustomCategory.setText("");
            edtCustomItem.setText("");
            edtCustomPrice.setText("");
            edtSelectCategory.setText("");
            selectedCustomCategoryId = null;

            // Switch back to Catalog view and filter
            layoutCustomContainer.setVisibility(View.GONE);
            layoutCatalogContainer.setVisibility(View.VISIBLE);
            
            filterItems();
            updateSelectedCount();
            Toast.makeText(requireContext(), "Đã thêm dịch vụ tùy chỉnh!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);

                // Set height to 85% of screen height to fix the ConstraintLayout 0dp measurement bug
                int screenHeight = getResources().getDisplayMetrics().heightPixels;
                int desiredHeight = (int) (screenHeight * 0.85);
                ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
                layoutParams.height = desiredHeight;
                bottomSheet.setLayoutParams(layoutParams);
            }
        }
    }

    private void initCatalogItems() {
        categories.clear();
        categories.addAll(originalCategories);

        allCatalogItems.clear();
        for (ServiceCategory category : originalCategories) {
            boolean isSelected = alreadySelectedIds.contains(category.getId());
            if (!isSelected) {
                allCatalogItems.add(new CatalogServiceItem(
                        category.getId(),
                        category.getId(),
                        category.getServiceName(),
                        category.getServiceName(),
                        150000.0 // Giá gợi ý mặc định
                ));
            }
        }
    }

    private void filterItems() {
        filteredCatalogItems.clear();
        for (CatalogServiceItem item : allCatalogItems) {
            boolean matchesCategory = (currentSelectedCategoryId == -1 || item.getCategoryId().equals(currentSelectedCategoryId));
            boolean matchesSearch = item.getServiceName().toLowerCase().contains(currentSearchQuery.toLowerCase()) || 
                                    item.getCategoryName().toLowerCase().contains(currentSearchQuery.toLowerCase());
            if (matchesCategory && matchesSearch) {
                filteredCatalogItems.add(item);
            }
        }

        serviceAdapter.notifyDataSetChanged();
    }

    private void updateSelectedCount() {
        int count = 0;
        for (CatalogServiceItem item : allCatalogItems) {
            if (item.isSelected()) {
                count++;
            }
        }

        if (count > 0) {
            tvSelectedCount.setText("Đã chọn: " + count + " lĩnh vực chuyên môn");
        } else {
            tvSelectedCount.setText("Chưa chọn lĩnh vực chuyên môn nào");
        }
    }
}
