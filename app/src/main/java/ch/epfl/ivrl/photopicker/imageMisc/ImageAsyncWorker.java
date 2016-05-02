package ch.epfl.ivrl.photopicker.imageMisc;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by Sidney on 05.04.2016.
 */
public class ImageAsyncWorker extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private String path;

    public ImageAsyncWorker(ImageView imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        if (params.length < 3)
            throw new IllegalArgumentException("Must be called with at least 3 arguments.");

        path = params[0];
        int width = Integer.parseInt(params[1]);
        int height = Integer.parseInt(params[2]);
        return ImageUtils.decodeSampledBitmapFromFile(path, width, height);
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

}
