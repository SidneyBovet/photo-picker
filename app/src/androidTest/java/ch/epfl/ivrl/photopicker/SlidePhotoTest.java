package ch.epfl.ivrl.photopicker;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.ivrl.photopicker.activities.SlidePhoto;

/**
 * Created by Sidney on 15.03.2016.
 */
@RunWith(AndroidJUnit4.class)
public class SlidePhotoTest
        extends ActivityInstrumentationTestCase2<SlidePhoto> {

    Activity mActivity;

    public SlidePhotoTest() {
        super(SlidePhoto.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();

        // Injecting the Instrumentation instance is required
        // for your test to run with AndroidJUnitRunner.
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = getActivity();
    }

    @Test
    public void testActivityExists() {
        assertNotNull(mActivity);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }
}