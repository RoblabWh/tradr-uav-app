package tradr.uav.app.utils;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by tradr on 21.04.17.
 */

public class GeneralUtils {
    public static final double ONE_METER_OFFSET = 0.00000899322;
    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 800) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static boolean checkGpsCoordinate(double latitude, double longitude) {
        return (latitude > -90 && latitude < 90 && longitude > -180 && longitude < 180) && (latitude != 0f
                && longitude != 0f);
    }

    public static double toRadian(double x) {
        return x * Math.PI / 180.0;
    }

    public static double toDegree(double x) {
        return x * 180 / Math.PI;
    }

    public static double cosForDegree(double degree) {
        return Math.cos(degree * Math.PI / 180.0f);
    }

    public static double calcLongitudeOffset(double latitude) {
        return ONE_METER_OFFSET / cosForDegree(latitude);
    }

    public static void addLineToSB(StringBuffer sb, String name, Object value) {
        if (sb == null) return;
        sb.
                append(name == null ? "" : name + ": ").
                append(value == null ? "" : value + "").
                append("\n");
    }

    public static String nulltoIntegerDefalt(String value) {
        if(!isIntValue(value)) value="0";
        return value;
    }

    public static boolean isIntValue(String val) {
        try {
            val=val.replace(" ","");
            Integer.parseInt(val);
        } catch (Exception e) {return false;}
        return true;
    }

    public static void checkPermissions(Activity activity) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_PHONE_STATE);

        // When the compile and target version is higher than 22, please request the following permissions at runtime to ensure the SDK work well.
        if (permissionCheck != 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.VIBRATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.WAKE_LOCK,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.READ_PHONE_STATE,
            }, 1);
        }
    }
}
