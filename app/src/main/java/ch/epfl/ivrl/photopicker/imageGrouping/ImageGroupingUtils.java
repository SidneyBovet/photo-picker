package ch.epfl.ivrl.photopicker.imageGrouping;

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
            ImageDistanceMetric distanceMetric,
            ImageClusteringTask callback) {
        DenseMatrix32F pairwiseDistances = new DenseMatrix32F(photos.size(),photos.size());

        for (int i = 0; i < photos.size(); ++i) {
            for (int j = i+1; j < photos.size(); ++j) {
                float distance = distanceMetric.computeDistance(photos.get(i),photos.get(j));
                pairwiseDistances.set(i, j, distance);
                pairwiseDistances.set(j, i, distance);

                // don't ask how or why, it just works (maybe)
                int progress = 100 * 2*j + (1-i)*(2*photos.size() - i) / (photos.size() * (photos.size()-1));

                callback.makeProgress(progress);
            }
        }

        return pairwiseDistances;
    }
}
