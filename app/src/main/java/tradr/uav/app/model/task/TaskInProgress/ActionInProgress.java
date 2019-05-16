package tradr.uav.app.model.task.TaskInProgress;

import java.util.HashSet;
import java.util.Set;

import tradr.uav.app.model.task.Task.Action;

/**
 * Created by tradr on 29.10.17.
 */

public class ActionInProgress {

    public enum Status {
        WAIT_FOR_EXECUTION,
        EXECUTION,
        FINISHED,
        CANCELED
    }

    private Status status;

    private Action action;



    public ActionInProgress(Action action) {
        this.status = Status.WAIT_FOR_EXECUTION;
        this.action = action;

        this.statusChangedListenerSet = new HashSet<>();
    }



    public Action getAction() {
        return this.action;
    }

    public Status getStatus() {
        return this.status;
    }



    public void executionStarts() {
        if (Status.EXECUTION != this.status) {
            this.status = Status.EXECUTION;
            this.emit_statusChangedListener_statusChanged(this.action.getTaskId(), this.action.getWaypointId(), this.action.getActionId(), Status.EXECUTION);
        }
    }

    public void executionFinished() {
        if (Status.FINISHED != this.status) {
            this.status = Status.FINISHED;
            this.emit_statusChangedListener_statusChanged(this.action.getTaskId(), this.action.getWaypointId(), this.action.getActionId(), Status.FINISHED);
        }
    }

    public void executionCanceledByError() {
        if (Status.CANCELED != this.status) {
            this.status = Status.CANCELED;
            this.emit_statusChangedListener_statusChanged(this.action.getTaskId(), this.action.getWaypointId(), this.action.getActionId(), Status.CANCELED);
        }
    }



    /* StatusChangedListener */
    private Set<StatusChangedListener> statusChangedListenerSet;

    public interface StatusChangedListener {
        void onStatusChanged(int taskId, int waypointId, int actionID, Status newStatus);
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

    private void emit_statusChangedListener_statusChanged(int taskId, int waypointId, int actionID, Status newStatus) {
        synchronized (this.statusChangedListenerSet) {
            for (StatusChangedListener listener : this.statusChangedListenerSet) {
                listener.onStatusChanged(taskId, waypointId, actionID, newStatus);
            }
        }
    }
}
