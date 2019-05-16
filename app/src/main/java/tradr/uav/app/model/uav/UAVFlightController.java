package tradr.uav.app.model.uav;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashSet;
import java.util.Set;

import dji.common.flightcontroller.FlightControllerState;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;

/**
 * Created by tradr on 29.10.17.
 */

public class UAVFlightController {
    private FlightController flightController;

    public enum State {
        FLYING,
        GOING_HOME
    }

    private State state;

    public UAVFlightController(Aircraft aircraft) {
        this.dronePoseListenerSet = new HashSet<>();
        this.stateListenerSet = new HashSet<>();

        this.flightController = aircraft.getFlightController();

        this.flightController.setStateCallback(new FlightControllerState.Callback() {
            @Override
            public void onUpdate(@NonNull FlightControllerState flightControllerState) {
                on_flightController_update(flightControllerState);
            }
        });
    }

    private void on_flightController_update(FlightControllerState flightControllerState) {
        double latitude = flightControllerState.getAircraftLocation().getLatitude();
        double longitude = flightControllerState.getAircraftLocation().getLongitude();
        double altitude = flightControllerState.getAircraftLocation().getAltitude();

        double roll = flightControllerState.getAttitude().roll;
        double pitch = flightControllerState.getAttitude().pitch;
        double yaw = flightControllerState.getAttitude().yaw;

        double velX = flightControllerState.getVelocityX();
        double velY = flightControllerState.getVelocityY();
        double velZ = flightControllerState.getVelocityZ();

        emit_dronePoseListener_poseChanged(latitude, longitude, altitude, roll, pitch, yaw, velX, velY, velZ);

        if (flightControllerState.isGoingHome()) {
            if (this.state != State.GOING_HOME) {
                emit_stateListener_stateChanged(State.GOING_HOME);
            }
            this.state = State.GOING_HOME;
        } else {
            if (this.state != State.FLYING) {
                emit_stateListener_stateChanged(State.FLYING);
            }
            this.state = State.FLYING;
        }
    }

    public FlightController getFlightController() {
        return flightController;
    }

    public LatLng getPosition() {
        double latitude = flightController.getState().getAircraftLocation().getLatitude();
        double longitude = flightController.getState().getAircraftLocation().getLongitude();

        return new LatLng(latitude, longitude);
    }



    /* DronePoseListener */
    private Set<DronePoseListener> dronePoseListenerSet;

    public interface DronePoseListener {
        void onPoseChanged(double latitude, double longitude, double altitude, double roll, double pitch, double yaw, double velX, double velY, double velZ);
    }

    public void addDronePoseListener(DronePoseListener dronePoseListener) {
        synchronized (this.dronePoseListenerSet) {
            this.dronePoseListenerSet.add(dronePoseListener);
            Log.d("PoseListenerAdress", "add: " + dronePoseListener.toString());
        }
    }

    public void removeDronePoseListener(DronePoseListener dronePoseListener) {
        synchronized (this.dronePoseListenerSet) {
            this.dronePoseListenerSet.remove(dronePoseListener);
            Log.d("PoseListenerAdress", "add: " + dronePoseListener.toString());
        }
    }

    private void emit_dronePoseListener_poseChanged(double latitude, double longitude, double altitude, double roll, double pitch, double yaw, double velX, double velY, double velZ) {
        synchronized (this.dronePoseListenerSet) {
            for (DronePoseListener listener : this.dronePoseListenerSet) {
                listener.onPoseChanged(latitude, longitude, altitude, roll, pitch, yaw, velX, velY, velZ);
            }
        }
    }



    /* StateListener */
    private Set<StateListener> stateListenerSet;

    public interface StateListener {
        void onStateChanged(State state);
    }

    public void addStateListener(StateListener stateListener) {
        synchronized (this.stateListenerSet) {
            this.stateListenerSet.add(stateListener);
        }
    }

    public void removeStateListener(StateListener stateListener) {
        synchronized (this.stateListenerSet) {
            this.stateListenerSet.remove(stateListener);
        }
    }

    private void emit_stateListener_stateChanged(State state) {
        synchronized (this.stateListenerSet) {
            for (StateListener listener : this.stateListenerSet) {
                listener.onStateChanged(state);
            }
        }
    }

}