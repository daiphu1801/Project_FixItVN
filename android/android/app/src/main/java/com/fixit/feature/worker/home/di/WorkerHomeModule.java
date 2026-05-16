package com.fixit.feature.worker.home.di;

import com.fixit.feature.worker.home.data.repository.WorkerHomeRepositoryImpl;
import com.fixit.feature.worker.home.domain.repository.WorkerHomeRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class WorkerHomeModule {
    @Binds
    @Singleton
    public abstract WorkerHomeRepository bindWorkerHomeRepository(WorkerHomeRepositoryImpl impl);
}
