package com.fixit.core.network;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import okhttp3.logging.HttpLoggingInterceptor;

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
public final class NetworkModule_ProvideLoggingInterceptorFactory implements Factory<HttpLoggingInterceptor> {
  private final NetworkModule module;

  public NetworkModule_ProvideLoggingInterceptorFactory(NetworkModule module) {
    this.module = module;
  }

  @Override
  public HttpLoggingInterceptor get() {
    return provideLoggingInterceptor(module);
  }

  public static NetworkModule_ProvideLoggingInterceptorFactory create(NetworkModule module) {
    return new NetworkModule_ProvideLoggingInterceptorFactory(module);
  }

  public static HttpLoggingInterceptor provideLoggingInterceptor(NetworkModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideLoggingInterceptor());
  }
}
