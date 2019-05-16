package tradr.uav.app;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import tradr.uav.app.model.uav.UAV;
import tradr.uav.app.utils.ToastUtils;


/**
 * DJI UavApplication
 */
public class UavApplication extends Application {

    public static final String TAG = UavApplication.class.getName();

    public static final String FLAG_CONNECTION_CHANGE = "dji_sdk_connection_change";


    private static UavApplication instance;

    private static UAV uav;

    public static String ipAddress;

    // @TODO: constructor fehlt eventuell???
    //private UavApplication () {};




    public static UavApplication getInstance() {
        if (instance == null) {
            instance = new UavApplication();
        }

        return instance;
    }


    public static UAV getUav() {

        if (uav == null) {
            uav = new UAV();
        }
        return uav;
    }



    @Override
    public void onCreate() {
        super.onCreate();

        /*
         * handles SDK Registration using the API_KEY
         */
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);


        if (permissionCheck == 0 || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //ToastUtils.showToast("Marke", Toast.LENGTH_LONG);

            uav = getUav();

        } else {
            ToastUtils.showToast("Error Connecting DJI...", Toast.LENGTH_LONG);
        }

        instance = this;
    }

    @Override
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }



}