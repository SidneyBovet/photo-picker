package ch.epfl.ivrl.photopicker.imageFilter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ch.epfl.ivrl.photopicker.permissionManagement.PermissionGranter;

/**
 * Created by Sidney on 07.03.2016.
 */
public class ImageDateFilter {
    public static final String CAMERA_IMAGE_BUCKET_NAME =
            Environment.getExternalStorageDirectory().toString()
                    + "/DCIM/Camera";
    public static final String CAMERA_IMAGE_BUCKET_ID =
            getBucketId(CAMERA_IMAGE_BUCKET_NAME);

    /**
     * Matches code in MediaProvider.computeBucketValues. Should be a common
     * function.
     */
    public static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }

    public static ArrayList<String> getCameraImages(Activity thisActivity) {
        ArrayList<String> result = null;

        if (PermissionGranter.checkForPermission((thisActivity))) {
            final String[] projection = {MediaStore.Images.Media.DATA};
            final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
            final String[] selectionArgs = {CAMERA_IMAGE_BUCKET_ID};


            final Cursor cursor = thisActivity.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null);
            result = new ArrayList<String>(cursor.getCount());
            if (cursor.moveToFirst()) {
                final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                do {
                    final String data = cursor.getString(dataColumn);
                    result.add(data);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return result;
    }
}
