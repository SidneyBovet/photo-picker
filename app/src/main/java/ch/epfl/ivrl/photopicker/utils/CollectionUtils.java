package ch.epfl.ivrl.photopicker.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Sidney on 01.06.2016.
 */
public class CollectionUtils {
    public static
    <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
        List<T> list = new ArrayList<T>(c);
        java.util.Collections.sort(list);
        return list;
    }
}
