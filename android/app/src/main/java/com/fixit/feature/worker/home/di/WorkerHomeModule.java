package com.fixit.feature.worker.home.di;

import com.fixit.feature.worker.home.data.remote.api.WorkerHomeApi;
import com.fixit.feature.worker.home.data.repository.WorkerHomeRepositoryImpl;
import com.fixit.feature.worker.home.domain.repository.WorkerHomeRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;

@Module
@InstallIn(SingletonComponent.class)
public class WorkerHomeModule {

    @Provides
    @Singleton
    public WorkerHomeApi provideWorkerHomeApi(Retrofit retrofit) {
        return retrofit.create(WorkerHomeApi.class);
    }

    @Provides
    @Singleton
    public WorkerHomeRepository provideWorkerHomeRepository(
            WorkerHomeRepositoryImpl repository
    ) {
        return repository;
    }
}