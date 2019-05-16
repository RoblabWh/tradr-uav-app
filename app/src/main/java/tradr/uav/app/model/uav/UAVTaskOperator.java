package tradr.uav.app.model.uav;

import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.mission.waypoint.WaypointMissionDownloadEvent;
import dji.common.mission.waypoint.WaypointMissionExecuteState;
import dji.common.mission.waypoint.WaypointMissionExecutionEvent;
import dji.common.mission.waypoint.WaypointMissionUploadEvent;
import dji.common.util.CommonCallbacks;
import dji.sdk.camera.DownloadListener;
import dji.sdk.camera.MediaFile;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.mission.waypoint.WaypointMissionOperatorListener;
import dji.sdk.products.Aircraft;
import tradr.uav.app.UavApplication;
import tradr.uav.app.model.task.Task.Task;
import tradr.uav.app.model.task.TaskInProgress.TaskInProgress;
import tradr.uav.app.model.task.TaskInProgress.WaypointInProgress;
import tradr.uav.app.utils.ToastUtils;

/**
 * Created by Artur Leinweber on 10.05.17.
 */

public class UAVTaskOperator {


    private Aircraft aircraft;
    private UAV uav;

    /**
     * The waypoint operator is the only object that controls, runs and monitors Waypoint Missions.
     */
    private WaypointMissionOperator waypointMissionOperator;

    private Task currentTask;
    private TaskInProgress currentTaskInProgress;


    /**
     * Private Constructor for Singleton Pattern
     */
    public UAVTaskOperator(Aircraft aircraft, UAV uav){
        this.aircraft = aircraft;

        this.uav = uav;

        uploadNotificationListenerSet = new HashSet<>();
        downloadNotificationListenerSet = new HashSet<>();
        executionNotificationListenerSet = new HashSet<>();


        waypointMissionOperator = new WaypointMissionOperator();


        waypointMissionOperator.addListener(new WaypointMissionOperatorListener() {
            @Override
            public void onDownloadUpdate(@NonNull WaypointMissionDownloadEvent waypointMissionDownloadEvent) {
                on_waypointMissionOperator_downloadUpdate(waypointMissionDownloadEvent);
            }

            @Override
            public void onUploadUpdate(@NonNull WaypointMissionUploadEvent waypointMissionUploadEvent) {
                on_waypointMissionOperator_uploadUpdate(waypointMissionUploadEvent);
            }

            @Override
            public void onExecutionUpdate(@NonNull WaypointMissionExecutionEvent waypointMissionExecutionEvent) {
                on_waypointMissionOperator_executionUpdate(waypointMissionExecutionEvent);
            }

            @Override
            public void onExecutionStart() {
                on_waypointMissionOperator_executionStart();
            }

            @Override
            public void onExecutionFinish(@Nullable DJIError error) {
                on_waypointMissionOperator_executionFinished(error);
            }
        });

    }




    public Task getCurrentTask() {
        return this.currentTask;
    }

    public TaskInProgress getCurrentTaskInProgress() {
        return this.currentTaskInProgress;
    }

    private void on_waypointMissionOperator_uploadUpdate(WaypointMissionUploadEvent event) {
        emit_UploadNotificationListener_uploadUpdate(event);
    }

    private void on_waypointMissionOperator_downloadUpdate(WaypointMissionDownloadEvent event) {
        emit_DownloadNotificationListener_downloadUpdate(event);
    }

    private void on_waypointMissionOperator_executionUpdate(WaypointMissionExecutionEvent event) {
        int target = event.getProgress().targetWaypointIndex;
        WaypointMissionExecuteState state = event.getProgress().executeState;

        Log.d("UAV_EVENT", "WaypointIndex: " + target + "    ExecutionState: " + state.toString());

        switch (state) {
            case INITIALIZING:
                this.currentTaskInProgress.getWaypointInProgress(target).flyingStarts();
                break;
            case MOVING:
                this.currentTaskInProgress.getWaypointInProgress(target - 1).executionFinished();
                this.currentTaskInProgress.getWaypointInProgress(target).flyingStarts();
                break;
            case DOING_ACTION:
                this.currentTaskInProgress.getWaypointInProgress(target).doingActionsStarts();
                break;
            case FINISHED_ACTION:
                this.currentTaskInProgress.getWaypointInProgress(target).executionFinished();
                break;
        }

        emit_ExecutionNotificationListener_executionUpdate(event);
    }

