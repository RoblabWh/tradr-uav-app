package tradr.uav.app.activities.map;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import dji.common.mission.waypoint.WaypointMissionFinishedAction;
import tradr.uav.app.R;
import tradr.uav.app.model.map.MapWidget;
import tradr.uav.app.model.task.Task.Task;
import tradr.uav.app.model.task.TaskBuilder.CircleTaskBuilder;
import tradr.uav.app.model.task.TaskBuilder.CircleTaskBuilderOld;
import tradr.uav.app.utils.ToastUtils;


public class TaskWizCircleFragment extends Fragment {

    private enum STATE {
        CIRC_1_PLACE_POI,
        CIRC_2_PLACE_RADIUS,
        CIRC_3_EVALUATE_CIRC,
        CIRC_4_CONFIG,
        CIRC_5_EVALUATE_WAYPOINTS,
        FINISH
    }


    public static TaskWizCircleFragment newInstance(MapWidget mapWidget) {
        TaskWizCircleFragment fragment = new TaskWizCircleFragment();
        fragment.setMapWidget(mapWidget);
        return fragment;
    }





    private STATE state;

    private View view;

    private Button btnCancel;

    private FragmentManager fragmentManager;

    private TaskWizCircleFragment_1_PoI taskWizCircleFragment_1_PoI;
    private TaskWizCircleFragment_2_Radius taskWizCircleFragment_2_Radius;
    private TaskWizCircleFragment_3_CircEval taskWizCircleFragment_3_CircEval;
    private TaskWizCircleFragment_4_Config taskWizCircleFragment_4_Config;
    private TaskWizCircleFragment_5_WaypointsEval taskWizCircleFragment_5_WaypointsEval;



    private MapWidget mapWidget;


    private CircleTaskBuilder circTaskBuilder;


    private InteractionListener listener;





    public TaskWizCircleFragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setInteractionListener(InteractionListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_taskwiz_circ, container, false);

        initUI();

