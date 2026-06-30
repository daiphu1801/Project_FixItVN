package com.fixit.feature.customer.workerprofile.di;

import com.fixit.feature.customer.workerprofile.data.remote.api.PublicWorkerApi;
import com.fixit.feature.customer.workerprofile.data.repository.PublicWorkerRepositoryImpl;
import com.fixit.feature.customer.workerprofile.domain.repository.PublicWorkerRepository;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;

import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public class WorkerProfileModule {

    @Provides
    @Singleton
    public PublicWorkerApi providePublicWorkerApi(Retrofit retrofit) {
        return retrofit.create(PublicWorkerApi.class);
    }

    @Provides
    @Singleton
    public PublicWorkerRepository providePublicWorkerRepository(PublicWorkerApi api) {
        return new PublicWorkerRepositoryImpl(api);
    }
}
