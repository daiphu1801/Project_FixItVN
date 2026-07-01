package com.fixit.feature.customer.search.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.core.ui.BaseViewModel;
import com.fixit.feature.customer.service.domain.model.ServiceCategory;
import com.fixit.feature.customer.service.domain.usecase.GetAllServiceCategoriesUseCase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CustomerSearchViewModel extends BaseViewModel {

    private final GetAllServiceCategoriesUseCase getAllCategoriesUseCase;

    private final MutableLiveData<List<ServiceCategory>> _categories = new MutableLiveData<>();
    public final LiveData<List<ServiceCategory>> categories = _categories;

    private List<ServiceCategory> allCategories = new ArrayList<>();
    private String currentFilterType = "ALL";
    private String currentSearchQuery = "";

    @Inject
    public CustomerSearchViewModel(GetAllServiceCategoriesUseCase getAllCategoriesUseCase) {
        this.getAllCategoriesUseCase = getAllCategoriesUseCase;
    }

    public void fetchCategories() {
        setLoading(true);
        getAllCategoriesUseCase.execute(result -> {
            setLoading(false);
            if (result.isSuccess()) {
                allCategories = result.getData();
                applyFilter();
            } else {
                setError(result.getError().getMessage());
            }
        });
    }

    public void searchCategories(String query) {
        currentSearchQuery = query != null ? query : "";
        applyFilter();
    }

    public void filterByChip(String filterType) {
        currentFilterType = filterType != null ? filterType : "ALL";
        applyFilter();
    }

    private void applyFilter() {
        List<ServiceCategory> filteredList = new ArrayList<>();
        String lowerQuery = currentSearchQuery.toLowerCase().trim();

        for (ServiceCategory category : allCategories) {
            String name = category.getName();
            if (name == null) continue;
            String lowerName = name.toLowerCase();

            // 1. Check search query
            if (!lowerQuery.isEmpty() && !lowerName.contains(lowerQuery)) {
                continue;
            }

            // 2. Check chip filter type
            boolean matchesChip = false;
            switch (currentFilterType) {
                case "ALL":
                    matchesChip = true;
                    break;
                case "ELECTRIC":
                    matchesChip = lowerName.contains("điện nước") || lowerName.contains("điện");
                    break;
                case "AC":
                    matchesChip = lowerName.contains("điện lạnh") || lowerName.contains("máy lạnh") || lowerName.contains("lạnh");
                    break;
                case "PLUMBING":
                    matchesChip = lowerName.contains("thông nghẹt") || lowerName.contains("ống nước") || lowerName.contains("cơ khí");
                    break;
                case "CLEANING":
                    matchesChip = lowerName.contains("vệ sinh") || lowerName.contains("giặt ghế") || lowerName.contains("dọn dẹp");
                    break;
                default:
                    matchesChip = true;
                    break;
            }

            if (matchesChip) {
                filteredList.add(category);
            }
        }
        _categories.setValue(filteredList);
    }
}
