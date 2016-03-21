package ch.epfl.ivrl.photopicker.imageFilter;

import java.io.File;

/**
 * Created by LogitechVR on 21.03.2016.
 */
public interface ImageDistanceMetric {
    public float computeDistance(File imageOne, File imageTwo);
}
