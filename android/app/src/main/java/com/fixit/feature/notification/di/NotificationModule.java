package com.fixit.feature.notification.di;

import com.fixit.feature.notification.data.remote.api.NotificationApi;
import com.fixit.feature.notification.data.repository.NotificationRepositoryImpl;
import com.fixit.feature.notification.domain.repository.NotificationRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;

@Module
@InstallIn(SingletonComponent.class)
public class NotificationModule {

    @Provides
    @Singleton
    public NotificationApi provideNotificationApi(Retrofit retrofit) {
        return retrofit.create(NotificationApi.class);
    }

    @Provides
    @Singleton
    public NotificationRepository provideNotificationRepository(NotificationRepositoryImpl repository) {
        return repository;
    }
}
