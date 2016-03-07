package ch.epfl.ivrl.photopicker;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.ivrl.photopicker.dateSelect.DateChangedListener;
import ch.epfl.ivrl.photopicker.dateSelect.DatePickerFragment;
import ch.epfl.ivrl.photopicker.imageFilter.ImageDateFilter;
import ch.epfl.ivrl.photopicker.permissionManagement.PermissionGranter;

public class MainActivity extends AppCompatActivity
    implements DateChangedListener {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

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
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        PermissionGranter.askForPermission(this);
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
        client.connect();
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
        AppIndex.AppIndexApi.start(client, viewAction);
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
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
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
    public void changeTheButton(boolean isStartDate, String newText) {
        Button datePicker;
        if (isStartDate) {
            datePicker = (Button) findViewById(R.id.start_date_button);
        } else {
            datePicker = (Button) findViewById(R.id.end_date_button);
        }

        datePicker.setText(newText);
    }

    private void getImageList(Context context) {
        Log.d("FILE", "Size = " + ImageDateFilter.getCameraImages(this).size());
        for (String s : ImageDateFilter.getCameraImages(this)) {
            Log.d("FILE",s);
        }
    }

    public void goToPhotoSelection(View v) {
        Intent slidePhotoIntent = new Intent();
        ArrayList<String> images = ImageDateFilter.getCameraImages(this);
        slidePhotoIntent.putExtra("uri-list", images);
        slidePhotoIntent.setClass(this.getBaseContext(), SlidePhoto.class);
        startActivity(slidePhotoIntent);
    }
}
