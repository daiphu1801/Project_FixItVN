package com.fixit.feature.auth.di;

import com.fixit.feature.auth.data.remote.api.AuthApi;
import com.fixit.feature.auth.data.repository.AuthRepositoryImpl;
import com.fixit.feature.auth.domain.repository.AuthRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;

@Module
@InstallIn(SingletonComponent.class)
public class AuthModule {

    @Provides
    @Singleton
    public AuthApi provideAuthApi(Retrofit retrofit) {
        return retrofit.create(AuthApi.class);
    }

    @Provides
    @Singleton
    public AuthRepository provideAuthRepository(AuthRepositoryImpl repository) {
        return repository;
    }
}
