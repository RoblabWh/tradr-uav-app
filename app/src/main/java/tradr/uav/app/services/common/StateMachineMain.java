package tradr.uav.app.services.common;

import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.HashSet;
import java.util.Set;

import tradr.uav.api.common.AssetMsg;
import tradr.uav.api.common.AssetRegReqMsg;
import tradr.uav.api.common.BRIDGEMsg;

import tradr.uav.app.UavApplication;
import tradr.uav.app.model.uav.UAV;
import tradr.uav.app.model.task.Task.Task;

/**
 * Created by tradr on 18.07.17.
 */

public class StateMachineMain {

    enum MainState {
        TRY_TO_REGISTER,
        REGISTERED,
        FINISHED
    }

    private MainState mainState;

    private StateMachineStatus smStatus;
    private StateMachineTask smTask;
    private StateMachineVideo smVideo;

    private TradrUavInterfaceClient tradrUavInterfaceClient;
    private NetworkClient networkClient;

    private UAV uav;

    private EasyTimer timer;

    public StateMachineMain(TradrUavInterfaceClient tradrUavInterfaceClient, NetworkClient networkClient) {
        this.smStatus = null;
        this.smTask = null;
        this.smVideo = null;

        this.mainState = MainState.TRY_TO_REGISTER;

        this.uav = UavApplication.getUav();

        this.tradrUavInterfaceClient = tradrUavInterfaceClient;
        this.networkClient = networkClient;

        this.networkClient.addNetworkListener(this.networkListener);

        this.timer = new EasyTimer(1000);
        this.timer.addTimerListener(this.timerListener);

        taskListenerSet = new HashSet<>();

        stateTransition_init_to_tryToRegister();

    }

    private NetworkClient.NetworkListener networkListener = new NetworkClient.NetworkListener() {
        @Override
        public void onMessageReceived(byte[] message) {
            on_networkClient_messageReceived(message);
        }
    };

    private EasyTimer.TimerListener timerListener = new EasyTimer.TimerListener() {
        @Override
        public void onRinging() {
            on_timer_ringing();
        }
    };

    private void stateTransition_init_to_tryToRegister() {
        timer.start();

        this.mainState = MainState.TRY_TO_REGISTER;
    }

    private void stateTransition_tryToRegister_to_registered() {
        timer.stop();

        smStatus = new StateMachineStatus(tradrUavInterfaceClient, networkClient);
        smTask = new StateMachineTask(tradrUavInterfaceClient, networkClient);
        smVideo = new StateMachineVideo(tradrUavInterfaceClient, networkClient);

        smTask.addTaskListener(new StateMachineTask.TaskListener() {
            @Override
            public void onTaskReceived(Task task) {
                emit_taskListener_taskReceived(task);
            }

            @Override
            public void onTaskControlled() {
                emit_taskListener_taskControlled();
            }
        });

        this.mainState = MainState.REGISTERED;
    }




    private void on_networkClient_messageReceived(byte[] message) {

        try {
            BRIDGEMsg envelope = BRIDGEMsg.parseFrom(message);

            Log.d("TCP", "oneOfCase: " + envelope.getBRIDGEoneofCase());
            switch (envelope.getBRIDGEoneofCase()) {
                case REGRESPONSE:
                    Log.d("TCP", "Message received and state Transition");
                    stateTransition_tryToRegister_to_registered();
            }

        } catch (InvalidProtocolBufferException e) {
            Log.e("TCP", "could not deserialize message: " + e.getMessage());
        }

    }

    private void on_timer_ringing() {
        sendRegisterMessage();
    }

    private void sendRegisterMessage() {
        AssetRegReqMsg.Builder responseMsgBuilder = AssetRegReqMsg.newBuilder();
        responseMsgBuilder.setApiVersion("0.1.0");
        if (this.uav != null && this.uav.isAircraftConnected()) {
            responseMsgBuilder.setAssetModel(this.uav.getModelName());
        } else {
            responseMsgBuilder.setAssetModel("unknown Model");
        }
        responseMsgBuilder.setAssetName("FireBird");
        AssetRegReqMsg registerRequestMsg = responseMsgBuilder.build();

        AssetMsg.Builder envelopeBuilder = AssetMsg.newBuilder();
        envelopeBuilder.setRegRequest(registerRequestMsg);
        AssetMsg envelope = envelopeBuilder.build();

        this.networkClient.sendOverTCP(envelope.toByteArray());

    }

    @Override
    public void finalize() {
        Log.d("NetworkClient", "finalize: " + this);
        this.smStatus.finalize();
        this.smTask.finalize();
        this.smVideo.finalize();

        this.networkClient.removeNetworkListener(this.networkListener);
        this.timer.removeTimerListener(this.timerListener);
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
