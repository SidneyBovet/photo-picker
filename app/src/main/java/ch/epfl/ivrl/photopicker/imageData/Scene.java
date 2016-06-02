package ch.epfl.ivrl.photopicker.imageData;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Sidney on 30.03.2016.
 *
 * This class represents a group of photos showing the same point of view.
 * Multiple scenes constitute a Vacation.
 */
public class Scene implements Serializable, Comparable {

    private final List<Photograph> mPhotographs;

    public Scene() {
        mPhotographs = new ArrayList<>(1);
    }

    public void addPhoto(Photograph photo) {
        mPhotographs.add(photo);
    }

    public void addPhotos(Collection<Photograph> photos) {
        mPhotographs.addAll(photos);
    }

    public List<Photograph> getPhotographs() {
        return mPhotographs;
    }

    public String toString() {
        String ret = "Scene ";

        for (Photograph p: mPhotographs) {
            ret += p.toString();
        }

        ret += "enecS ";

        return ret;
    }

    public float getMeanTime() {
        long sum = 0;
        for (Photograph p : mPhotographs) {
            long newSum = sum + p.getTime();
            if (newSum < sum)
                throw new ArithmeticException("Overflow in Mean time computation, consider reimplementing method.");

            sum = newSum;
        }

        return sum / mPhotographs.size();
    }

    @Override
    public int compareTo(@NonNull Object another) {
        if (another.getClass() != this.getClass()) {
            throw new IllegalArgumentException("Trying to compare two objects of different class types.");
        }

        Scene other = (Scene) another;

        float delta = this.getMeanTime() - other.getMeanTime();

        if (delta < 0)
            return -1;
        else if (delta > 0)
            return 1;
        else
            return 0;
    }
}
