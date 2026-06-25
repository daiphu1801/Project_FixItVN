package com.fixit.feature.customer.complaint.di;

import com.fixit.feature.customer.complaint.data.remote.api.ComplaintApi;
import com.fixit.feature.customer.complaint.data.repository.ComplaintRepositoryImpl;
import com.fixit.feature.customer.complaint.domain.repository.ComplaintRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;

@Module
@InstallIn(SingletonComponent.class)
public abstract class ComplaintModule {

    @Binds
    @Singleton
    public abstract ComplaintRepository bindComplaintRepository(ComplaintRepositoryImpl repository);

    @Provides
    @Singleton
    public static ComplaintApi provideComplaintApi(Retrofit retrofit) {
        return retrofit.create(ComplaintApi.class);
    }
}
