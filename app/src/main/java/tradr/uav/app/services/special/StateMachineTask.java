package tradr.uav.app.services.special;

import tradr.uav.api.special.BRIDGEMsg;

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

    private TradrUavInterfaceClient tradrUavInterfaceClient;
    private NetworkClient networkClient;

    public StateMachineTask(TradrUavInterfaceClient tradrUavInterfaceClient, NetworkClient networkClient) {
        this.tradrUavInterfaceClient = tradrUavInterfaceClient;
        this.networkClient = networkClient;

        this.networkClient.addNetworkListener(new NetworkClient.NetworkListener() {
            @Override
            public void onMessageReceived(BRIDGEMsg message) {
                on_networkClient_messageReceived(message);
            }
        });
    }

    private void on_networkClient_messageReceived(BRIDGEMsg message) {

    }
}
