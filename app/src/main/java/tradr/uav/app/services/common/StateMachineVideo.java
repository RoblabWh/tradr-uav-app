package tradr.uav.app.services.common;

import android.util.Log;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import tradr.uav.api.common.AssetMsg;
import tradr.uav.api.common.BRIDGEMsg;
import tradr.uav.api.common.VideostreamStartAckMsg;
import tradr.uav.api.common.VideostreamDataMsg;
import tradr.uav.api.common.VideostreamStartReqMsg;
import tradr.uav.api.common.VideostreamStopReqMsg;
import tradr.uav.app.UavApplication;
import tradr.uav.app.model.uav.UAV;
import tradr.uav.app.model.uav.UAVLiveStream;

/**
 * Created by tradr on 18.07.17.
 */

public class StateMachineVideo {

    enum VideoState {
        SENDING_VIDEODATA_OFF,
        SENDING_VIDEODATA_ON,
        FINISHED
    }

    private VideoState videoState;

    private UAV uav;

    private long i = 0;

    private TradrUavInterfaceClient tradrUavInterfaceClient;
    private NetworkClient networkClient;

    public StateMachineVideo(TradrUavInterfaceClient tradrUavInterfaceClient, NetworkClient networkClient) {
        this.uav = UavApplication.getUav();

        this.tradrUavInterfaceClient = tradrUavInterfaceClient;
        this.networkClient = networkClient;

        this.networkClient.addNetworkListener(networkListener);

        stateTransition_init_to_sendingVideoDataOff();
    }


    private NetworkClient.NetworkListener networkListener = new NetworkClient.NetworkListener() {
        @Override
        public void onMessageReceived(byte[] message) {
            on_networkClient_messageReceived(message);
        }
    };


    private UAVLiveStream.H264Listener h264Listener = new UAVLiveStream.H264Listener() {
        @Override
        public void onDataReceived(byte[] data, int size) {
            on_uavCamera_dataArrived(data, size);
        }
    };


    private void stateTransition_init_to_sendingVideoDataOff() {

        this.videoState = VideoState.SENDING_VIDEODATA_OFF;
    }

    private void stateTransition_sendingVideoDataOff_to_sendingVideoDataOn() {

        if (this.uav.isAircraftConnected()) {
            this.uav.liveStream.addH264Listener(h264Listener);
        }

        this.videoState = VideoState.SENDING_VIDEODATA_ON;
    }

    private void stateTransition_sendingVideoDataOn_to_sendingVideoDataOff() {

        if (this.uav.isAircraftConnected()) {
            this.uav.liveStream.removeH264Listener(h264Listener);
        }

        this.videoState = VideoState.SENDING_VIDEODATA_OFF;
    }

    private void on_networkClient_messageReceived(byte[] message) {

        try {
            BRIDGEMsg envelope = BRIDGEMsg.parseFrom(message);

            switch (envelope.getBRIDGEoneofCase()) {
                case STARTVIDEOSTREAMREQUEST:
                    if (this.videoState == VideoState.SENDING_VIDEODATA_OFF) {
                        answerRequest(envelope.getStartVideostreamRequest());
                    } else if (this.videoState == VideoState.SENDING_VIDEODATA_ON) {
                        // Error
                    } else if (this.videoState == VideoState.FINISHED) {
                        // Error
                    } else {
                        // Error
                    }
                    break;
                case STOPVIDEOSTREAMREQUEST:
                    if (this.videoState == VideoState.SENDING_VIDEODATA_OFF) {
                        // Error
                    } else if (this.videoState == VideoState.SENDING_VIDEODATA_ON) {
                        stateTransition_sendingVideoDataOn_to_sendingVideoDataOff();
                    } else if (this.videoState == VideoState.FINISHED) {
                        // Error
                    } else {
                        // Error
                    }
                    break;
            }

        } catch (InvalidProtocolBufferException e) {
            Log.e("TCP", "could not deserialize message: " + e.getMessage());
        }

    }

    private void answerRequest(VideostreamStartReqMsg request) {

        if (true) {
            VideostreamStartAckMsg.Builder responseMsgBuilder = VideostreamStartAckMsg.newBuilder();
            if (uav.isAircraftConnected()) {
                responseMsgBuilder.setIframeInjection(uav.liveStream.isIframeNecessary());
                if (uav.liveStream.isIframeNecessary()) {
                    responseMsgBuilder.setIframeData(ByteString.copyFrom(uav.liveStream.getIframe()));
                }
            }
            VideostreamStartAckMsg responseMsg = responseMsgBuilder.build();

            AssetMsg.Builder envelopeBuilder = AssetMsg.newBuilder();
            envelopeBuilder.setStartVideostreamAck(responseMsg);
            AssetMsg envelope = envelopeBuilder.build();

            this.networkClient.sendOverTCP(envelope.toByteArray());

            stateTransition_sendingVideoDataOff_to_sendingVideoDataOn();

        } else {
            VideostreamStartAckMsg.Builder responseMsgBuilder = VideostreamStartAckMsg.newBuilder();
            //responseMsgBuilder.setVideoStarted(true);
            VideostreamStartAckMsg responseMsg = responseMsgBuilder.build();

            AssetMsg.Builder envelopeBuilder = AssetMsg.newBuilder();
            envelopeBuilder.setStartVideostreamAck(responseMsg);
            AssetMsg envelope = envelopeBuilder.build();

            this.networkClient.sendOverTCP(envelope.toByteArray());
        }
    }


    private void on_uavCamera_dataArrived(byte[] data, int size)
    {
        if (i < 100) {
            Log.d("TCP", "Videodata-Key: " + i + "\t" + data[5]);
        }
        i++;
        VideostreamDataMsg.Builder videostreamDataMsgBuilder = VideostreamDataMsg.newBuilder();
        videostreamDataMsgBuilder.setData(ByteString.copyFrom(data));
        videostreamDataMsgBuilder.setSize(size);
        VideostreamDataMsg videostreamDataMsg = videostreamDataMsgBuilder.build();

        AssetMsg.Builder envelopeBuilder = AssetMsg.newBuilder();
        envelopeBuilder.setVideostreamData(videostreamDataMsg);
        AssetMsg envelope = envelopeBuilder.build();

        this.networkClient.sendOverUDP(envelope.toByteArray());
    }


    @Override
    public void finalize() {
        this.networkClient.removeNetworkListener(this.networkListener);
        if (uav.isAircraftConnected()) {
            this.uav.liveStream.removeH264Listener(this.h264Listener);
        }
    }
}
