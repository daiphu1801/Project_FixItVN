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
                _categories.setValue(allCategories);
            } else {
                setError(result.getError().getMessage());
            }
        });
    }

    public void searchCategories(String query) {
        if (query == null || query.trim().isEmpty()) {
            _categories.setValue(allCategories);
            return;
        }

        String lowerQuery = query.toLowerCase().trim();
        List<ServiceCategory> filteredList = new ArrayList<>();
        for (ServiceCategory category : allCategories) {
            if (category.getName() != null && category.getName().toLowerCase().contains(lowerQuery)) {
                filteredList.add(category);
            }
        }
        _categories.setValue(filteredList);
    }
}
