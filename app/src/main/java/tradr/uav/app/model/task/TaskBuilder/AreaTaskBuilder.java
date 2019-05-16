package tradr.uav.app.model.task.TaskBuilder;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.geometry.Point;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import tradr.uav.app.model.task.Task.FotoAction;
import tradr.uav.app.model.task.Task.Pose;
import tradr.uav.app.model.task.Task.Task;
import tradr.uav.app.model.task.Task.Waypoint;
import tradr.uav.app.model.uav.UAV;
import tradr.uav.app.utils.GPSUtils;
import tradr.uav.app.utils.MissionUtil;

/**
 * Created by tradr on 29.10.17.
 */

public class AreaTaskBuilder {

    public enum BuildMethod {
        BY_DISTANCE,
        BY_OVERLAP
    }

    private List<LatLng> polygon;

    private List<Pose> cameraPoseList;

    private double flightHeight;

    private double speed;

    private BuildMethod buildMethod;

    private double overlap;

    private double distance;

    public AreaTaskBuilder() {
        this.polygon = new ArrayList<>(10);
        this.cameraPoseList = new ArrayList<>(5);
        this.flightHeight = 25.0;
        this.overlap = 90.0;
        this.distance = 6.0;
    }

    public void setPolygon(List<LatLng> polygon) {
        this.polygon = polygon;
    }

    public void addCameraPose(Pose cameraPose) {
        this.cameraPoseList.add(cameraPose);
    }

    public void setFlightHeight(double flightHeight) {
        this.flightHeight = flightHeight;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setOverlap(double overlap) {
        this.overlap = overlap;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setBuildMethod(BuildMethod buildMethod) {
        this.buildMethod = buildMethod;
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

        Point bottomRightCorner = GPSUtils.toPoint(new LatLng(GPSUtils.maxLatitude(this.polygon), GPSUtils.minLongitude(this.polygon)));
        Point topLeftCorner = GPSUtils.toPoint(new LatLng(GPSUtils.minLatitude(this.polygon), GPSUtils.maxLongitude(this.polygon)));

        double distanceXAxis = Math.abs(topLeftCorner.x - bottomRightCorner.x);
        double distanceYAxis = Math.abs(topLeftCorner.y - bottomRightCorner.y);

        double distance;
        if (this.buildMethod == BuildMethod.BY_DISTANCE) {
            distance = this.distance;
        } else {
            distance = MissionUtil.getDistance(((double) this.overlap) / 100.0, UAV.APERATURE_ANGLE_IN_RAD, flightHeight);
        }

        List<LatLng> meander = new ArrayList<>();

        /* Create meander */
        for (double y = 0.0; y <= distanceYAxis; y += distance) {
            for (double x = 0.0; x <= distanceXAxis; x += distance) {
                meander.add(GPSUtils.toLatLng(new Point(topLeftCorner.x + x, topLeftCorner.y - y)));
                Log.d("MissionBuilder", "x: " + (topLeftCorner.x + x) + "   y: " + (topLeftCorner.y - y));
            }

            y += distance;

            for (double x = distanceXAxis; x >= 0.0; x -= distance) {
                meander.add(GPSUtils.toLatLng(new Point(topLeftCorner.x + x, topLeftCorner.y - y)));
                Log.d("MissionBuilder", "x: " + (topLeftCorner.x + x) + "   y: " + (topLeftCorner.y - y));
            }
        }

        Task task = new Task(0);
        int n = 0;
        for (LatLng point : meander) {
            Waypoint waypoint = new Waypoint(n, 0);
            waypoint.setLatitude(point.latitude);
            waypoint.setLongitude(point.longitude);
            waypoint.setAltitude(flightHeight);

            int m = 0;
            for (Pose cameraPose : this.cameraPoseList) {
                FotoAction action = new FotoAction(m, n, 0);
                action.setCameraPose(cameraPose);
                waypoint.addActionAtEnd(action);
                m++;
            }

            task.addWaypoint(waypoint);
            n++;
        }

        return task;

    }
}
