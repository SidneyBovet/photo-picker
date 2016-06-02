package ch.epfl.ivrl.photopicker.imageGrouping;

import android.app.ProgressDialog;

import org.ejml.data.DenseMatrix32F;

import java.util.List;

import ch.epfl.ivrl.photopicker.imageData.Photograph;

/**
 * Created by Sidney on 05.04.2016.
 *
 * Utilities for computing distance-related objects
 */
public class ImageGroupingUtils {
    public static DenseMatrix32F getDistanceMatrix(
            List<Photograph> photos,
            ImageDistanceMetric distanceMetric) {
        DenseMatrix32F pairwiseDistances = new DenseMatrix32F(photos.size(),photos.size());

        for (int i = 0; i < photos.size(); ++i) {
            for (int j = i+1; j < photos.size(); ++j) {
                float distance = distanceMetric.computeDistance(photos.get(i),photos.get(j));
                pairwiseDistances.set(i, j, distance);
                pairwiseDistances.set(j, i, distance);
            }
        }

        return pairwiseDistances;
    }
}
