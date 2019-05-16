package tradr.uav.app.services.special;

import java.util.Set;

import tradr.uav.api.special.BRIDGEMsg;


/**
 * Created by tradr on 01.08.17.
 */

public class NetworkClient {

    public void send() {

    }



    /* NetworkListener */
    private Set<NetworkListener> networkListenerSet;

    public interface NetworkListener {
        void onMessageReceived(BRIDGEMsg message);
    }

    public void addNetworkListener(NetworkListener networkListener) {
        this.networkListenerSet.add(networkListener);
    }

    public void removeNetworkListener(NetworkListener networkListener) {
        this.networkListenerSet.remove(networkListener);
    }

    private void emit_networkListener_messageReceived(BRIDGEMsg message) {
        for (NetworkListener listener : this.networkListenerSet) {
            listener.onMessageReceived(message);
        }
    }

}
