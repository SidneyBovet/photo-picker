package ch.epfl.ivrl.photopicker.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import ch.epfl.ivrl.photopicker.R;
import ch.epfl.ivrl.photopicker.dateSelect.DateChangedListener;
import ch.epfl.ivrl.photopicker.dateSelect.DatePickerFragment;
import ch.epfl.ivrl.photopicker.imageData.Photograph;
import ch.epfl.ivrl.photopicker.imageData.Vacation;
import ch.epfl.ivrl.photopicker.imageGrouping.ImageClusteringTask;
import ch.epfl.ivrl.photopicker.imageGrouping.ImageClusteringTechnique;
import ch.epfl.ivrl.photopicker.imageGrouping.ImageDBSCAN;
import ch.epfl.ivrl.photopicker.imageGrouping.TimeDistance;
import ch.epfl.ivrl.photopicker.imageMisc.ImageDateFilter;
import ch.epfl.ivrl.photopicker.permissionManagement.PermissionGranter;
import ch.epfl.ivrl.photopicker.view.CoverFlow;

public class MainActivity extends AppCompatActivity
    implements DateChangedListener {

    private Calendar mStartDate;
    private Calendar mEndDate;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                getImageList(view.getContext());
                Intent slidePhotoIntent = new Intent();
                slidePhotoIntent.putExtra("uri-list", ImageDateFilter.getCameraImages());
                slidePhotoIntent.setClass(view.getContext(), SlidePhoto.class);
                startActivity(slidePhotoIntent);
            }
        });*/


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        PermissionGranter.askForPermission(this);

        goToPhotoSelection(null); //TODO: remove this
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mClient.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Main Page",
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse(""),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://ch.epfl.ivrl.photopicker/http/host/path")
        );
        AppIndex.AppIndexApi.start(mClient, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Choose a start and end date", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse(""),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://ch.epfl.ivrl.photopicker/http/host/path")
        );
        AppIndex.AppIndexApi.end(mClient, viewAction);
        mClient.disconnect();
    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();

        if (v.getId() == R.id.start_date_button)
            newFragment.setIsStartDate(true);
        else
            newFragment.setIsStartDate(false);

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);


        newFragment.show(ft, "date_picker");
    }

    @Override
    public void changeTheButton(boolean isStartDate, Calendar newDate) {
        String newText;

        if (isStartDate) {
            newDate.set(Calendar.HOUR_OF_DAY, 0);
        } else {
            newDate.set(Calendar.HOUR_OF_DAY, 23);
            newDate.set(Calendar.MINUTE, 59);
        }

        final int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_WEEKDAY | DateUtils.FORMAT_SHOW_YEAR;

        newText = DateUtils.formatDateTime(getBaseContext(), newDate.getTimeInMillis(), flags);

        Button datePicker;
        if (isStartDate) {
            mStartDate = newDate;
            datePicker = (Button) findViewById(R.id.start_date_button);
        } else {
            mEndDate = newDate;
            datePicker = (Button) findViewById(R.id.end_date_button);
        }

        datePicker.setText(newText);
    }

    public void goToPhotoSelection(View v) {
        // retrieve photos and cluster them
        List<Photograph> filteredImages = getImagesAccordingToDates();

        ImageClusteringTask ict = new ImageClusteringTask(
                MainActivity.this,
                new TimeDistance(),
                new ImageDBSCAN(5000f, 2));
        try {
            Vacation vacation = ict.execute(filteredImages).get();
            Intent slidePhotoIntent = new Intent();
            slidePhotoIntent.putExtra("vacation", vacation);
            slidePhotoIntent.setClass(MainActivity.this, Tinder.class);
            startActivity(slidePhotoIntent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    /**
     * Uses this Activity's intent to retrieve the start- and end-date, and then filters all the
     * pictures from the camera to get only the ones within the time frame.
     * @return A list of all the Photographs matching the dates passed to this activity.
     */
    private List<Photograph> getImagesAccordingToDates() {
        // adjust dates if needed
        if (mStartDate == null) {
            mStartDate = Calendar.getInstance();
            mStartDate.set(Calendar.YEAR, 0);
            mStartDate.set(Calendar.HOUR_OF_DAY, 0);
        }
        if (mEndDate == null) {
            mEndDate = Calendar.getInstance();
            mEndDate.set(Calendar.HOUR_OF_DAY, 23);
            mEndDate.set(Calendar.MINUTE, 59);
        }

        // get all pictures from the camera and filter them
        List<String> paths = ImageDateFilter.getCameraImages(this);
        return ImageDateFilter.getFilesWithinDates(MainActivity.this, paths, mStartDate, mEndDate);
    }
}