    private void on_waypointMissionOperator_executionStart() {
        /*
        Handler handler = new Handler(UavApplication.getInstance().getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                final File destDir = new File(Environment.getExternalStorageDirectory().getPath() + "/DJI_Images/");

                aircraft.getCamera().setMediaFileCallback(new MediaFile.Callback() {

                    int pictureNumber = 0;

                    @Override
                    public void onNewFile(@NonNull MediaFile mediaFile) {
                        Log.d("UAV_DOWNLOAD", "new File");

                        final MediaFile newFile = mediaFile;

                        aircraft.getCamera().setMode(SettingsDefinitions.CameraMode.MEDIA_DOWNLOAD, new CommonCallbacks.CompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {
                                if (djiError == null) {
                                    Log.d("UAV_DOWNLOAD", "Camera-Mode changed");
                                } else {
                                    Log.d("UAV_DOWNLOAD", "Camera-Mode not changed:\n" + djiError.getDescription());
                                }
                            }
                        });

                        aircraft.getCamera().getMediaManager();

                        newFile.fetchFileData(destDir, String.format("image_%03d", pictureNumber), new DownloadListener<String>() {
                            @Override
                            public void onStart() {
                                    Log.d("UAV_DOWNLOAD", "download started");
                                }

                            @Override
                            public void onRateUpdate(long l, long l1, long l2) {
                                Log.d("UAV_DOWNLOAD", "download rate update: " + l + "  " + l1 + "  " + l2);
                            }

                            @Override
                            public void onProgress(long l, long l1) {
                                Log.d("UAV_DOWNLOAD", "download: " + l + "  " + l1);
                            }

                            @Override
                            public void onSuccess(String s) {
                                Log.d("UAV_DOWNLOAD", "download finished: " + s);
                            }

                            @Override
                            public void onFailure(DJIError djiError) {
                                Log.d("UAV_DOWNLOAD", "download error: " + djiError.getDescription());
                            }
                        });

                        pictureNumber++;
                    }
                });
            }
        });
        */

        emit_ExecutionNotificationListener_executionStarted();
    }

    private void on_waypointMissionOperator_executionFinished(DJIError error) {
        if (error == null) {
            emit_ExecutionNotificationListener_executionSuccessfullyFished();
        } else {
            emit_ExecutionNotificationListener_executionFailed(error);
        }
    }



    /**
     * Add listener to listen for events.
     */ /*
    public boolean addWMOListener(@NotNull WaypointMissionOperatorListener eventNotificationListener) {

        boolean eventNotificationListenerExists = this.eventNotificationListenerSet.add(eventNotificationListener);

        if (eventNotificationListenerExists){
            this.waypointMissionOperator.addListener(eventNotificationListener);
        }
        return eventNotificationListenerExists;
    } */



    /**
     * Removes listener. If the same listener is listening to multiple events and
     * notifications (e.g. upload event and download event), it will not receive any update of them.
     */ /*
    public boolean removeWMOListener(@NotNull WaypointMissionOperatorListener eventNotificationListener) {

        boolean eventNotificationListenerExists = this.eventNotificationListeners.remove(eventNotificationListener);

        if (eventNotificationListenerExists) {
            this.waypointMissionOperator.removeListener(eventNotificationListener);
        }

        return eventNotificationListenerExists;
    } */



    /**
     * Loads the WaypointMission into device memory. This also verifies all the information
     * of mission. If something is incorrect, callback.result() will be called with an Error.
     * Otherwise, callback.result() will be called with a null value. The mission object will
     * remain in device memory even after the WaypointMission execution has finished.
     */
    public void configTask(Task task) {
        this.currentTask = task;
        this.currentTaskInProgress = new TaskInProgress(task);

        WaypointMission mission = this.currentTask.getWaypointMission();

        DJIError error = this.waypointMissionOperator.loadMission(mission);
        if (error == null) {
            ToastUtils.setResultToToast("Config Task succeeded");
        } else {
            ToastUtils.setResultToToast("Config Task failed " + error.getDescription());
        }

    }




