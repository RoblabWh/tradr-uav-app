package tradr.uav.app.model.task.TaskInProgress;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tradr.uav.app.model.task.Task.Task;
import tradr.uav.app.model.task.Task.Waypoint;

/**
 * Created by tradr on 29.10.17.
 */

public class TaskInProgress {

    public enum Status {
        WAIT_FOR_EXECUTION,
        EXECUTION,
        FINISHED,
        CANCELED
    }



    private Status status;

    private Task task;

    private List<WaypointInProgress> waypointInProgressList;

    public TaskInProgress(Task task) {
        this.status = Status.WAIT_FOR_EXECUTION;
        this.task = task;
        this.waypointInProgressList = new ArrayList<>(20);

        this.statusChangedListenerSet = new HashSet<>();

        for (Waypoint waypoint : task.getWaypointList()) {
            this.waypointInProgressList.add(new WaypointInProgress(waypoint));
        }
    }



    public Task getTask() {
        return this.task;
    }

    public Status getStatus() {
        return this.status;
    }



    public WaypointInProgress getWaypointInProgress(int waypointId) {
        return this.waypointInProgressList.get(waypointId);
    }

    public List<WaypointInProgress> getWaypointInProgressList() {
        return this.waypointInProgressList;
    }



    public void executionStarts() {
        if (Status.EXECUTION != this.status) {
            this.status = Status.EXECUTION;
            this.emit_statusChangedListener_statusChanged(this.task.getTaskId(), Status.EXECUTION);
        }
    }

    public void executionFinished() {
        if (Status.FINISHED != this.status) {
            this.status = Status.FINISHED;
            this.emit_statusChangedListener_statusChanged(this.task.getTaskId(), Status.FINISHED);
        }
    }

    public void executionCanceledByError() {
        if (Status.CANCELED != this.status) {
            this.status = Status.CANCELED;
            this.emit_statusChangedListener_statusChanged(this.task.getTaskId(), Status.CANCELED);
        }
    }



    /* StatusChangedListener */
    private Set<StatusChangedListener> statusChangedListenerSet;

    public interface StatusChangedListener {
        void onStatusChanged(int taskID, Status newStatus);
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

    private void emit_statusChangedListener_statusChanged(int taskID, Status newStatus) {
        synchronized (this.statusChangedListenerSet) {
            for (StatusChangedListener listener : this.statusChangedListenerSet) {
                listener.onStatusChanged(taskID, newStatus);
            }
        }
    }
}
