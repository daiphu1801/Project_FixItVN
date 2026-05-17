package com.fixit.feature.worker.profile.presentation;

import com.fixit.feature.auth.domain.usecase.LogoutUseCase;
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
public final class WorkerProfileViewModel_Factory implements Factory<WorkerProfileViewModel> {
  private final Provider<LogoutUseCase> logoutUseCaseProvider;

  public WorkerProfileViewModel_Factory(Provider<LogoutUseCase> logoutUseCaseProvider) {
    this.logoutUseCaseProvider = logoutUseCaseProvider;
  }

  @Override
  public WorkerProfileViewModel get() {
    return newInstance(logoutUseCaseProvider.get());
  }

  public static WorkerProfileViewModel_Factory create(
      Provider<LogoutUseCase> logoutUseCaseProvider) {
    return new WorkerProfileViewModel_Factory(logoutUseCaseProvider);
  }

  public static WorkerProfileViewModel newInstance(LogoutUseCase logoutUseCase) {
    return new WorkerProfileViewModel(logoutUseCase);
  }
}
