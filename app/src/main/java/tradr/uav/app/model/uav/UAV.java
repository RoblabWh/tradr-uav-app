package tradr.uav.app.model.uav;

import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.log.DJILog;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

import tradr.uav.app.R;
import tradr.uav.app.UavApplication;
import tradr.uav.app.model.uav.profile.UAVProfile;
import tradr.uav.app.model.uav.profile.UAVProfileManager;
import tradr.uav.app.utils.ToastUtils;

/**
 * Created by tradr on 25.04.17.
 */

public class UAV {

    public static final String TAG = UavApplication.class.getName();

    public static final double APERATURE_ANGLE_IN_DEG = 78.8;
    public static final double APERATURE_ANGLE_IN_RAD = 1.375319451;

    public static final int MAX_PITCH_ANGLE = 0;
    public static final int MIN_PITCH_ANGLE = -90;

    public static final int MAX_NUMBER_OF_WAYPOINTS = 99;
    public static final int MIN_NUMBER_OF_WAYPOINTS = 5;

    public static final int MAX_FLIGHT_SPEED_M_PER_S = 15;
    public static final int MIN_FLIGHT_SPEED_M_PER_S = 2;

    public static final int MAX_ALTITUDE = 50;
    public static final int MIN_ALTITUDE = -25;


    /* SDK */
    private DJISDKManager sdkManager;

    /* Aircraft */
    private Aircraft aircraft;

    /* Components */
    //private Camera camera;
    //private Gimbal gimbal;
    //private FlightController flightController;




    //private static BluetoothProductConnector bluetoothConnector = null;

    @Nullable
    public Aircraft getAircraftInstance() {
        if (isAircraftConnected()) {
            return aircraft;
        } else {
            return null;
        }
    }


    @Nullable
    /*
    public synchronized Camera getCameraInstance() {
        if (camera != null) {
            return camera;
        } else {
            return null;
        }
    }


    @Nullable
    public synchronized Gimbal getGimbalInstance() {
        if(gimbal != null) {
            return gimbal;
        } else {
            return null;
        }
    }
    */



    /**
     * Gets instance of the specific product connected after the
     * API KEY is successfully validated. Please make sure the
     * API_KEY has been added in the Manifest
     */
    /*
    public static synchronized BaseProduct getProductInstance() {
        if (null == ) {
            product = DJISDKManager.getInstance().getProduct();
        }
        return product;
    }
    */


    /*
    public static synchronized BluetoothProductConnector getBluetoothProductConnector() {
        if (null == bluetoothConnector) {
            bluetoothConnector = DJISDKManager.getInstance().getBluetoothProductConnector();
        }
        return bluetoothConnector;
    }
    */


    public boolean isAircraftConnected() {
        return aircraft != null && aircraft.isConnected()
                && flightController != null
                && battery != null
                && camera != null
                && gimbal != null
                && liveStream != null
                && taskOperator != null;
    }






    public UAV() {
        this.droneConnectionListenerSet = new HashSet<>();

        sdkManager = DJISDKManager.getInstance();
    }


    public void init() {
        registerSDKManagerListener();
    }



    public String getModelName() {
        String modelName = new String("unknown Model");

        if (this.isAircraftConnected()) {
            modelName = this.aircraft.getModel().getDisplayName();
        }

        return modelName;
    }


    private void registerSDKManagerListener() {
        sdkManager.registerApp(UavApplication.getInstance(), new DJISDKManager.SDKManagerCallback() {
            @Override
            public void onRegister(DJIError error) {
                on_SDKManager_register(error);
            }

            @Override
            public void onProductChange(BaseProduct oldProduct, BaseProduct newProduct) {
                on_SDKManager_aircraftChanged(oldProduct, newProduct);
            }
        });
    }

    private void registerAircraftListener() {
        aircraft.setBaseProductListener(new BaseProduct.BaseProductListener() {
            @Override
            public void onComponentChange(BaseProduct.ComponentKey key, BaseComponent oldComponent, BaseComponent newComponent) {
                on_Aircraft_componentChanged(key, oldComponent, newComponent);
            }

            @Override
            public void onConnectivityChange(boolean isConnected) {
                on_Aircraft_connectivityChanged(isConnected);
            }
        });
    }

    private void registerComponentListener(BaseComponent component) {
        component.setComponentListener(new BaseComponent.ComponentListener() {
            @Override
            public void onConnectivityChange(boolean isConnected) {
                on_Component_connectivityChanged(isConnected);
            }
        });
    }




    /* Callback */

