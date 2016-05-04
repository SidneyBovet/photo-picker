package ch.epfl.ivrl.photopicker.imageMisc;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by Sidney on 05.04.2016.
 */
public class ImageAsyncDisplay extends AsyncTask<String, Void, Bitmap> {

    private ProgressDialog mProgressDialog;

    private final WeakReference<ImageView> mImageViewReference;
    private String mPath;

    public ImageAsyncDisplay(Context ctx, ImageView imageView) {
        mProgressDialog = new ProgressDialog(ctx);
        // Use a WeakReference to ensure the ImageView can be garbage collected
        mImageViewReference = new WeakReference<ImageView>(imageView);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog.setMessage("Loading photos...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        if (params.length < 3)
            throw new IllegalArgumentException("Must be called with 3 arguments: path, height, width");

        mPath = params[0];
        int height = Integer.parseInt(params[1]);
        int width = Integer.parseInt(params[2]);

        return ImageUtils.decodeSampledBitmapFromPath(mPath, width, height);
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        mProgressDialog.dismiss();
        if (mImageViewReference != null && bitmap != null) {
            final ImageView imageView = mImageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

}
