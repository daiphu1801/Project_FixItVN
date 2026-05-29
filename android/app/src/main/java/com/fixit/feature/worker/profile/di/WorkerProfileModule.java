package com.fixit.feature.worker.profile.di;

import com.fixit.feature.worker.profile.data.remote.api.WorkerProfileApi;
import com.fixit.feature.worker.profile.data.repository.WorkerProfileRepositoryImpl;
import com.fixit.feature.worker.profile.domain.repository.WorkerProfileRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;

@Module
@InstallIn(SingletonComponent.class)
public class WorkerProfileModule {

    @Provides
    @Singleton
    public WorkerProfileApi provideWorkerProfileApi(Retrofit retrofit) {
        return retrofit.create(WorkerProfileApi.class);
    }

    @Provides
    @Singleton
    public WorkerProfileRepository provideWorkerProfileRepository(
            WorkerProfileRepositoryImpl repository
    ) {
        return repository;
    }
}