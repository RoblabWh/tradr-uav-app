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

import java.util.ArrayList;
import java.util.List;

import tradr.uav.app.R;
import tradr.uav.app.model.map.MapWidget;
import tradr.uav.app.model.task.TaskBuilder.AreaTaskBuilderOld;
import tradr.uav.app.model.task.Task.Task;
import tradr.uav.app.model.task.Task.Waypoint;
import tradr.uav.app.utils.ToastUtils;


public class TaskReceivedFragment extends Fragment {

    private enum STATE {
        RECEIVED_1_OFFER,
        RECEIVED_2_EVALUATE_WAYPOINTS,
        FINISH
    }


    public static TaskReceivedFragment newInstance(MapWidget mapWidget, Task task) {
        TaskReceivedFragment fragment = new TaskReceivedFragment();
        fragment.setMapWidget(mapWidget);
        fragment.setTask(task);
        return fragment;
    }




    private STATE state;

    private View view;

    private Button btnCancel;

    private FragmentManager fragmentManager;

    private TaskReceivedFragment_1_Offer        taskReceivedFragment_1_Offer;
    private TaskReceivedFragment_2_WaypointEval taskReceivedFragment_2_WaypointEval;


    private Task task;

    private MapWidget mapWidget;

    private AreaTaskBuilderOld areaTaskBuilder;

    private InteractionListener listener;




    public TaskReceivedFragment() {

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

        view = inflater.inflate(R.layout.fragment_taskreceived, container, false);

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


    private void setTask(Task task) {
        this.task = task;
    }







    private void initState(Object... param) {
        stateTransition_init_to_1offer();
        state = STATE.RECEIVED_1_OFFER;
    }

    private void switchState(STATE newState, Object... param) {
        switch (state)
        {
            case RECEIVED_1_OFFER:
                if (newState == STATE.RECEIVED_2_EVALUATE_WAYPOINTS) {
                    stateTransition_1offer_to_2evaluateWaypoints();
                    state = STATE.RECEIVED_2_EVALUATE_WAYPOINTS;
                } else {
                    ToastUtils.setResultToToast("State transition not possible");
                }
                break;


            case RECEIVED_2_EVALUATE_WAYPOINTS:
                if (newState == STATE.FINISH) {
                    stateTransition_2evaluateWaypoints_to_finish();
                    state = STATE.FINISH;
                } else {
                    ToastUtils.setResultToToast("State transition not possible");
                }
                break;

        }
    }

    private void stateTransition_init_to_1offer() {
        areaTaskBuilder = new AreaTaskBuilderOld();

        mapWidget.clearMapMarker();

        showTaskReceivedFragment_1_Offer();
    }

    private void stateTransition_1offer_to_2evaluateWaypoints() {
        List<LatLng> waypoints = new ArrayList<>(task.getNumberOfWaypoints());
        for (Waypoint waypoint : task.getWaypointList()) {
            waypoints.add(new LatLng(waypoint.getLatitude(), waypoint.getLongitude()));
        }
        mapWidget.drawWaypoints(waypoints);

        mapWidget.mapGoTo(waypoints.get(0), 18.0f);

        showTaskReceivedFragment_2_EvaluateWaypoints();
    }



    private void stateTransition_2evaluateWaypoints_to_finish() {
        listener.finished(this.task);
    }



    private void showTaskReceivedFragment_1_Offer() {
        taskReceivedFragment_1_Offer = TaskReceivedFragment_1_Offer.newInstance(mapWidget);

        taskReceivedFragment_1_Offer.setInteractionListener(new TaskReceivedFragment_1_Offer.InteractionListener() {
            @Override
            public void onCanceled() {
                on_taskReceivedFragment_1_Offer_canceled();
            }

            @Override
            public void onAccepted() {
                on_taskReceivedFragment_1_Offer_accepted();
            }
        });


        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.wiz_page, taskReceivedFragment_1_Offer);
        fragmentTransaction.commit();
    }

    private void showTaskReceivedFragment_2_EvaluateWaypoints() {
        taskReceivedFragment_2_WaypointEval = TaskReceivedFragment_2_WaypointEval.newInstance();

        taskReceivedFragment_2_WaypointEval.setInteractionListener(new TaskReceivedFragment_2_WaypointEval.InteractionListener() {
            @Override
            public void onAccepted() {
                on_taskReceivedFragment_2_WaypointEval_accepted();
            }

            @Override
            public void onCanceled() {
                on_taskReceivedFragment_2_WaypointEval_canceled();
            }
        });

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.wiz_page, taskReceivedFragment_2_WaypointEval);
        fragmentTransaction.commit();
    }


    private void hideTaskReceivedFragment_1_Offer() {

        taskReceivedFragment_1_Offer.setInteractionListener(null);

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(taskReceivedFragment_1_Offer);
        fragmentTransaction.commit();

        taskReceivedFragment_1_Offer = null;
    }

    private void hideTaskReceivedFragment_2_WaypointsEval() {

        taskReceivedFragment_2_WaypointEval.setInteractionListener(null);

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(taskReceivedFragment_2_WaypointEval);
        fragmentTransaction.commit();

        taskReceivedFragment_2_WaypointEval = null;
    }





    private void on_taskReceivedFragment_1_Offer_canceled() {
        listener.canceled();
    }

    private void on_taskReceivedFragment_1_Offer_accepted() {
        switchState(STATE.RECEIVED_2_EVALUATE_WAYPOINTS);
    }

    private void on_taskReceivedFragment_2_WaypointEval_accepted() {
        listener.finished(this.task);
    }

    private void on_taskReceivedFragment_2_WaypointEval_canceled() {
        listener.canceled();
    }




    private void on_btnCancle_click(Button btn) {
        listener.canceled();
    }





    public interface InteractionListener {
        void canceled();
        void finished(Task task);
    }

}
