package com.fixit.feature.worker.job.domain.usecase;

import com.fixit.feature.worker.job.domain.repository.WorkerJobRepository;
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
public final class GetWorkerJobSummaryUseCase_Factory implements Factory<GetWorkerJobSummaryUseCase> {
  private final Provider<WorkerJobRepository> repositoryProvider;

  public GetWorkerJobSummaryUseCase_Factory(Provider<WorkerJobRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetWorkerJobSummaryUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetWorkerJobSummaryUseCase_Factory create(
      Provider<WorkerJobRepository> repositoryProvider) {
    return new GetWorkerJobSummaryUseCase_Factory(repositoryProvider);
  }

  public static GetWorkerJobSummaryUseCase newInstance(WorkerJobRepository repository) {
    return new GetWorkerJobSummaryUseCase(repository);
  }
}
