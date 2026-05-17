package com.fixit.core.di;

import android.content.Context;
import android.content.SharedPreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AppModule_ProvideSharedPreferencesFactory implements Factory<SharedPreferences> {
  private final AppModule module;

  private final Provider<Context> contextProvider;

  public AppModule_ProvideSharedPreferencesFactory(AppModule module,
      Provider<Context> contextProvider) {
    this.module = module;
    this.contextProvider = contextProvider;
  }

  @Override
  public SharedPreferences get() {
    return provideSharedPreferences(module, contextProvider.get());
  }

  public static AppModule_ProvideSharedPreferencesFactory create(AppModule module,
      Provider<Context> contextProvider) {
    return new AppModule_ProvideSharedPreferencesFactory(module, contextProvider);
  }

  public static SharedPreferences provideSharedPreferences(AppModule instance, Context context) {
    return Preconditions.checkNotNullFromProvides(instance.provideSharedPreferences(context));
  }
}
