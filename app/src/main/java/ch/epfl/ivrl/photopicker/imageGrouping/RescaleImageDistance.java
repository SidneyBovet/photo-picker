package ch.epfl.ivrl.photopicker.imageGrouping;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import boofcv.android.ConvertBitmap;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;

import boofcv.struct.image.MultiSpectral;
import ch.epfl.ivrl.photopicker.imageData.Photograph;
import ch.epfl.ivrl.photopicker.imageMisc.ImageUtils;

/**
 * Created by LogitechVR on 21.03.2016.
 */
public class RescaleImageDistance implements ImageDistanceMetric {

    private int mResolution = 0;

    public RescaleImageDistance (int resolution) {
        mResolution = resolution;
    }

    /**
     * @param imageOne The first image to be compared
     * @param imageTwo The second image to be compared
     * @return The distance between image one and image two according to a specific algorithm.
     */
    @Override
    public float computeDistance(@NonNull Photograph imageOne, @NonNull Photograph imageTwo) {

        byte[] buffer = null;

        MultiSpectral<ImageUInt8> img1 = getResizedBoofFromPath(imageOne.getPath(),
                mResolution, mResolution, buffer);

        MultiSpectral<ImageUInt8> img2 = getResizedBoofFromPath(imageTwo.getPath(),
                mResolution, mResolution, buffer);

        Log.d("Image Distance", img1.toString());
        Log.d("Image Distance", img2.toString());

        return 0;
    }

    private MultiSpectral<ImageUInt8> getResizedBoofFromPath(String path, int width, int height, byte[] buffer) {

        MultiSpectral<ImageUInt8> img;

        // import with approx. dimension
        Bitmap bm = ImageUtils.decodeSampledBitmapFromFile(path, width, height);
        // resize to exactly the required resolution
        bm = Bitmap.createScaledBitmap(bm, width, height, true);
        // reallocate buffer if size is too small
        buffer = ConvertBitmap.declareStorage(bm, buffer);
        // convert to boof
        img = ConvertBitmap.bitmapToMS(bm, null, ImageUInt8.class, buffer);

        return img;
    }
}
