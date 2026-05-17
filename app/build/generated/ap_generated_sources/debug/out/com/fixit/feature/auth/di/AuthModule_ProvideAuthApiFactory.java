package com.fixit.feature.auth.di;

import com.fixit.feature.auth.data.remote.api.AuthApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

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
public final class AuthModule_ProvideAuthApiFactory implements Factory<AuthApi> {
  private final AuthModule module;

  private final Provider<Retrofit> retrofitProvider;

  public AuthModule_ProvideAuthApiFactory(AuthModule module, Provider<Retrofit> retrofitProvider) {
    this.module = module;
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public AuthApi get() {
    return provideAuthApi(module, retrofitProvider.get());
  }

  public static AuthModule_ProvideAuthApiFactory create(AuthModule module,
      Provider<Retrofit> retrofitProvider) {
    return new AuthModule_ProvideAuthApiFactory(module, retrofitProvider);
  }

  public static AuthApi provideAuthApi(AuthModule instance, Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(instance.provideAuthApi(retrofit));
  }
}
