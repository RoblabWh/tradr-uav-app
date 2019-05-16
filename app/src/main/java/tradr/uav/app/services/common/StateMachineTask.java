package tradr.uav.app.services.common;

import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.HashSet;
import java.util.Set;

import tradr.uav.api.common.ActionStatusMsg;
import tradr.uav.api.common.AssetMsg;
import tradr.uav.api.common.BRIDGEMsg;
import tradr.uav.api.common.ImageRequestMsg;
import tradr.uav.api.common.TaskFeedbackMsg;
import tradr.uav.api.common.TaskMsg;
import tradr.uav.api.common.TaskStatusMsg;
import tradr.uav.api.common.WaypointMsg;
import tradr.uav.api.common.WaypointStatusMsg;
import tradr.uav.app.UavApplication;
import tradr.uav.app.model.task.TaskInProgress.ActionInProgress;
import tradr.uav.app.model.task.TaskInProgress.TaskInProgress;
import tradr.uav.app.model.task.TaskInProgress.WaypointInProgress;
import tradr.uav.app.model.uav.UAV;
import tradr.uav.app.model.task.Task.FotoAction;
import tradr.uav.app.model.task.Task.Task;
import tradr.uav.app.model.task.Task.Waypoint;

/**
 * Created by tradr on 18.07.17.
 */

public class StateMachineTask {

    enum TaskState {
        WAITING_FOR_TASKREQUEST,
        TASK_EXECUTION,
        FINISHED
    }

    private TaskState taskState;

    private UAV uav;

    private TradrUavInterfaceClient tradrUavInterfaceClient;
    private NetworkClient networkClient;

    public StateMachineTask(TradrUavInterfaceClient tradrUavInterfaceClient, NetworkClient networkClient) {
        this.uav = UavApplication.getUav();

        this.tradrUavInterfaceClient = tradrUavInterfaceClient;
        this.networkClient = networkClient;

        this.networkClient.addNetworkListener(this.networkListener);

        taskListenerSet = new HashSet<>();

        stateTransition_init_to_waitingForTaskRequest();
    }


    NetworkClient.NetworkListener networkListener = new NetworkClient.NetworkListener() {
        @Override
        public void onMessageReceived(byte[] message) {
            on_networkClient_messageReceived(message);
        }
    };


    private void stateTransition_init_to_waitingForTaskRequest() {
        this.taskState = TaskState.WAITING_FOR_TASKREQUEST;
    }

    private void on_networkClient_messageReceived(byte[] message) {

        try {
            BRIDGEMsg envelope = BRIDGEMsg.parseFrom(message);

            switch (envelope.getBRIDGEoneofCase()) {
                case TASK:
                    if (this.taskState == TaskState.WAITING_FOR_TASKREQUEST) {
                        answerRequest(envelope.getTask());
                    } else if (this.taskState == TaskState.TASK_EXECUTION) {
                        // Error
                    } else if (this.taskState == TaskState.FINISHED) {
                        // Error
                    } else {
                        // Error
                    }
            }

        } catch (InvalidProtocolBufferException e) {
            Log.e("TCP", "could not deserialize message: " + e.getMessage());
        }
    }

    private void answerRequest(TaskMsg msg) {
        Log.d("TCP", "Task received");
        Task task = new Task(msg.getTaskID());

        for (WaypointMsg waypointMsg : msg.getWaypointsList()) {
            Waypoint waypoint = new Waypoint(waypointMsg.getWaypointID(), task.getTaskId());

            waypoint.setAltitude(waypointMsg.getUavPose().getAltitude());
            waypoint.setLongitude(waypointMsg.getUavPose().getLongitude());
            waypoint.setLatitude(waypointMsg.getUavPose().getLatitude());

            waypoint.setRoll(waypointMsg.getUavPose().getRoll());
            waypoint.setPitch(waypointMsg.getUavPose().getPitch());
            waypoint.setYaw(waypointMsg.getUavPose().getYaw());

            for (ImageRequestMsg imageRequestMsg : waypointMsg.getImageRequestsList()) {
                FotoAction action = new FotoAction(waypoint.getActionList().size(), waypointMsg.getWaypointID(), task.getTaskId());

                action.setLongitude(imageRequestMsg.getCamPose().getLongitude());
                action.setLatitude(imageRequestMsg.getCamPose().getLatitude());
                action.setAltitude(imageRequestMsg.getCamPose().getAltitude());

                action.setRoll(imageRequestMsg.getCamPose().getRoll());
                action.setPitch(imageRequestMsg.getCamPose().getPitch());
                action.setYaw(imageRequestMsg.getCamPose().getYaw());

                waypoint.addActionAtEnd(action);
            }

            task.addWaypoint(waypoint);

        }

        emit_taskListener_taskReceived(task);

    }


    private void sendTaskResponse() {
        if (uav.isAircraftConnected()) {
            TaskInProgress taskInProgress = uav.taskOperator.getCurrentTaskInProgress();
            taskInProgress.addStatusChangedListener(this.task_statusChangedListener);

            for (WaypointInProgress waypointInProgress : taskInProgress.getWaypointInProgressList()) {
                waypointInProgress.addStatusChangedListener(this.waypoint_statusChangedListener);

                for (ActionInProgress actionInProgress : waypointInProgress.getActionInProgressList()) {
                    actionInProgress.addStatusChangedListener(this.action_statusChangedListener);
                }
            }
        }
    }

