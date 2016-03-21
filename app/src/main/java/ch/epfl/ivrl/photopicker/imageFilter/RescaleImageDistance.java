package ch.epfl.ivrl.photopicker.imageFilter;

import java.io.File;

import boofcv.struct.image.ImageUInt8;

/**
 * Created by LogitechVR on 21.03.2016.
 */
public class RescaleImageDistance implements ImageDistanceMetric {

    public RescaleImageDistance () {}

    @Override
    public float computeDistance(File imageOne, File imageTwo) {

        ImageUInt8 img = null;

        return 0;
    }
}
