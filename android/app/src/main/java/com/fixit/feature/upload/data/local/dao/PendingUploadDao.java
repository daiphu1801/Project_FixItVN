package com.fixit.feature.upload.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.fixit.feature.upload.data.local.entity.PendingUploadEntity;

import java.util.List;

@Dao
public interface PendingUploadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(PendingUploadEntity upload);

    @Update
    void update(PendingUploadEntity upload);

    @Delete
    void delete(PendingUploadEntity upload);

    @Query("DELETE FROM pending_uploads WHERE id = :id")
    void deleteById(long id);

    @Query("DELETE FROM pending_uploads WHERE uploadId = :uploadId")
    void deleteByUploadId(String uploadId);

    @Query("SELECT * FROM pending_uploads WHERE id = :id LIMIT 1")
    PendingUploadEntity findById(long id);

    @Query("SELECT * FROM pending_uploads WHERE uploadId = :uploadId LIMIT 1")
    PendingUploadEntity findByUploadId(String uploadId);

    @Query("SELECT * FROM pending_uploads WHERE status != 'CONSUMED' AND status != 'LOCAL_DRAFT' ORDER BY createdAt ASC")
    List<PendingUploadEntity> getRunnableUploads();

    @Query("SELECT * FROM pending_uploads WHERE status != 'CONSUMED' AND status != 'LOCAL_DRAFT' ORDER BY createdAt ASC LIMIT 1")
    PendingUploadEntity getNextRunnableUpload();

    @Query("SELECT * FROM pending_uploads WHERE groupId = :groupId ORDER BY createdAt ASC")
    List<PendingUploadEntity> getByGroupId(String groupId);

    /**
     * Kiểm tra nhanh xem có upload nào đang pending/chờ cho targetType này không.
     * Dùng để quyết định redirect sang màn Pending thay vì màn Upload.
     */
    @Query("SELECT COUNT(*) FROM pending_uploads WHERE targetType = :targetType AND status != 'CONSUMED'")
    int countActiveByTargetType(String targetType);

    @Query("SELECT COUNT(*) FROM pending_uploads WHERE targetType = :targetType AND status != 'CONSUMED' AND status != 'LOCAL_DRAFT'")
    int countSubmittedActiveByTargetType(String targetType);

    @Query("UPDATE pending_uploads SET status = 'LOCAL_SELECTED' WHERE groupId = :groupId AND status = 'LOCAL_DRAFT'")
    void submitGroup(String groupId);

    @Query("DELETE FROM pending_uploads WHERE targetType = :targetType AND (status = 'LOCAL_DRAFT' OR createdAt < :timeLimit)")
    void deleteStaleUploads(String targetType, long timeLimit);
}
