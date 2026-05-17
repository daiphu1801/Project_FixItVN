package com.fixit.feature.auth.data.repository;

import com.fixit.core.storage.SessionStorage;
import com.fixit.feature.auth.data.remote.api.AuthApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class AuthRepositoryImpl_Factory implements Factory<AuthRepositoryImpl> {
  private final Provider<AuthApi> authApiProvider;

  private final Provider<SessionStorage> sessionStorageProvider;

  public AuthRepositoryImpl_Factory(Provider<AuthApi> authApiProvider,
      Provider<SessionStorage> sessionStorageProvider) {
    this.authApiProvider = authApiProvider;
    this.sessionStorageProvider = sessionStorageProvider;
  }

  @Override
  public AuthRepositoryImpl get() {
    return newInstance(authApiProvider.get(), sessionStorageProvider.get());
  }

  public static AuthRepositoryImpl_Factory create(Provider<AuthApi> authApiProvider,
      Provider<SessionStorage> sessionStorageProvider) {
    return new AuthRepositoryImpl_Factory(authApiProvider, sessionStorageProvider);
  }

  public static AuthRepositoryImpl newInstance(AuthApi authApi, SessionStorage sessionStorage) {
    return new AuthRepositoryImpl(authApi, sessionStorage);
  }
}
