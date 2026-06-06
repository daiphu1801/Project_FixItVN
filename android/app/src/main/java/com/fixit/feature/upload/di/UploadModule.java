package com.fixit.feature.upload.di;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.fixit.feature.upload.data.remote.api.CloudinaryUploadApi;
import com.fixit.feature.upload.data.remote.api.UploadApi;
import com.fixit.feature.upload.data.repository.UploadRepositoryImpl;
import com.fixit.feature.upload.domain.repository.UploadRepository;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class UploadModule {

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `pending_uploads` ("
                            + "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                            + "`localFilePath` TEXT, "
                            + "`originalFileName` TEXT, "
                            + "`contentType` TEXT, "
                            + "`fileSize` INTEGER NOT NULL, "
                            + "`purpose` TEXT, "
                            + "`uploadId` TEXT, "
                            + "`objectKey` TEXT, "
                            + "`uploadUrl` TEXT, "
                            + "`fileUrl` TEXT, "
                            + "`formDataJson` TEXT, "
                            + "`presignedExpiresAt` INTEGER NOT NULL, "
                            + "`status` TEXT, "
                            + "`targetType` TEXT, "
                            + "`targetEntityId` TEXT, "
                            + "`groupId` TEXT, "
                            + "`slotKey` TEXT, "
                            + "`extraPayloadJson` TEXT, "
                            + "`retryCount` INTEGER NOT NULL, "
                            + "`lastError` TEXT, "
                            + "`createdAt` INTEGER NOT NULL, "
                            + "`updatedAt` INTEGER NOT NULL, "
                            + "`lastAttemptAt` INTEGER NOT NULL)"
            );
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_pending_uploads_status` ON `pending_uploads` (`status`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_pending_uploads_purpose` ON `pending_uploads` (`purpose`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_pending_uploads_targetType` ON `pending_uploads` (`targetType`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_pending_uploads_groupId` ON `pending_uploads` (`groupId`)");
            database.execSQL("DROP TABLE IF EXISTS `pending_upload_confirms`");
        }
    };

    @Provides
    @Singleton
    public com.fixit.feature.upload.data.local.db.UploadDatabase provideUploadDatabase(
            @dagger.hilt.android.qualifiers.ApplicationContext android.content.Context context
    ) {
        return Room.databaseBuilder(
                context,
                com.fixit.feature.upload.data.local.db.UploadDatabase.class,
                "upload_db"
        ).addMigrations(MIGRATION_1_2).build();
    }

    @Provides
    @Singleton
    public com.fixit.feature.upload.data.local.dao.PendingUploadDao providePendingUploadDao(
            com.fixit.feature.upload.data.local.db.UploadDatabase database
    ) {
        return database.pendingUploadDao();
    }

    @Provides
    @Singleton
    public Gson provideGson() {
        return new Gson();
    }

    @Provides
    @Singleton
    public UploadApi provideUploadApi(Retrofit retrofit) {
        return retrofit.create(UploadApi.class);
    }

    @Provides
    @Singleton
    public CloudinaryUploadApi provideCloudinaryUploadApi() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.cloudinary.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(CloudinaryUploadApi.class);
    }

    @Provides
    @Singleton
    public UploadRepository provideUploadRepository(
            UploadRepositoryImpl repository
    ) {
        return repository;
    }
}
