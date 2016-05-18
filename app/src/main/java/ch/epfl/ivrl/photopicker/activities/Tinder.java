package ch.epfl.ivrl.photopicker.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.FileInputStream;
import java.util.Calendar;
import java.util.List;

import ch.epfl.ivrl.photopicker.R;
import ch.epfl.ivrl.photopicker.imageData.Photograph;
import ch.epfl.ivrl.photopicker.imageMisc.ImageAsyncDisplay;
import ch.epfl.ivrl.photopicker.imageMisc.ImageDateFilter;
import ch.epfl.ivrl.photopicker.view.CoverFlow;
import ch.epfl.ivrl.photopicker.view.TinderView;
import ch.epfl.ivrl.photopicker.view.VerticalCarouselView;

public class Tinder extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgressDialog = createAndSetProgressDialog(Tinder.this);

        setContentView(R.layout.activity_tinder);

        List<Photograph> photos = getImagesAccordingToDates();

        setCoverFlow(photos, (VerticalCarouselView) findViewById(R.id.kept), 300);
        setCoverFlow(photos, (VerticalCarouselView) findViewById(R.id.discarded), 300);
        setCoverFlow(photos, (VerticalCarouselView) findViewById(R.id.current), 500);

        mProgressDialog.dismiss();
    }

    private VerticalCarouselView setCoverFlow(List<Photograph> photos, VerticalCarouselView coverFlow, int height) {

        coverFlow.setAdapter(new ImageAdapter(Tinder.this, photos, height));
        coverFlow.setMaxZoom(-120);
        //coverFlow.setSpacing(-25);
        coverFlow.setSelection(coverFlow.getCount());
        //coverFlow.setAnimationDuration(1000);

        return coverFlow;
    }

    private ProgressDialog createAndSetProgressDialog(Context ctx) {
        ProgressDialog progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage("Loading pictures...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();

        return progressDialog;
    }
    /**
     * Uses this Activity's intent to retrieve the start- and end-date, and then filters all the
     * pictures from the camera to get only the ones within the time frame.
     * @return A list of all the Photographs matching the dates passed to this activity.
     */
    private List<Photograph> getImagesAccordingToDates() {

        // retrieve start and end dates
        Intent intent = getIntent();
        Calendar startDate = (Calendar) intent.getSerializableExtra("start-date");
        Calendar endDate = (Calendar) intent.getSerializableExtra("end-date");

        // adjust dates to toady if needed
        if (startDate == null) {
            startDate = Calendar.getInstance();
            startDate.set(Calendar.YEAR, 0);
            startDate.set(Calendar.HOUR_OF_DAY, 0);
        }
        if (endDate == null) {
            endDate = Calendar.getInstance();
            endDate.set(Calendar.HOUR_OF_DAY, 23);
            endDate.set(Calendar.MINUTE, 59);
        }

        // get all pictures from the camera and filter them
        List<String> paths = ImageDateFilter.getCameraImages(this);
        return ImageDateFilter.getFilesWithinDates(Tinder.this, paths, startDate, endDate);
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        private List<Photograph> mPhotographs;
        private int mRowHeight;

        public ImageAdapter(Context c, List<Photograph> photos, int rowHeight) {
            mContext = c;
            mPhotographs = photos;
            mRowHeight = rowHeight;
        }

        public int getCount() {
            return mPhotographs.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;

            // try to reuse given view, create a new one if needed
            if(convertView != null && convertView.getClass() == ImageView.class) {
                imageView = (ImageView) convertView;
            } else {
                imageView = new ImageView(mContext);
            }

            /*
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(parent.getWidth(), parent.getHeight());
            layoutParams.gravity=Gravity.CENTER_HORIZONTAL;
            layoutParams.weight = 1.0f;
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = 500000sadf;
            imageView.setLayoutParams(layoutParams);
            imageView.requestLayout();
            */

            // set view photo
            ImageAsyncDisplay imageAsyncDisplay = new ImageAsyncDisplay(Tinder.this, imageView, false);

            mPhotographs.get(position).setTargetHeight(parent.getHeight());
            mPhotographs.get(position).setTargetHeight(parent.getWidth());

            imageAsyncDisplay.execute(mPhotographs.get(position));


            // set view height
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            if (params == null) {
                params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mRowHeight);
            } else {
                params.height = mRowHeight;
            }
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            //imageView.setLayoutParams(new CoverFlow.LayoutParams(130, 130));

            /*
            //Make sure we set anti-aliasing otherwise we get jaggies
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            drawable.setAntiAlias(true);
            */
            return imageView;

            //return mImages[position];
        }
        /** Returns the size (0.0f to 1.0f) of the views
         * depending on the 'offset' to the center. */
        public float getScale(boolean focused, int offset) {
        /* Formula: 1 / (2 ^ offset) */
            return Math.max(0, 1.0f / (float)Math.pow(2, Math.abs(offset)));
        }

    }
}
