package tradr.uav.app.model.task;

import android.support.annotation.Nullable;

import java.util.ArrayList;

import dji.common.gimbal.Attitude;
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.timeline.actions.AircraftYawAction;
import dji.sdk.mission.timeline.actions.GimbalAttitudeAction;
import dji.sdk.mission.timeline.actions.ShootPhotoAction;
import dji.sdk.sdkmanager.DJISDKManager;
import tradr.uav.app.model.task.Operator.PanoramaTaskOperator;

/**
 * Created by Artur Leinweber on 13.06.17.
 */

public class PanoramaTask {

    private ArrayList<Integer> cameraAngles;
    private MissionControl missionControl;
    private PanoramaTaskOperator panoramaTaskOperator;
    private int yawActionSpeed;
    private int numberOfTakenPicture;


    public PanoramaTask() {
        cameraAngles = new ArrayList<>();
        missionControl = DJISDKManager.getInstance().getMissionControl();
        cameraAngles.add(-45);
        yawActionSpeed = 20;
        numberOfTakenPicture = 6;
        this.implementPanoramaTaskOperator();

    }


    private void implementPanoramaTaskOperator() {
        panoramaTaskOperator = new PanoramaTaskOperator() {
            @Override
            public void startMission(@Nullable CallBack callBack) {
                missionControl.startTimeline();
                if(missionControl.isTimelineRunning()) {
                    callBack.onResult(null);
                }else {
                    callBack.onResult(new Exception("MissionsControl.startMission failed!"));
                }
            }

            @Override
            public void resumeMission(@Nullable CallBack callBack) {
                missionControl.resumeTimeline();
                if(missionControl.isTimelineRunning()) {
                    callBack.onResult(null);
                }else {
                    callBack.onResult(new Exception("missionControl.resumeTimeline failed!"));
                }
            }

            @Override
            public void pauseMission(@Nullable CallBack callBack) {
                missionControl.pauseTimeline();
                if(missionControl.isTimelinePaused()) {
                    callBack.onResult(null);
                }else {
                    callBack.onResult(new Exception("missionControl.pauseTimeline failed!"));
                }
            }

            @Override
            public void stopMission(@Nullable CallBack callBack) {
                missionControl.stopTimeline();
                if(!missionControl.isTimelineRunning()) {
                    callBack.onResult(null);
                }else {
                    callBack.onResult(new Exception("missionControl.stopTimeline failed!"));
                }
            }
        };
    }

    public void setCameraAngles(Integer... cameraAngles) {
        this.cameraAngles.clear();
        for(Integer i : cameraAngles) {
            this.cameraAngles.add((-1) * Math.abs(i));
        }
    }

    public void setNumberOfTakenPicture(int numberOfTakenPicture) {
        this.numberOfTakenPicture = numberOfTakenPicture;
    }

    public PanoramaTaskOperator generatePanoramaTask() {
     float yawAngle = 360f/numberOfTakenPicture;
        for(int n = 0; n < numberOfTakenPicture;n++) {
            for (Integer i : cameraAngles) {
                missionControl.scheduleElement(new ShootPhotoAction());
                missionControl.scheduleElement(new GimbalAttitudeAction(new Attitude(i, 0, 0)));
            }
            missionControl.scheduleElement(new AircraftYawAction(yawAngle , yawActionSpeed));
        }

        return panoramaTaskOperator;
    }



}
