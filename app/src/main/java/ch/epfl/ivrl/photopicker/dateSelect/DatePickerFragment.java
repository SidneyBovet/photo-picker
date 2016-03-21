package ch.epfl.ivrl.photopicker.dateSelect;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Sidney on 07.03.2016.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private boolean isStartDate;
    DateChangedListener dcl;

    public boolean isStartDate() {
        return isStartDate;
    }

    public void setIsStartDate(boolean isStartDate) {
        this.isStartDate = isStartDate;
    }

    @Override
    public void onAttach (Activity theParentActivity) {
        dcl = (DateChangedListener) theParentActivity;
        super.onAttach(theParentActivity);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar returned = new GregorianCalendar(year,monthOfYear,dayOfMonth);

        dcl.changeTheButton(isStartDate, returned);
    }
}
