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
public final class GetWalletBalanceUseCase_Factory implements Factory<GetWalletBalanceUseCase> {
  private final Provider<WorkerWalletRepository> repositoryProvider;

  public GetWalletBalanceUseCase_Factory(Provider<WorkerWalletRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetWalletBalanceUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetWalletBalanceUseCase_Factory create(
      Provider<WorkerWalletRepository> repositoryProvider) {
    return new GetWalletBalanceUseCase_Factory(repositoryProvider);
  }

  public static GetWalletBalanceUseCase newInstance(WorkerWalletRepository repository) {
    return new GetWalletBalanceUseCase(repository);
  }
}
