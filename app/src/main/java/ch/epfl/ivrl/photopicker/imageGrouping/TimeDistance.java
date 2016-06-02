package ch.epfl.ivrl.photopicker.imageGrouping;

import ch.epfl.ivrl.photopicker.imageData.Photograph;

/**
 * Created by Sidney on 26.04.2016.
 *
 * Basic distance metric using the time the photo was taken on as sole criteria.
 */
public class TimeDistance implements ImageDistanceMetric {

    public TimeDistance() {}

    @Override
    public float computeDistance(Photograph imageOne, Photograph imageTwo) {
        long i1 = imageOne.getTime();
        long i2 = imageTwo.getTime();

        return i2 - i1;
    }
}
