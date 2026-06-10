package com.fixit.feature.customer.favorite.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fixit.core.common.AppError;
import com.fixit.feature.customer.favorite.domain.model.FavoriteWorker;
import com.fixit.feature.customer.favorite.domain.usecase.GetFavoriteWorkersUseCase;
import com.fixit.feature.customer.favorite.domain.usecase.RemoveFavoriteWorkerUseCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FavoriteWorkersViewModel extends ViewModel {

    private final GetFavoriteWorkersUseCase getFavoriteWorkersUseCase;
    private final RemoveFavoriteWorkerUseCase removeFavoriteWorkerUseCase;

    private final MutableLiveData<List<FavoriteWorker>> _favoriteWorkers = new MutableLiveData<>();
    public LiveData<List<FavoriteWorker>> favoriteWorkers = _favoriteWorkers;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

    private List<FavoriteWorker> originalList = new ArrayList<>();

    @Inject
    public FavoriteWorkersViewModel(
            GetFavoriteWorkersUseCase getFavoriteWorkersUseCase,
            RemoveFavoriteWorkerUseCase removeFavoriteWorkerUseCase) {
        this.getFavoriteWorkersUseCase = getFavoriteWorkersUseCase;
        this.removeFavoriteWorkerUseCase = removeFavoriteWorkerUseCase;
    }

    public void fetchFavoriteWorkers() {
        _isLoading.setValue(true);
        getFavoriteWorkersUseCase.execute(result -> {
            _isLoading.postValue(false);
            if (result.isSuccess()) {
                originalList = result.getData() != null ? result.getData() : new ArrayList<>();
                _favoriteWorkers.postValue(originalList);
            } else {
                AppError error = result.getError();
                _errorMessage.postValue(error != null ? error.getMessage() : "Lỗi tải danh sách thợ quen");
            }
        });
    }

    public void removeFromFavorites(FavoriteWorker worker) {
        removeFavoriteWorkerUseCase.execute(worker.getWorkerId(), result -> {
            if (result.isSuccess()) {
                originalList.remove(worker);
                _favoriteWorkers.postValue(new ArrayList<>(originalList));
            } else {
                AppError error = result.getError();
                _errorMessage.postValue(error != null ? error.getMessage() : "Lỗi khi xóa thợ quen");
            }
        });
    }

    public void filterWorkers(String query) {
        if (query == null || query.trim().isEmpty()) {
            _favoriteWorkers.setValue(originalList);
            return;
        }

        String searchStr = query.toLowerCase(Locale.getDefault()).trim();
        List<FavoriteWorker> filtered = new ArrayList<>();

        for (FavoriteWorker worker : originalList) {
            if (worker.getFullName().toLowerCase(Locale.getDefault()).contains(searchStr)) {
                filtered.add(worker);
            }
        }
        _favoriteWorkers.setValue(filtered);
    }
}
