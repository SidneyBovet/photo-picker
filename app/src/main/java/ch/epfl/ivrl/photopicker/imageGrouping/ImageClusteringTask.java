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
        mProgressDialog = new ProgressDialog(ctx);
        mDistanceMetric = distanceMetric;
        mClusteringTechnique = clusteringTechnique;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog.setMessage("Loading photos...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected Vacation doInBackground(List<Photograph>... filteredPhotos) {
        if (filteredPhotos.length != 1)
            throw new IllegalArgumentException("I must be given exactly one list of photos.");

        DenseMatrix32F pairwiseDistances = ImageGroupingUtils.getDistanceMatrix(filteredPhotos[1], mDistanceMetric, this);

        Log.d("AsyncTask DoInBg", pairwiseDistances.toString());

        Vacation collection = mClusteringTechnique.clusterPhotographs(filteredPhotos[1], pairwiseDistances);

        return collection;
    }

    public void makeProgress(int progress) {
        publishProgress(progress);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        mProgressDialog.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(Vacation result) {
        mProgressDialog.dismiss();

        if(result != null)
            Log.d("AsyncTask", result.toString());
    }
}
