package com.fixit.feature.worker.wallet.di;

import com.fixit.feature.worker.wallet.data.repository.WorkerWalletRepositoryImpl;
import com.fixit.feature.worker.wallet.domain.repository.WorkerWalletRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class WorkerWalletModule {
    @Binds
    @Singleton
    public abstract WorkerWalletRepository bindWorkerWalletRepository(WorkerWalletRepositoryImpl impl);
}
