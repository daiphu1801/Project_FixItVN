package com.fixit.feature.customer.service.di;

import com.fixit.feature.customer.service.data.remote.api.ServiceApi;
import com.fixit.feature.customer.service.data.repository.ServiceRepositoryImpl;
import com.fixit.feature.customer.service.domain.repository.ServiceRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;

@Module
@InstallIn(SingletonComponent.class)
public abstract class ServiceModule {

    @Binds
    @Singleton
    public abstract ServiceRepository bindServiceRepository(
            ServiceRepositoryImpl serviceRepositoryImpl
    );

    @Module
    @InstallIn(SingletonComponent.class)
    public static class ProvidesModule {
        @Provides
        @Singleton
        public static ServiceApi provideServiceApi(Retrofit retrofit) {
            return retrofit.create(ServiceApi.class);
        }
    }
}
