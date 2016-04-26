package ch.epfl.ivrl.photopicker.imageGrouping;

import android.util.Log;

import ch.epfl.ivrl.photopicker.imageData.Photograph;

/**
 * Created by LogitechVR on 26.04.2016.
 */
public class TimeDistance implements ImageDistanceMetric {
    @Override
    public float computeDistance(Photograph imageOne, Photograph imageTwo) {
        long i1 = imageOne.getFile().lastModified();
        long i2 = imageTwo.getFile().lastModified();

        Log.d("Time i1", "" + i1);
        Log.d("Time i2", "" + i2);

        return i2 - i1;
    }
}