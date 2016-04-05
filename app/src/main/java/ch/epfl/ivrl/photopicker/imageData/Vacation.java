package ch.epfl.ivrl.photopicker.imageData;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.ivrl.photopicker.imageGrouping.ImageDistanceMetric;

/**
 * Created by Sidney on 30.03.2016.
 *
 * This class holds a list of Scenes and represents the topmost structure for classifying photographs.
 */
public class Vacation {
    private List<Scene> mScenes;

    public Vacation() {
        mScenes = new ArrayList<>(1);
    }

    public void addScene(Scene scene) {
        mScenes.add(scene);
    }
}
