package com.fixit.feature.worker.orders.di;

import com.fixit.feature.worker.orders.data.remote.api.WorkerOrdersApi;
import com.fixit.feature.worker.orders.data.repository.WorkerOrdersRepositoryImpl;
import com.fixit.feature.worker.orders.domain.repository.WorkerOrdersRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

import dagger.Provides;
import retrofit2.Retrofit;
import com.fixit.feature.worker.orders.data.remote.api.WorkerBookingApi;
import com.fixit.feature.worker.orders.data.repository.WorkerBookingRepositoryImpl;
import com.fixit.feature.worker.orders.domain.repository.WorkerBookingRepository;

@Module
@InstallIn(SingletonComponent.class)
public abstract class WorkerOrdersModule {
    @Binds
    @Singleton
    public abstract WorkerOrdersRepository bindWorkerOrdersRepository(WorkerOrdersRepositoryImpl impl);

    @Binds
    @Singleton
    public abstract WorkerBookingRepository bindWorkerBookingRepository(WorkerBookingRepositoryImpl impl);

    @Provides
    @Singleton
    public static WorkerBookingApi provideWorkerBookingApi(Retrofit retrofit) {
        return retrofit.create(WorkerBookingApi.class);
    }
}
