package com.fixit.feature.upload.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.fixit.feature.upload.domain.model.LocalUploadFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

//Helper chuyển Uri Android thành File trong cache để Retrofit upload.

public final class UploadFilePreparer {
    private static final String PENDING_UPLOAD_DIR = "pending_uploads";

    private UploadFilePreparer() {}

    public static LocalUploadFile fromUri(
            Context context,
            Uri uri,
            String purpose
    ) throws Exception {
        if (context == null || uri == null || purpose == null) {
            throw new IllegalArgumentException("Thiếu dữ liệu file upload");
        }

        String contentType = context.getContentResolver().getType(uri);
        if (contentType == null || contentType.isBlank()) {
            contentType = "image/jpeg";
        }
        contentType = normalizeContentType(contentType);

        String originalFileName = getDisplayName(context, uri);
        if (originalFileName == null || originalFileName.isBlank()) {
            originalFileName = "upload_" + UUID.randomUUID() + guessExtension(contentType);
        }

        File uploadDir = new File(context.getFilesDir(), PENDING_UPLOAD_DIR);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new IllegalStateException("KhÃ´ng táº¡o Ä‘Æ°á»£c thÆ° má»¥c upload táº¡m");
        }

        File outputFile = new File(
                uploadDir,
                "fixit_upload_" + UUID.randomUUID() + guessExtension(contentType)
        );

        try (
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                FileOutputStream outputStream = new FileOutputStream(outputFile)
        ) {
            if (inputStream == null) {
                throw new IllegalStateException("Không đọc được file");
            }

            byte[] buffer = new byte[8192];
            int read;

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
        }

        return new LocalUploadFile(
                outputFile,
                originalFileName,
                contentType,
                outputFile.length(),
                purpose
        );
    }

    public static void deleteLocalFile(String localFilePath) {
        if (localFilePath == null || localFilePath.isBlank()) {
            return;
        }

        File file = new File(localFilePath);
        if (file.exists() && file.isFile()) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
    }

    private static String getDisplayName(Context context, Uri uri) {
        try (Cursor cursor = context.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        )) {
            if (cursor == null || !cursor.moveToFirst()) {
                return null;
            }

            int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (index < 0) {
                return null;
            }

            return cursor.getString(index);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String guessExtension(String contentType) {
        if ("image/heic".equalsIgnoreCase(contentType)) {
            return ".heic";
        }

        if ("image/heif".equalsIgnoreCase(contentType)) {
            return ".heif";
        }

        if ("image/png".equalsIgnoreCase(contentType)) {
            return ".png";
        }

        if ("image/webp".equalsIgnoreCase(contentType)) {
            return ".webp";
        }

        return ".jpg";
    }

    private static String normalizeContentType(String contentType) {
        String normalized = contentType.trim().toLowerCase();
        if ("image/jpg".equals(normalized)) {
            return "image/jpeg";
        }
        return normalized;
    }
}
