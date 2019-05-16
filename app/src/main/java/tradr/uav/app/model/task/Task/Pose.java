package tradr.uav.app.model.task.Task;

import java.io.Serializable;

/**
 * Created by tradr on 14.10.17.
 */

public class Pose implements Serializable {
    private double longitude;
    private double latitude;
    private double altitude;

    private double yaw;
    private double pitch;
    private double roll;

    public Pose() {
        this.longitude = 0.0;
        this.latitude = 0.0;
        this.altitude = 0.0;

        this.roll = 0.0;
        this.pitch = 0.0;
        this.yaw = 0.0;
    }

    public Pose(double longitude, double latitude, double altitude, double yaw, double pitch, double roll) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;

        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public void setRoll(double roll) {
        this.roll = roll;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getAltitude() {
        return this.altitude;
    }

    public double getRoll() {
        return this.roll;
    }

    public double getPitch() {
        return this.pitch;
    }

    public double getYaw() {
        return this.yaw;
    }
}
