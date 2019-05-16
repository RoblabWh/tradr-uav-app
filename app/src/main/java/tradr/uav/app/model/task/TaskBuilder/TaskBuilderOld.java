package tradr.uav.app.model.task.TaskBuilder;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointAction;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.mission.waypoint.WaypointMissionFinishedAction;
import dji.common.mission.waypoint.WaypointMissionFlightPathMode;
import dji.common.mission.waypoint.WaypointMissionHeadingMode;
import dji.common.model.LocationCoordinate2D;
import tradr.uav.app.utils.ToastUtils;

/**
 * Created by Artur Leinweber on 04.05.17.
 */

public abstract class TaskBuilderOld implements Airworthy {


    private float altitude;
    private float speed;
    private LocationCoordinate2D pointOfInterest;
    protected List<WaypointAction> waypointActions;

    /**
     * Constant variable
     */
    protected static final int NOTHING = 0;


    private WaypointMissionFinishedAction actionAfterFinished;
    private WaypointMissionHeadingMode headingMode;
    private static WaypointMission.Builder waypointMissionBuilder;
    protected List<Waypoint> generatedWaypoints;

    /**
     * Constructor
     */
    public TaskBuilderOld(){
        this.waypointMissionBuilder = new WaypointMission.Builder();
        this.generatedWaypoints = new ArrayList<Waypoint>();
        this.waypointActions = new ArrayList<WaypointAction>();
        this.altitude = 30;
    }

    /**
     *
     */
    protected abstract void generateWaypointAction();

    /**
     *
     */
    protected abstract void checkForGeneratedWaypoints();

    /**
     * Returns the calculated waypoints for google maps
     * @return List of LatLng waypoint
     */
    @Override
    public List<LatLng> getWaypoints() {
        checkForGeneratedWaypoints();
        List<LatLng> googleMaps = new ArrayList<>();
        for(Waypoint wp : generatedWaypoints) {
            googleMaps.add(new LatLng(wp.coordinate.getLatitude(),wp.coordinate.getLongitude()));
        }
        return googleMaps;
    }

    /**
     * Set the altitude for all waypoints in a WaypointMission
     * @param altitude Altitude
     */
    public void setAltitude(float altitude) {

        this.altitude = altitude;

        if(!generatedWaypoints.isEmpty()) {
            for(Waypoint wp : generatedWaypoints) {
                wp.altitude = this.altitude;
            }
        }
    }

    /**
     * Return the altitude for all waypoint in a WaypointMission
     * @return Altitude
     */
    public float getAltitude() {
        return this.altitude;
    }


    /**
     * Sets the max Flight Speed and the auto Flight Speed in m/s.
     * @param speed Speed
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * Sets the Heading of the aircraft as it moves between waypoints
     * @param headingMode Heading Mode
     */
    protected void setHeadingMode(WaypointMissionHeadingMode headingMode) {
        this.headingMode = headingMode;
    }

    /**
     * Action the aircraft will take when the waypoint mission is complete.
     */
    public void setActionAfterFinished(WaypointMissionFinishedAction actionFinished) {
        this.actionAfterFinished = actionFinished;
    }

    /**
     * Aircraft heading will be fixed to the point of interest location during the waypoint
     * mission.
     * @param locationCoordinate2D point of interest
     */
    protected void setPointOfInterest(LocationCoordinate2D locationCoordinate2D) {
        this.pointOfInterest = locationCoordinate2D;
    }


    /**
     *
     */
    protected void addWaypointActionForEachWaypoint() {
        this.generateWaypointAction();

        for(Waypoint waypoint : this.generatedWaypoints) {
            for(WaypointAction waypointAction : waypointActions) {
                waypoint.addAction(waypointAction);
            }
        }
    }

    private void configAltitude() {
        for(Waypoint wp : generatedWaypoints) {
            wp.altitude = this.altitude;
        }
    }

    /**
     * Build the object into a WaypointMission object.
     * @return Way Point Mission
     * @throws Exception Nullpointer if something was not set from the user of this class.
     */
    public WaypointMission generateWaypointTask(){
        try {

            //this.configAltitude();
            //Keine Schleife
            checkForGeneratedWaypoints();

            //Keine Schleife
            this.addWaypointActionForEachWaypoint();


            /*Aircraft heading will be fixed to the point of interest location during the waypoint
             mission. */
            if(headingMode == WaypointMissionHeadingMode.TOWARD_POINT_OF_INTEREST) {
                waypointMissionBuilder.setPointOfInterest(this.pointOfInterest);
            }

            /*Action the aircraft will take when the waypoint mission is complete.*/
            waypointMissionBuilder.finishedAction(actionAfterFinished);

            /*Heading of the aircraft as it moves between waypoints.
              - AUTO	Aircraft's heading will always be in the direction of flight.
              - USING_INITIAL_DIRECTION	Aircraft's heading will be set to the initial
                take-off heading.
              - CONTROL_BY_REMOTE_CONTROLLER	Aircraft's heading will be controlled by the
                remote controller.
              - USING_WAYPOINT_HEADING	Aircraft's heading will be set to the previous
                waypoint's heading while travelling between waypoints.
              - TOWARD_POINT_OF_INTEREST	Aircraft's heading will always toward
                point of interest.*/
            waypointMissionBuilder.headingMode(headingMode);

            /*The base automatic speed of the aircraft as it moves between waypoints
            with range [-15, 15] m/s. The aircraft's actual speed is a combination of
            the base automatic speed, and the speed control given by the throttle joystick
            on the remote controller. If getAutoFlightSpeed >0: Actual speed is
            getAutoFlightSpeed + Joystick Speed (with combined max of getMaxFlightSpeed)
            If getAutoFlightSpeed =0: Actual speed is controlled only by the remote controller
            joystick. If getAutoFlightSpeed <0 and the aircraft is at the first waypoint, the
            aircraft will hover in place until the speed is made positive by the remote
            controller joystick.*/
            waypointMissionBuilder.autoFlightSpeed(speed);

            /*While the aircraft is traveling between waypoints, you can offset its speed by
            using the throttle joystick on the remote controller.
            For example, If maxFlightSpeed is 10 m/s, then pushing the throttle joystick all
            the way up will add 10 m/s to the aircraft speed, while
            pushing down will subtract 10 m/s from the aircraft speed.
            If the remote controller stick is not at maximum deflection, then the
            offset speed will be interpolated between [0, getMaxFlightSpeed] with a
            resolution of 1000 steps. If the offset speed is negative, then the aircraft will
            fly backwards to previous waypoints. */
            waypointMissionBuilder.maxFlightSpeed(speed);

            /*The flight path will be normal and the aircraft will move from one waypoint
             to the next in straight lines.*/
            waypointMissionBuilder.flightPathMode(WaypointMissionFlightPathMode.NORMAL);

            waypointMissionBuilder.waypointCount(this.generatedWaypoints.size());

            /*List of waypoints that will define this mission*/
            waypointMissionBuilder.waypointList(this.generatedWaypoints);




        }catch (Exception E) {
            ToastUtils.setResultToToast("TaskBuilderOld: " + E.getMessage());
        }
        ToastUtils.setResultToToast("WaypointCount:" + Integer.toString(waypointMissionBuilder.getWaypointCount()));
        ToastUtils.setResultToToast("AutoFlightSpeed:" + Float.toString(waypointMissionBuilder.getAutoFlightSpeed()));
        ToastUtils.setResultToToast("MaxFlightSpeed:"+Float.toString(waypointMissionBuilder.getMaxFlightSpeed()));
        /*Build the object into a WaypointMission object and return it.*/

        return this.waypointMissionBuilder.build();
    }


}
