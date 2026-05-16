package com.fixit.core.di;

import android.content.Context;
import android.content.SharedPreferences;

import com.fixit.core.common.Constants;
import com.fixit.core.storage.SessionStorage;
import com.fixit.core.storage.SharedPrefsSessionStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {
    @Provides
    @Singleton
    public SharedPreferences provideSharedPreferences(@ApplicationContext Context context) {
        return context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    public SessionStorage provideSessionStorage(SharedPrefsSessionStorage sessionStorage) {
        return sessionStorage;
    }
}
