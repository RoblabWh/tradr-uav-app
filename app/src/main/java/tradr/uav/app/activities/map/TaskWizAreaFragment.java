package tradr.uav.app.activities.map;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import dji.common.mission.waypoint.WaypointMission;
import dji.common.mission.waypoint.WaypointMissionFinishedAction;
import tradr.uav.app.R;
import tradr.uav.app.model.map.MapWidget;
import tradr.uav.app.model.task.Task.Pose;
import tradr.uav.app.model.task.Task.Task;
import tradr.uav.app.model.task.TaskBuilder.AreaTaskBuilder;
import tradr.uav.app.model.task.TaskBuilder.AreaTaskBuilderOld;
import tradr.uav.app.utils.ToastUtils;


public class TaskWizAreaFragment extends Fragment {

    private enum STATE {
        AREA_1_PLACE_POLYGON_POINTS,
        AREA_2_CONFIG,
        AREA_3_EVALUATE_WAYPOINTS,
        FINISH
    }


    public static TaskWizAreaFragment newInstance(MapWidget mapWidget) {
        TaskWizAreaFragment fragment = new TaskWizAreaFragment();
        fragment.setMapWidget(mapWidget);
        return fragment;
    }




    private STATE state;

    private View view;

    private Button btnCancel;

    private FragmentManager fragmentManager;

    private TaskWizAreaFragment_1_Polygon taskWizAreaFragment_1_Polygon;
    private TaskWizAreaFragment_2_Config taskWizAreaFragment_2_Cofig;
    private TaskWizAreaFragment_3_WaypointsEval taskWizAreaFragment_3_WaypointEval;



    private MapWidget mapWidget;

    private AreaTaskBuilder areaTaskBuilder;

    private InteractionListener listener;




    public TaskWizAreaFragment() {

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

        view = inflater.inflate(R.layout.fragment_taskwiz_area, container, false);

        initUI();

        return view;
    }


    private void initUI() {

        fragmentManager = this.getChildFragmentManager();

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
        stateTransition_init_to_1placePolygonePoints();
        state = STATE.AREA_1_PLACE_POLYGON_POINTS;
    }

    private void switchState(STATE newState, Object... param) {
        switch (state)
        {
            case AREA_1_PLACE_POLYGON_POINTS:
                if (newState == STATE.AREA_2_CONFIG) {
                    stateTransition_1placePolygonePoints_to_2config();
                    state = STATE.AREA_2_CONFIG;
                } else {
                    ToastUtils.setResultToToast("State transition not possible");
                }
                break;


            case AREA_2_CONFIG:
                if (newState == STATE.AREA_3_EVALUATE_WAYPOINTS) {
                    stateTransition_2config_to_3evaluateWaypoints();
                    state = STATE.AREA_3_EVALUATE_WAYPOINTS;
                } else if (newState == STATE.AREA_1_PLACE_POLYGON_POINTS) {
                    stateTransition_2config_to_1placePolygonePoints();
                    state = STATE.AREA_1_PLACE_POLYGON_POINTS;
                } else {
                    ToastUtils.setResultToToast("State transition not possible");
                }
                break;

            case AREA_3_EVALUATE_WAYPOINTS:
                if (newState == STATE.FINISH) {
                    stateTransition_3evaluateWaypoints_to_finish((Task) param[0]);
                    state = STATE.FINISH;
                }
                else if (newState == STATE.AREA_2_CONFIG) {
                    stateTransition_3evaluateWaypoints_to_2config();
                    state = STATE.AREA_2_CONFIG;
                } else {
                    ToastUtils.setResultToToast("State transition not possible");
                }
                break;

        }
    }

    private void stateTransition_init_to_1placePolygonePoints() {
        areaTaskBuilder = new AreaTaskBuilder();

        mapWidget.clearMapMarker();

        showTaskWizAreaFragment_1_Polygon();
    }

    private void stateTransition_1placePolygonePoints_to_2config() {
        showTaskWizAreaFragment_2_Config();
    }

    private void stateTransition_2config_to_3evaluateWaypoints() {
        mapWidget.clearMapMarker();

        mapWidget.drawWaypoints(areaTaskBuilder.getWaypoints());

        showTaskWizAreaFragment_3_WaypointsEval();
    }

    private void stateTransition_2config_to_1placePolygonePoints() {
        areaTaskBuilder = new AreaTaskBuilder();

        mapWidget.clearMapMarker();

        showTaskWizAreaFragment_1_Polygon();
    }

    private void stateTransition_3evaluateWaypoints_to_2config() {
        showTaskWizAreaFragment_2_Config();
    }

