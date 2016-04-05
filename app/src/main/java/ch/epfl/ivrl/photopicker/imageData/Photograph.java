package ch.epfl.ivrl.photopicker.imageData;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

import ch.epfl.ivrl.photopicker.imageMisc.ImageUtils;

/**
 * Created by Sidney on 30.03.2016.
 */
public class Photograph {

    private File mFile;
    private int mScalingFactor = -1;

    public Photograph (String absolutePath, int targetWidth, int targetHeight) {
        this(absolutePath, "no name", targetWidth, targetHeight);
    }

    public Photograph (String absolutePath, String name, int targetWidth, int targetHeight) {
        mFile = new File(absolutePath);
        mScalingFactor = ImageUtils.getOptionsFromPath(absolutePath, targetWidth, targetHeight).inSampleSize;
    }

    public String getPath() {
        return mFile.getAbsolutePath();
    }

    public int getScalingFactor() {
        return mScalingFactor;
    }

    public Bitmap getScaledBitmap() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = mScalingFactor;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(mFile.getAbsolutePath(), options);
    }

    public Bitmap getScaledBitmap(int specificScaling) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = specificScaling;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(mFile.getAbsolutePath(), options);
    }

    public String getName() {
        return mFile.getName();
    }

    public boolean equals(Photograph other) {
        if(!other.getPath().equals(this.getPath()))
            return false;

        if(other.getScalingFactor() != mScalingFactor)
            return false;

        return true;
    }

    public String toString() {
        return "Photo: '" + mFile.getName() + "' : s=" + mScalingFactor;
    }
}
