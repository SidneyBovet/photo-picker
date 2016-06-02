package ch.epfl.ivrl.photopicker.view;

/**
 * Created by Sidney on 5/22/2016.
 *
 * Interface for listening to swipe gestures.
 * See the implementation of TinderActivity for an example.
 */
public interface OnSwipeListener {
    void onSwipe(boolean isSwipeLeft);
}
