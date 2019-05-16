package tradr.uav.app.activities.map;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import dji.common.error.DJIError;
import dji.common.mission.waypoint.WaypointMissionUploadEvent;
import tradr.uav.app.R;
import tradr.uav.app.UavApplication;
import tradr.uav.app.model.map.MapWidget;
import tradr.uav.app.model.task.Task.Task;
import tradr.uav.app.model.task.TaskInProgress.TaskInProgress;
import tradr.uav.app.model.uav.UAV;
import tradr.uav.app.model.uav.UAVTaskOperator;
import tradr.uav.app.utils.ToastUtils;


public class TaskUploadFragment extends Fragment {

    public static TaskUploadFragment newInstance(MapWidget mapWidget, Task task) {
        TaskUploadFragment fragment = new TaskUploadFragment();
        fragment.setMapWidget(mapWidget);
        fragment.setTask(task);
        return fragment;
    }

    private UAV uav;

    private View view;

    private MapWidget mapWidget;
    private Task task;

    private TaskInProgress taskInProgress;

    private Button btnCancel;

    private Button btnUpload;

    private InteractionListener listener;

    public TaskUploadFragment() {
        // Required empty public constructor
    }


    private void setMapWidget(MapWidget mapWidget) {
        this.mapWidget = mapWidget;
    }

    private void setTask(Task task) {
        this.task = task;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.uav = UavApplication.getUav();
    }

    public void setInteractionListener(InteractionListener listener) {
        this.listener = listener;
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_taskupload, container, false);

        initUI();

        return view;
    }


    private void initUI() {

        btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        btnUpload = (Button) view.findViewById(R.id.btn_upload);

        registerCallback();
    }


    private void registerCallback() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnCancel_click((Button) v);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnUpload_click((Button) v);
            }
        });


    }


    private void on_btnCancel_click(Button btn) {
        if (listener != null) {
            listener.canceled();
        }
    }

    private void on_btnUpload_click(Button btn) {
        if (uav.isAircraftConnected()) {
            uav.taskOperator.addUploadNotificationListener(new UAVTaskOperator.UploadNotificationListener() {
                @Override
                public void uploadStarted() {
                    on_taskHandler_uploadStarted();
                }

                @Override
                public void uploadUpdate(WaypointMissionUploadEvent event) {
                    on_taskHandler_uploadUpdate(event);
                }

                @Override
                public void uploadSuccessfullyFinished() {
                    on_taskHandler_uploadSuccessfullyFinished();
                }

                @Override
                public void uploadFailed(DJIError error) {
                    on_taskHandler_uploadFailed(error);
                }
            });

            this.uav.taskOperator.configTask(task);

            this.uav.taskOperator.uploadTask();

            this.taskInProgress = this.uav.taskOperator.getCurrentTaskInProgress();
        } else {
            ToastUtils.setResultToToast("Aircraft not connected");
        }
    }

    private void on_taskHandler_uploadStarted() {

    }

    private void on_taskHandler_uploadUpdate(WaypointMissionUploadEvent event) {

    }

    private void on_taskHandler_uploadSuccessfullyFinished() {
        if (listener != null) {
            listener.finished(this.taskInProgress);
        }
    }

    private void on_taskHandler_uploadFailed(DJIError error) {

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




    public interface InteractionListener {
        void canceled();
        void finished(TaskInProgress taskInProgress);
    }

}
