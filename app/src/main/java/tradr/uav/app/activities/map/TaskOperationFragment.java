package tradr.uav.app.activities.map;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.File;
import java.text.Format;
import java.text.NumberFormat;

import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.mission.waypoint.WaypointMissionExecutionEvent;
import dji.common.util.CommonCallbacks;
import dji.sdk.camera.DownloadListener;
import dji.sdk.camera.MediaFile;
import tradr.uav.app.R;
import tradr.uav.app.UavApplication;
import tradr.uav.app.model.map.MapWidget;
import tradr.uav.app.model.uav.UAV;
import tradr.uav.app.model.uav.UAVTaskOperator;


public class TaskOperationFragment extends Fragment {

    public static TaskOperationFragment newInstance(MapWidget mapWidget) {
        TaskOperationFragment fragment = new TaskOperationFragment();
        fragment.setMapWidget(mapWidget);
        return fragment;
    }


    private MapWidget mapWidget;

    private View view;

    private Button btnCancel;

    private Button btnStart;
    private Button btnStop;

    private InteractionListener listener;

    private UAV uav;

    private MediaFile lastFotoFile;



    public TaskOperationFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.uav = UavApplication.getUav();
    }

    public void setInteractionListener(InteractionListener listener) {
        this.listener = listener;
    }


    private void setMapWidget(MapWidget mapWidget) {
        this.mapWidget = mapWidget;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_taskoperation, container, false);

        initUI();

        return this.view;
    }


    private void initUI() {

        btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        btnStart = (Button) view.findViewById(R.id.btn_start);
        btnStop = (Button) view.findViewById(R.id.btn_stop);

        //
        // btnStop.setEnabled(false);

        registerCallback();
    }


    private void registerCallback() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnCancel_click((Button) v);
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnStart_click((Button) v);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnStop_click((Button) v);
            }
        });

        uav.taskOperator.addExecutionNotificationListener(new UAVTaskOperator.ExecutionNotificationListener() {
            @Override
            public void executionStarted() {
                on_taskHandler_executionStarted();
            }

            @Override
            public void executionUpdate(WaypointMissionExecutionEvent event) {
                on_taskHandler_executionUpdate(event);
            }

            @Override
            public void executionSuccessfullyFinished() {
                on_taskHandler_executionSuccessfullyFinished();
            }

            @Override
            public void executionFailed(DJIError error) {
                on_taskHandler_executionFailed(error);
            }

            @Override
            public void executionStopped() {
                on_taskHandler_executionStopped();
            }
        });

    }


    private void on_btnCancel_click(Button btn) {
        if (listener != null) {
            listener.canceled();
        }
    }

    private void on_btnStart_click(Button btn) {
        uav.taskOperator.startTask();
    }

    private void on_btnStop_click(Button btn) {
        uav.taskOperator.stopTask();
    }


    private void on_taskHandler_executionStarted() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnStart.setEnabled(false);
                    btnStop.setEnabled(true);
                }
            });
        }

    }

    private void on_taskHandler_executionUpdate(final WaypointMissionExecutionEvent event) {

    }

    private void on_taskHandler_executionSuccessfullyFinished() {

        Log.d("UAV_TASK", "Task finished");

        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnStart.setEnabled(true);
                    btnStop.setEnabled(false);

                }
            });
        }

    }

    private void on_taskHandler_executionFailed(DJIError error) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnStart.setEnabled(true);
                    btnStop.setEnabled(false);
                }
            });
        }

    }

    private void on_taskHandler_executionStopped() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnStart.setEnabled(true);
                    btnStop.setEnabled(false);
                }
            });
        }
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
        void finished();
    }

}
