package ch.epfl.ivrl.photopicker.dateSelect;

import java.util.Calendar;

/**
 * Created by Sidney on 07.03.2016.
 *
 * Listens to a change in date.
 *
 * See implementation of MainActivity
 */
public interface DateChangedListener {
    void changeTheButton(boolean isStartDate, Calendar newDate);
}
