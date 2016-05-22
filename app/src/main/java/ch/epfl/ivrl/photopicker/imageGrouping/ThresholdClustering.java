package ch.epfl.ivrl.photopicker.imageGrouping;

import android.util.Log;

import org.ejml.data.DenseMatrix32F;
import org.ejml.data.Matrix;

import java.util.List;

import ch.epfl.ivrl.photopicker.imageData.Photograph;
import ch.epfl.ivrl.photopicker.imageData.Vacation;

/**
 * Created by Sidney on 31.03.2016.
 */
public class ThresholdClustering implements ImageClusteringTechnique {

    /**
     * @param photos                 The list of photographs to cluster among different scenes
     * @param pairwiseDistanceMatrix the matrix giving how far a is from b, a from c, b from c, etc.
     * @return A Vacation consisting of different scenes, all having at least one photograph
     */
    @Override
    public Vacation clusterPhotographs(List<Photograph> photos, DenseMatrix32F pairwiseDistanceMatrix) {
        Vacation ret = null;

        Log.d("Clustering", pairwiseDistanceMatrix.toString());

        return ret;
    }
}
