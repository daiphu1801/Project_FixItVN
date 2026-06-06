package com.fixit.feature.customer.profile.di;

import com.fixit.feature.customer.profile.data.remote.api.CustomerProfileApi;
import com.fixit.feature.customer.profile.data.repository.CustomerProfileRepositoryImpl;
import com.fixit.feature.customer.profile.domain.repository.CustomerProfileRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;

// CÚ PHÁP: @Module và @InstallIn
// Ý NGHĨA: Báo cho Hilt (Quản gia của Android) biết đây là cuốn "Sổ tay hướng dẫn" để tạo đồ nghề.
// SingletonComponent có nghĩa là đồ nghề tạo ra sẽ tồn tại mãi mãi trong suốt vòng đời của App.
@Module
@InstallIn(SingletonComponent.class)
public class ProfileModule {

    // ----------------------------------------------------
    // HƯỚNG DẪN 1: TẠO ỐNG NGHE ĐIỆN THOẠI (CustomerProfileApi)
    // ----------------------------------------------------
    @Provides
    @Singleton
    public CustomerProfileApi provideCustomerProfileApi(Retrofit retrofit) {
        // Hilt hỏi: Lấy ống nghe ở đâu?
        // Ta trả lời: Lôi cái máy Retrofit tổng ra, nhét cái Interface Api vào để nó đúc thành ống nghe thật!
        return retrofit.create(CustomerProfileApi.class);
    }

    // ----------------------------------------------------
    // HƯỚNG DẪN 2: TẠO BỘ NÃO (CustomerProfileRepository)
    // ----------------------------------------------------
    @Provides
    @Singleton
    public CustomerProfileRepository provideCustomerProfileRepository(CustomerProfileApi api) {
        // Hilt hỏi: Cái Tivi và UseCase cứ đòi mượn Bộ não, tôi lấy ở đâu ra đưa cho nó?
        // Ta trả lời: Anh hãy khởi tạo thằng Làm Thuê (RepositoryImpl) rồi nhét cái ống nghe vào tay nó. 
        // Sau đó giao nó cho cái Tivi xài!
        return new CustomerProfileRepositoryImpl(api);
    }
}
