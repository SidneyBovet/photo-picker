package ch.epfl.ivrl.photopicker.view;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;


/**
 * Copyright (C) 2010 Neil Davies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This code is base on the CoverFlow Gallery widget created
 * by Neil Davies neild001@gmail.com
 *
 * @author Sidney Bovet
 */
public class VerticalCarouselView extends ListView implements AbsListView.OnScrollListener {

    private boolean isScrolling = false;

    /**
     * Graphics Camera used for transforming the matrix of ImageViews
     */
    private final Camera mCamera = new Camera();

    /**
     * The offset from the bottom of the "center" of the carousel
     * 0 means on top, 1 all the way down
     */
    private final static float BOTTOM_OFFSET_FACTOR = 0.75f;

    /**
     * This is used as a fake rotation angle in order to zoom out the photos
     */
    private int mMaxRotationAngle = 60;

    /**
     * The maximum zoom on the centre Child
     */
    private int mMaxZoom = -120;

    /**
     * The Centre of the carousel
     */
    private int mCarouselCenter;

    public VerticalCarouselView(Context context) {
        super(context);
        setUpScroll();
    }

    public VerticalCarouselView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpScroll();
    }

    public VerticalCarouselView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setUpScroll();
    }

    private void setUpScroll() {
        this.setStaticTransformationsEnabled(true);
        this.setVerticalScrollBarEnabled(false);
        this.setOnScrollListener(this);
    }

    /**
     * {@inheritDoc}
     *
     * Sets whether this view is scrolling.
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        isScrolling = scrollState == SCROLL_STATE_TOUCH_SCROLL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    /**
     * Get the Max zoom of the centre image
     * @return the mMaxZoom
     */
    public int getMaxZoom() {
        return mMaxZoom;
    }

    /**
     * Set the max zoom of the centre image
     * @param maxZoom the mMaxZoom to set
     */
    public void setMaxZoom(int maxZoom) {
        mMaxZoom = maxZoom;
    }

    /**
     * Get the Centre of the carousel
     * @return The centre of this carousel.
     */
    private int getCenterOfCoverFlow() {
        return (int)((getHeight() - getPaddingBottom()) * BOTTOM_OFFSET_FACTOR);
        //return (getHeight() - getPaddingTop() - getPaddingBottom()) / 2 + getPaddingTop();
    }

    public void recomputeCenterOfCoverFlow() {
        mCarouselCenter = getCenterOfCoverFlow();
    }

    /**
     * Get the Centre of the View
     * @return The centre of the given view.
     */
    private static int getCenterOfView(View view) {
        return view.getTop() + view.getHeight() / 2;
    }

    /**
     * {@inheritDoc}
     *
     * @see #setStaticTransformationsEnabled(boolean)
     */
    protected boolean getChildStaticTransformation(View child, Transformation t) {


        final int childCenter = getCenterOfView(child);
        final int childWidth = child.getWidth() ;
        int rotationAngle = 0;

        t.clear();
        t.setTransformationType(Transformation.TYPE_MATRIX);

        if (childCenter == mCarouselCenter) {
            transformImageBitmap((ImageView) child, t, 0);
        } else {
            rotationAngle = (int) (((float) (mCarouselCenter - childCenter)/ childWidth) *  mMaxRotationAngle);
            if (Math.abs(rotationAngle) > mMaxRotationAngle) {
                rotationAngle = (rotationAngle < 0) ? -mMaxRotationAngle : mMaxRotationAngle;
            }

            transformImageBitmap((ImageView) child, t, rotationAngle);
        }

        // scroll to bottom if we stopped scrolling in the middle of the list
        if (!isScrolling ) {
            smoothScrollToPosition(getCount() - 1);
        }

        child.invalidate();

        return true;
    }

    /**
     * This is called during layout when the size of this view has changed. If
     * you were just added to the view hierarchy, you're called with the old
     * values of 0.
     *
     * @param w Current width of this view.
     * @param h Current height of this view.
     * @param oldw Old width of this view.
     * @param oldh Old height of this view.
     */
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mCarouselCenter = getCenterOfCoverFlow();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * Transform the Image Bitmap by the Angle passed
     *
     * @param child ImageView the ImageView whose bitmap we want to rotate
     * @param t transformation
     * @param rotationAngle the Angle by which to rotate the Bitmap
     */
    private void transformImageBitmap(ImageView child, Transformation t, int rotationAngle) {
        mCamera.save();
        final Matrix imageMatrix = t.getMatrix();
        final int imageHeight = child.getLayoutParams().height;
        final int imageWidth = child.getLayoutParams().width;
        final int rotation = Math.abs(rotationAngle);

        mCamera.translate(0.0f, 0.0f, 100.0f);

        //As the angle of the view gets less, zoom in
        if ( rotation < mMaxRotationAngle ) {
            float zoomAmount = (mMaxZoom +  (rotation * 5.0f));//1.5f));
            mCamera.translate(60.0f + zoomAmount * 0.75f, 0.0f, zoomAmount);
        }

        //mCamera.rotateX(-rotationAngle);
        mCamera.getMatrix(imageMatrix);
        imageMatrix.preTranslate(-(imageWidth/2), -(imageHeight/2));
        imageMatrix.postTranslate((imageWidth/2), (imageHeight/2));
        mCamera.restore();
    }
}
