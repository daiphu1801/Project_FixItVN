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
public final class CalculateTotalExtraUseCase_Factory implements Factory<CalculateTotalExtraUseCase> {
  private final Provider<WorkerOrdersRepository> repositoryProvider;

  public CalculateTotalExtraUseCase_Factory(Provider<WorkerOrdersRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public CalculateTotalExtraUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static CalculateTotalExtraUseCase_Factory create(
      Provider<WorkerOrdersRepository> repositoryProvider) {
    return new CalculateTotalExtraUseCase_Factory(repositoryProvider);
  }

  public static CalculateTotalExtraUseCase newInstance(WorkerOrdersRepository repository) {
    return new CalculateTotalExtraUseCase(repository);
  }
}
