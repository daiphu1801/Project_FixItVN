package com.fixit.feature.customer.favorite.di;

import com.fixit.feature.customer.favorite.data.remote.FavoriteApi;
import com.fixit.feature.customer.favorite.data.repository.FavoriteRepositoryImpl;
import com.fixit.feature.customer.favorite.domain.repository.FavoriteRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;

@Module
@InstallIn(SingletonComponent.class)
public abstract class FavoriteModule {

    // Bind tầng interface Repository vào implementation
    @Binds
    @Singleton
    public abstract FavoriteRepository bindFavoriteRepository(FavoriteRepositoryImpl repository);

    // Cung cấp instance của FavoriteApi cho Retrofit
    @Provides
    @Singleton
    public static FavoriteApi provideFavoriteApi(Retrofit retrofit) {
        return retrofit.create(FavoriteApi.class);
    }
}
