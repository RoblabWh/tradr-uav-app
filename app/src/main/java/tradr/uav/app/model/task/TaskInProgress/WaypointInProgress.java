package tradr.uav.app.model.task.TaskInProgress;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tradr.uav.app.model.task.Task.Action;
import tradr.uav.app.model.task.Task.Waypoint;

/**
 * Created by tradr on 29.10.17.
 */

public class WaypointInProgress {

    public enum Status {
        WAIT_FOR_EXECUTION,
        EXECUTION_FLYING_TO_WAYPOINT,
        EXECUTION_DOING_ACTIONS,
        FINISHED,
        CANCELED
    }


    private Status status;

    private Waypoint waypoint;

    private List<ActionInProgress> actionInProgressList;

    public WaypointInProgress(Waypoint waypoint) {
        this.status = Status.WAIT_FOR_EXECUTION;
        this.waypoint = waypoint;
        this.actionInProgressList = new ArrayList<>(15);

        this.statusChangedListenerSet = new HashSet<>();

        for (Action action : waypoint.getActionList()) {
            this.actionInProgressList.add(new ActionInProgress(action));
        }
    }



    public Waypoint getWaypoint() {
        return this.waypoint;
    }

    public Status getStatus() {
        return this.status;
    }



    public ActionInProgress getWaypointInProgress(int actionId) {
        return this.actionInProgressList.get(actionId);
    }

    public List<ActionInProgress> getActionInProgressList() {
        return this.actionInProgressList;
    }



    public void flyingStarts() {
        if (Status.EXECUTION_FLYING_TO_WAYPOINT != this.status) {
            this.status = Status.EXECUTION_FLYING_TO_WAYPOINT;
            this.emit_statusChangedListener_statusChanged(this.waypoint.getTaskId(), this.waypoint.getWaypointId(), Status.EXECUTION_FLYING_TO_WAYPOINT);
        }
    }

    public void doingActionsStarts() {
        if (Status.EXECUTION_DOING_ACTIONS != this.status) {
            this.status = Status.EXECUTION_DOING_ACTIONS;
            this.emit_statusChangedListener_statusChanged(this.waypoint.getTaskId(), this.waypoint.getWaypointId(), Status.EXECUTION_DOING_ACTIONS);
        }
    }

    public void executionFinished() {
        if (Status.FINISHED != this.status) {
            this.status = Status.FINISHED;
            this.emit_statusChangedListener_statusChanged(this.waypoint.getTaskId(), this.waypoint.getWaypointId(), Status.FINISHED);
        }
    }

    public void executionCanceledByError() {
        if (Status.CANCELED != this.status) {
            this.status = Status.CANCELED;
            this.emit_statusChangedListener_statusChanged(this.waypoint.getTaskId(), this.waypoint.getWaypointId(), Status.CANCELED);
        }
    }



    /* StatusChangedListener */
    private Set<StatusChangedListener> statusChangedListenerSet;

    public interface StatusChangedListener {
        void onStatusChanged(int taskId, int waypointID, Status newStatus);
    }

    public void addStatusChangedListener(StatusChangedListener statusChangedListener) {
        synchronized (this.statusChangedListenerSet) {
            this.statusChangedListenerSet.add(statusChangedListener);
        }
    }

    public void removeStatusChangedListener(StatusChangedListener statusChangedListener) {
        synchronized (this.statusChangedListenerSet) {
            this.statusChangedListenerSet.remove(statusChangedListener);
        }
    }

    private void emit_statusChangedListener_statusChanged(int taskId, int waypointID, Status newStatus) {
        synchronized (this.statusChangedListenerSet) {
            for (StatusChangedListener listener : this.statusChangedListenerSet) {
                listener.onStatusChanged(taskId, waypointID, newStatus);
            }
        }
    }
}
