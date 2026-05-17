package com.fixit.feature.worker.home.presentation;

import com.fixit.feature.worker.home.domain.usecase.GetTodayAppointmentsUseCase;
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
public final class WorkerHomeViewModel_Factory implements Factory<WorkerHomeViewModel> {
  private final Provider<GetTodayAppointmentsUseCase> getTodayAppointmentsUseCaseProvider;

  public WorkerHomeViewModel_Factory(
      Provider<GetTodayAppointmentsUseCase> getTodayAppointmentsUseCaseProvider) {
    this.getTodayAppointmentsUseCaseProvider = getTodayAppointmentsUseCaseProvider;
  }

  @Override
  public WorkerHomeViewModel get() {
    return newInstance(getTodayAppointmentsUseCaseProvider.get());
  }

  public static WorkerHomeViewModel_Factory create(
      Provider<GetTodayAppointmentsUseCase> getTodayAppointmentsUseCaseProvider) {
    return new WorkerHomeViewModel_Factory(getTodayAppointmentsUseCaseProvider);
  }

  public static WorkerHomeViewModel newInstance(
      GetTodayAppointmentsUseCase getTodayAppointmentsUseCase) {
    return new WorkerHomeViewModel(getTodayAppointmentsUseCase);
  }
}
