package ch.epfl.ivrl.photopicker;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.text.format.DateUtils;
import android.widget.Button;
import android.widget.DatePicker;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.core.deps.guava.base.Predicates.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static java.util.EnumSet.allOf;

/**
 * Created by LogitechVR on 15.03.2016.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest
        extends ActivityInstrumentationTestCase2<MainActivity> {

    Activity mActivity;

    public MainActivityTest() {
        super(MainActivity.class);
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

    @Test
    public void testShowDatePickerDialog() throws Exception {
        // click on first da
        onView(withText("First day")).perform(click());

        // set date
        onView(withId(android.R.id.button1)).perform(click());

        //correct date
        Date now = new Date();
        String correctDate = DateUtils.formatDateTime (mActivity.getBaseContext(), now.getTime(), DateUtils.FORMAT_ABBREV_ALL);

        // check button text
        String buttonText = ((Button) mActivity.findViewById(R.id.start_date_button)).getText().toString();
        assertEquals(buttonText, correctDate);
    }

    @Test
    public void testChangeTheButton() throws Exception {

    }

    @Test
    public void testGoToPhotoSelection() throws Exception {

    }
}