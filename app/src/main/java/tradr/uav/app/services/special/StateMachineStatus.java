package tradr.uav.app.services.special;

import tradr.uav.api.special.BRIDGEMsg;

/**
 * Created by tradr on 18.07.17.
 */

public class StateMachineStatus {

    enum StatusState {
        SENDING_STATUS,
        FINISHED
    }

    private StatusState statusState;

    private TradrUavInterfaceClient tradrUavInterfaceClient;
    private NetworkClient networkClient;

    public StateMachineStatus(TradrUavInterfaceClient tradrUavInterfaceClient, NetworkClient networkClient) {
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
