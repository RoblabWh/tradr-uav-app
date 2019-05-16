package tradr.uav.app.activities.fpv;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Timer;

import dji.common.error.DJIError;
import dji.common.flightcontroller.Attitude;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.util.CommonCallbacks;
import dji.sdk.camera.Camera;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.timeline.TimelineElement;
import dji.sdk.mission.timeline.TimelineEvent;
import dji.sdk.mission.timeline.actions.AircraftYawAction;
import dji.sdk.mission.timeline.actions.ShootPhotoAction;
import dji.sdk.sdkmanager.DJISDKManager;

import tradr.uav.app.R;
import tradr.uav.app.UavApplication;
import tradr.uav.app.activities.fpv.virtualStickFlightControl.SendVirtualStickData;
import tradr.uav.app.model.uav.UAV;
import tradr.uav.app.model.task.Operator.PanoramaTaskOperator;
import tradr.uav.app.model.task.PanoramaTask;
import tradr.uav.app.utils.ToastUtils;

import static dji.common.flightcontroller.FlightOrientationMode.AIRCRAFT_HEADING;
import static dji.common.flightcontroller.virtualstick.YawControlMode.ANGLE;
import static dji.sdk.mission.timeline.TimelineEvent.STARTED;
import static dji.sdk.mission.timeline.TimelineEvent.FINISHED;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PanoramaActionFragment.InteractionListener} interface
 * to handle interaction events.
 * Use the {@link PanoramaActionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PanoramaActionFragment extends Fragment {

    /**
     * View
     */
    private View view;

    /**
     * Photo Action Button
     */
    private Button btnPhotoAction;

    /**
     * Panorama Action Listener
     */
    private InteractionListener mListener;

    /**
     * State of the Action
     */
    private boolean isBusy;

    /**
     * Unmanned Aircraft vehicle
     */
    private UAV uav;

    /**
     *
     */
    private FlightController flightController;

    /**
     *
     */
    private Camera camera;

    /**
     *
     */
    private int numberOfTakenPictures;


    private PanoramaTask panoramaTask;
    private PanoramaTaskOperator panoramaTaskOperator;

    /**
     * Preconditions
     */
    boolean runningElement;
    boolean setVirtualStickModeEnabled;
    boolean setFlightOrientationMode;
    boolean setTerrainFollowModeEnabled;
    boolean setTripodModeEnabled;

    /**
     * FlightControlData
     */
    private FlightControlData flightControlData;

    private SendVirtualStickData sendVirtualStickDataPublisher;

    private Timer sendVirtualStickDataTimer;

    private static final float VERTICAL_THROTTLE = 1;

    private static final float ANGLE_OFFSET = 90;

    private static final int BORDER = 360 /(int)ANGLE_OFFSET;

    /**
     * Constructor
     */
    public PanoramaActionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return a new instance of fragment PanoramaAction.
     */
    public static PanoramaActionFragment newInstance() {
        PanoramaActionFragment fragment = new PanoramaActionFragment();
        return fragment;
    }



    /**
     * Called once the fragment is associated with its activity.
     * @param context Context from the activity.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    /**
     * Called to do initial creation of the fragment.
     * @param savedInstanceState A mapping from String keys to
     *                           various Parcelable values from this fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        isBusy = false;
        numberOfTakenPictures = 0;
        uav = UavApplication.getUav();
        super.onCreate(savedInstanceState);
    }


    /**
     * Creates and returns the view hierarchy associated with the fragment.
     * @param inflater Instantiates a layout XML file into its corresponding View objects.
     * @param container A ViewGroup is a special view that can
     *                  contain other views (called children.) The view group is the
     *                  base class for layouts and views containers.
     * @param savedInstanceState A mapping from String keys to
     *                           various Parcelable values from this fragment.
     * @return the basic building block representation for user interface components.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_panorama_action, container, false);

        initUI();

        return view;
    }



    /**
     * Initialize the User Interface.
     */
    private void initUI() {

        btnPhotoAction = (Button) view.findViewById(R.id.btn_photo_action);

        registerCallback();

    }


    /**
     * Set on click listener for the Photo Action button.
     */
    private void registerCallback() {

        btnPhotoAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnPanoramaTask_click((Button) v);
            }
        });
    }


    private final float yawAngle = 360f / 8;

    private void on_btnPanoramaTask_click(Button btn) {
        /*panoramaTask = new PanoramaTask();
        panoramaTask.setCameraAngles(0,45,90);
        panoramaTaskOperator = panoramaTask.generatePanoramaTask();
        panoramaTaskOperator.startMission(new Operator.CallBack() {
            @Override
            public void onResult(Exception e) {
                ToastUtils.setResultToToast(e.toString());
            }
        });

        */
        if(DJISDKManager.getInstance().getProduct().isConnected()) {
            MissionControl missionControl = DJISDKManager.getInstance().getMissionControl();
            missionControl.stopTimeline();
            missionControl.unscheduleEverything();

            missionControl.addListener(new MissionControl.Listener() {
                @Override
                public void onEvent(@Nullable TimelineElement timelineElement, TimelineEvent timelineEvent, @Nullable DJIError djiError) {
                    if (timelineElement instanceof ShootPhotoAction && timelineEvent == FINISHED) {
                        ToastUtils.setResultToToast("Foto wurde erfolgreich aufgenommen!");
                    }

                    if (timelineElement instanceof AircraftYawAction && timelineEvent == STARTED) {
                        ToastUtils.setResultToToast("UAV um " + Float.toString(yawAngle)+"° rotieren!");
                    }

                    if(timelineEvent == FINISHED) {
                        ToastUtils.setResultToToast("PanoramaAction erfolgreich beendet!");
                    }
                }
            });


            for (int i = 0; i < 8; i++) {
                missionControl.scheduleElement(new ShootPhotoAction());
                missionControl.scheduleElement(new AircraftYawAction(yawAngle, 20));
            }

            missionControl.startTimeline();
            ToastUtils.setResultToToast("Start PanoramaAction!");
        }else{
            ToastUtils.setResultToToast("Please connect UAV!");
        }

    }
    /**
     * Action that should be executed.
     * @param btn Button
     */
    private void on_btnPhotoAction_click(Button btn) {
        //FlightController flightController = uav.flightController.getFlightController();

        if(uav.isAircraftConnected()) {
            initPhotoActionPrecondition();

            flightController = uav.flightController.getFlightController();
            camera = uav.camera.getCamera();

            if(flightController.isVirtualStickControlModeAvailable()) {
                this.initCallbackSendVirtualStickFlightControlData();
                this.initSendVirtualStickData();
                this.initFlightControlData();
                this.initCamera();
                this.startPhotoAction();
            }
        }else{
            ToastUtils.setResultToToast("Please connect aircraft!");
        }

    }

    private void startPhotoAction() {
        isBusy = true;
        on_sendVirtualStickFlightControlData();
    }


    private void on_sendVirtualStickFlightControlData() {
        if(numberOfTakenPictures < BORDER) {
            camera.startShootPhoto(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        numberOfTakenPictures++;
                        on_afterShootPhoto();
                    } else {
                        ToastUtils.setResultToToast(djiError.toString());
                    }
                }
            });
        }else {
            isBusy = false;
        }
    }


    private void on_afterShootPhoto() {
        this.accumulateYawAngleClockwise(ANGLE_OFFSET);
        sendVirtualStickDataPublisher.setFlightControlData(flightControlData);
        sendVirtualStickDataTimer.schedule(sendVirtualStickDataPublisher, 0, 200);
    }

    private void accumulateYawAngleClockwise(float angle) {
        Attitude attitude = flightController.getState().getAttitude();
        float value = ((float)(attitude.yaw) + angle);
        float newYaw = value > 180
                ? -(value - 180)
                : value;

        flightControlData.setYaw(newYaw);
    }

    private void initCallbackSendVirtualStickFlightControlData() {
        sendVirtualStickDataPublisher.setOnSendVirtualStickFlightControlDataListener(
                new SendVirtualStickData.onSendVirtualStickFlightControlData() {
                    @Override
                    public void onFinished() {
                        on_sendVirtualStickFlightControlData();
                    }

                    @Override
                    public void onCanceled(DJIError djiError) {
                        ToastUtils.setResultToToast(djiError.toString());
                    }
                });
    }

    private void initCamera() {
        uav.camera.configCamera();
    }

    private void initSendVirtualStickData() {
        sendVirtualStickDataPublisher = new SendVirtualStickData(flightController);
        sendVirtualStickDataTimer = new Timer();
    }

    private void initFlightControlData() {

        Attitude attitude = flightController.getState().getAttitude();

        flightControlData = new FlightControlData(
                (float)attitude.roll,
                (float)attitude.pitch,
                (float)attitude.yaw,
                VERTICAL_THROTTLE);
    }

    private boolean initPhotoActionPrecondition() {
        //FlightController flightController = uav.flightController.getFlightController();

        /*The current element that is running in the Timeline. If the Timeline is not running,
         then this is null.*/
        runningElement = DJISDKManager.getInstance().getMissionControl().getRunningElement() == null;

        /*Enables/disables virtual stick control mode. By enabling virtual stick control mode,
         the aircraft can be controlled using sendVirtualStickFlightControlData*/
        flightController.setVirtualStickModeEnabled(true, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    errorCheck(setVirtualStickModeEnabled,djiError);
                }
            });

        /*Tells the aircraft how to interpret flight commands for forward, backward,
         left and right. See the Flight Controller User Guide for more information.*/
        flightController.setFlightOrientationMode(AIRCRAFT_HEADING, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                    errorCheck(setFlightOrientationMode,djiError);
            }
        });

        /*Enable/disable terrain follow mode. The aircraft uses height information gathered by
         the onboard ultrasonic system and its downward facing cameras to keep flying at the same
         height above the ground.*/
        flightController.setTerrainFollowModeEnabled(false, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                    errorCheck(setTerrainFollowModeEnabled,djiError);
            }
        });

        /*Enable/disable terrain follow mode. The aircraft uses height information gathered
         by the onboard ultrasonic system and its downward facing cameras to keep flying at
         the same height above the ground.*/

        flightController.setTripodModeEnabled(false, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                errorCheck(setTripodModeEnabled,djiError);
            }
        });

        /*Sets whether virtual stick yaw controller changes aircraft's
         heading by angle or by angular velocity.*/
        flightController.setYawControlMode(ANGLE);

        return runningElement &&
                setVirtualStickModeEnabled &&
                setFlightOrientationMode &&
                setTerrainFollowModeEnabled &&
                setTripodModeEnabled;

    }

    private void errorCheck(boolean var, DJIError djiError) {
        var = (djiError == null);
        if(!var) {
            StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
            StackTraceElement e = stacktrace[2];//maybe this number needs to be corrected
            String methodName = e.getMethodName();
            ToastUtils.setResultToToast(methodName + " errorCheck: " + djiError.toString());
        }
    }




    /**
     * State of the Photo Action
     * @return true if the Photo Action is executing and false if not.
     */
    public boolean isBusy() {
        return isBusy;
    }


    /**
     * Called immediately prior to the fragment no longer being associated with its activity.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * Sets the interaction listener for the Photo Action.
     * @param interactionListener
     */
    public void setInteractionListener(InteractionListener interactionListener) {
        this.mListener = interactionListener;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface InteractionListener {
        void finished();
        void canceld(DJIError diiError);
    }

}
