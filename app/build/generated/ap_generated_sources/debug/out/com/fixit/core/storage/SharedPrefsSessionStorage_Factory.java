package com.fixit.core.storage;

import android.content.SharedPreferences;
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
public final class SharedPrefsSessionStorage_Factory implements Factory<SharedPrefsSessionStorage> {
  private final Provider<SharedPreferences> prefsProvider;

  public SharedPrefsSessionStorage_Factory(Provider<SharedPreferences> prefsProvider) {
    this.prefsProvider = prefsProvider;
  }

  @Override
  public SharedPrefsSessionStorage get() {
    return newInstance(prefsProvider.get());
  }

  public static SharedPrefsSessionStorage_Factory create(
      Provider<SharedPreferences> prefsProvider) {
    return new SharedPrefsSessionStorage_Factory(prefsProvider);
  }

  public static SharedPrefsSessionStorage newInstance(SharedPreferences prefs) {
    return new SharedPrefsSessionStorage(prefs);
  }
}
