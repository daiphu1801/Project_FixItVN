package com.fixit.feature.upload.data.local.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.fixit.feature.upload.data.local.dao.PendingUploadDao;
import com.fixit.feature.upload.data.local.entity.PendingUploadEntity;

/**
 * Cơ sở dữ liệu SQLite cục bộ được quản lý bởi Room
 * chuyên biệt cho các tác vụ upload và đồng bộ hóa tệp tin ngoại tuyến.
 */
@Database(entities = {PendingUploadEntity.class}, version = 2, exportSchema = false)
public abstract class UploadDatabase extends RoomDatabase {
    
    public abstract PendingUploadDao pendingUploadDao();
}
