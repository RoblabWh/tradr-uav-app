package tradr.uav.app.model.task.TaskBuilder;

import android.util.Log;

import com.google.maps.android.geometry.Point;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointAction;
import dji.common.mission.waypoint.WaypointActionType;
import dji.common.mission.waypoint.WaypointMissionHeadingMode;
import dji.common.model.LocationCoordinate2D;
import tradr.uav.app.utils.GPSUtils;

/**
 * Created by Artur Leinweber on 04.05.17.
 */

public class CircleTaskBuilderOld extends TaskBuilderOld {

    /**
     * Point of interest
     */
    private LatLng pointOfInterest;

    /**
     * Radius point
     */
    private LatLng radiusPoint;

    /**
     * Number of waypoints
     */
    private int numberOfWaypoints;

    private List<Integer> cameraPosition;

    /**
     * Constructor
     */
    public CircleTaskBuilderOld() {
        super();
        this.numberOfWaypoints = 36;
        this.cameraPosition = new ArrayList<>();
        cameraPosition.add(-60);
        Log.wtf("circle","Start: CircleTaskBuilderOld");
    }


    /**
     * Checks that the waypoints were calculated
     */
    protected void checkForGeneratedWaypoints() {
        if(this.generatedWaypoints.isEmpty()) {
            this.generateWaypoints();
        }else{
            super.generatedWaypoints.clear();
            this.generateWaypoints();
        }
    }

    /**
     * Rotates the gimbal's pitch. The actionParam value should be in range [-90, 0] degrees.
     * Starts to shoot a photo. The actionParam for the waypoint action will be ignored.
     * The maximum time set to execute this waypoint action is 6 seconds. If the time while
     * executing the waypoint action goes above 6 seconds, the aircraft will stop executing
     * the waypoint action and will move on to the next waypoint action, if one exists.
     * @param gimbalPitchAngles Array of Angles for the Gimbal that should
     *                          be approached for taking photos
     */
    public void setCameraPositions(Integer... gimbalPitchAngles) {
        cameraPosition.clear();
        for(Integer i : gimbalPitchAngles) {
            this.cameraPosition.add(i);
        }
    }

    @Override
    protected void generateWaypointAction() {
        for (Integer i :  this.cameraPosition) {
            waypointActions.add(new WaypointAction(WaypointActionType.GIMBAL_PITCH,i));
            waypointActions.add(new WaypointAction(WaypointActionType.START_TAKE_PHOTO,NOTHING));
        }
    }


    /**
     * Calculates a circle in WGS84 for the mission.
     */
    private void generateWaypoints() {
        Log.wtf("circle","Generate Circle");
        //Mercator Projection
        double radius = GPSUtils.calculateDistanceBetweenWGS84(this.pointOfInterest,this.radiusPoint);
        Point poi = GPSUtils.toPoint(this.pointOfInterest);

        //int stepSize = Math.round(360 / this.numberOfWaypoints);

        //Calculate step size
        double stepSize = 360.0 / this.numberOfWaypoints;

        for(double i = 0; i < 359.0; i+=stepSize) {
            double x = poi.x + (Math.cos(Math.toRadians(i)) * radius);
            double y = poi.y + (Math.sin(Math.toRadians(i)) * radius);
            LatLng poiToLatLng = GPSUtils.toLatLng(new Point(x,y));
            super.generatedWaypoints.add(new Waypoint(poiToLatLng.latitude,(poiToLatLng.longitude),super.getAltitude()));
        }
        Log.wtf("circle","Done");
    }

    /**
     * Sets the point of interest for the circle, the drone will always look in this direction.
     * @param poi Point of Interest (LatLng)
     */
    public void setPointOfInterest(LatLng poi){
        this.pointOfInterest = poi;
        super.setHeadingMode(WaypointMissionHeadingMode.TOWARD_POINT_OF_INTEREST);
        super.setPointOfInterest(new LocationCoordinate2D(this.pointOfInterest.latitude,this.pointOfInterest.longitude));
    }

    /**
     * Sets the radius point. This is needed for calculating circle radius.
     * @param radiusPoint Radius Point (LatLng)
     */
    public void  setRadiusPoint(LatLng radiusPoint) {
        this.radiusPoint = radiusPoint;
    }

    /**
     * Returns the radius of the circle.
     * @return Radius
     */
    public double getRadiusInMeters() {
        return SphericalUtil.computeDistanceBetween(this.pointOfInterest,this.radiusPoint);
    }

    /**
     * Returns the point of interest.
     * @return LatLng point of interest
     */
    public LatLng getPointOfInterest() {
        return this.pointOfInterest;
    }

    /**
     * Set the Number of Waypoints that should be reached and flight from the aircraft
     * @param numberOfWaypoints Number of waypoints
     */
    public void setNumberOfWaypoints(int numberOfWaypoints){
        this.numberOfWaypoints = numberOfWaypoints;
    }

    /**
     * Get the Number of waypoints that should be reached and flight from the aircraft
     * @return Number of waypoints
     */
    public int getNumberOfWaypoints() {
        return this.numberOfWaypoints;
    }
}
