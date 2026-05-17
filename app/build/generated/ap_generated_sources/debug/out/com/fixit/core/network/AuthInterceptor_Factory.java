package com.fixit.core.network;

import com.fixit.core.storage.SessionStorage;
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
public final class AuthInterceptor_Factory implements Factory<AuthInterceptor> {
  private final Provider<SessionStorage> sessionStorageProvider;

  public AuthInterceptor_Factory(Provider<SessionStorage> sessionStorageProvider) {
    this.sessionStorageProvider = sessionStorageProvider;
  }

  @Override
  public AuthInterceptor get() {
    return newInstance(sessionStorageProvider.get());
  }

  public static AuthInterceptor_Factory create(Provider<SessionStorage> sessionStorageProvider) {
    return new AuthInterceptor_Factory(sessionStorageProvider);
  }

  public static AuthInterceptor newInstance(SessionStorage sessionStorage) {
    return new AuthInterceptor(sessionStorage);
  }
}