    private TaskInProgress.StatusChangedListener task_statusChangedListener = new TaskInProgress.StatusChangedListener() {
        @Override
        public void onStatusChanged(int taskID, TaskInProgress.Status newStatus) {
            TaskStatusMsg.Builder builder = TaskStatusMsg.newBuilder();
            builder.setTaskID(taskID);
            switch (newStatus) {
                case WAIT_FOR_EXECUTION:
                    builder.setStatus(TaskStatusMsg.Status.WAIT_FOR_EXECUTION);
                    break;
                case EXECUTION:
                    builder.setStatus(TaskStatusMsg.Status.EXECUTION);
                    break;
                case FINISHED:
                    builder.setStatus(TaskStatusMsg.Status.FINISHED);
                    break;
                case CANCELED:
                    builder.setStatus(TaskStatusMsg.Status.CANCELED);
                    break;
            }

            TaskStatusMsg msg = builder.build();

            TaskFeedbackMsg.Builder feedbackBuilder = TaskFeedbackMsg.newBuilder();
            feedbackBuilder.setTaskStatus(msg);
            TaskFeedbackMsg feedback = feedbackBuilder.build();

            AssetMsg.Builder envelopeBuilder = AssetMsg.newBuilder();
            envelopeBuilder.setTaskFeedback(feedback);
            AssetMsg envelope = envelopeBuilder.build();

            networkClient.sendOverTCP(envelope.toByteArray());
        }
    };

    private WaypointInProgress.StatusChangedListener waypoint_statusChangedListener = new WaypointInProgress.StatusChangedListener() {
        @Override
        public void onStatusChanged(int taskId, int waypointID, WaypointInProgress.Status newStatus) {
            WaypointStatusMsg.Builder builder = WaypointStatusMsg.newBuilder();
            builder.setTaskID(taskId);
            builder.setWaypointID(waypointID);
            switch (newStatus) {
                case WAIT_FOR_EXECUTION:
                    builder.setStatus(WaypointStatusMsg.Status.WAIT_FOR_EXECUTION);
                    break;
                case EXECUTION_FLYING_TO_WAYPOINT:
                    builder.setStatus(WaypointStatusMsg.Status.EXECUTION_FLYING_TO_WAYPOINT);
                    break;
                case EXECUTION_DOING_ACTIONS:
                    builder.setStatus(WaypointStatusMsg.Status.EXECUTION_DOING_ACTIONS);
                    break;
                case FINISHED:
                    builder.setStatus(WaypointStatusMsg.Status.FINISHED);
                    break;
                case CANCELED:
                    builder.setStatus(WaypointStatusMsg.Status.CANCELED);
                    break;
            }

            WaypointStatusMsg msg = builder.build();

            TaskFeedbackMsg.Builder feedbackBuilder = TaskFeedbackMsg.newBuilder();
            feedbackBuilder.setWaypointStatus(msg);
            TaskFeedbackMsg feedback = feedbackBuilder.build();

            AssetMsg.Builder envelopeBuilder = AssetMsg.newBuilder();
            envelopeBuilder.setTaskFeedback(feedback);
            AssetMsg envelope = envelopeBuilder.build();

            networkClient.sendOverTCP(envelope.toByteArray());
        }
    };


    private ActionInProgress.StatusChangedListener action_statusChangedListener = new ActionInProgress.StatusChangedListener() {
        @Override
        public void onStatusChanged(int taskId, int waypointId, int actionID, ActionInProgress.Status newStatus) {
            ActionStatusMsg.Builder builder = ActionStatusMsg.newBuilder();
            builder.setTaskID(taskId);
            builder.setWaypointID(waypointId);
            builder.setActionID(actionID);
            switch (newStatus) {
                case WAIT_FOR_EXECUTION:
                    builder.setStatus(ActionStatusMsg.Status.WAIT_FOR_EXECUTION);
                    break;
                case EXECUTION:
                    builder.setStatus(ActionStatusMsg.Status.EXECUTION);
                    break;
                case FINISHED:
                    builder.setStatus(ActionStatusMsg.Status.FINISHED);
                    break;
                case CANCELED:
                    builder.setStatus(ActionStatusMsg.Status.CANCELED);
                    break;
            }

            ActionStatusMsg msg = builder.build();

            TaskFeedbackMsg.Builder feedbackBuilder = TaskFeedbackMsg.newBuilder();
            feedbackBuilder.setActionStatus(msg);
            TaskFeedbackMsg feedback = feedbackBuilder.build();

            AssetMsg.Builder envelopeBuilder = AssetMsg.newBuilder();
            envelopeBuilder.setTaskFeedback(feedback);
            AssetMsg envelope = envelopeBuilder.build();

            networkClient.sendOverTCP(envelope.toByteArray());
        }
    };


    @Override
    public void finalize() {
        this.networkClient.removeNetworkListener(this.networkListener);
    }


    /* TaskListener */
    private Set<TaskListener> taskListenerSet;

    public interface TaskListener {
        void onTaskReceived(Task task);
        void onTaskControlled();
    }

    public void addTaskListener(TaskListener taskListener) {
        synchronized (this.taskListenerSet) {
            this.taskListenerSet.add(taskListener);
        }
    }

    public void removeTaskListener(TaskListener taskListener) {
        synchronized (this.taskListenerSet) {
            this.taskListenerSet.remove(taskListener);
        }
    }

    private void emit_taskListener_taskReceived(Task task) {
        synchronized (this.taskListenerSet) {
            for (TaskListener listener : this.taskListenerSet) {
                listener.onTaskReceived(task);
            }
        }
    }

    private void emit_taskListener_taskControlled() {
        synchronized (this.taskListenerSet) {
            for (TaskListener listener : this.taskListenerSet) {
                listener.onTaskControlled();
            }
        }
    }
}
