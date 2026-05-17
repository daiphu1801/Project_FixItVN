package com.fixit.feature.worker.wallet.domain.usecase;

import com.fixit.feature.worker.wallet.domain.repository.WorkerWalletRepository;
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
public final class GetWalletTransactionsUseCase_Factory implements Factory<GetWalletTransactionsUseCase> {
  private final Provider<WorkerWalletRepository> repositoryProvider;

  public GetWalletTransactionsUseCase_Factory(Provider<WorkerWalletRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetWalletTransactionsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetWalletTransactionsUseCase_Factory create(
      Provider<WorkerWalletRepository> repositoryProvider) {
    return new GetWalletTransactionsUseCase_Factory(repositoryProvider);
  }

  public static GetWalletTransactionsUseCase newInstance(WorkerWalletRepository repository) {
    return new GetWalletTransactionsUseCase(repository);
  }
}
