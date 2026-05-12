package com.fixit.feature.worker.orders.di;

import com.fixit.feature.worker.orders.data.repository.WorkerOrdersRepositoryImpl;
import com.fixit.feature.worker.orders.domain.repository.WorkerOrdersRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class WorkerOrdersModule {
    @Binds
    @Singleton
    public abstract WorkerOrdersRepository bindWorkerOrdersRepository(WorkerOrdersRepositoryImpl impl);
}
