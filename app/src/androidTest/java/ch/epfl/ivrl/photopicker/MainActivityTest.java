package ch.epfl.ivrl.photopicker;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.text.format.DateUtils;
import android.widget.Button;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import ch.epfl.ivrl.photopicker.activities.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

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
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_WEEKDAY | DateUtils.FORMAT_SHOW_YEAR;
        String correctDate = DateUtils.formatDateTime (mActivity.getBaseContext(), now.getTime(), flags);

        // check button text
        String buttonText = ((Button) mActivity.findViewById(R.id.start_date_button)).getText().toString();
        assertEquals(buttonText, correctDate);
    }
}