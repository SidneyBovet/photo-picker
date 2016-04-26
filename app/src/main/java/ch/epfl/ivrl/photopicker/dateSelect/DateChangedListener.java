package ch.epfl.ivrl.photopicker.dateSelect;

import java.util.Calendar;

/**
 * Created by Sidney on 07.03.2016.
 */
public interface DateChangedListener {
    void changeTheButton(boolean isStartDate, Calendar newDate);
}
