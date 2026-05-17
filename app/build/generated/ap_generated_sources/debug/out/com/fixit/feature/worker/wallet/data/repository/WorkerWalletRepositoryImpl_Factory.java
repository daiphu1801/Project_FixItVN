package com.fixit.feature.worker.wallet.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class WorkerWalletRepositoryImpl_Factory implements Factory<WorkerWalletRepositoryImpl> {
  @Override
  public WorkerWalletRepositoryImpl get() {
    return newInstance();
  }

  public static WorkerWalletRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static WorkerWalletRepositoryImpl newInstance() {
    return new WorkerWalletRepositoryImpl();
  }

  private static final class InstanceHolder {
    private static final WorkerWalletRepositoryImpl_Factory INSTANCE = new WorkerWalletRepositoryImpl_Factory();
  }
}
