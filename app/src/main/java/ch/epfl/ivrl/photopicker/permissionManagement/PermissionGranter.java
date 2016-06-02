package ch.epfl.ivrl.photopicker.permissionManagement;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Sidney on 07.03.2016.
 *
 * Provides methods to check and request permissions.
 */
public class PermissionGranter {

    private final static String fPermission = Manifest.permission.READ_EXTERNAL_STORAGE;

    public static void askForPermission(Activity thisActivity) {
        if (!hasPermission(thisActivity)) {
            ActivityCompat.requestPermissions(
                    thisActivity,
                    new String[]{fPermission},
                    0
            );
        }
    }

    public static boolean hasPermission(Activity thisActivity) {
        return ContextCompat.checkSelfPermission(thisActivity, fPermission)
                == PackageManager.PERMISSION_GRANTED;
    }
}
