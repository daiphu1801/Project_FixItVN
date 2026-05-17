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
public final class SaveExtraCostsUseCase_Factory implements Factory<SaveExtraCostsUseCase> {
  private final Provider<WorkerOrdersRepository> repositoryProvider;

  public SaveExtraCostsUseCase_Factory(Provider<WorkerOrdersRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SaveExtraCostsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static SaveExtraCostsUseCase_Factory create(
      Provider<WorkerOrdersRepository> repositoryProvider) {
    return new SaveExtraCostsUseCase_Factory(repositoryProvider);
  }

  public static SaveExtraCostsUseCase newInstance(WorkerOrdersRepository repository) {
    return new SaveExtraCostsUseCase(repository);
  }
}
