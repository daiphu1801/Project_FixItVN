package com.fixit.feature.worker.wallet.di;

import com.fixit.feature.worker.wallet.data.remote.api.WorkerBankAccountApi;
import com.fixit.feature.worker.wallet.data.repository.WorkerBankRepositoryImpl;
import com.fixit.feature.worker.wallet.data.repository.WorkerWalletRepositoryImpl;
import com.fixit.feature.worker.wallet.domain.repository.WorkerBankRepository;
import com.fixit.feature.worker.wallet.domain.repository.WorkerWalletRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;

@Module
@InstallIn(SingletonComponent.class)
public abstract class WorkerWalletModule {

    @Binds
    @Singleton
    public abstract WorkerWalletRepository bindWorkerWalletRepository(WorkerWalletRepositoryImpl impl);

    @Binds
    @Singleton
    public abstract WorkerBankRepository bindWorkerBankRepository(WorkerBankRepositoryImpl impl);

    @Provides
    @Singleton
    public static WorkerBankAccountApi provideWorkerBankAccountApi(Retrofit retrofit) {
        return retrofit.create(WorkerBankAccountApi.class);
    }
}