    private void on_SDKManager_register(DJIError error) {
        if (error == DJISDKError.REGISTRATION_SUCCESS) {
            DJILog.e("App registration", DJISDKError.REGISTRATION_SUCCESS.getDescription());
            DJISDKManager.getInstance().startConnectionToProduct();
        } else {
            ToastUtils.showToast(UavApplication.getInstance().getResources().getString(R.string.sdk_registration_message),Toast.LENGTH_LONG);
        }
        Log.v(TAG, error.getDescription());
    }

    private void on_SDKManager_aircraftChanged(BaseProduct oldAircraft, BaseProduct newAircraft) {

        Log.d(TAG, String.format("onProductChanged oldProduct:%s, newProduct:%s", oldAircraft, newAircraft));

        if (newAircraft != null && newAircraft instanceof Aircraft) {
            aircraft = (Aircraft) newAircraft;

                registerAircraftListener();
                //ToastUtils.setResultToToast("Aircraft changed: Product not null");

            if (aircraft.isConnected()) {
                flightController = new UAVFlightController(aircraft);
                camera = new UAVCamera(aircraft);
                gimbal = new UAVGimbal(aircraft);
                battery = new UAVBattery(aircraft);
                liveStream = new UAVLiveStream(aircraft);
                taskOperator = new UAVTaskOperator(aircraft, this);
                profile = new UAVProfile(aircraft);

                emit_droneConnectionListener_connectivityChanged(true);
            }


        } else {
            emit_droneConnectionListener_connectivityChanged(false);
            //ToastUtils.setResultToToast("Aircraft changed: Product null");
        }

    }


    private void on_Aircraft_componentChanged(BaseProduct.ComponentKey key, BaseComponent oldComponent, BaseComponent newComponent) {

        if (aircraft.isConnected()) {
            profile = (new UAVProfileManager()).getUavProfile(aircraft.getModel());
            flightController = new UAVFlightController(aircraft);
            camera = new UAVCamera(aircraft);
            gimbal = new UAVGimbal(aircraft);
            battery = new UAVBattery(aircraft);
            liveStream = new UAVLiveStream(aircraft);
            taskOperator = new UAVTaskOperator(aircraft, this);
            profile = new UAVProfile(aircraft);

            emit_droneConnectionListener_connectivityChanged(true);
        }

        Log.d(TAG, String.format("onComponentChange key:%s, oldComponent:%s, newComponent:%s", key, oldComponent, newComponent));

        if (oldComponent != null) {
            //ToastUtils.setResultToToast("oldComponent: " + oldComponent.toString());
        } else {
            //ToastUtils.setResultToToast("oldComponent: null");
        }


        if (newComponent != null) {
            //ToastUtils.setResultToToast("newComponent: " + newComponent.toString());
        } else {
            //ToastUtils.setResultToToast("newComponent: null");
        }


        if (newComponent != null) {
            registerComponentListener(newComponent);

            if (newComponent instanceof FlightController) {

            } else if (newComponent instanceof Camera) {

            }
        }

        //ToastUtils.setResultToToast("Component changed");
        //emit_connectivityChangeListener_connectivityChanged(false);
    }

    private void on_Aircraft_connectivityChanged(boolean isConnected) {
        Log.d(TAG, "onProductConnectivityChanged: " + isConnected);
        emit_droneConnectionListener_connectivityChanged(isConnected);
        //ToastUtils.setResultToToast("Product Connection changed");
    }


    private void on_Component_connectivityChanged(boolean isConnected) {
        Log.d(TAG, "onComponentConnectivityChanged: " + isConnected);
        //ToastUtils.setResultToToast("Component Connection changed");
        //emit_connectivityChangeListener_connectivityChanged(isConnected);
    }




    /* DroneConnectionListener */

    private Set<DroneConnectionListener> droneConnectionListenerSet;

    public interface DroneConnectionListener {
        void onConnectivityChanged(boolean isConnected);
    }

    public void addDroneConnectionListener(DroneConnectionListener droneConnectionListener) {
        synchronized (this.droneConnectionListenerSet) {
            this.droneConnectionListenerSet.add(droneConnectionListener);
        }
    }

    public void removeDroneConnectionListener(DroneConnectionListener droneConnectionListener) {
        synchronized (this.droneConnectionListenerSet) {
            this.droneConnectionListenerSet.remove(droneConnectionListener);
        }
    }

    private void emit_droneConnectionListener_connectivityChanged(boolean isConnected) {
        synchronized (this.droneConnectionListenerSet) {
            for (DroneConnectionListener listener : this.droneConnectionListenerSet) {
                listener.onConnectivityChanged(isConnected);
            }
        }
    }





    public UAVFlightController flightController;

    public UAVCamera camera;

    public UAVGimbal gimbal;

    public UAVBattery battery;

    public UAVLiveStream liveStream;

    public UAVTaskOperator taskOperator;

    public UAVProfile profile;

}
