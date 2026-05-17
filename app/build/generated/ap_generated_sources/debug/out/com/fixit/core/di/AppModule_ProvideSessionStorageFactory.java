package com.fixit.core.di;

import com.fixit.core.storage.SessionStorage;
import com.fixit.core.storage.SharedPrefsSessionStorage;
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
public final class AppModule_ProvideSessionStorageFactory implements Factory<SessionStorage> {
  private final AppModule module;

  private final Provider<SharedPrefsSessionStorage> sessionStorageProvider;

  public AppModule_ProvideSessionStorageFactory(AppModule module,
      Provider<SharedPrefsSessionStorage> sessionStorageProvider) {
    this.module = module;
    this.sessionStorageProvider = sessionStorageProvider;
  }

  @Override
  public SessionStorage get() {
    return provideSessionStorage(module, sessionStorageProvider.get());
  }

  public static AppModule_ProvideSessionStorageFactory create(AppModule module,
      Provider<SharedPrefsSessionStorage> sessionStorageProvider) {
    return new AppModule_ProvideSessionStorageFactory(module, sessionStorageProvider);
  }

  public static SessionStorage provideSessionStorage(AppModule instance,
      SharedPrefsSessionStorage sessionStorage) {
    return Preconditions.checkNotNullFromProvides(instance.provideSessionStorage(sessionStorage));
  }
}
