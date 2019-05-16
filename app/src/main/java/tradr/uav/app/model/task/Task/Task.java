package tradr.uav.app.model.task.Task;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import dji.common.mission.waypoint.WaypointMission;
import dji.common.mission.waypoint.WaypointMissionFinishedAction;
import dji.common.mission.waypoint.WaypointMissionFlightPathMode;
import dji.common.mission.waypoint.WaypointMissionHeadingMode;

/**
 * Created by tradr on 07.08.17.
 */

public class Task implements Serializable {

    private int taskId;

    private Pose pointOfInterest;

    private double flightSpeed;

    private List<Waypoint> waypointList;

    public Task(int taskId) {
        this.taskId = taskId;
        this.waypointList = new LinkedList<Waypoint>();
        this.pointOfInterest = new Pose(7.205702, 50.748953, 2.0, 0.0, 0.0, 0.0);
    }

    public int getTaskId() {
        return this.taskId;
    }

    public void addWaypoint(Waypoint waypoint) {
        this.waypointList.add(waypoint);
    }

    public Waypoint getWaypoint(int index) {
        return this.getWaypoint(index);
    }

    public int getNumberOfWaypoints() {
        return this.waypointList.size();
    }

    public List<Waypoint> getWaypointList() {
        return this.waypointList;
    }


    public WaypointMission getWaypointMission() {
        WaypointMission.Builder missionBuilder = new WaypointMission.Builder();
        for (Waypoint wp : this.getWaypointList()) {
            missionBuilder.addWaypoint(wp.getWaypoint());
        }

        /*
         * Action the aircraft will take when the waypoint mission is complete.
         */
        missionBuilder.finishedAction(WaypointMissionFinishedAction.GO_HOME);

        /*
         * Heading of the aircraft as it moves between waypoints.
         *  - AUTO                           Aircraft's heading will always be in the direction of flight.
         *  - USING_INITIAL_DIRECTION	     Aircraft's heading will be set to the initial take-off heading.
         *  - CONTROL_BY_REMOTE_CONTROLLER   Aircraft's heading will be controlled by the remote controller.
         *  - USING_WAYPOINT_HEADING         Aircraft's heading will be set to the previous waypoint's heading while travelling between waypoints.
         *  - TOWARD_POINT_OF_INTEREST       Aircraft's heading will always toward point of interest.
         */
        missionBuilder.headingMode(WaypointMissionHeadingMode.AUTO);

        /*
         * The base automatic speed of the aircraft as it moves between waypoints
         * with range [-15, 15] m/s. The aircraft's actual speed is a combination of
         * the base automatic speed, and the speed control given by the throttle joystick
         * on the remote controller. If getAutoFlightSpeed >0: Actual speed is
         * getAutoFlightSpeed + Joystick Speed (with combined max of getMaxFlightSpeed)
         * If getAutoFlightSpeed =0: Actual speed is controlled only by the remote controller
         * joystick. If getAutoFlightSpeed <0 and the aircraft is at the first waypoint, the
         * aircraft will hover in place until the speed is made positive by the remote
         * controller joystick.
         */
        missionBuilder.autoFlightSpeed(2.0f);

        /*
         * While the aircraft is traveling between waypoints, you can offset its speed by
         * using the throttle joystick on the remote controller.
         * For example, If maxFlightSpeed is 10 m/s, then pushing the throttle joystick all
         * the way up will add 10 m/s to the aircraft speed, while
         * pushing down will subtract 10 m/s from the aircraft speed.
         * If the remote controller stick is not at maximum deflection, then the
         * offset speed will be interpolated between [0, getMaxFlightSpeed] with a
         * resolution of 1000 steps. If the offset speed is negative, then the aircraft will
         * fly backwards to previous waypoints.
         */
        missionBuilder.maxFlightSpeed(2.0f);

        /*
         * The flight path will be normal and the aircraft will move from one waypoint to the next in straight lines.
         */
        missionBuilder.flightPathMode(WaypointMissionFlightPathMode.NORMAL);

        missionBuilder.setGimbalPitchRotationEnabled(true);

        return missionBuilder.build();
    }





}