        return view;
    }


    private void initUI() {

        fragmentManager = this.getChildFragmentManager();

        taskWizCircleFragment_1_PoI = TaskWizCircleFragment_1_PoI.newInstance(mapWidget);
        taskWizCircleFragment_2_Radius = TaskWizCircleFragment_2_Radius.newInstance(mapWidget);
        taskWizCircleFragment_3_CircEval = TaskWizCircleFragment_3_CircEval.newInstance();
        taskWizCircleFragment_4_Config = TaskWizCircleFragment_4_Config.newInstance();
        taskWizCircleFragment_5_WaypointsEval = TaskWizCircleFragment_5_WaypointsEval.newInstance();

        btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        registerCallback();

        initState();
    }


    private void registerCallback() {

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnCancle_click((Button) v);
            }
        });
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }



    private void setMapWidget(MapWidget mapWidget) {
        this.mapWidget = mapWidget;
    }



    private void initState(Object... param) {
        stateTransition_init_to_1placePOI();
        state = STATE.CIRC_1_PLACE_POI;
    }

    private void switchState(STATE newState, Object... param) {
        switch (state) {
            case CIRC_1_PLACE_POI:
                if (newState == STATE.CIRC_2_PLACE_RADIUS) {
                    stateTransition_1placePOI_to_2placeRadius();
                    state = STATE.CIRC_2_PLACE_RADIUS;
                } else {
                    ToastUtils.setResultToToast("State transition not possible");
                }
                break;

            case CIRC_2_PLACE_RADIUS:
                if (newState == STATE.CIRC_3_EVALUATE_CIRC) {
                    stateTransition_2placeRadius_to_3evaluateCirc();
                    state = STATE.CIRC_3_EVALUATE_CIRC;
                } else if (newState == STATE.CIRC_1_PLACE_POI) {
                    stateTransition_2placeRadius_to_1placePOI();
                    state = STATE.CIRC_1_PLACE_POI;
                } else {
                    ToastUtils.setResultToToast("State transition not possible");
                }
                break;

            case CIRC_3_EVALUATE_CIRC:
                if (newState == STATE.CIRC_4_CONFIG) {
                    stateTransition_3evaluateCirc_to_4config();
                    state = STATE.CIRC_4_CONFIG;
                } else if (newState == STATE.CIRC_1_PLACE_POI) {
                    stateTransition_3evaluateCirc_to_1placePOI();
                    state = STATE.CIRC_1_PLACE_POI;
                } else {
                    ToastUtils.setResultToToast("State transition not possible");
                }
                break;

            case CIRC_4_CONFIG:
                if (newState == STATE.CIRC_5_EVALUATE_WAYPOINTS) {
                    stateTransition_4config_to_5evaluateWaypoints();
                    state = STATE.CIRC_5_EVALUATE_WAYPOINTS;
                } else if (newState == STATE.CIRC_1_PLACE_POI) {
                    stateTransition_4config_to_1placePOI();
                    state = STATE.CIRC_1_PLACE_POI;
                } else {
                    ToastUtils.setResultToToast("State transition not possible");
                }
                break;

            case CIRC_5_EVALUATE_WAYPOINTS:
                if (newState == STATE.FINISH) {
                    stateTransition_5evaluateWaypoints_to_finish((Task) param[0]);
                    state = STATE.FINISH;
                }else if (newState == STATE.CIRC_4_CONFIG) {
                    stateTransition_5evaluateWaypoints_to_4config();
                    state = STATE.CIRC_4_CONFIG;
                } else {
                    ToastUtils.setResultToToast("State transition not possible");
                }
                break;
        }
    }

    private void stateTransition_init_to_1placePOI() {
        mapWidget.clearMapMarker();
        circTaskBuilder = new CircleTaskBuilder();

        showTaskWizCircleFragment_1_POI();
    }

    private void stateTransition_1placePOI_to_2placeRadius() {
        showTaskWizCircleFragment_2_Radius();
    }

    private void stateTransition_2placeRadius_to_3evaluateCirc() {


        showTaskWizCircleFragment_3_CircEval();
    }

    private void stateTransition_2placeRadius_to_1placePOI() {
        mapWidget.clearMapMarker();
        circTaskBuilder = new CircleTaskBuilder();

        showTaskWizCircleFragment_1_POI();
    }

    private void stateTransition_3evaluateCirc_to_4config() {
        showTaskWizCircleFragment_4_Config();
    }

    private void stateTransition_3evaluateCirc_to_1placePOI() {
        mapWidget.clearMapMarker();
        circTaskBuilder = new CircleTaskBuilder();

        showTaskWizCircleFragment_1_POI();
    }

    private void stateTransition_4config_to_5evaluateWaypoints() {
        mapWidget.drawWaypoints(circTaskBuilder.getWaypoints());

        showTaskWizCircleFragment_5_WaypointsEval();
    }

    private void stateTransition_4config_to_1placePOI() {
        mapWidget.clearMapMarker();
        circTaskBuilder = new CircleTaskBuilder();

        showTaskWizCircleFragment_1_POI();
    }

    private void stateTransition_5evaluateWaypoints_to_4config() {
        mapWidget.clearMapMarker();

        showTaskWizCircleFragment_4_Config();
    }

    private void stateTransition_5evaluateWaypoints_to_finish(Task task) {
        listener.finished(task);
    }



    private void showTaskWizCircleFragment_1_POI() {

        taskWizCircleFragment_1_PoI.setInteractionListener(new TaskWizCircleFragment_1_PoI.InteractionListener() {


            @Override
            public void onFinished(LatLng pos) {
                on_taskWizCircleFragment_1_PoI_finished(pos);
            }
        });

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.wiz_page, taskWizCircleFragment_1_PoI);
        fragmentTransaction.commit();
    }

    private void showTaskWizCircleFragment_2_Radius() {

        taskWizCircleFragment_2_Radius.setInteractionListener(new TaskWizCircleFragment_2_Radius.InteractionListener() {


            @Override
            public void onFinished(LatLng pos) {
                on_taskWizCircleFragment_2_Radius_finished(pos);
            }
        });

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.wiz_page, taskWizCircleFragment_2_Radius);
        fragmentTransaction.commit();
    }

    private void showTaskWizCircleFragment_3_CircEval() {

        taskWizCircleFragment_3_CircEval.setInteractionListener(new TaskWizCircleFragment_3_CircEval.InteractionListener() {

            @Override
            public void onAccepted() {
                on_taskWizCircleFragment_3_CircEval_accepted();
            }

            @Override
            public void onRefused() {
                on_taskWizCircleFragment_3_CircEval_refused();
            }
        });

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.wiz_page, taskWizCircleFragment_3_CircEval);
        fragmentTransaction.commit();
    }

    private void showTaskWizCircleFragment_4_Config() {

        taskWizCircleFragment_4_Config.setInteractionListener(new TaskWizCircleFragment_4_Config.InteractionListener() {

            @Override
            public void onNext(int numberOfWaypoints, float altitude, float speed, int gimbelPitch) {
                on_taskWizCircleFragment_4_Config_next(numberOfWaypoints, altitude, speed, gimbelPitch);
            }

            @Override
            public void onBack() {
                on_taskWizCircleFragment_4_Config_back();
            }
        });

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.wiz_page, taskWizCircleFragment_4_Config);
        fragmentTransaction.commit();
    }

    private void showTaskWizCircleFragment_5_WaypointsEval() {

        taskWizCircleFragment_5_WaypointsEval.setInteractionListener(new TaskWizCircleFragment_5_WaypointsEval.InteractionListener() {

            @Override
            public void onAccepted() {
                on_taskWizCircleFragment_5_WaypointsEval_accepted();
            }

            @Override
            public void onRefused() {
                on_taskWizCircleFragment_5_WaypointsEval_refused();
            }
        });

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.wiz_page, taskWizCircleFragment_5_WaypointsEval);
        fragmentTransaction.commit();
    }

    private void hideTaskWizCircleFragment_1_POI() {
        taskWizCircleFragment_1_PoI.setInteractionListener(null);

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(taskWizCircleFragment_1_PoI);
        fragmentTransaction.commit();

        taskWizCircleFragment_1_PoI = null;
    }

    private void hideTaskWizCircleFragment_2_Radius() {
        taskWizCircleFragment_2_Radius.setInteractionListener(null);

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(taskWizCircleFragment_2_Radius);
        fragmentTransaction.commit();

        taskWizCircleFragment_2_Radius = null;
    }

    private void hideTaskWizCircleFragment_3_CircEval() {
        taskWizCircleFragment_3_CircEval.setInteractionListener(null);

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(taskWizCircleFragment_3_CircEval);
        fragmentTransaction.commit();

        taskWizCircleFragment_3_CircEval = null;
    }

    private void hideTaskWizCircleFragment_4_Config() {
        taskWizCircleFragment_4_Config.setInteractionListener(null);

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(taskWizCircleFragment_4_Config);
        fragmentTransaction.commit();

        taskWizCircleFragment_4_Config = null;
    }

    private void hideTaskWizCircleFragment_5_WaypointsEval() {
        taskWizCircleFragment_5_WaypointsEval.setInteractionListener(null);

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(taskWizCircleFragment_5_WaypointsEval);
        fragmentTransaction.commit();

        taskWizCircleFragment_5_WaypointsEval = null;
    }



    private void on_taskWizCircleFragment_1_PoI_finished(LatLng pos) {
        mapWidget.markWaypointOnMap(pos);
        circTaskBuilder.setPointOfInterest(pos);
        switchState(STATE.CIRC_2_PLACE_RADIUS);
    }

    private void on_taskWizCircleFragment_2_Radius_finished(LatLng pos) {
        mapWidget.markWaypointOnMap(pos);
        circTaskBuilder.setRadius(pos);

        mapWidget.drawCircOnMap(circTaskBuilder.getPointOfInterest(), circTaskBuilder.getRadius(), 4, Color.BLUE, Color.TRANSPARENT);

        switchState(STATE.CIRC_3_EVALUATE_CIRC);
    }

    private void on_taskWizCircleFragment_3_CircEval_accepted() {
        switchState(STATE.CIRC_4_CONFIG);
    }

    private void on_taskWizCircleFragment_3_CircEval_refused() {
        switchState(STATE.CIRC_1_PLACE_POI);
    }

    private void on_taskWizCircleFragment_4_Config_next(int numberOfWaypoints, float altitude, float speed, int gimbelPitch) {
        circTaskBuilder.setNumberOfWaypoints(numberOfWaypoints);
        circTaskBuilder.setFlightHeight(altitude);
        circTaskBuilder.setSpeed(speed);
        circTaskBuilder.addCameraAngle(gimbelPitch);
        switchState(STATE.CIRC_5_EVALUATE_WAYPOINTS);
    }

    private void on_taskWizCircleFragment_4_Config_back() {
        switchState(STATE.CIRC_1_PLACE_POI);
    }

    private void on_taskWizCircleFragment_5_WaypointsEval_accepted() {
        Task task = circTaskBuilder.build();
        switchState(STATE.FINISH, task);
    }

    private void on_taskWizCircleFragment_5_WaypointsEval_refused() {
        switchState(STATE.CIRC_4_CONFIG);
    }




    private void on_btnCancle_click(Button btn) {
        listener.canceled();
    }




    public interface InteractionListener {
        void canceled();
        void finished(Task task);
    }

}
