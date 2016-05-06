package ch.epfl.ivrl.photopicker.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import ch.epfl.ivrl.photopicker.R;
import ch.epfl.ivrl.photopicker.imageData.Photograph;
import ch.epfl.ivrl.photopicker.imageGrouping.ImageClusteringTask;
import ch.epfl.ivrl.photopicker.imageGrouping.ThresholdClustering;
import ch.epfl.ivrl.photopicker.imageGrouping.TimeDistance;
import ch.epfl.ivrl.photopicker.imageMisc.ImageAsyncDisplay;
import ch.epfl.ivrl.photopicker.imageMisc.ImageDateFilter;
import ch.epfl.ivrl.photopicker.view.TinderView;

//import android.support.v7.widget.ContentFrameLayout;

public class SlidePhoto extends AppCompatActivity {

    /**
     * The {@link TinderView} that will host the section contents.
     */
    private TinderView mTinderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_slide_photo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<Photograph> filteredImages = getImagesAccordingToDates();

        // use async task to get the vacation here
        ImageClusteringTask ict = new ImageClusteringTask(
                SlidePhoto.this,
                new TimeDistance(),
                new ThresholdClustering());
        ict.execute(filteredImages);

        // Set up the tinder view for this activity
        mTinderView = (TinderView) findViewById(R.id.container);

        // Add a few images
        TinderView.LayoutParams lp = new TinderView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;

        ImageView iv1 = new ImageView(SlidePhoto.this);
        lp.position = TinderView.LayoutParams.POSITION_LEFT;
        mTinderView.addView(iv1, lp);

        ImageView iv2 = new ImageView(SlidePhoto.this);
        lp.position = TinderView.LayoutParams.POSITION_MIDDLE;
        mTinderView.addView(iv2, lp);

        ImageView iv3 = new ImageView(SlidePhoto.this);
        lp.position = TinderView.LayoutParams.POSITION_RIGHT;
        mTinderView.addView(iv3, lp);

        ImageAsyncDisplay imageAsyncDisplay = new ImageAsyncDisplay(SlidePhoto.this, iv1);
        Photograph photo = filteredImages.get(0);
        imageAsyncDisplay.execute(photo.getPath(), photo.getTargetHeight() / 20 + "", photo.getTargetWidth() / 20 + "");

        imageAsyncDisplay = new ImageAsyncDisplay(SlidePhoto.this, iv2);
        photo = filteredImages.get(1);
        imageAsyncDisplay.execute(photo.getPath(), photo.getTargetHeight() / 2 + "", photo.getTargetWidth() / 2 + "");

        imageAsyncDisplay = new ImageAsyncDisplay(SlidePhoto.this, iv3);
        photo = filteredImages.get(2);
        imageAsyncDisplay.execute(photo.getPath(), photo.getTargetHeight() / 20 + "", photo.getTargetWidth() / 20 + "");
    }

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
        return ImageDateFilter.getFilesWithinDates(SlidePhoto.this, paths, startDate, endDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_slide_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
