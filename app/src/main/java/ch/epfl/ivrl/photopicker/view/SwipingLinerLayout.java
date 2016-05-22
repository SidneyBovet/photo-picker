package ch.epfl.ivrl.photopicker.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

/**
 * Created by Sidney on 5/22/2016.
 *
 * This quite straightforward subclass intends to notify a listener of swipe events,
 * both left and right.
 *
 * @see <a href="https://developer.android.com/training/gestures/viewgroup.html">Managing Touch Events in a ViewGroup</a>
 */
public class SwipingLinerLayout extends LinearLayout {
    private int mTouchSlop;
    private boolean mIsSwiping = false;
    private int mDownX, mDownY;

    private OnSwipeListener mSwipeListener;

    public SwipingLinerLayout(Context context) {
        super(context);
        onConstructor(context);
    }

    public SwipingLinerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        onConstructor(context);
    }

    public OnSwipeListener getSwipeListener() {
        return mSwipeListener;
    }

    public void setSwipeListener(OnSwipeListener listener) {
        mSwipeListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * swiping there.
         */

        final int action = MotionEventCompat.getActionMasked(ev);

        // Always handle the case of the touch gesture being complete.
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            // Release the scroll.
            mIsSwiping = false;
            return false; // Do not intercept touch event, let the child handle it
        }

        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                if (mIsSwiping) {
                    // We're currently swiping, so yes, intercept the
                    // touch event!
                    return true;
                }

                // If the user has dragged her finger horizontally more than
                // the touch slop, start the swipe

                // Touch slop should be calculated using ViewConfiguration
                // constants.

                if (isActuallySwiping(ev)) {
                    // Start swiping!
                    mIsSwiping = true;
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                // Touch was just initiated, we record starting point and let go
                mDownX = (int)ev.getX();
                mDownY = (int)ev.getY();
                return false;
            }
        }

        // In general, we don't want to intercept touch events. They should be
        // handled by the child view.
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Here we actually handle the touch event.
        // This method will only be called if the touch event was intercepted in
        // onInterceptTouchEvent
        final int action = MotionEventCompat.getActionMasked(ev);

        if (action == MotionEvent.ACTION_UP) {
            mIsSwiping = false;

            if (mSwipeListener != null) {
                boolean isLeft = ev.getX() < mDownX;

                mSwipeListener.onSwipe(isLeft);
            }
        }

        return true;
    }

    private void onConstructor(Context ctx) {
        ViewConfiguration vc = ViewConfiguration.get(ctx);
        mTouchSlop = vc.getScaledTouchSlop();
    }

    private boolean isActuallySwiping(MotionEvent ev) {
        int deltaX = Math.abs((int)ev.getX() - mDownX);
        int deltaY = Math.abs((int)ev.getY() - mDownY);
        return deltaX > mTouchSlop && deltaY < mTouchSlop;
    }
}
