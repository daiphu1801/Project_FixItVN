package com.fixit.feature.worker.kyc.di;

import com.fixit.feature.worker.kyc.data.remote.api.WorkerKycApi;
import com.fixit.feature.worker.kyc.data.repository.WorkerKycRepositoryImpl;
import com.fixit.feature.worker.kyc.domain.repository.WorkerKycRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;

@Module
@InstallIn(SingletonComponent.class)
public class WorkerKycModule {

    @Provides
    @Singleton
    public WorkerKycApi provideWorkerKycApi(Retrofit retrofit) {
        return retrofit.create(WorkerKycApi.class);
    }

    @Provides
    @Singleton
    public WorkerKycRepository provideWorkerKycRepository(
            WorkerKycRepositoryImpl repository
    ) {
        return repository;
    }
}
