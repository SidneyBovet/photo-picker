package ch.epfl.ivrl.photopicker.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Sidney on 01.06.2016.
 *
 * Provides helpers for showing ProgressDialogs.
 */
public class ProgressDialogUtils {
    public static void showProgressFor(Context ctx, final long duration, String message) {

        final ProgressDialog progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        Thread tProgress = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        });
        tProgress.start();
    }
}
