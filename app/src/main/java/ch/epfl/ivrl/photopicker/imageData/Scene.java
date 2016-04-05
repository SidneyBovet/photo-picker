package ch.epfl.ivrl.photopicker.imageData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sidney on 30.03.2016.
 *
 * This class represents a group of photos showing the same point of view.
 * Multiple scenes constitute a Vacation.
 */
public class Scene {

    private List<Photograph> mPhotographs;

    public Scene() {
        mPhotographs = new ArrayList<Photograph>(1);
    }

    public void addPhoto(Photograph photo) {
        mPhotographs.add(photo);
    }
}
