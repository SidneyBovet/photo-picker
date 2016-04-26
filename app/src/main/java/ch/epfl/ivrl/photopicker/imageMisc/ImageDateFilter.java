package ch.epfl.ivrl.photopicker.imageMisc;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ch.epfl.ivrl.photopicker.imageData.Photograph;
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

    public static List<Photograph> getFilesWithinDates(Context context, List<String> files, Calendar start, Calendar end)
    {
        if (files == null) {
            Log.e("File list", "Cannot process null file list");
            return null;
        }

        if (start == null || end == null) {
            Log.e("File list", "Cannot process files with null start or end time");
            return null;
        }


        // get target H and W for bitmap resizing
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int targetWidth = size.x;
        int targetHeight = size.y;

        // construct the array of Photographs
        ArrayList<Photograph> result = new ArrayList<>(files.size());
        for (String path: files) {
            File f = new File(path);
            long lastModified = f.lastModified();
            if(lastModified >= start.getTimeInMillis() && lastModified <= end.getTimeInMillis()) {
                Photograph newPhoto = new Photograph(f, targetWidth, targetHeight);
                result.add(newPhoto);
            }
        }

        return result;
    }
}
