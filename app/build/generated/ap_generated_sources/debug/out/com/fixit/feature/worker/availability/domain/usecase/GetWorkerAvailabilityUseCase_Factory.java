package com.fixit.feature.worker.availability.domain.usecase;

import com.fixit.feature.worker.availability.domain.repository.WorkerAvailabilityRepository;
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
public final class GetWorkerAvailabilityUseCase_Factory implements Factory<GetWorkerAvailabilityUseCase> {
  private final Provider<WorkerAvailabilityRepository> repositoryProvider;

  public GetWorkerAvailabilityUseCase_Factory(
      Provider<WorkerAvailabilityRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetWorkerAvailabilityUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetWorkerAvailabilityUseCase_Factory create(
      Provider<WorkerAvailabilityRepository> repositoryProvider) {
    return new GetWorkerAvailabilityUseCase_Factory(repositoryProvider);
  }

  public static GetWorkerAvailabilityUseCase newInstance(WorkerAvailabilityRepository repository) {
    return new GetWorkerAvailabilityUseCase(repository);
  }
}
