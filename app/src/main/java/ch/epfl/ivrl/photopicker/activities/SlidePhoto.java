package ch.epfl.ivrl.photopicker.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.List;

import ch.epfl.ivrl.photopicker.R;
import ch.epfl.ivrl.photopicker.imageData.Photograph;
import ch.epfl.ivrl.photopicker.imageGrouping.ImageClusteringTask;
import ch.epfl.ivrl.photopicker.imageGrouping.ThresholdClustering;
import ch.epfl.ivrl.photopicker.imageGrouping.TimeDistance;
import ch.epfl.ivrl.photopicker.imageMisc.ImageAsyncDisplay;
import ch.epfl.ivrl.photopicker.imageMisc.ImageDateFilter;
import ch.epfl.ivrl.photopicker.view.CoverFlow;
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

        // Left
        ImageView iv1 = new ImageView(SlidePhoto.this);
        setElevation(iv1);
        lp.position = TinderView.LayoutParams.POSITION_LEFT;
        mTinderView.addView(iv1, lp);

        // Middle
        CoverFlow coverFlow = new CoverFlow(SlidePhoto.this);
        String[] photos = getPathsFromPhotos(getImagesAccordingToDates());
        ImageAdapter coverImageAdapter =  new ImageAdapter(SlidePhoto.this, photos);
        coverFlow.setAdapter(coverImageAdapter);
        coverFlow.setSpacing(-25);
        coverFlow.setSelection(2, true);
        coverFlow.setAnimationDuration(1000);
        lp.position = TinderView.LayoutParams.POSITION_MIDDLE;
        mTinderView.addView(coverFlow, lp);

        // Right
        ImageView iv3 = new ImageView(SlidePhoto.this);
        setElevation(iv3);
        lp.position = TinderView.LayoutParams.POSITION_RIGHT;
        mTinderView.addView(iv3, lp);

        ImageAsyncDisplay imageAsyncDisplay = new ImageAsyncDisplay(SlidePhoto.this, iv1);
        Photograph photo = filteredImages.get(0);
        imageAsyncDisplay.execute(photo);


        imageAsyncDisplay = new ImageAsyncDisplay(SlidePhoto.this, iv3);
        photo = filteredImages.get(2);
        imageAsyncDisplay.execute(photo);
    }

    private void setElevation(View v) {
        v.setBackgroundColor(Color.WHITE);
        v.setElevation(0);
        v.setTranslationZ(10);
    }

    private String[] getPathsFromPhotos(List<Photograph> photographs) {
        String[] ret = new String[photographs.size()];

        for (int i = 0; i < photographs.size(); i++) {
            ret[i] = photographs.get(i).getPath();
        }

        return ret;
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
        if (id == R.id.action_tutorial) {
            Intent slidePhotoIntent = new Intent();
            slidePhotoIntent.setClass(SlidePhoto.this, TutorialActivity.class);
            startActivity(slidePhotoIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class ImageAdapter extends BaseAdapter {
        int mGalleryItemBackground;
        private Context mContext;

        private FileInputStream fis;

        private String[] mImagePaths;

        private ImageView[] mImages;

        public ImageAdapter(Context c, String[] paths) {
            mContext = c;
            mImagePaths = paths;
            mImages = new ImageView[mImagePaths.length];
        }
        public boolean createReflectedImages() {
            //The gap we want between the reflection and the original image
            final int reflectionGap = 4;


            int index = 0;
            for (String imagePath : mImagePaths) {
                Bitmap originalImage = BitmapFactory.decodeFile(imagePath);
                int width = originalImage.getWidth();
                int height = originalImage.getHeight();


                //This will not scale but will flip on the Y axis
                Matrix matrix = new Matrix();
                matrix.preScale(1, -1);

                //Create a Bitmap with the flip matrix applied to it.
                //We only want the bottom half of the image
                Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height/2, width, height/2, matrix, false);


                //Create a new bitmap with same width but taller to fit reflection
                Bitmap bitmapWithReflection = Bitmap.createBitmap(width
                        , (height + height/2), Bitmap.Config.ARGB_8888);

                //Create a new Canvas with the bitmap that's big enough for
                //the image plus gap plus reflection
                Canvas canvas = new Canvas(bitmapWithReflection);
                //Draw in the original image
                canvas.drawBitmap(originalImage, 0, 0, null);
                //Draw in the gap
                Paint defaultPaint = new Paint();
                canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
                //Draw in the reflection
                canvas.drawBitmap(reflectionImage,0, height + reflectionGap, null);

                //Create a shader that is a linear gradient that covers the reflection
                Paint paint = new Paint();
                LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0,
                        bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff,
                        Shader.TileMode.CLAMP);
                //Set the paint to use this shader (linear gradient)
                paint.setShader(shader);
                //Set the Transfer mode to be porter duff and destination in
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                //Draw a rectangle using the paint with our linear gradient
                canvas.drawRect(0, height, width,
                        bitmapWithReflection.getHeight() + reflectionGap, paint);

                ImageView imageView = new ImageView(mContext);
                imageView.setImageBitmap(bitmapWithReflection);
                imageView.setLayoutParams(new CoverFlow.LayoutParams(120, 180));
                imageView.setScaleType(ImageView.ScaleType.MATRIX);
                mImages[index++] = imageView;

            }
            return true;
        }

        public int getCount() {
            return mImagePaths.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            //Use this code if you want to load from resources
            ImageView i = new ImageView(mContext);
            i.setImageBitmap(BitmapFactory.decodeFile(mImagePaths[position]));
            i.setLayoutParams(new CoverFlow.LayoutParams(130, 130));
            i.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            //Make sure we set anti-aliasing otherwise we get jaggies
            BitmapDrawable drawable = (BitmapDrawable) i.getDrawable();
            drawable.setAntiAlias(true);
            return i;

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
