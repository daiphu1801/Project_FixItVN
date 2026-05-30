package com.fixit.core.upload.di;

import com.fixit.core.upload.data.remote.api.UploadApi;
import com.fixit.core.upload.data.repository.UploadRepositoryImpl;
import com.fixit.core.upload.domain.repository.UploadRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;

@Module
@InstallIn(SingletonComponent.class)
public class UploadModule {
    @Provides
    @Singleton
    public UploadApi provideUploadApi(Retrofit retrofit) {
        return retrofit.create(UploadApi.class);
    }

    @Provides
    @Singleton
    public UploadRepository provideUploadRepository(UploadRepositoryImpl repository) {
        return repository;
    }
}
