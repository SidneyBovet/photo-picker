package ch.epfl.ivrl.photopicker.imageGrouping;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import org.ejml.data.DenseMatrix32F;

import java.util.List;

import ch.epfl.ivrl.photopicker.imageData.Photograph;
import ch.epfl.ivrl.photopicker.imageData.Vacation;

/**
 * Created by Sidney on 05.04.2016.
 *
 * Uses threading to cluster Photographs according to a ImageDistanceMetric and a ImageClusteringTechnique.
 */
public class ImageClusteringTask extends AsyncTask <List<Photograph>, Integer, Vacation> {

    private ProgressDialog mProgressDialog;
    private final ImageDistanceMetric mDistanceMetric;
    private final ImageClusteringTechnique mClusteringTechnique;

    public ImageClusteringTask(@Nullable Context ctx,
            ImageDistanceMetric distanceMetric,
            ImageClusteringTechnique clusteringTechnique) {

        mDistanceMetric = distanceMetric;
        mClusteringTechnique = clusteringTechnique;
        if (ctx != null) {
            mProgressDialog = new ProgressDialog(ctx);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mProgressDialog != null) {
            mProgressDialog.setMessage("Grouping your pictures...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
    }

    @Override
    protected Vacation doInBackground(List<Photograph>... filteredPhotos) {
        if (filteredPhotos.length != 1)
            throw new IllegalArgumentException("I must be given exactly one list of photos.");

        DenseMatrix32F pairwiseDistances = ImageGroupingUtils.getDistanceMatrix(filteredPhotos[0], mDistanceMetric);

        return mClusteringTechnique.clusterPhotographs(filteredPhotos[0], pairwiseDistances);
    }

    public void makeProgress(int progress) {
        publishProgress(progress);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        mProgressDialog.setProgress(progress[0] * 100);
    }

    @Override
    protected void onPostExecute(Vacation result) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
