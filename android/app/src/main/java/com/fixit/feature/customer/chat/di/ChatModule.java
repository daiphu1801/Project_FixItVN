package com.fixit.feature.customer.chat.di;

import com.fixit.feature.customer.chat.data.repository.ChatRepositoryImpl;
import com.fixit.feature.customer.chat.domain.repository.ChatRepository;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public abstract class ChatModule {

    @Binds
    @Singleton
    public abstract ChatRepository bindChatRepository(ChatRepositoryImpl chatRepositoryImpl);
}
