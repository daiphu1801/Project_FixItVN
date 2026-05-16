package com.fixit.feature.worker.job.di;

import com.fixit.feature.worker.job.data.repository.WorkerJobRepositoryImpl;
import com.fixit.feature.worker.job.domain.repository.WorkerJobRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class WorkerJobModule {
    @Binds
    @Singleton
    public abstract WorkerJobRepository bindWorkerJobRepository(WorkerJobRepositoryImpl impl);
}
