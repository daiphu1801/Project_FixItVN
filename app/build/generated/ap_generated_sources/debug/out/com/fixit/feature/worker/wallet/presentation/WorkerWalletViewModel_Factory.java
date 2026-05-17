package com.fixit.feature.worker.wallet.presentation;

import com.fixit.feature.worker.wallet.domain.usecase.GetWalletBalanceUseCase;
import com.fixit.feature.worker.wallet.domain.usecase.GetWalletTransactionsUseCase;
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
public final class WorkerWalletViewModel_Factory implements Factory<WorkerWalletViewModel> {
  private final Provider<GetWalletBalanceUseCase> getWalletBalanceUseCaseProvider;

  private final Provider<GetWalletTransactionsUseCase> getWalletTransactionsUseCaseProvider;

  public WorkerWalletViewModel_Factory(
      Provider<GetWalletBalanceUseCase> getWalletBalanceUseCaseProvider,
      Provider<GetWalletTransactionsUseCase> getWalletTransactionsUseCaseProvider) {
    this.getWalletBalanceUseCaseProvider = getWalletBalanceUseCaseProvider;
    this.getWalletTransactionsUseCaseProvider = getWalletTransactionsUseCaseProvider;
  }

  @Override
  public WorkerWalletViewModel get() {
    return newInstance(getWalletBalanceUseCaseProvider.get(), getWalletTransactionsUseCaseProvider.get());
  }

  public static WorkerWalletViewModel_Factory create(
      Provider<GetWalletBalanceUseCase> getWalletBalanceUseCaseProvider,
      Provider<GetWalletTransactionsUseCase> getWalletTransactionsUseCaseProvider) {
    return new WorkerWalletViewModel_Factory(getWalletBalanceUseCaseProvider, getWalletTransactionsUseCaseProvider);
  }

  public static WorkerWalletViewModel newInstance(GetWalletBalanceUseCase getWalletBalanceUseCase,
      GetWalletTransactionsUseCase getWalletTransactionsUseCase) {
    return new WorkerWalletViewModel(getWalletBalanceUseCase, getWalletTransactionsUseCase);
  }
}
