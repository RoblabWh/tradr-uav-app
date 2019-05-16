package tradr.uav.app.services.common;

import android.util.Log;

import java.net.UnknownHostException;
import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.Set;

import tradr.uav.app.UavApplication;
import tradr.uav.app.model.task.Task.Task;

/**
 * Created by tradr on 18.07.17.
 */

public class TradrUavInterfaceClient {

    private enum STATE {
        DISCONNECTED,
        TRY_TO_CONNECT,
        CONNECTED
    }

    private StateMachineMain stateMachine;

    private NetworkClient networkClient;

    private STATE state;


    private EasyTimer timer;


    public TradrUavInterfaceClient() {
        try {
            //networkClient = new NetworkClient("172.16.35.138", 10000, 10000);  // Gelsenkirchen
            //networkClient = new NetworkClient("192.168.29.73", 10000, 10000);  // Hünxe
            //networkClient = new NetworkClient("192.168.7.128", 10000, 10000);  // Sankt Augustin
            networkClient = new NetworkClient(UavApplication.ipAddress, 10000, 10000);

            networkClient.addConnectionListener(new NetworkClient.ConnectionListener() {
                @Override
                public void onConnectionClosed() {
                    on_networkConnection_connectionClosed();
                }
            });
        } catch (InvalidParameterException e) {
            Log.e("TCP", "Invalid Port Number: \n" + e.getMessage());
        } catch (UnknownHostException e) {
            Log.e("TCP", "Invalid IP-Address: \n" + e.getMessage());
        }

        timer = new EasyTimer(1000);
        timer.addTimerListener(new EasyTimer.TimerListener() {
            @Override
            public void onRinging() {
                on_timer_ringing();
            }
        });

        taskListenerSet = new HashSet<>();

        stateTransition_init_to_disconnected();
    }


    public void connect() {
        stateTransition_disconnected_to_tryToConnect();
    }



    private void stateTransition_init_to_disconnected() {
        state = STATE.DISCONNECTED;
    }

    private void stateTransition_disconnected_to_tryToConnect() {
        timer.start();

        state = STATE.TRY_TO_CONNECT;
    }

    private void stateTransition_tryToConnect_to_connected() {
        timer.stop();

        stateMachine = new StateMachineMain(this, networkClient);

        stateMachine.addTaskListener(new StateMachineMain.TaskListener() {
            @Override
            public void onTaskReceived(Task task) {
                emit_taskListener_taskReceived(task);
            }

            @Override
            public void onTaskControlled() {
                emit_taskListener_taskControlled();
            }
        });

        state = STATE.CONNECTED;
    }

    private void stateTransition_connected_to_tryToConnect() {

        stateMachine.finalize();
        stateMachine = null;

        timer.start();

        state = STATE.TRY_TO_CONNECT;
    }


    private void on_timer_ringing() {
        Log.d("Marke", "Timer rings");
        if (!networkClient.isConnected()) {
            networkClient.connectToServer();
        } else {
            stateTransition_tryToConnect_to_connected();
        }
    }


    private void on_networkConnection_connectionClosed() {
        stateTransition_connected_to_tryToConnect();
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
