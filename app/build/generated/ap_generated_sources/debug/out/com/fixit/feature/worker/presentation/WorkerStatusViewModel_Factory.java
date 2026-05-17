package com.fixit.feature.worker.presentation;

import com.fixit.feature.worker.availability.domain.usecase.GetWorkerAvailabilityUseCase;
import com.fixit.feature.worker.availability.domain.usecase.ToggleWorkerAvailabilityUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class WorkerStatusViewModel_Factory implements Factory<WorkerStatusViewModel> {
  private final Provider<GetWorkerAvailabilityUseCase> getWorkerAvailabilityUseCaseProvider;

  private final Provider<ToggleWorkerAvailabilityUseCase> toggleWorkerAvailabilityUseCaseProvider;

  public WorkerStatusViewModel_Factory(
      Provider<GetWorkerAvailabilityUseCase> getWorkerAvailabilityUseCaseProvider,
      Provider<ToggleWorkerAvailabilityUseCase> toggleWorkerAvailabilityUseCaseProvider) {
    this.getWorkerAvailabilityUseCaseProvider = getWorkerAvailabilityUseCaseProvider;
    this.toggleWorkerAvailabilityUseCaseProvider = toggleWorkerAvailabilityUseCaseProvider;
  }

  @Override
  public WorkerStatusViewModel get() {
    return newInstance(getWorkerAvailabilityUseCaseProvider.get(), toggleWorkerAvailabilityUseCaseProvider.get());
  }

  public static WorkerStatusViewModel_Factory create(
      Provider<GetWorkerAvailabilityUseCase> getWorkerAvailabilityUseCaseProvider,
      Provider<ToggleWorkerAvailabilityUseCase> toggleWorkerAvailabilityUseCaseProvider) {
    return new WorkerStatusViewModel_Factory(getWorkerAvailabilityUseCaseProvider, toggleWorkerAvailabilityUseCaseProvider);
  }

  public static WorkerStatusViewModel newInstance(
      GetWorkerAvailabilityUseCase getWorkerAvailabilityUseCase,
      ToggleWorkerAvailabilityUseCase toggleWorkerAvailabilityUseCase) {
    return new WorkerStatusViewModel(getWorkerAvailabilityUseCase, toggleWorkerAvailabilityUseCase);
  }
}
