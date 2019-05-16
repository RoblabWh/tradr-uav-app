package tradr.uav.app.utils;

/**
 * Created by tradr on 20.04.17.
 */

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;
import tradr.uav.app.UavApplication;

public class ToastUtils {
    private static Handler mUIHandler = new Handler(Looper.getMainLooper());

    public static void setResultToToast(final String string) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UavApplication.getInstance(), string, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void setResultToText(final TextView tv, final String s) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                if (tv == null) {
                    Toast.makeText(UavApplication.getInstance(), "tv is null", Toast.LENGTH_SHORT).show();
                } else {
                    tv.setText(s);
                }
            }
        });
    }

    public static void showToast(final Activity activity, final String msg) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showToast(final String string, final int duration) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(UavApplication.getInstance(), string, duration).show();
            }
        });
    }
}

