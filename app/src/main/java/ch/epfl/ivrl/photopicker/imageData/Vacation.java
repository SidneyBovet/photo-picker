package ch.epfl.ivrl.photopicker.imageData;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Sidney on 30.03.2016.
 *
 * This class holds a list of Scenes and represents the topmost structure for classifying photographs.
 */
public class Vacation implements Serializable {
    private List<Scene> mScenes;

    public Vacation() {
        mScenes = new ArrayList<>(1);
    }

    public void addScene(Scene scene) {
        mScenes.add(scene);
    }

    public void addScenes(Collection<Scene> scenes) {
        mScenes.addAll(scenes);
    }

    public Scene getScene(int position) {
        return mScenes.get(position);
    }

    public boolean removeScene(Scene s) {
        return mScenes.remove(s);
    }

    public int getCount() {
        return mScenes.size();
    }

    public String toString() {
        String ret = "Vacation ";

        for (Scene s: mScenes) {
            ret += s.toString();
        }

        ret += "noitacaV ";
        return ret;
    }
}
