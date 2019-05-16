package tradr.uav.app.model.task.Task;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dji.common.mission.waypoint.WaypointAction;
import dji.common.mission.waypoint.WaypointTurnMode;

/**
 * Created by tradr on 07.08.17.
 */

public class Waypoint implements Serializable {

    private int waypointId;
    private int taskId;

    private List<Action> actionList;

    private Pose pose;

    public Waypoint(int waypointId, int taskId) {
        this.waypointId = waypointId;
        this.taskId = taskId;
        this.pose = new Pose();
        this.actionList = new ArrayList<Action>(20);
    }

    public Waypoint(int waypointId, int taskId, Pose pose, List<Action> actionList) {
        this.waypointId = waypointId;
        this.taskId = taskId;
        this.pose = pose;
        this.actionList = actionList;
    }

    public Waypoint(int waypointId, int taskId, Pose pose) {
        this.waypointId = waypointId;
        this.taskId = taskId;
        this.pose = pose;
        this.actionList = new ArrayList<Action>(20);
    }

    public int getWaypointId() {
        return this.waypointId;
    }

    public int getTaskId() {
        return this.taskId;
    }

    public void setPose(Pose pose) {
        this.pose = pose;
    }

    public Pose getPose() {
        return this.pose;
    }

    public void setLongitude(double longitude) {
        this.pose.setLongitude(longitude);
    }

    public void setLatitude(double latitude) {
        this.pose.setLatitude(latitude);
    }

    public void setAltitude(double altitude) {
        this.pose.setAltitude(altitude);
    }

    public void setRoll(double roll) {
        this.pose.setRoll(roll);
    }

    public void setPitch(double pitch) {
        this.pose.setPitch(pitch);
    }

    public void setYaw(double yaw) {
        this.pose.setYaw(yaw);
    }

    public double getLongitude() {
        return this.pose.getLongitude();
    }

    public double getLatitude() {
        return this.pose.getLatitude();
    }

    public double getAltitude() {
        return this.pose.getAltitude();
    }

    public double getRoll() {
        return this.pose.getRoll();
    }

    public double getPitch() {
        return this.pose.getPitch();
    }

    public double getYaw() {
        return this.pose.getYaw();
    }

    public void setActionList(List<Action> actionList) {
        this.actionList = actionList;
    }

    public List<Action> getActionList() {
        return this.actionList;
    }

    public void addActionAtBegin(Action action) {
        this.actionList.add(0, action);
    }

    public void addActionAtEnd(Action action) {
        this.actionList.add(this.actionList.size(), action);
    }

    public void addActionAt(Action action, int index) {
        this.actionList.add(index, action);
    }

    public Action getAction(int index) {
        return this.actionList.get(index);
    }

    public dji.common.mission.waypoint.Waypoint getWaypoint() {
        dji.common.mission.waypoint.Waypoint waypoint = new dji.common.mission.waypoint.Waypoint(this.pose.getLatitude(), this.pose.getLongitude(), (float) this.pose.getAltitude());
        waypoint.heading = (int) this.getYaw();
        waypoint.gimbalPitch = (float) this.getPitch();
        for (Action action : this.getActionList()) {
            List<WaypointAction> waypointActions = action.getWaypointActions();
            for (WaypointAction waypointAction : waypointActions) {
                waypoint.addAction(waypointAction);
            }
        }
        waypoint.turnMode = WaypointTurnMode.CLOCKWISE;

        if (waypoint.checkParameters() != null) {
            Log.e("MissionBuilder", waypoint.checkParameters().toString());
        } else {
            Log.d("MissionBuilder", "New Waypoint: " + waypoint.waypointActions.size());
        }

        return waypoint;
    }


}
