package com.fixit.feature.worker.availability.di;

import com.fixit.feature.worker.availability.data.repository.WorkerAvailabilityRepositoryImpl;
import com.fixit.feature.worker.availability.domain.repository.WorkerAvailabilityRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class WorkerAvailabilityModule {
    @Binds
    @Singleton
    public abstract WorkerAvailabilityRepository bindWorkerAvailabilityRepository(
            WorkerAvailabilityRepositoryImpl impl);
}
