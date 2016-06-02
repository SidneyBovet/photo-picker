package ch.epfl.ivrl.photopicker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.ivrl.photopicker.R;
import ch.epfl.ivrl.photopicker.imageData.Photograph;
import ch.epfl.ivrl.photopicker.imageData.Scene;
import ch.epfl.ivrl.photopicker.imageData.Vacation;
import ch.epfl.ivrl.photopicker.imageMisc.ImageAsyncDisplay;
import ch.epfl.ivrl.photopicker.utils.ProgressDialogUtils;
import ch.epfl.ivrl.photopicker.view.OnSwipeListener;
import ch.epfl.ivrl.photopicker.view.SwipingLinerLayout;
import ch.epfl.ivrl.photopicker.view.VerticalCarouselView;

public class Tinder extends AppCompatActivity implements OnSwipeListener {

    private static int ROW_HEIGHT = 550;
    private static int EMPTY_PHOTO_COUNT = 1;

    private long mBackPressTime = 0;
    private boolean isActivityDone = false;

    private VerticalCarouselView mPhotoListView;
    private GridView mKeptGrid;
    private GridView mDiscardedGrid;

    private Vacation mVacation;
    private Scene mScene;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ProgressDialogUtils.showProgressFor(Tinder.this, 500, "Loading pictures...");

