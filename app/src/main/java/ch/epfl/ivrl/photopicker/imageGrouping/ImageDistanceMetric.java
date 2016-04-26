package ch.epfl.ivrl.photopicker.imageGrouping;

import ch.epfl.ivrl.photopicker.imageData.Photograph;

/**
 * Created by LogitechVR on 21.03.2016.
 */
public interface ImageDistanceMetric {
    /**
     *
     * @param imageOne The first image to be compared
     * @param imageTwo The second image to be compared
     * @return The distance between image one and image two according to a specific algorithm.
     */
    float computeDistance(Photograph imageOne, Photograph imageTwo);
}
