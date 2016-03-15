package ch.epfl.ivrl.photopicker.dateSelect;

import java.util.Date;

/**
 * Created by Sidney on 07.03.2016.
 */
public interface DateChangedListener {
    public void changeTheButton(boolean isStartDate, Date newDate);
}
