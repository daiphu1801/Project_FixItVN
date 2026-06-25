package com.fixit.feature.worker.stats.di;

import com.fixit.feature.worker.stats.data.remote.api.WorkerStatsApi;
import com.fixit.feature.worker.stats.data.repository.WorkerStatsRepositoryImpl;
import com.fixit.feature.worker.stats.domain.repository.WorkerStatsRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;

@Module
@InstallIn(SingletonComponent.class)
public class WorkerStatsModule {

    @Provides
    @Singleton
    public WorkerStatsApi provideWorkerStatsApi(Retrofit retrofit) {
        return retrofit.create(WorkerStatsApi.class);
    }

    @Provides
    @Singleton
    public WorkerStatsRepository provideWorkerStatsRepository(
            WorkerStatsRepositoryImpl repository
    ) {
        return repository;
    }
}
