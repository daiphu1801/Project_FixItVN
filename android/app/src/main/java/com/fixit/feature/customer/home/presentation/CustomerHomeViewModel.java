package com.fixit.feature.customer.home.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.ui.BaseViewModel;
import com.fixit.feature.customer.service.domain.model.ServiceCategory;
import com.fixit.feature.customer.service.domain.usecase.GetAllServiceCategoriesUseCase;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CustomerHomeViewModel extends BaseViewModel {

    private final GetAllServiceCategoriesUseCase getAllCategoriesUseCase;

    private final MutableLiveData<List<ServiceCategory>> _categories = new MutableLiveData<>();
    public final LiveData<List<ServiceCategory>> categories = _categories;

    @Inject
    public CustomerHomeViewModel(GetAllServiceCategoriesUseCase getAllCategoriesUseCase) {
        this.getAllCategoriesUseCase = getAllCategoriesUseCase;
    }

    public void fetchCategories() {
        setLoading(true);
        getAllCategoriesUseCase.execute(result -> {
            setLoading(false);
            if (result.isSuccess()) {
                _categories.setValue(result.getData());
            } else {
                setError(result.getError().getMessage());
            }
        });
    }
}
