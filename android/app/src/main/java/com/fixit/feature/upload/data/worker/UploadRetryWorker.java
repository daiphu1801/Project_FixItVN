package com.fixit.feature.upload.data.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.fixit.core.common.Result;
import com.fixit.feature.upload.data.repository.UploadWorkflowProcessor;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.android.EntryPointAccessors;
import dagger.hilt.components.SingletonComponent;

public class UploadRetryWorker extends Worker {

    public UploadRetryWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            UploadWorkerEntryPoint entryPoint = EntryPointAccessors.fromApplication(
                    getApplicationContext(),
                    UploadWorkerEntryPoint.class);
            entryPoint.uploadWorkflowProcessor().processAll();
            return Result.success();
        } catch (Exception ex) {
            return Result.retry();
        }
    }

    @EntryPoint
    @InstallIn(SingletonComponent.class)
    public interface UploadWorkerEntryPoint {
        UploadWorkflowProcessor uploadWorkflowProcessor();
    }
}
