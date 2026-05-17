package com.fixit.feature.worker.home.data.repository;

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
public final class WorkerHomeRepositoryImpl_Factory implements Factory<WorkerHomeRepositoryImpl> {
  @Override
  public WorkerHomeRepositoryImpl get() {
    return newInstance();
  }

  public static WorkerHomeRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static WorkerHomeRepositoryImpl newInstance() {
    return new WorkerHomeRepositoryImpl();
  }

  private static final class InstanceHolder {
    private static final WorkerHomeRepositoryImpl_Factory INSTANCE = new WorkerHomeRepositoryImpl_Factory();
  }
}
