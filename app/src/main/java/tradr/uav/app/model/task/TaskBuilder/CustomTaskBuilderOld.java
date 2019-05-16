package tradr.uav.app.model.task.TaskBuilder;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointAction;
import dji.common.mission.waypoint.WaypointActionType;

/**
 * Created by Artur Leinweber on 08.05.17.
 */

public class CustomTaskBuilderOld extends TaskBuilderOld {


    /**
     * Constructor
     */
    public CustomTaskBuilderOld(){
        super();
    }

    private List<Integer> cameraPosition;


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
        for(Integer i : gimbalPitchAngles) {
            this.cameraPosition.add(i);
        }
    }

    @Override
    protected void generateWaypointAction() {
        List<WaypointAction> waypointActions = new ArrayList<>();

        for (Integer i :  this.cameraPosition) {
            waypointActions.add(new WaypointAction(WaypointActionType.GIMBAL_PITCH,i));
            waypointActions.add(new WaypointAction(WaypointActionType.START_TAKE_PHOTO,NOTHING));
        }
    }

    /**
     *
     */
    @Override
    protected void checkForGeneratedWaypoints() {
       //Nothing
    }


    /**
     * Adds a waypoint to a mission.
     * @param waypoint LatLng Coordinate
     */
    public void addWaypoint(LatLng waypoint) {
        super.generatedWaypoints.add(new Waypoint(waypoint.latitude,waypoint.longitude,super.getAltitude()));
    }



}
