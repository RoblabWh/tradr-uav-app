package tradr.uav.app.model.task.Task;

import java.io.Serializable;
import java.util.List;

import dji.common.mission.waypoint.WaypointAction;

/**
 * Created by tradr on 07.08.17.
 */

public abstract class Action implements Serializable {

    private int actionId;
    private int waypointId;
    private int taskId;

    public Action(int actionId, int waypointId, int taskId) {
        this.actionId = actionId;
        this.waypointId = waypointId;
        this.taskId = taskId;
    }

    public int getActionId() {
        return this.actionId;
    }

    public int getWaypointId() {
        return this.waypointId;
    }

    public int getTaskId() {
        return this.taskId;
    }

    abstract public List<WaypointAction> getWaypointActions();
}
