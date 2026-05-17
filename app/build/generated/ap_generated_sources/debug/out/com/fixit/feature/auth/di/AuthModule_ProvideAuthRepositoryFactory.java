package com.fixit.feature.auth.di;

import com.fixit.feature.auth.data.repository.AuthRepositoryImpl;
import com.fixit.feature.auth.domain.repository.AuthRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AuthModule_ProvideAuthRepositoryFactory implements Factory<AuthRepository> {
  private final AuthModule module;

  private final Provider<AuthRepositoryImpl> repositoryProvider;

  public AuthModule_ProvideAuthRepositoryFactory(AuthModule module,
      Provider<AuthRepositoryImpl> repositoryProvider) {
    this.module = module;
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public AuthRepository get() {
    return provideAuthRepository(module, repositoryProvider.get());
  }

  public static AuthModule_ProvideAuthRepositoryFactory create(AuthModule module,
      Provider<AuthRepositoryImpl> repositoryProvider) {
    return new AuthModule_ProvideAuthRepositoryFactory(module, repositoryProvider);
  }

  public static AuthRepository provideAuthRepository(AuthModule instance,
      AuthRepositoryImpl repository) {
    return Preconditions.checkNotNullFromProvides(instance.provideAuthRepository(repository));
  }
}
