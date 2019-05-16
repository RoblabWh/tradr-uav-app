package tradr.uav.app.services.common;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * TCPClient
 */

public class TCPClient extends Thread {

    int mPriority;
    int mTid = -1;
    Looper mLooper;

    /*
    private static final String Tag = "TCPClient";
    //private static String IP = "192.168.7.128";
    private static String IP = "192.168.29.73";
    private static int PORT = 10000;
    */

    private Handler handler;
    private Handler guiHandler;

    private char[] sendQueue;
    private char[] receivedQueue;

    private PrintWriter tcpOut;
    private BufferedReader tcpIn;


    private boolean mRun = false;

    public TCPClient(Handler guiHandler)  {

        //super("TCPClient");
        this.guiHandler = guiHandler;
    }


    /**
     * Call back method that can be explicitly overridden if needed to execute some
     * setup before Looper loops.
     */
    protected void onLooperPrepared() {
    }



//    @Override
//    public void run() {
//
//        mRun = true;
//
//        mTid = Process.myTid();
//
//        Looper.prepare();
//
//
//        try {
//            Log.v("Thread", "started..");
//
//            InetAddress serverAdr = InetAddress.getByName(IP);
//            Log.v("Socket", "Addr:" + serverAdr.getHostAddress());
//
//            Socket socket = new Socket(serverAdr, PORT);
//            Log.v("Socket", "Socket open..");
//
//            // Create PrintWriter object for sending messages to server.
//            tcpOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
//
//            //Create BufferedReader object for receiving messages from server.
//            tcpIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//            // @ TODO Exception Handling
//        } catch (Exception ex) {
//
//            Log.e("TCP", "C: Error", ex);
//        }
//
//
//
//
//
//        synchronized (this) {
//            mLooper = Looper.myLooper();
//            notifyAll();
//
//            handler = new Handler() {
//                @Override
//                public void handleMessage(Message msg) {
//                    // process incoming messages here
//                    Log.v("Socket", " Handle message...");
//                    sendQueue = "Test".toCharArray();
//
//                    if (sendQueue != null) {
//                        Log.v("Socket", " Send message...");
//
//                        tcpOut.println(sendQueue);
//                        tcpOut.flush();
//
//                        sendQueue = null;
//
//                    }
//                }
//
//            };
//        }
//
//        Process.setThreadPriority(mPriority);
//
//
//
//
//        /* Create a Thread for Message receiving */
//        Thread thread = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    while(true) {
//                        receivedQueue = tcpIn.readLine().toCharArray();
//
//                        Message msg = new Message();
//                        msg.obj = receivedQueue.toString();
//                        Log.v("Socket", "Reveived: " +receivedQueue.toString());
//                        msg.setTarget(guiHandler);
//                        msg.sendToTarget();
//
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//
//
//        thread.start();
//
//        onLooperPrepared();
//        Looper.loop();
//        Log.v("Socket", "Looper exited");
//
//        mTid = -1;
//    }


    /**
     * This method returns the Looper associated with this thread. If this thread not been started
     * or for any reason is isAlive() returns false, this method will return null. If this thread
     * has been started, this method will block until the looper has been initialized.
     * @return The looper.
     */
    public Looper getLooper() {
        if (!isAlive()) {
            return null;
        }

        // If the thread has been started, wait until the looper has been created.
        synchronized (this) {
            while (isAlive() && mLooper == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
        return mLooper;
    }



    public synchronized Handler getHandler() {
        while (handler == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                //Ignore and try again.
            }
        }
        return handler;
    }



    public void stopClient() {

        mRun = false;

        if (tcpOut != null) {
            tcpOut.flush();
            tcpOut.close();
        }

        tcpIn = null;
        tcpOut = null;
    }



    public void sendMessage(String message){

        synchronized (sendQueue) {

            sendQueue = message.toCharArray();
        }
    }


}