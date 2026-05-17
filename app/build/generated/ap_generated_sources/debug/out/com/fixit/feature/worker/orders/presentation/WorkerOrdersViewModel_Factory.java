package com.fixit.feature.worker.orders.presentation;

import com.fixit.feature.worker.orders.domain.usecase.AdvanceJobStatusUseCase;
import com.fixit.feature.worker.orders.domain.usecase.CalculateTotalExtraUseCase;
import com.fixit.feature.worker.orders.domain.usecase.FilterWorkerOrdersUseCase;
import com.fixit.feature.worker.orders.domain.usecase.GenerateWorkerPaymentQrUseCase;
import com.fixit.feature.worker.orders.domain.usecase.GetExtraCostsUseCase;
import com.fixit.feature.worker.orders.domain.usecase.GetInitialJobStatusUseCase;
import com.fixit.feature.worker.orders.domain.usecase.GetWorkerOrderByIdUseCase;
import com.fixit.feature.worker.orders.domain.usecase.SaveExtraCostsUseCase;
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
public final class WorkerOrdersViewModel_Factory implements Factory<WorkerOrdersViewModel> {
  private final Provider<FilterWorkerOrdersUseCase> filterWorkerOrdersUseCaseProvider;

  private final Provider<GetWorkerOrderByIdUseCase> getWorkerOrderByIdUseCaseProvider;

  private final Provider<GetInitialJobStatusUseCase> getInitialJobStatusUseCaseProvider;

  private final Provider<AdvanceJobStatusUseCase> advanceJobStatusUseCaseProvider;

  private final Provider<SaveExtraCostsUseCase> saveExtraCostsUseCaseProvider;

  private final Provider<GetExtraCostsUseCase> getExtraCostsUseCaseProvider;

  private final Provider<CalculateTotalExtraUseCase> calculateTotalExtraUseCaseProvider;

  private final Provider<GenerateWorkerPaymentQrUseCase> generateWorkerPaymentQrUseCaseProvider;

  public WorkerOrdersViewModel_Factory(
      Provider<FilterWorkerOrdersUseCase> filterWorkerOrdersUseCaseProvider,
      Provider<GetWorkerOrderByIdUseCase> getWorkerOrderByIdUseCaseProvider,
      Provider<GetInitialJobStatusUseCase> getInitialJobStatusUseCaseProvider,
      Provider<AdvanceJobStatusUseCase> advanceJobStatusUseCaseProvider,
      Provider<SaveExtraCostsUseCase> saveExtraCostsUseCaseProvider,
      Provider<GetExtraCostsUseCase> getExtraCostsUseCaseProvider,
      Provider<CalculateTotalExtraUseCase> calculateTotalExtraUseCaseProvider,
      Provider<GenerateWorkerPaymentQrUseCase> generateWorkerPaymentQrUseCaseProvider) {
    this.filterWorkerOrdersUseCaseProvider = filterWorkerOrdersUseCaseProvider;
    this.getWorkerOrderByIdUseCaseProvider = getWorkerOrderByIdUseCaseProvider;
    this.getInitialJobStatusUseCaseProvider = getInitialJobStatusUseCaseProvider;
    this.advanceJobStatusUseCaseProvider = advanceJobStatusUseCaseProvider;
    this.saveExtraCostsUseCaseProvider = saveExtraCostsUseCaseProvider;
    this.getExtraCostsUseCaseProvider = getExtraCostsUseCaseProvider;
    this.calculateTotalExtraUseCaseProvider = calculateTotalExtraUseCaseProvider;
    this.generateWorkerPaymentQrUseCaseProvider = generateWorkerPaymentQrUseCaseProvider;
  }

  @Override
  public WorkerOrdersViewModel get() {
    return newInstance(filterWorkerOrdersUseCaseProvider.get(), getWorkerOrderByIdUseCaseProvider.get(), getInitialJobStatusUseCaseProvider.get(), advanceJobStatusUseCaseProvider.get(), saveExtraCostsUseCaseProvider.get(), getExtraCostsUseCaseProvider.get(), calculateTotalExtraUseCaseProvider.get(), generateWorkerPaymentQrUseCaseProvider.get());
  }

  public static WorkerOrdersViewModel_Factory create(
      Provider<FilterWorkerOrdersUseCase> filterWorkerOrdersUseCaseProvider,
      Provider<GetWorkerOrderByIdUseCase> getWorkerOrderByIdUseCaseProvider,
      Provider<GetInitialJobStatusUseCase> getInitialJobStatusUseCaseProvider,
      Provider<AdvanceJobStatusUseCase> advanceJobStatusUseCaseProvider,
      Provider<SaveExtraCostsUseCase> saveExtraCostsUseCaseProvider,
      Provider<GetExtraCostsUseCase> getExtraCostsUseCaseProvider,
      Provider<CalculateTotalExtraUseCase> calculateTotalExtraUseCaseProvider,
      Provider<GenerateWorkerPaymentQrUseCase> generateWorkerPaymentQrUseCaseProvider) {
    return new WorkerOrdersViewModel_Factory(filterWorkerOrdersUseCaseProvider, getWorkerOrderByIdUseCaseProvider, getInitialJobStatusUseCaseProvider, advanceJobStatusUseCaseProvider, saveExtraCostsUseCaseProvider, getExtraCostsUseCaseProvider, calculateTotalExtraUseCaseProvider, generateWorkerPaymentQrUseCaseProvider);
  }

  public static WorkerOrdersViewModel newInstance(
      FilterWorkerOrdersUseCase filterWorkerOrdersUseCase,
      GetWorkerOrderByIdUseCase getWorkerOrderByIdUseCase,
      GetInitialJobStatusUseCase getInitialJobStatusUseCase,
      AdvanceJobStatusUseCase advanceJobStatusUseCase, SaveExtraCostsUseCase saveExtraCostsUseCase,
      GetExtraCostsUseCase getExtraCostsUseCase,
      CalculateTotalExtraUseCase calculateTotalExtraUseCase,
      GenerateWorkerPaymentQrUseCase generateWorkerPaymentQrUseCase) {
    return new WorkerOrdersViewModel(filterWorkerOrdersUseCase, getWorkerOrderByIdUseCase, getInitialJobStatusUseCase, advanceJobStatusUseCase, saveExtraCostsUseCase, getExtraCostsUseCase, calculateTotalExtraUseCase, generateWorkerPaymentQrUseCase);
  }
}
