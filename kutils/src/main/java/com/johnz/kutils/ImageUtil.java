package com.johnz.kutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtil {

    private static final String TAG = "kutils";

    private File mRoot;

    private static ImageUtil sImageUtil;

    private ImageUtil() {
        if (FileSystem.isExternalStorageWriteable()) { // Ues external storage first
            try {
                mRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                if (!mRoot.mkdirs()) {
                    Log.d(TAG, "ImageUtil: external picture storage is exist");
                }
            } catch (Exception e) { // In case of folder missing, should not be call
                mRoot = Environment.getExternalStorageDirectory();
                e.printStackTrace();
            } finally {
                Log.d(TAG, "ImageUtil: external storage is " + mRoot.getAbsolutePath());
            }
        } else {
            Log.e(TAG, "ImageUtil: external storage is not writeable");
        }
    }

    public static ImageUtil getUtil() {
        if (sImageUtil == null) {
            sImageUtil = new ImageUtil();
        }
        return sImageUtil;
    }

    public File saveGalleryBitmap(Context context, Bitmap bitmap, String fileName) {
        File file = saveBitmap(bitmap, fileName);
        String[] paths = {file.getAbsolutePath()};
        String[] mimeTypes = null;
        MediaScannerConnection.scanFile(context, paths, mimeTypes, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                Log.d(TAG, "Scanned " + path + ":" + "-> uri= " + uri);
            }
        });
        return file;
    }

    private File saveBitmap(Bitmap bitmap, String fileName) {
        File file = createFile(mRoot, fileName);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    private File createFile(File root, String fileName) {
        int lastIndexOfSeparator = fileName.lastIndexOf(File.separator);
        if (lastIndexOfSeparator != -1) {
            String subDir = fileName.substring(0, lastIndexOfSeparator);
            String newFileName = fileName.substring(lastIndexOfSeparator + 1, fileName.length());
            File fullDir = new File(root, subDir);
            if (!fullDir.mkdirs()) {
                Log.d(TAG, "createFile: directory create failure or directory had created");
            }

            if (fullDir.exists()) {
                return new File(fullDir, newFileName);
            }
            return new File(root, newFileName);

        } else {
            return new File(root, fileName);
        }
    }
}
