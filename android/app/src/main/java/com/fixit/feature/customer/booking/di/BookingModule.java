package com.fixit.feature.customer.booking.di;

import com.fixit.feature.customer.booking.data.remote.api.CustomerBookingApi;
import com.fixit.feature.customer.booking.data.repository.CustomerBookingRepositoryImpl;
import com.fixit.feature.customer.booking.domain.repository.CustomerBookingRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;

@Module
@InstallIn(SingletonComponent.class)
public class BookingModule {

    @Provides
    @Singleton
    public CustomerBookingApi provideCustomerBookingApi(Retrofit retrofit) {
        return retrofit.create(CustomerBookingApi.class);
    }

    @Provides
    @Singleton
    public CustomerBookingRepository provideCustomerBookingRepository(CustomerBookingApi api) {
        return new CustomerBookingRepositoryImpl(api);
    }
}
