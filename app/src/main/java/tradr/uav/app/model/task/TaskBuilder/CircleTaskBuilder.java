package tradr.uav.app.model.task.TaskBuilder;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.geometry.Point;

import java.util.ArrayList;
import java.util.List;

import dji.common.mission.waypoint.WaypointAction;
import tradr.uav.app.model.task.Task.Action;
import tradr.uav.app.model.task.Task.FotoAction;
import tradr.uav.app.model.task.Task.Task;
import tradr.uav.app.model.task.Task.Waypoint;
import tradr.uav.app.utils.GPSUtils;

/**
 * Created by tradr on 29.10.17.
 */

public class CircleTaskBuilder {

    private LatLng pointOfInterest;

    private double radius;

    private List<Double> cameraAngleList;

    private double flightHeight;

    private int numberOfWaypoints;

    private double speed;


    public CircleTaskBuilder() {
        this.pointOfInterest = new LatLng(50.748953, 7.205702); // Fraunhofer IAIS Sankt Augustin
        this.radius = 20.0;
        this.cameraAngleList = new ArrayList<>();
        this.cameraAngleList.add(-60.0);
        this.flightHeight = 25.0;
        this.speed = 3.0;
        this.numberOfWaypoints = 36;
    }

    public void setPointOfInterest(LatLng pointOfInterest) {
        this.pointOfInterest = pointOfInterest;
    }

    public LatLng getPointOfInterest() {
        return this.pointOfInterest;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setRadius(LatLng radiusPoint) {
        this.radius = GPSUtils.calculateDistanceBetweenWGS84(this.pointOfInterest, radiusPoint);
    }

    public double getRadius() {
        return this.radius;
    }

    public void addCameraAngle(double cameraAngle) {
        this.cameraAngleList.add(cameraAngle);
    }

    public void setFlightHeight(double flightHeight) {
        this.flightHeight = flightHeight;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setNumberOfWaypoints(int numberOfWaypoints) {
        this.numberOfWaypoints = numberOfWaypoints;
    }

    public List<LatLng> getWaypoints() {
        Task task = this.build();

        List<LatLng> waypoints = new ArrayList<>(task.getNumberOfWaypoints());

        for (Waypoint waypoint : task.getWaypointList()) {
            waypoints.add(new LatLng(waypoint.getLatitude(), waypoint.getLongitude()));
        }

        return waypoints;
    }

    public Task build() {
        Task task = new Task(0);

        Point poi = GPSUtils.toPoint(this.pointOfInterest);

        //Calculate step size
        double stepSize = 360.0 / this.numberOfWaypoints;

        int n = 0;
        for (double i = 0; i < 359.0; i += stepSize) {
            double x = poi.x + (Math.cos(Math.toRadians(i)) * radius);
            double y = poi.y + (Math.sin(Math.toRadians(i)) * radius);
            LatLng position = GPSUtils.toLatLng(new Point(x,y));
            Waypoint waypoint = new Waypoint(n, 0);
            waypoint.setLatitude(position.latitude);
            waypoint.setLongitude(position.longitude);
            waypoint.setAltitude(this.flightHeight);
            waypoint.setYaw(GPSUtils.angleBetweenToWGS84(position, this.pointOfInterest));

            int m = 0;
            for (double cameraAngle : this.cameraAngleList) {
                FotoAction action = new FotoAction(m, n, 0);
                action.setLatitude(position.latitude);
                action.setLongitude(position.longitude);
                action.setAltitude(this.flightHeight);
                action.setYaw(GPSUtils.angleBetweenToWGS84(position, this.pointOfInterest));
                action.setPitch(cameraAngle);

                waypoint.addActionAtEnd(action);
                m++;
            }

            task.addWaypoint(waypoint);
            n++;
        }

        return task;
    }
}
