package ch.epfl.ivrl.photopicker.imageMisc;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.Random;

import ch.epfl.ivrl.photopicker.imageData.Photograph;

/**
 * Created by Sidney on 05.04.2016.
 */
public class ImageAsyncDisplay extends AsyncTask<Photograph, Void, Bitmap> {

    private ProgressDialog mProgressDialog;

    private final WeakReference<ImageView> mImageViewReference;

    public ImageAsyncDisplay(ImageView imageView) {
        this(null, imageView, false);
    }

    public ImageAsyncDisplay(Context ctx, ImageView imageView) {
        this(ctx, imageView, true);
    }

    public ImageAsyncDisplay(Context ctx, ImageView imageView, boolean displayProgress) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        mImageViewReference = new WeakReference<>(imageView);
        if (displayProgress && ctx != null) {
            mProgressDialog = new ProgressDialog(ctx);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (mProgressDialog != null) {
            mProgressDialog.setMessage("Loading photos...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
    }

    @Override
    protected Bitmap doInBackground(Photograph... params) {
        if (params.length != 1)
            throw new IllegalArgumentException("Must be called with exactly one photo");

        Photograph p = params[0];

        Bitmap returned = null;

        if (p != null) {
            returned = ImageUtils.decodeSampledBitmapFromPath(p.getPath(), p.getTargetWidth(), p.getTargetHeight());
        }

        return returned;
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        if (mImageViewReference != null && bitmap != null) {
            final ImageView imageView = mImageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
                //imageView.setTranslationZ(new Random().nextInt(200));
            }
        }
    }
}
