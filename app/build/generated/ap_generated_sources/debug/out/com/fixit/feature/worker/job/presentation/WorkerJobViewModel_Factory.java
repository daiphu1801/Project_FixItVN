package com.fixit.feature.worker.job.presentation;

import com.fixit.feature.worker.job.domain.usecase.GetWorkerJobSummaryUseCase;
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
public final class WorkerJobViewModel_Factory implements Factory<WorkerJobViewModel> {
  private final Provider<GetWorkerJobSummaryUseCase> getWorkerJobSummaryUseCaseProvider;

  public WorkerJobViewModel_Factory(
      Provider<GetWorkerJobSummaryUseCase> getWorkerJobSummaryUseCaseProvider) {
    this.getWorkerJobSummaryUseCaseProvider = getWorkerJobSummaryUseCaseProvider;
  }

  @Override
  public WorkerJobViewModel get() {
    return newInstance(getWorkerJobSummaryUseCaseProvider.get());
  }

  public static WorkerJobViewModel_Factory create(
      Provider<GetWorkerJobSummaryUseCase> getWorkerJobSummaryUseCaseProvider) {
    return new WorkerJobViewModel_Factory(getWorkerJobSummaryUseCaseProvider);
  }

  public static WorkerJobViewModel newInstance(
      GetWorkerJobSummaryUseCase getWorkerJobSummaryUseCase) {
    return new WorkerJobViewModel(getWorkerJobSummaryUseCase);
  }
}
