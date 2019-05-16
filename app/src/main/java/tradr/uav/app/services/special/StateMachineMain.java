package tradr.uav.app.services.special;

import tradr.uav.api.special.BRIDGEMsg;

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

    public StateMachineMain(TradrUavInterfaceClient tradrUavInterfaceClient, NetworkClient networkClient) {
        this.smStatus = null;
        this.smTask = null;
        this.smVideo = null;

        this.mainState = MainState.TRY_TO_REGISTER;

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
        switch (message.getBRIDGEoneofCase()) {
            case REGRESPONSE:
                break;
        }
    }



    private void stateTransition_tryToRegister_to_registered() {
        smStatus = new StateMachineStatus(tradrUavInterfaceClient, networkClient);
        smTask = new StateMachineTask(tradrUavInterfaceClient, networkClient);
        smVideo = new StateMachineVideo(tradrUavInterfaceClient, networkClient);

    }




}
