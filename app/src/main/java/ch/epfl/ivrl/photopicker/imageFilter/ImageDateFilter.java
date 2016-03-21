package ch.epfl.ivrl.photopicker.imageFilter;

import android.app.Activity;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

        Log.d("PERMISSION","" + PermissionGranter.hasPermission((thisActivity)));

        if (PermissionGranter.hasPermission((thisActivity))) {
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

    public static List<File> getFilesWithinDates(List<String> files, Calendar start, Calendar end)
    {
        if (files == null) {
            Log.e("File list", "Cannot process null file list");
            return null;
        }

        if (start == null || end == null) {
            Log.e("File list", "Cannot process files with null start or end time");
            return null;
        }

        ArrayList<File> result = new ArrayList<>(files.size());

        for (String path: files) {
            File f = new File(path);
            long lastModified = f.lastModified();
            if(lastModified >= start.getTimeInMillis() && lastModified <= end.getTimeInMillis()) {
                result.add(f);
            }
        }

        return result;
    }
}
