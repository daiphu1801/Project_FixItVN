package com.fixit.feature.customer.home.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.core.common.Result;
import com.fixit.core.common.ResultCallback;
import com.fixit.core.ui.BaseViewModel;
import com.fixit.feature.customer.service.domain.model.ServiceItem;
import com.fixit.feature.customer.service.domain.usecase.GetServiceItemsUseCase;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ServiceCategoryBottomSheetViewModel extends BaseViewModel {

    private final GetServiceItemsUseCase getServiceItemsUseCase;

    private final MutableLiveData<List<ServiceItem>> _items = new MutableLiveData<>();
    public LiveData<List<ServiceItem>> items = _items;

    @Inject
    public ServiceCategoryBottomSheetViewModel(GetServiceItemsUseCase getServiceItemsUseCase) {
        this.getServiceItemsUseCase = getServiceItemsUseCase;
    }

    public void fetchItems(Integer categoryId) {
        setLoading(true);
        getServiceItemsUseCase.execute(categoryId, result -> {
            setLoading(false);
            if (result.isSuccess()) {
                _items.setValue(result.getData());
            } else {
                setError(result.getError().getMessage());
            }
        });
    }
}