        onCreateWork();
    }

    private void onCreateWork() {
        setContentView(R.layout.activity_tinder);

        // Listen to swipe gestures on top view
        ((SwipingLinerLayout) findViewById(R.id.tinderContainer)).setSwipeListener(this);

        // Get the pictures from the intent
        mVacation = getVacationFromIntent();
        mScene = mVacation.getScene(mVacation.getCount() - 1);
        List<Photograph> photos = mScene.getPhotographs();

        // Set up the three sub views
        mKeptGrid = (GridView) findViewById(R.id.kept);
        mDiscardedGrid = (GridView) findViewById(R.id.discarded);
        mPhotoListView = (VerticalCarouselView) findViewById(R.id.current);
        setGridView(mKeptGrid);
        setGridView(mDiscardedGrid);
        setCoverFlow(photos, mPhotoListView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == TabbedPhotoZoom.RESULT_MODIFIED) {
            List<Photograph> placedBack = (List<Photograph>) data.getSerializableExtra("to-be-retreated");
            movePhotosBack(placedBack);
        }
    }

    @Override
    public void onSwipe(boolean isSwipeLeft) {
        ImageAdapter listAdapter = (ImageAdapter) mPhotoListView.getAdapter(); // this is not very nice
        Photograph treated = listAdapter.pop();

        if (treated == null)
            throw new IllegalStateException("Should not swipe on empty list");

        ImageAdapter gridAdapter;
        if (isSwipeLeft) {
            gridAdapter = (ImageAdapter) mDiscardedGrid.getAdapter();
        } else {
            gridAdapter = (ImageAdapter) mKeptGrid.getAdapter();
        }

        gridAdapter.addItem(treated);

        if (listAdapter.isEmpty()) {
            isActivityDone = true;
            if (mVacation.getCount() == 1)
                startEndActivity();
            else
                startNewTinderActivity();
        }
    }

    /**
     * Works with two presses:
     * 1. display the toast
     * 2. if close enough in time, start the main activity again
     */
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mBackPressTime > 4000) {
            Snackbar.make(findViewById(R.id.tinderContainer),
                    "You cannot go back with this version of the app.\n" +
                            "Press again to start over.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            mBackPressTime = System.currentTimeMillis();
        } else {
            Intent intent = new Intent();
            intent.setClass(Tinder.this, MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        }
    }

    /**
     * Removes all the photos given from both grid views and adds them to the vertical carousel view
     * @param photos The photos to be moved
     */
    private void movePhotosBack(List<Photograph> photos) {
        ImageAdapter discardedAdapter = (ImageAdapter) mDiscardedGrid.getAdapter();
        removePhotosFromAdapter(photos, discardedAdapter);

        ImageAdapter keptAdapter = (ImageAdapter) mKeptGrid.getAdapter();
        removePhotosFromAdapter(photos, keptAdapter);

        ImageAdapter currentAdapter = (ImageAdapter) mPhotoListView.getAdapter();
        addPhotosFromAdapter(photos, currentAdapter);
    }

    /**
     * Removes the given photos from the given adapter
     * @param photos
     * @param adapter
     */
    private void removePhotosFromAdapter(List<Photograph> photos, ImageAdapter adapter) {
        for (Photograph p : photos) {
            adapter.removePhoto(p);
        }
    }

    /**
     * Adds the given photos from the given adapter
     * @param photos
     * @param adapter
     */
    private void addPhotosFromAdapter(List<Photograph> photos, ImageAdapter adapter) {
        for (Photograph p : photos) {
            adapter.addItem(p);
        }
    }

    /**
     * @param fromThisActivity the {@code Photographs} selected by this activity
     * @return All the pictures kept by previous activities plus the ones selected now
     */
    private List<Photograph> getAllKeptPhotos(List<Photograph> fromThisActivity) {
        List<Photograph> previouslyKept = (List<Photograph>) getIntent().getSerializableExtra("kept-photos");
        if (previouslyKept != null) {
            previouslyKept.addAll(fromThisActivity);
        } else {
            previouslyKept = new ArrayList<>(fromThisActivity);
        }

        return previouslyKept;
    }

    /**
     * Starts a new Tinder activity (this very class) after having removed the scene taken care of
     * previously.
     * Also passes the result of its sorting of the aforementioned scene.
     */
    private void startNewTinderActivity() {
        // remove this scene from the vacation
        mVacation.removeScene(mScene);

        // pass the vacation to a new instance of this activity
        Intent tinderIntent = getIntent();

        tinderIntent.removeExtra("vacation");
        tinderIntent.putExtra("vacation", mVacation);

        // also add the result of our sorting
        List<Photograph> keptPhotos = ((ImageAdapter)mKeptGrid.getAdapter()).getPhotographs();
        List<Photograph> keptAllTime = getAllKeptPhotos(keptPhotos);
        tinderIntent.removeExtra("kept-photos");
        tinderIntent.putExtra("kept-photos", (Serializable) keptAllTime);

        // start the new tinder activity
        tinderIntent.setClass(Tinder.this, Tinder.class);
        startActivity(tinderIntent);
        finish();
        overridePendingTransition(0, 0);
    }

    /**
     * For now the end activity is just a TabbedZoomView displaying all kept pictures
     */
    private void startEndActivity() {
        Intent tabbedZoomIntent = new Intent();

        // collect all the kept pictures (previously discarded ones are lost FOREVER)
        List<Photograph> keptPhotos = ((ImageAdapter)mKeptGrid.getAdapter()).getPhotographs();
        List<Photograph> keptAllTime = getAllKeptPhotos(keptPhotos);
        tabbedZoomIntent.putExtra("photos", (Serializable) keptAllTime);

        // in case we come back after displaying all the kept photos
        getIntent().removeExtra("kept-photos");

        // set correct title
        String title = "Done sorting photos!";
        tabbedZoomIntent.putExtra("title", title);

        // launch the activity and permit to come back to re-sort some pictures
        tabbedZoomIntent.setClass(Tinder.this, TabbedPhotoZoom.class);
        startActivityForResult(tabbedZoomIntent, 0);
    }

    private Vacation getVacationFromIntent() {
        Intent intent = getIntent();
        return (Vacation) intent.getSerializableExtra("vacation");
    }

    private void setGridView(final GridView view) {
        final ImageAdapter gridAdapter = new ImageAdapter(null, -1);
        view.setAdapter(gridAdapter);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<Photograph> content = gridAdapter.getPhotographs();
                Intent tabbedZoomIntent = new Intent();
                tabbedZoomIntent.putExtra("photos", (Serializable) content);

                String title;
                if (parent.getId() == R.id.kept)
                    title = "Kept pictures";
                else
                    title = "Discarded pictures";
                tabbedZoomIntent.putExtra("title", title);

                tabbedZoomIntent.setClass(Tinder.this, TabbedPhotoZoom.class);
                startActivityForResult(tabbedZoomIntent, 0);
            }
        });
    }

    private void setCoverFlow(List<Photograph> photos, VerticalCarouselView coverFlow) {

        coverFlow.setAdapter(new ImageAdapter(photos, ROW_HEIGHT, EMPTY_PHOTO_COUNT));
        coverFlow.setMaxZoom(-120);
        //coverFlow.setSpacing(-25);
        coverFlow.setSelection(coverFlow.getCount() - 1);
        //coverFlow.setAnimationDuration(1000);
    }

    public class ImageAdapter extends BaseAdapter {

        private List<Photograph> mPhotographs;
        private int mEmptyCount; // number of blank photographs at the end of this list

        private int mRowHeight;

        public ImageAdapter(List<Photograph> photos, int rowHeight) {
            this(photos, rowHeight, 0);
        }

        public ImageAdapter(List<Photograph> photos, int rowHeight, int emptyStart) {

            if (photos != null) {
                mPhotographs = photos;
            } else {
                mPhotographs = new ArrayList<>();
            }

            mEmptyCount = emptyStart;
            while (emptyStart > 0) {
                mPhotographs.add(mPhotographs.size(), null);
                emptyStart--;
            }
            Collections.reverse(mPhotographs);

            mRowHeight = rowHeight;
        }

        public int getCount() {
            return mPhotographs.size();
        }

        public boolean isEmpty() {
            return mPhotographs.size()-mEmptyCount == 0;
        }

        public void addItem(Photograph newPicture) {
            addItem(newPicture, getCount());
        }

        public void addItem(Photograph newPicture, int position) {
            if (position < 0 || position > getCount())
                throw new ArrayIndexOutOfBoundsException("Cannot add new picture at position " + position);
            if (newPicture == null)
                throw new IllegalArgumentException("Cannot add null picture");

            mPhotographs.add(position, newPicture);
            notifyDataSetChanged();
        }

        public Photograph pop() {

            Photograph popped = null;

            if (getCount() > mEmptyCount) {
                popped = mPhotographs.remove(mPhotographs.size() - 1);
                notifyDataSetChanged();
            }

            return popped;
        }

        public boolean removePhoto(Photograph photo) {
            Photograph toBeRemoved = null;
            for (Photograph p : mPhotographs) {
                if (p.equals(photo)) {
                    toBeRemoved = p;
                    break;
                }
            }
            boolean returned =  mPhotographs.remove(toBeRemoved);
            notifyDataSetChanged();
            return returned;
        }

        public Object getItem(int position) {
            return mPhotographs.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public List<Photograph> getPhotographs() {
            return mPhotographs;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            //Log.d("ImageAdapter", "photo list size = "+mPhotographs.size());
            //Log.d("ImageAdapter", "empty end count = "+ mEmptyCount);

            ImageView imageView;

            // try to reuse given view, create a new one if needed
            if(convertView != null && convertView.getClass() == ImageView.class) {
                imageView = (ImageView) convertView;
            } else {
                imageView = new ImageView(Tinder.this);
            }

            // set view photo if any
            if (position < mEmptyCount) {
                //imageView.setImageResource(R.drawable.common_ic_googleplayservices);
                imageView.setImageResource(android.R.color.transparent);
            } else {
                Photograph photograph = mPhotographs.get(position);

                //Log.d("ImageAdapter", "launching async task on photo " + mPhotographs.get(position).toString());
                ImageAsyncDisplay imageAsyncDisplay = new ImageAsyncDisplay(
                        null, imageView);

                if (mRowHeight > 0)
                    photograph.setTargetHeight(mRowHeight);
                else
                    photograph.setTargetHeight(parent.getHeight());
                photograph.setTargetWidth(parent.getMeasuredWidth());
                imageAsyncDisplay.execute(photograph);
            }


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
            } else {
                imageView.setAdjustViewBounds(true);
                imageView.setBackgroundColor(Color.WHITE);
                imageView.setTranslationZ(5.0f);
            }

            return imageView;
        }
    }
}
