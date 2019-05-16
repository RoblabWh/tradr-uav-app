package tradr.uav.app.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import tradr.uav.app.UavApplication;


import dji.common.error.DJIError;
import dji.log.DJILog;
import dji.sdk.base.BaseProduct;
import dji.sdk.sdkmanager.DJISDKManager;

import tradr.uav.app.model.uav.UAV;
import tradr.uav.app.utils.GeneralUtils;


public class MainActivity extends AppCompatActivity {

    protected static final String TAG = "uavapp";

    private MainView mainView;

    UAV uav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GeneralUtils.checkPermissions(this);

        mainView = new MainView(this);

        mainView.refreshSDKRelativeUI();

        uav = UavApplication.getUav();

        uav.addDroneConnectionListener(new UAV.DroneConnectionListener() {
            @Override
            public void onConnectivityChanged(final boolean isConnected) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        on_uav_connectivityChanged(isConnected);
                    }
                });
            }
        });

        uav.init();

    }


    private void on_uav_connectivityChanged(boolean isConnected) {
        mainView.refreshSDKRelativeUI();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        DJISDKManager.getInstance().registerApp(this, new DJISDKManager.SDKManagerCallback() {

            @Override
            public void onRegister(DJIError djiError) {
                DJILog.e("App registration", djiError == null ? "null" : djiError.getDescription());
            }

            @Override
            public void onProductChange(BaseProduct baseProduct, BaseProduct baseProduct1) {
                // DO nothing.
            }

        });
    }




    public void switchToMapActivity() {

        startActivity(new Intent(this, tradr.uav.app.activities.map.MapActivity.class));
        this.finish();
    }

    public void  switchToDebugActivity() {

        startActivity(new Intent(this, tradr.uav.app.activities.debug.DebugActivtity.class));
    }

}