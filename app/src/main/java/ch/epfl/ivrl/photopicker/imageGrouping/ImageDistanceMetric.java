package ch.epfl.ivrl.photopicker.imageGrouping;

import ch.epfl.ivrl.photopicker.imageData.Photograph;

/**
 * Created by Sidney on 21.03.2016.
 *
 * Classes implementing this interface can then be used by an ImageClusteringTask.
 *
 * @see ImageClusteringTask
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
