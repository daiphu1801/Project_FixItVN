// android/app/src/main/java/com/fixit/FixitApp.java
package com.fixit;

import android.app.Application;

import com.fixit.feature.upload.data.worker.UploadWorkManagerScheduler;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class FixitApp extends Application {
    @Inject
    UploadWorkManagerScheduler uploadWorkManagerScheduler;

    @Override
    public void onCreate() {
        super.onCreate();
        uploadWorkManagerScheduler.schedule();
    }
}
