package tradr.uav.app.services.common;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.Set;

import tradr.uav.api.common.AssetMsg;
import tradr.uav.api.common.BRIDGEMsg;
import tradr.uav.app.utils.ToastUtils;


/**
 * Created by tradr on 01.08.17.
 */

public class NetworkClient {

    private InetAddress ipAddress;
    private int tcpPort;
    private int udpPort;

    private boolean connected;

    private Socket         tcpSocket;
    private DatagramSocket udpSocket;

    private OutputStream tcpOut;
    private InputStream tcpIn;

    private Receiver receiver;

    public NetworkClient(String ipAdress, int tcpPort, int udpPort)
            throws UnknownHostException, InvalidParameterException {

        this.ipAddress = InetAddress.getByName(ipAdress);

        if (tcpPort >= 0 && tcpPort < 65536) {
            this.tcpPort = tcpPort;
        } else {
            throw new InvalidParameterException("tcpPort number must be between 0 and 65535");
        }

        if (tcpPort >= 0 && tcpPort < 65536) {
            this.udpPort = udpPort;
        } else {
            throw new InvalidParameterException("udpPort number must be between 0 and 65535");
        }

        this.tcpSocket = null;
        this.tcpOut = null;
        this.tcpIn = null;

        this.connected = false;

        this.networkListenerSet = new HashSet<NetworkListener>();
        this.connectionListenerSet = new HashSet<ConnectionListener>();
    }



    public void connectToServer() {
        try {
            tcpSocket = new Socket(ipAddress, tcpPort);
            udpSocket = new DatagramSocket();

            // Create BufferedWriter object for sending messages to server.
            tcpOut = tcpSocket.getOutputStream();

            //Create BufferedReader object for receiving messages from server.
            tcpIn = tcpSocket.getInputStream();

            connected = true;

            receiver = new Receiver();
            receiver.start();

        } catch (IOException e) {
            Log.e("TCP", "could not connect to server: \n" + e.getMessage());
        }
    }

    public boolean isConnected() {
        return connected;
    }


    /*
    public void sendOverTCP(AssetMsg message) {
        if (connected) {
            try {
                message.writeDelimitedTo(tcpOut);
            } catch (IOException e) {
                Log.e("TCP", "could not send message: \n" + e.getMessage());
            }
        }
    }
    */

    public void sendOverTCP(byte[] message) {
        if (connected) {
            try {
                byte[] lengthData = ByteBuffer.allocate(4).putInt(message.length).array();
                //Log.d("TCP", "lengthData: " + lengthData + "   length: " + ByteBuffer.wrap(lengthData).getInt());
                synchronized (tcpOut) {
                    tcpOut.write(lengthData);
                    tcpOut.write(message);
                }
            } catch (IOException e) {
                Log.e("TCP", "could not send message: \n" + e.getMessage());
            }
        }
    }

    public void sendOverUDP(byte[] message) {
        if (connected) {
            try {
                /*ByteBuffer data = ByteBuffer.allocate(4 + message.length);
                data.putInt(message.length);
                data.put(message);*/
                DatagramPacket udpPacket = new DatagramPacket(message, message.length, ipAddress, udpPort);
                udpSocket.send(udpPacket);
            } catch (Exception e) {
                ToastUtils.setResultToToast(e.getMessage());
            }
        }
    }



    private class Receiver extends Thread {

        /*
        @Override
        public void run() {
            while (true) {
                try {
                    BRIDGEMsg message = BRIDGEMsg.parseDelimitedFrom(tcpIn);
                    if (message != null) {
                        emit_networkListener_messageReceived(message);
                    }
                } catch (IOException e) {
                    Log.e("TCP", "could not receive message: " + e.getMessage());
                }

                if (socket.isClosed()) {
                    Log.e("TCP", "connection lost");
                    break;
                }
            }
        }
        */

        @Override
        public void run() {
            while (true) {
                try {
                    int readBytes;
                    int readReturn;

                    byte[] lengthData = new byte[4];
                    readBytes = 0;
                    while (readBytes < 4) {
                        readReturn = tcpIn.read(lengthData, readBytes, 4 - readBytes);
                        if (readReturn > 0) {
                            readBytes += readReturn;
                        } else {
                            Log.e("TCP", "could not read message length: -1");
                            emit_connectionListener_connectionClosed();
                            connected = false;
                            return;
                        }
                    }

                    int length = ByteBuffer.wrap(lengthData).getInt();

                    byte[] message = new byte[length];
                    readBytes = 0;
                    while (readBytes < length) {
                        readReturn = tcpIn.read(message, readBytes, length - readBytes);
                        if (readReturn > 0) {
                            readBytes += readReturn;
                        } else {
                            Log.e("TCP", "could not read message content: -1");
                            emit_connectionListener_connectionClosed();
                            connected = false;
                            return;
                        }
                    }

                    if (message != null) {
                        Log.d("TCP", "Message received: " + message.length);
                        emit_networkListener_messageReceived(message);
                    } else {
                        Log.e("TCP", "could not read message: null");
                    }

                } catch (IOException e) {
                    Log.e("TCP", "could not receive message: " + e.getMessage());
                    emit_connectionListener_connectionClosed();
                    connected = false;
                }

                if (tcpSocket.isClosed()) {
                    Log.e("TCP", "connection lost");
                    emit_connectionListener_connectionClosed();
                    connected = false;
                    break;
                }
            }
        }
    }



    /* NetworkListener */
    private Set<NetworkListener> networkListenerSet;

    public interface NetworkListener {
        void onMessageReceived(byte[] message);
    }

    public void addNetworkListener(NetworkListener networkListener) {
        synchronized (this.networkListenerSet) {
            this.networkListenerSet.add(networkListener);
        }
    }

    public void removeNetworkListener(NetworkListener networkListener) {
        synchronized (this.networkListenerSet) {
            this.networkListenerSet.remove(networkListener);
        }
    }

    private void emit_networkListener_messageReceived(byte[] message) {
        synchronized (this.networkListenerSet) {
            Set<NetworkListener> listenerSet = new HashSet<>();
            listenerSet.addAll(this.networkListenerSet);
            for (NetworkListener listener : listenerSet) {
                listener.onMessageReceived(message);
            }
        }
    }


    /* ConnectionListener */
    private Set<ConnectionListener> connectionListenerSet;

    public interface ConnectionListener {
        void onConnectionClosed();
    }

    public void addConnectionListener(ConnectionListener connectionListener) {
        synchronized (this.connectionListenerSet) {
            this.connectionListenerSet.add(connectionListener);
        }
    }

    public void removeConnectionListener(ConnectionListener connectionListener) {
        synchronized (this.connectionListenerSet) {
            this.connectionListenerSet.remove(connectionListener);
        }
    }

    private void emit_connectionListener_connectionClosed() {
        synchronized (this.connectionListenerSet) {
            for (ConnectionListener listener : this.connectionListenerSet) {
                listener.onConnectionClosed();
            }
        }
    }

}
