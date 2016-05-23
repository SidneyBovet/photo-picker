package ch.epfl.ivrl.photopicker.activities;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.ivrl.photopicker.R;
import ch.epfl.ivrl.photopicker.imageData.Photograph;
import ch.epfl.ivrl.photopicker.imageMisc.ImageAsyncDisplay;
import ch.epfl.ivrl.photopicker.imageMisc.ImageUtils;

public class TabbedPhotoZoom extends AppCompatActivity {

    final static int RESULT_NONE = 0;
    final static int RESULT_MODIFIED = 1;

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

    private List<Photograph> mDiscardedPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_photo_zoom);
        setTitle(getTitleFromIntent());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // init the photo lists
        List<Photograph> photographsFromIntent = getPhotographsFromIntent();
        mDiscardedPhotos = new ArrayList<>();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), photographsFromIntent);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int positionToToggle = mViewPager.getCurrentItem();
                Photograph toToggle = mSectionsPagerAdapter.getPhoto(positionToToggle);

                if (mSectionsPagerAdapter.toggleAlpha(positionToToggle)) {
                    mDiscardedPhotos.add(toToggle);
                    Snackbar.make(view, "This picture will be sorted once again.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    mDiscardedPhotos.remove(toToggle);
                    Snackbar.make(view, "The picture is sorted as previously.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed()
    {
        if (mDiscardedPhotos.isEmpty()) {
            this.setResult(RESULT_NONE);
        } else {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("to-be-retreated", (Serializable) mDiscardedPhotos);
            this.setResult(RESULT_MODIFIED, resultIntent);
        }

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed_photo_zoom, menu);
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

    private List<Photograph> getPhotographsFromIntent() {
        Intent myIntent = getIntent();
        return (List<Photograph>) myIntent.getSerializableExtra("photos");
    }

    private String getTitleFromIntent() {
        Intent myIntent = getIntent();
        return myIntent.getStringExtra("title");
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
        private static final String ARG_SECTION_PHOTO = "section_photo";

        public PlaceholderFragment() {}

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, Photograph sectionPhoto) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putSerializable(ARG_SECTION_PHOTO, sectionPhoto);
            fragment.setArguments(args);
            return fragment;
        }

        public boolean toggleAlpha() {

            View rootView = getView();
            if (rootView != null) {
                ImageView imageView = (ImageView) rootView.findViewById(R.id.section_image);

                int alpha = imageView.getImageAlpha();
                if (alpha < 255) {
                    imageView.setImageAlpha(255);
                    imageView.invalidate();
                    return false;
                } else {
                    imageView.setImageAlpha(128);
                    imageView.invalidate();
                    return true;
                }
            } else {
                Log.e("PlaceHolderFragment","Unable to find a root view, check if you are not" +
                        " getting a new fragment instead of accessing an existing one");
                return false;
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tabbed_photo_zoom, container, false);

            Photograph photograph = (Photograph)getArguments().getSerializable(ARG_SECTION_PHOTO);
            if (photograph != null) {
                photograph.setTargetHeight(container.getMeasuredHeight());
                photograph.setTargetWidth(container.getMeasuredWidth());

                ImageView imageView = (ImageView) rootView.findViewById(R.id.section_image);
                ImageAsyncDisplay imageAsyncDisplay = new ImageAsyncDisplay(getContext(), imageView); // TODO: check that this context is valid
                imageAsyncDisplay.execute(photograph);
            }

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private List<Photograph> mPhotographs;

        public SectionsPagerAdapter(FragmentManager fm, List<Photograph> photos) {
            super(fm);
            mPhotographs = photos;
        }

        public Photograph getPhoto(int position) {
            if (position < 0 || position > getCount())
                throw new IllegalArgumentException("Cannot remove photo at position " + position);
            return mPhotographs.get(position);
        }

        private boolean toggleAlpha(int position) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();

            if(position < 0 || position > fragments.size())
                throw new ArrayIndexOutOfBoundsException("Cannot access fragment " + position);

            PlaceholderFragment fragment = (PlaceholderFragment)fragments.get(position);
            return fragment.toggleAlpha();
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class above).
            return PlaceholderFragment.newInstance(position + 1, mPhotographs.get(position));
        }

        @Override
        public int getCount() {
            return mPhotographs.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mPhotographs.get(position).getName();
        }
    }
}
