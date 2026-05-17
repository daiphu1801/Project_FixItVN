package com.fixit.feature.worker.orders.data.repository;

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
public final class WorkerOrdersRepositoryImpl_Factory implements Factory<WorkerOrdersRepositoryImpl> {
  @Override
  public WorkerOrdersRepositoryImpl get() {
    return newInstance();
  }

  public static WorkerOrdersRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static WorkerOrdersRepositoryImpl newInstance() {
    return new WorkerOrdersRepositoryImpl();
  }

  private static final class InstanceHolder {
    private static final WorkerOrdersRepositoryImpl_Factory INSTANCE = new WorkerOrdersRepositoryImpl_Factory();
  }
}
