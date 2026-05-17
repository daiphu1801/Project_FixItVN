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
public final class ToggleWorkerAvailabilityUseCase_Factory implements Factory<ToggleWorkerAvailabilityUseCase> {
  private final Provider<WorkerAvailabilityRepository> repositoryProvider;

  public ToggleWorkerAvailabilityUseCase_Factory(
      Provider<WorkerAvailabilityRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ToggleWorkerAvailabilityUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ToggleWorkerAvailabilityUseCase_Factory create(
      Provider<WorkerAvailabilityRepository> repositoryProvider) {
    return new ToggleWorkerAvailabilityUseCase_Factory(repositoryProvider);
  }

  public static ToggleWorkerAvailabilityUseCase newInstance(
      WorkerAvailabilityRepository repository) {
    return new ToggleWorkerAvailabilityUseCase(repository);
  }
}