    /**
     * Uploads the loaded WaypointMission to aircraft. This should only be called after
     * a WaypointTask has been successfully loaded into device memory.
     */
    public void uploadTask() {
        if (aircraft.isConnected()) {
            this.waypointMissionOperator.uploadMission(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError error) {
                    on_missionOperator_uploadMissionResult(error);
                }
            });

            emit_UploadNotificationListener_uploadStarted();
        } else {
            ToastUtils.setResultToToast("Failed: Please connect aircraft!");
        }
    }

    /**
     * Executes the current WaypointMission inside aircraft. This should only be called after
     * a WaypointMission has been successfully uploaded to the aircraft using uploadMission(),
     * To check the progress of execution, use addExecutionListener().
     */
    public void startTask() {
        if (aircraft.isConnected()) {
            this.waypointMissionOperator.startMission(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError error) {
                    on_missionOperator_startMissionResult(error);
                }
            });
            this.uav.flightController.addStateListener(new UAVFlightController.StateListener() {
                @Override
                public void onStateChanged(UAVFlightController.State state) {
                    on_flightController_stateChanged(state);
                }
            });
        } else {
            ToastUtils.setResultToToast("Failed: Please connect aircraft!");
        }
    }

    /**
     * Stops the executing or paused task. It can only be called when the getCurrentState
     * is one of the following: - EXECUTING - EXECUTION_PAUSED After a mission is stopped
     * successfully, getCurrentState will become READY_TO_UPLOAD.
     */
    public void stopTask() {
        if (aircraft.isConnected()) {
            this.waypointMissionOperator.stopMission(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError error) {
                    on_missionOperator_stopMissionResult(error);
                }
            });
        } else {
            ToastUtils.setResultToToast("Failed: Please connect aircraft!");
        }
    }



    private void on_missionOperator_uploadMissionResult(DJIError error) {

        if (error == null) {
            ToastUtils.setResultToToast("task successfully uploaded");
            emit_UploadNotificationListener_uploadSuccessfullyFished();
        } else {
            ToastUtils.setResultToToast("failed to upload task: " + error.getDescription());
            emit_UploadNotificationListener_uploadFailed(error);
        }
    }

    private void on_missionOperator_startMissionResult(DJIError error) {

        if (error == null) {
            ToastUtils.setResultToToast("execution successfully started");
            emit_ExecutionNotificationListener_executionStarted();
        } else {
            ToastUtils.setResultToToast("failed to execute task: " + error.getDescription());
            emit_ExecutionNotificationListener_executionFailed(error);
        }
    }

    private void on_missionOperator_stopMissionResult(DJIError error) {

        if (error == null) {
            ToastUtils.setResultToToast("execution successfully stopped");
            emit_ExecutionNotificationListener_executionStopped();
        } else {
            ToastUtils.setResultToToast("failed to stop execution: " + error.getDescription());
            emit_ExecutionNotificationListener_executionFailed(error);
        }

    }


    private void on_flightController_stateChanged(UAVFlightController.State state) {
        if (this.currentTaskInProgress != null && state == UAVFlightController.State.GOING_HOME) {
            if (this.currentTaskInProgress.getWaypointInProgress(this.currentTaskInProgress.getWaypointInProgressList().size() - 1).getStatus() == WaypointInProgress.Status.EXECUTION_DOING_ACTIONS) {
                this.currentTaskInProgress.getWaypointInProgress(this.currentTaskInProgress.getWaypointInProgressList().size() - 1).executionFinished();
            }
        }
    }







    public interface UploadNotificationListener {
        void uploadStarted();
        void uploadUpdate(WaypointMissionUploadEvent event);
        void uploadSuccessfullyFinished();
        void uploadFailed(DJIError error);
    }

    /**
     * List of listeners that are implementing the interface for Waypoint mission operator events.
     */
    private Set<UploadNotificationListener> uploadNotificationListenerSet;

    public void addUploadNotificationListener(UploadNotificationListener listener){
        synchronized (this.uploadNotificationListenerSet) {
            this.uploadNotificationListenerSet.add(listener);
        }
    }

    public void removeUploadNotificationListener(UploadNotificationListener listener){
        synchronized (this.uploadNotificationListenerSet) {
            this.uploadNotificationListenerSet.remove(listener);
        }
    }

    private void emit_UploadNotificationListener_uploadStarted() {
        synchronized (this.uploadNotificationListenerSet) {
            for (UploadNotificationListener listener : this.uploadNotificationListenerSet) {
                listener.uploadStarted();
            }
        }
    }

    private void emit_UploadNotificationListener_uploadUpdate(WaypointMissionUploadEvent event) {
        synchronized (this.uploadNotificationListenerSet) {
            for (UploadNotificationListener listener : this.uploadNotificationListenerSet) {
                listener.uploadUpdate(event);
            }
        }
    }

    private void emit_UploadNotificationListener_uploadSuccessfullyFished() {
        synchronized (this.uploadNotificationListenerSet) {
            for (UploadNotificationListener listener : this.uploadNotificationListenerSet) {
                listener.uploadSuccessfullyFinished();
            }
        }
    }

    private void emit_UploadNotificationListener_uploadFailed(DJIError error) {
        synchronized (this.uploadNotificationListenerSet) {
            for (UploadNotificationListener listener : this.uploadNotificationListenerSet) {
                listener.uploadFailed(error);
            }
        }
    }





    public interface DownloadNotificationListener {
        void downloadStarted();
        void downloadUpdate(WaypointMissionDownloadEvent event);
        void downloadSuccessfullyFinished();
        void downloadFailed(DJIError error);
    }

    /**
     * List of listeners that are implementing the interface for Waypoint mission operator events.
     */
    private Set<DownloadNotificationListener> downloadNotificationListenerSet;

    public void addDownloadNotificationListener(DownloadNotificationListener listener){
        this.downloadNotificationListenerSet.add(listener);
    }

    public void removeDownloadNotificationListener(DownloadNotificationListener listener){
        this.downloadNotificationListenerSet.remove(listener);
    }

    private void emit_DownloadNotificationListener_downloadStarted() {
        for (DownloadNotificationListener listener : this.downloadNotificationListenerSet) {

            listener.downloadStarted();
        }
    }

    private void emit_DownloadNotificationListener_downloadUpdate(WaypointMissionDownloadEvent event) {
        for (DownloadNotificationListener listener : this.downloadNotificationListenerSet) {
            listener.downloadUpdate(event);
        }
    }

    private void emit_DownloadNotificationListener_downloadSuccessfullyFished() {
        for (DownloadNotificationListener listener : this.downloadNotificationListenerSet) {
            listener.downloadSuccessfullyFinished();
        }
    }

    private void emit_DownloadNotificationListener_downloadFailed(DJIError error) {
        for (DownloadNotificationListener listener : this.downloadNotificationListenerSet) {
            listener.downloadFailed(error);
        }
    }






    public interface ExecutionNotificationListener {
        void executionStarted();
        void executionUpdate(WaypointMissionExecutionEvent event);
        void executionSuccessfullyFinished();
        void executionFailed(DJIError error);
        void executionStopped();
    }

    /**
     * List of listeners that are implementing the interface for Waypoint mission operator events.
     */
    private Set<ExecutionNotificationListener> executionNotificationListenerSet;

    public void addExecutionNotificationListener(ExecutionNotificationListener listener){
        this.executionNotificationListenerSet.add(listener);
    }

    public void removeExecutionNotificationListener(ExecutionNotificationListener listener){
        this.executionNotificationListenerSet.remove(listener);
    }

    private void emit_ExecutionNotificationListener_executionStarted() {
        for (ExecutionNotificationListener listener : this.executionNotificationListenerSet) {
            listener.executionStarted();
        }
    }

    private void emit_ExecutionNotificationListener_executionUpdate(WaypointMissionExecutionEvent event) {
        for (ExecutionNotificationListener listener : this.executionNotificationListenerSet) {
            listener.executionUpdate(event);
        }
    }

    private void emit_ExecutionNotificationListener_executionSuccessfullyFished() {
        for (ExecutionNotificationListener listener : this.executionNotificationListenerSet) {
            listener.executionSuccessfullyFinished();
        }
    }

    private void emit_ExecutionNotificationListener_executionFailed(DJIError error) {
        for (ExecutionNotificationListener listener : this.executionNotificationListenerSet) {
            listener.executionFailed(error);
        }
    }

    private void emit_ExecutionNotificationListener_executionStopped() {
        for (ExecutionNotificationListener listener : this.executionNotificationListenerSet) {
            listener.executionStopped();
        }
    }
}