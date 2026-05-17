package com.fixit.feature.worker.orders.domain.usecase;

import com.fixit.feature.worker.orders.domain.repository.WorkerOrdersRepository;
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
public final class GetWorkerOrdersUseCase_Factory implements Factory<GetWorkerOrdersUseCase> {
  private final Provider<WorkerOrdersRepository> repositoryProvider;

  public GetWorkerOrdersUseCase_Factory(Provider<WorkerOrdersRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetWorkerOrdersUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetWorkerOrdersUseCase_Factory create(
      Provider<WorkerOrdersRepository> repositoryProvider) {
    return new GetWorkerOrdersUseCase_Factory(repositoryProvider);
  }

  public static GetWorkerOrdersUseCase newInstance(WorkerOrdersRepository repository) {
    return new GetWorkerOrdersUseCase(repository);
  }
}
