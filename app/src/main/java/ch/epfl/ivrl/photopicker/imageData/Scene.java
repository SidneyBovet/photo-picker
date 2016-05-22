package ch.epfl.ivrl.photopicker.imageData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sidney on 30.03.2016.
 *
 * This class represents a group of photos showing the same point of view.
 * Multiple scenes constitute a Vacation.
 */
public class Scene implements Serializable {

    private List<Photograph> mPhotographs;

    public Scene() {
        mPhotographs = new ArrayList<Photograph>(1);
    }

    public void addPhoto(Photograph photo) {
        mPhotographs.add(photo);
    }

    public List<Photograph> getPhotographs() {
        return mPhotographs;
    }

    public String toString() {
        String ret = "Scene:\n";

        for (Photograph p: mPhotographs) {
            ret += "\t" + p.toString();
            ret += "\n";
        }

        return ret.substring(0, ret.lastIndexOf('\n'));
    }
}
