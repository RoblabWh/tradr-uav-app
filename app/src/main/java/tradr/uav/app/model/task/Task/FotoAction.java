package tradr.uav.app.model.task.Task;

import java.util.ArrayList;
import java.util.List;

import dji.common.mission.waypoint.WaypointAction;
import dji.common.mission.waypoint.WaypointActionType;

/**
 * Created by tradr on 07.08.17.
 */

public class FotoAction extends Action {

    private enum STATUS {

        EXECUTION_FINISHED,
        FOTO_TRANSMITTED
    }

    private int id;

    private STATUS status;

    private Pose cameraPose;

    public FotoAction(int actionId, int waypointId, int taskId) {
        super(actionId, waypointId, taskId);
        this.cameraPose = new Pose();
    }

    public FotoAction(int actionId, int waypointId, int taskId, Pose cameraPose) {
        super(actionId, waypointId, taskId);
        this.cameraPose = cameraPose;
    }

    public FotoAction(int actionId, int waypointId, int taskId, double longitude, double latitude, double altitude, double roll, double pitch, double yaw) {
        super(actionId, waypointId, taskId);
        this.cameraPose = new Pose(longitude, latitude, altitude, yaw, pitch, roll);
    }

    public void setCameraPose(double longitude, double latitude, double altitude, double roll, double pitch, double yaw) {
        this.cameraPose = new Pose(longitude, latitude, altitude, yaw, pitch, roll);
    }

    public void setCameraPose(Pose camPose) {
        this.cameraPose = camPose;
    }

    public void setLongitude(double longitude) {
        this.cameraPose.setLongitude(longitude);
    }

    public void setLatitude(double latitude) {
        this.cameraPose.setLatitude(latitude);
    }

    public void setAltitude(double altitude) {
        this.cameraPose.setAltitude(altitude);
    }

    public void setRoll(double roll) {
        this.cameraPose.setRoll(roll);
    }

    public void setPitch(double pitch) {
        this.cameraPose.setPitch(pitch);
    }

    public void setYaw(double yaw) {
        this.cameraPose.setYaw(yaw);
    }

    @Override
    public List<WaypointAction> getWaypointActions() {
        List<WaypointAction> actionList = new ArrayList<WaypointAction>(3);

        actionList.add(new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT, (int) this.cameraPose.getYaw()));
        actionList.add(new WaypointAction(WaypointActionType.GIMBAL_PITCH, (int) this.cameraPose.getPitch()));
        actionList.add(new WaypointAction(WaypointActionType.START_TAKE_PHOTO, 0));

        return actionList;
    }
}
