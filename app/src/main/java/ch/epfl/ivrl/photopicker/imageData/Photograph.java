package ch.epfl.ivrl.photopicker.imageData;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.Serializable;

import ch.epfl.ivrl.photopicker.imageMisc.ImageUtils;

/**
 * Created by Sidney on 30.03.2016.
 *
 * Encapsulates all information representing a picture
 */
public class Photograph implements Serializable {

    private File mFile;
    private int mTargetHeight = -1;
    private int mTargetWidth = -1;

    public Photograph (String absolutePath, int targetWidth, int targetHeight) {
        this(new File(absolutePath), targetWidth, targetHeight);
    }

    public Photograph (File file, int targetWidth, int targetHeight) {
        mFile = file;
        mTargetHeight = targetHeight;
        mTargetWidth = targetWidth;
    }

    public String getPath() {
        return mFile.getAbsolutePath();
    }

    public File getFile() {
        return mFile;
    }

    public String getName() {
        return mFile.getName();
    }

    public int getScalingFactor() {
        return ImageUtils.getOptionsFromPath(mFile.getAbsolutePath(), mTargetWidth, mTargetHeight).inSampleSize;
    }

    public int getTargetHeight() {
        return mTargetHeight;
    }

    public int getTargetWidth() {
        return mTargetWidth;
    }

    public void setTargetHeight(int newHeight) {
        mTargetHeight = newHeight;
    }

    public void setTargetWidth(int newWidth) {
        mTargetWidth = newWidth;
    }

    public Bitmap getScaledBitmap() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = ImageUtils.getOptionsFromPath(mFile.getAbsolutePath(), mTargetWidth, mTargetHeight).inSampleSize;;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(mFile.getAbsolutePath(), options);
    }

    public Bitmap getScaledBitmap(int specificScaling) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = specificScaling;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(mFile.getAbsolutePath(), options);
    }


    public boolean equals(Photograph other) {
        return other.getPath().equals(this.getPath());
    }

    public String toString() {
        return "Photo: '" + mFile.getName() + "'\n";
    }
}
