package tradr.uav.app.services.common;

import android.util.Log;

import com.google.protobuf.InvalidProtocolBufferException;

import tradr.uav.api.common.AssetInformationMsg;
import tradr.uav.api.common.AssetMsg;
import tradr.uav.api.common.AssetStatusMsg;
import tradr.uav.api.common.AssetVelocityMsg;
import tradr.uav.api.common.BRIDGEMsg;
import tradr.uav.api.common.PoseMsg;
import tradr.uav.app.UavApplication;
import tradr.uav.app.model.uav.UAV;
import tradr.uav.app.model.uav.UAVFlightController;

/**
 * Created by tradr on 18.07.17.
 */

public class StateMachineStatus {

    enum StatusState {
        SENDING_STATUS,
        FINISHED
    }

    private StatusState statusState;

    private UAV uav;

    private TradrUavInterfaceClient tradrUavInterfaceClient;
    private NetworkClient networkClient;

    public StateMachineStatus(TradrUavInterfaceClient tradrUavInterfaceClient, NetworkClient networkClient) {
        this.uav = UavApplication.getUav();

        this.tradrUavInterfaceClient = tradrUavInterfaceClient;
        this.networkClient = networkClient;

        this.networkClient.addNetworkListener(this.networkListener);

        stateTransition_init_to_sendingStatus();
    }

    NetworkClient.NetworkListener networkListener = new NetworkClient.NetworkListener() {
        @Override
        public void onMessageReceived(byte[] message) {
            on_networkClient_messageReceived(message);
        }
    };

    private UAVFlightController.DronePoseListener dronePoseListener = new UAVFlightController.DronePoseListener() {
        @Override
        public void onPoseChanged(double latitude, double longitude, double altitude, double roll, double pitch, double yaw, double velX, double velY, double velZ) {
            on_uavFlightController_poseChanged(latitude, longitude, altitude, roll, pitch, yaw, velX, velY, velZ);
        }
    };

    private void stateTransition_init_to_sendingStatus() {

        if (this.uav.isAircraftConnected()) {
            this.uav.flightController.addDronePoseListener(this.dronePoseListener);
        }

        this.statusState = StatusState.SENDING_STATUS;
    }

    private void on_uavFlightController_poseChanged(double latitude, double longitude, double altitude, double roll, double pitch, double yaw, double velX, double velY, double velZ) {

        AssetVelocityMsg.Builder velMsgBuilder = AssetVelocityMsg.newBuilder();
        velMsgBuilder.setVelX((float) velX);
        velMsgBuilder.setVelY((float) velY);
        velMsgBuilder.setVelZ((float) velZ);
        velMsgBuilder.setVelRoll(Float.NaN);
        velMsgBuilder.setVelPitch(Float.NaN);
        velMsgBuilder.setVelYaw(Float.NaN);
        AssetVelocityMsg velMsg = velMsgBuilder.build();

        PoseMsg.Builder poseMsgBuilder = PoseMsg.newBuilder();
        poseMsgBuilder.setAltitude((float) altitude);
        poseMsgBuilder.setLatitude((float) latitude);
        poseMsgBuilder.setLongitude((float) longitude);
        poseMsgBuilder.setRoll((float) roll);
        poseMsgBuilder.setPitch((float) pitch);
        poseMsgBuilder.setYaw((float) yaw);
        PoseMsg poseMsg = poseMsgBuilder.build();



        AssetInformationMsg.Builder infoMsgBuilder = AssetInformationMsg.newBuilder();
        infoMsgBuilder.setAssetVelocity(velMsg);
        infoMsgBuilder.setAssetPose(poseMsg);
        infoMsgBuilder.setBatteryStatus(uav.battery.getBatteryStatus());
        AssetInformationMsg infoMsg = infoMsgBuilder.build();

        AssetStatusMsg.Builder statusMsgBuilder = AssetStatusMsg.newBuilder();
        statusMsgBuilder.addAssetInformation(infoMsg);
        AssetStatusMsg statusMsg = statusMsgBuilder.build();

        AssetMsg.Builder envelopeBuilder = AssetMsg.newBuilder();
        envelopeBuilder.setAssetStatus(statusMsg);
        AssetMsg envelope = envelopeBuilder.build();

        this.networkClient.sendOverUDP(envelope.toByteArray());
    }

    private void on_networkClient_messageReceived(byte[] message) {
        try {
            BRIDGEMsg envelope = BRIDGEMsg.parseFrom(message);

            switch (envelope.getBRIDGEoneofCase()) {
                case REGRESPONSE:
                    break;
            }

        } catch (InvalidProtocolBufferException e) {
            Log.e("TCP", "could not deserialize message: " + e.getMessage());
        }
    }


    @Override
    public void finalize() {
        this.networkClient.removeNetworkListener(this.networkListener);

        if (this.uav.isAircraftConnected()) {
            this.uav.flightController.removeDronePoseListener(this.dronePoseListener);
        }
    }
}