    private void stateTransition_3evaluateWaypoints_to_finish(Task task) {
        listener.finished(task);
    }



    private void showTaskWizAreaFragment_1_Polygon() {
        taskWizAreaFragment_1_Polygon = TaskWizAreaFragment_1_Polygon.newInstance(mapWidget);

        taskWizAreaFragment_1_Polygon.setInteractionListener(new TaskWizAreaFragment_1_Polygon.InteractionListener() {
            @Override
            public void onFinished(List<LatLng> polygon) {
                on_taskWizAreaFragment_1_Polygon_finished(polygon);
            }
        });

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.wiz_page, taskWizAreaFragment_1_Polygon);
        fragmentTransaction.commit();
    }

    private void showTaskWizAreaFragment_2_Config() {
        taskWizAreaFragment_2_Cofig = TaskWizAreaFragment_2_Config.newInstance();

        taskWizAreaFragment_2_Cofig.setInteractionListener(new TaskWizAreaFragment_2_Config.InteractionListener() {
            @Override
            public void onNext(int overlap, float altitude, float speed, int gimbalPitch) {
                on_taskWizAreaFragment_2_Config_next(overlap, altitude, speed, gimbalPitch);
            }

            @Override
            public void onBack() {
                on_taskWizAreaFragment_2_Config_back();
            }
        });

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.wiz_page, taskWizAreaFragment_2_Cofig);
        fragmentTransaction.commit();
    }

    private void showTaskWizAreaFragment_3_WaypointsEval() {
        taskWizAreaFragment_3_WaypointEval = TaskWizAreaFragment_3_WaypointsEval.newInstance();

        taskWizAreaFragment_3_WaypointEval.setInteractionListener(new TaskWizAreaFragment_3_WaypointsEval.InteractionListener() {
            @Override
            public void onAccepted() {
                on_taskWizAreaFragment_3_WaypointEval_accepted();
            }

            @Override
            public void onRefused() {
                on_taskWizAreaFragment_3_WaypointEval_refused();
            }
        });

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.wiz_page, taskWizAreaFragment_3_WaypointEval);
        fragmentTransaction.commit();
    }

    private void hideTaskWizAreaFragment_1_Polygon() {

        taskWizAreaFragment_1_Polygon.setInteractionListener(null);

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(taskWizAreaFragment_1_Polygon);
        fragmentTransaction.commit();

        taskWizAreaFragment_1_Polygon = null;
    }

    private void hideTaskWizAreaFragment_2_Config() {

        taskWizAreaFragment_2_Cofig.setInteractionListener(null);

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(taskWizAreaFragment_2_Cofig);
        fragmentTransaction.commit();

        taskWizAreaFragment_2_Cofig = null;
    }

    private void hideTaskWizAreaFragment_3_WaypointsEval() {

        taskWizAreaFragment_3_WaypointEval.setInteractionListener(null);

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(taskWizAreaFragment_3_WaypointEval);
        fragmentTransaction.commit();

        taskWizAreaFragment_3_WaypointEval = null;
    }





    private void on_taskWizAreaFragment_1_Polygon_finished(List<LatLng> polygon) {
        areaTaskBuilder.setPolygon(polygon);
        switchState(STATE.AREA_2_CONFIG);
    }

    private void on_taskWizAreaFragment_2_Config_next(int overlap, float altitude, float speed, int gimbalPitch) {
        areaTaskBuilder.setOverlap((double) overlap);
        areaTaskBuilder.setDistance(5.0);
        areaTaskBuilder.setBuildMethod(AreaTaskBuilder.BuildMethod.BY_DISTANCE);
        areaTaskBuilder.setFlightHeight(altitude);
        areaTaskBuilder.setSpeed(speed);
        areaTaskBuilder.addCameraPose(new Pose(0.0, 0.0, 0.0, 0.0, (double) gimbalPitch, 0.0));

        switchState(STATE.AREA_3_EVALUATE_WAYPOINTS);
    }

    private void on_taskWizAreaFragment_2_Config_back() {
        switchState(STATE.AREA_1_PLACE_POLYGON_POINTS);
    }

    private void on_taskWizAreaFragment_3_WaypointEval_accepted() {
        Task task = areaTaskBuilder.build();
        switchState(STATE.FINISH, task);
    }

    private void on_taskWizAreaFragment_3_WaypointEval_refused() {
        switchState(STATE.AREA_2_CONFIG);
    }



    private void on_btnCancle_click(Button btn) {
        listener.canceled();
    }





    public interface InteractionListener {
        void canceled();
        void finished(Task task);
    }

}
