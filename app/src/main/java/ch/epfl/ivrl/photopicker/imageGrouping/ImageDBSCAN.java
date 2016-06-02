package ch.epfl.ivrl.photopicker.imageGrouping;

import org.ejml.data.DenseMatrix32F;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.ivrl.photopicker.imageData.Photograph;
import ch.epfl.ivrl.photopicker.imageData.Scene;
import ch.epfl.ivrl.photopicker.imageData.Vacation;
import ch.epfl.ivrl.photopicker.utils.CollectionUtils;

/**
 * Created by Sidney on 5/22/2016.
 *
 * Implementation of the density-based clustering algorithm DBSCAN.
 */
public class ImageDBSCAN implements ImageClusteringTechnique {

    /*
                PSEUDOCODE
    -------------------------------------

    DBSCAN(D, eps, MinPts)
       C = 0
       for each unvisited point P in dataset D
          mark P as visited
          N = getNeighbors (P, eps)
          if sizeof(N) < MinPts
             mark P as NOISE
          else
             C = next cluster
             expandCluster(P, N, C, eps, MinPts)

    expandCluster(P, N, C, eps, MinPts)
       add P to cluster C
       for each point P' in N
          if P' is not visited
             mark P' as visited
             N' = getNeighbors(P', eps)
             if sizeof(N') >= MinPts
                N = N joined with N'
          if P' is not yet member of any cluster
             add P' to cluster C

    ---------------------------------------
     */

    private final float mEps; // the minimum distance between two points to be in the same cluster
    private final int mMinPointCount; // the minimum number of points that can form a cluster
    private Set<DataPoint> mDataSet; // the points to be processed

    /**
     *
     * @param eps           The threshold below which two points are considered in the same cluster
     * @param minPointCount The minimum number of points to form a cluster
     */
    public ImageDBSCAN(float eps, int minPointCount) {
        mEps = eps;
        mMinPointCount = minPointCount;
    }

    /**
     * @param photos                 The list of photographs to cluster among different scenes
     * @param pairwiseDistanceMatrix the matrix giving how far a is from b, a from c, b from c, etc.
     * @return A Vacation consisting of different scenes, all having at least one photograph
     */
    @Override
    public Vacation clusterPhotographs(List<Photograph> photos, DenseMatrix32F pairwiseDistanceMatrix) {
        if (pairwiseDistanceMatrix.getNumRows() != pairwiseDistanceMatrix.getNumCols())
            throw new IllegalArgumentException("Pairwise distance matrix should be a square matrix");
        if (photos == null)
            throw new IllegalArgumentException("Cannot cluster a null list of photographs");

        Set<Set<Integer>> clusters = dbscan(pairwiseDistanceMatrix);

        /*
        Log.d("Clustering", pairwiseDistanceMatrix.toString());
        for (Set<Integer> cluster : clusters) {
            Log.d("Clustering", "---------------------------");
            for (Integer p : cluster) {
                Log.d("Clustering", "" + p);
            }
            Log.d("Clustering", "---------------------------");
        }
        */

        Set<Scene> scenes = new HashSet<>(clusters.size());
        for (Set<Integer> cluster : clusters) {
            List<Photograph> scenePhotos = new ArrayList<>(cluster.size());
            for (Integer photoIndex : cluster) {
                scenePhotos.add(photos.get(photoIndex));
            }

            Scene newScene = new Scene();
            newScene.addPhotos(CollectionUtils.asSortedList(scenePhotos));
            scenes.add(newScene);
        }

        Vacation vacation = new Vacation();
        vacation.addScenes(CollectionUtils.asSortedList(scenes));

        return vacation;
    }

    /**
     * Performs the DBSCAN algorithm using the given pairwise distance matrix. This function is
     * element-agnostic: it only uses their indices, as in the distance matrix.
     * @param matrix The pairwise distance matrix of all the elements to be clustered.
     * @return a set of set of integers, representing a set of clusters of indices.
     */
    private Set<Set<Integer>> dbscan(DenseMatrix32F matrix) {

        Set<Set<Integer>> returned = new HashSet<>();

        // initialize data set
        mDataSet = new HashSet<>();
        for (int i = 0; i < matrix.getNumCols(); i++) {
            DataPoint point = new DataPoint(i);
            mDataSet.add(point);
        }

        for (DataPoint point : mDataSet) {
            if (!point.visited) {
                point.visited = true;

                Set<DataPoint> neighbors = getNeighbors(point.point, matrix);

                Set<Integer> newCluster;
                if (neighbors.size() < mMinPointCount-1) {
                    // photo is not part of any cluster (aka noise)
                    newCluster = new HashSet<>(1);
                    newCluster.add(point.point);
                } else {
                    newCluster = expandCluster(point, neighbors, matrix);
                }

                returned.add(newCluster);
            }
        }

        return returned;
    }

    private Set<DataPoint> getNeighbors(Integer p, DenseMatrix32F matrix) {
        Set<DataPoint> neighbors = new HashSet<>();

        for(int i = 0; i < matrix.getNumCols(); i++) {
            if(matrix.get(p, i) < mEps && p != i)
                neighbors.add(getDataPoint(i));
        }

        return neighbors;
    }

    private Set<Integer> expandCluster(DataPoint p, Set<DataPoint> neighbors, DenseMatrix32F matrix) {
        Set<Integer> newCluster = new HashSet<>(neighbors.size() + 1);

        p.clustered = true;
        newCluster.add(p.point);

        for (DataPoint pPrime : neighbors) {
            if (!pPrime.visited) {
                pPrime.visited = true;
                Set<DataPoint> neighborsPrime = getNeighbors(pPrime.point, matrix);
                if (neighborsPrime.size() >= mMinPointCount-1) {
                    //neighbors.addAll(neighborsPrime); // bug?
                    neighborsPrime.addAll(neighbors);
                    newCluster.addAll(expandCluster(pPrime, neighborsPrime, matrix));
                    //break; //isok?
                }
            }
            if (!pPrime.clustered) {
                pPrime.clustered = true;
                newCluster.add(pPrime.point);
            }
        }

        return newCluster;
    }

    private DataPoint getDataPoint(int point) {
        for (DataPoint p : mDataSet) {
            if (p.point == point)
                return p;
        }

        throw new IllegalStateException("Point " + point + " does not exist");
    }

    private class DataPoint {
        public boolean visited = false;
        public boolean clustered = false;
        public int point;

        public DataPoint(int point) {
            this.point = point;
        }
    }
}
