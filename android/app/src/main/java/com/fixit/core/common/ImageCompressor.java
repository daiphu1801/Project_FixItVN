package com.fixit.core.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageCompressor {

    public static File compressImage(Context context, File originalFile, String outputName) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeFile(originalFile.getAbsolutePath());
        if (bitmap == null) {
            throw new IOException("Không thể đọc được ảnh gốc: " + originalFile.getAbsolutePath());
        }

        File cacheDir = context.getCacheDir();
        File compressedFile = new File(cacheDir, outputName);

        try (FileOutputStream fos = new FileOutputStream(compressedFile)) {
            // Nén ảnh về định dạng JPEG, chất lượng 80%
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
        } finally {
            bitmap.recycle();
        }

        return compressedFile;
    }
}
