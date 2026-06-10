package com.fixit.feature.customer.workerprofile.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fixit.feature.customer.review.domain.model.Review;
import com.fixit.feature.customer.review.domain.usecase.GetWorkerReviewsUseCase;

import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkerPublicProfileViewModel extends ViewModel {
    private final GetWorkerReviewsUseCase getWorkerReviewsUseCase;
    private final MutableLiveData<List<Review>> _reviews = new MutableLiveData<>();
    public LiveData<List<Review>> getReviews() { return _reviews; }

    @Inject
    public WorkerPublicProfileViewModel(GetWorkerReviewsUseCase getWorkerReviewsUseCase) {
        this.getWorkerReviewsUseCase = getWorkerReviewsUseCase;
    }

    public void loadReviews(String workerId) {
        getWorkerReviewsUseCase.execute(workerId, result -> {
            if (result.isSuccess()) {
                _reviews.setValue(result.getData());
            } else {
                _reviews.setValue(null);
            }
        });
    }
}
