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
import android.view.Display;
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

//import android.support.v7.widget.ContentFrameLayout;

public class SlidePhoto extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

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
        // modify the sectionAdapter to use a vacation instead of a list of files
        // DEBUG ALL THE OUTPUTS
        //fsdjakl;fjdskal;fdsa;


        // Create the adapter that will return a fragment for each image
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), filteredImages);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    private List<Photograph> getImagesAccordingToDates() {

        // retrieve start and end dates
        Intent intent = getIntent();
        Calendar startDate = (Calendar) intent.getSerializableExtra("start-date");
        Calendar endDate = (Calendar) intent.getSerializableExtra("end-date");

        // adjust dates to toady if needed
        if(startDate == null) {
            startDate = Calendar.getInstance();
            startDate.set(Calendar.YEAR, 0);
            startDate.set(Calendar.HOUR_OF_DAY, 0);
        }
        if(endDate == null) {
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_SECTION_IMAGE = "section_image";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, String imagePath) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_SECTION_IMAGE, imagePath);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_slide_photo, container, false);

            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            String path = getArguments().getString(ARG_SECTION_IMAGE);

            if (path != null) {
                File imgFile = new File(path);
                if (imgFile.exists()) {
                    // section title
                    TextView textView = (TextView) rootView.findViewById(R.id.section_label);

                    textView.setText(imgFile.getName());
                    if (sectionNumber % 2 == 0) {
                        textView.setText(imgFile.getName() + " by BoofCV");
                    }

                    // section image

                    WindowManager wm = (WindowManager) inflater.getContext().getSystemService(Context.WINDOW_SERVICE);
                    Display display = wm.getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    int height = size.y;

                    ImageView imageView = (ImageView) rootView.findViewById(R.id.section_image);
                    ImageAsyncDisplay imageAsyncDisplay = new ImageAsyncDisplay(getContext(), imageView);
                    imageAsyncDisplay.execute(path, "" + width, "" + height);

                    if (sectionNumber % 2 == 0) {
                        //MultiSpectral<ImageFloat32> color = ConvertBitmap.bitmapToMS(myBitmap, null, ImageFloat32.class, null);
                        //ConvertBitmap.multiToBitmap(color, myBitmap, null);
                    }

                }

            }
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        List<Photograph> images = null;

        public SectionsPagerAdapter(FragmentManager fm, List<Photograph> filteredImages) {
            super(fm);

            images = filteredImages;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1, images.get(position).getPath());
        }

        @Override
        public int getCount() {
            if (images != null) {
                return images.size();
            } else {
                return 0;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (images != null) {
                return images.get(position).getName();
            } else {
                return null;
            }
        }
    }
}
