package ch.epfl.ivrl.photopicker.imageData;

import java.util.ArrayList;
import java.util.List;

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

    public String toString() {
        String ret = "=== Vacation ===";

        for (Scene s: mScenes) {
            ret += s.toString();
        }

        ret += "== ======== ===";
        return ret;
    }
}
