package com.fixit.feature.auth.domain.usecase;

import com.fixit.feature.auth.domain.repository.AuthRepository;
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
public final class GetCurrentSessionUseCase_Factory implements Factory<GetCurrentSessionUseCase> {
  private final Provider<AuthRepository> authRepositoryProvider;

  public GetCurrentSessionUseCase_Factory(Provider<AuthRepository> authRepositoryProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public GetCurrentSessionUseCase get() {
    return newInstance(authRepositoryProvider.get());
  }

  public static GetCurrentSessionUseCase_Factory create(
      Provider<AuthRepository> authRepositoryProvider) {
    return new GetCurrentSessionUseCase_Factory(authRepositoryProvider);
  }

  public static GetCurrentSessionUseCase newInstance(AuthRepository authRepository) {
    return new GetCurrentSessionUseCase(authRepository);
  }
}
