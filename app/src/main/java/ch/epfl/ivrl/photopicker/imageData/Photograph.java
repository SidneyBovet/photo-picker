package ch.epfl.ivrl.photopicker.imageData;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ch.epfl.ivrl.photopicker.imageMisc.ImageUtils;

/**
 * Created by Sidney on 30.03.2016.
 */
public class Photograph {

    private String mAbsolutePath;
    private int mScalingFactor = -1;

    public Photograph (String absolutePath, int targetWidth, int targetHeight) {
        mAbsolutePath = absolutePath;
        mScalingFactor = ImageUtils.getOptionsFromPath(absolutePath, targetWidth, targetHeight).inSampleSize;
    }

    public String getPath() {
        return mAbsolutePath;
    }

    public int getScalingFactor() {
        return mScalingFactor;
    }

    public Bitmap getScaledBitmap() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = mScalingFactor;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(mAbsolutePath, options);
    }

    public Bitmap getScaledBitmap(int specificScaling) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = specificScaling;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(mAbsolutePath, options);
    }
}
