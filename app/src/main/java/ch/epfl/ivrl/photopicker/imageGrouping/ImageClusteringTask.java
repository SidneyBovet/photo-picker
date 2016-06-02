package ch.epfl.ivrl.photopicker.imageGrouping;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.ejml.data.DenseMatrix32F;

import java.util.List;

import ch.epfl.ivrl.photopicker.imageData.Photograph;
import ch.epfl.ivrl.photopicker.imageData.Vacation;

/**
 * Created by LogitechVR on 05.04.2016.
 */
public class ImageClusteringTask extends AsyncTask <List<Photograph>, Integer, Vacation> {

    private ProgressDialog mProgressDialog;
    private ImageDistanceMetric mDistanceMetric;
    private ImageClusteringTechnique mClusteringTechnique;

    public ImageClusteringTask(Context ctx,
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
        Log.d("AsyncTask", "dialog shown");
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

        DenseMatrix32F pairwiseDistances = ImageGroupingUtils.getDistanceMatrix(filteredPhotos[0], mDistanceMetric, mProgressDialog);

        Vacation collection = mClusteringTechnique.clusterPhotographs(filteredPhotos[0], pairwiseDistances);

        return collection;
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

        Log.d("AsyncTask", "dialog dismissed");
    }
}
