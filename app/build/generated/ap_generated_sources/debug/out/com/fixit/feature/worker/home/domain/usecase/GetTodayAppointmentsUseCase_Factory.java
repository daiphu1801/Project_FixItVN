package com.fixit.feature.worker.home.domain.usecase;

import com.fixit.feature.worker.home.domain.repository.WorkerHomeRepository;
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
public final class GetTodayAppointmentsUseCase_Factory implements Factory<GetTodayAppointmentsUseCase> {
  private final Provider<WorkerHomeRepository> repositoryProvider;

  public GetTodayAppointmentsUseCase_Factory(Provider<WorkerHomeRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetTodayAppointmentsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetTodayAppointmentsUseCase_Factory create(
      Provider<WorkerHomeRepository> repositoryProvider) {
    return new GetTodayAppointmentsUseCase_Factory(repositoryProvider);
  }

  public static GetTodayAppointmentsUseCase newInstance(WorkerHomeRepository repository) {
    return new GetTodayAppointmentsUseCase(repository);
  }
}
