package ch.epfl.ivrl.photopicker.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ch.epfl.ivrl.photopicker.R;
import ch.epfl.ivrl.photopicker.imageData.Photograph;
import ch.epfl.ivrl.photopicker.imageMisc.ImageAsyncDisplay;
import ch.epfl.ivrl.photopicker.imageMisc.ImageDateFilter;
import ch.epfl.ivrl.photopicker.view.OnSwipeListener;
import ch.epfl.ivrl.photopicker.view.SwipingLinerLayout;
import ch.epfl.ivrl.photopicker.view.VerticalCarouselView;

public class Tinder extends AppCompatActivity implements OnSwipeListener {

    private ProgressDialog mProgressDialog;

    private VerticalCarouselView mPhotoListView;
    private GridView mKeptGrid;
    private GridView mDiscardedGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgressDialog = createAndSetProgressDialog(Tinder.this);
        setContentView(R.layout.activity_tinder);

        // Listen to swipe gestures on top view
        ((SwipingLinerLayout) findViewById(R.id.tinderContainer)).setSwipeListener(this);

        // Get the pictures from the intent
        List<Photograph> photos = getImagesAccordingToDates();

        // Set up the three sub views
        mKeptGrid = (GridView) findViewById(R.id.kept);
        mDiscardedGrid = (GridView) findViewById(R.id.discarded);
        mPhotoListView = (VerticalCarouselView) findViewById(R.id.current);
        setGridView(mKeptGrid);
        setGridView(mDiscardedGrid);
        setCoverFlow(photos, mPhotoListView, 500);

        mProgressDialog.dismiss();
    }

    @Override
    public void onSwipe(boolean isSwipeLeft) {
        ImageAdapter listAdapter = (ImageAdapter) mPhotoListView.getAdapter(); // this is not very nice
        Photograph treated = listAdapter.pop();

        if (treated == null)
            throw new IllegalStateException("Should not swipe on empty list");

        ImageAdapter gridAdapter = null;
        if (isSwipeLeft) {
            gridAdapter = (ImageAdapter) mDiscardedGrid.getAdapter();
        } else {
            gridAdapter = (ImageAdapter) mKeptGrid.getAdapter();
        }

        gridAdapter.addItem(treated);

        if (listAdapter.getCount() == 0) {
            Log.d("TinderActivity", "Done sorting this scene!");
        }
    }

    private void setGridView(GridView view) {
        view.setAdapter(new ImageAdapter(Tinder.this, null, -1));
    }

    private void setCoverFlow(List<Photograph> photos, VerticalCarouselView coverFlow, int height) {

        coverFlow.setAdapter(new ImageAdapter(Tinder.this, photos, height));
        coverFlow.setMaxZoom(-120);
        //coverFlow.setSpacing(-25);
        coverFlow.setSelection(coverFlow.getCount() - 1);
        //coverFlow.setAnimationDuration(1000);
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

            if (photos != null) {
                mPhotographs = photos;
            } else {
                mPhotographs = new ArrayList<>();
            }

            mRowHeight = rowHeight;
        }

        public int getCount() {
            return mPhotographs.size();
        }

        public void addItem(Photograph newPicture) {
            addItem(newPicture, getCount());
        }

        public void addItem(Photograph newPicture, int position) {
            if (position < 0 || position > getCount())
                throw new IllegalArgumentException("Cannot add new picture at position " + position);
            if (newPicture == null)
                throw new IllegalArgumentException("Cannot add null picture");

            mPhotographs.add(position, newPicture);
            notifyDataSetInvalidated();
        }

        public Photograph pop() {
            Photograph popped = null;

            if (getCount() > 0) {
                popped = mPhotographs.remove(mPhotographs.size() - 1);
                notifyDataSetInvalidated();
            }

            return popped;
        }

        public Object getItem(int position) {
            return mPhotographs.get(position);
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

            // set view photo
            ImageAsyncDisplay imageAsyncDisplay = new ImageAsyncDisplay(
                    Tinder.this, imageView, false);
            mPhotographs.get(position).setTargetHeight(parent.getHeight());
            mPhotographs.get(position).setTargetHeight(parent.getWidth());
            imageAsyncDisplay.execute(mPhotographs.get(position));


            // set view height
            if (mRowHeight > 0) {
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                if (params == null) {
                    params = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, mRowHeight);
                } else {
                    params.height = mRowHeight;
                }
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                //imageView.setLayoutParams(new CoverFlow.LayoutParams(130, 130));
            } else {
                imageView.setAdjustViewBounds(true);
                imageView.setBackgroundColor(Color.WHITE);
                imageView.setTranslationZ(10.0f);
            }

            return imageView;
        }
    }
}
