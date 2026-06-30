package com.fixit.feature.customer.review.di;

import com.fixit.feature.customer.review.data.remote.api.ReviewApi;
import com.fixit.feature.customer.review.data.repository.ReviewRepositoryImpl;
import com.fixit.feature.customer.review.domain.repository.ReviewRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;

@Module
@InstallIn(SingletonComponent.class)
public abstract class ReviewModule {

    @Binds
    @Singleton
    public abstract ReviewRepository bindReviewRepository(ReviewRepositoryImpl repository);

    @Provides
    @Singleton
    public static ReviewApi provideReviewApi(Retrofit retrofit) {
        return retrofit.create(ReviewApi.class);
    }
}
