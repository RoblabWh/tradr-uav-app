package tradr.uav.app.model.map;

import android.graphics.Color;
import android.os.Handler;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import tradr.uav.app.UavApplication;
import tradr.uav.app.model.task.TaskInProgress.ActionInProgress;
import tradr.uav.app.model.task.TaskInProgress.TaskInProgress;
import tradr.uav.app.model.task.TaskInProgress.WaypointInProgress;

/**
 * Created by tradr on 29.10.17.
 */

public class TaskMapMarkerLayer {

    private GoogleMap map;

    private TaskInProgress taskInProgress;

    private List<Marker> markerList;
    private List<Polyline> polylineList;

    public TaskMapMarkerLayer(GoogleMap map, TaskInProgress taskInProgress) {

        this.map = map;
        this.taskInProgress = taskInProgress;

        this.markerList = new ArrayList<>();
        this.polylineList = new ArrayList<>();

        Handler handler = new Handler(UavApplication.getInstance().getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                generate();
            }
        });
    }

    private void generate() {

        taskInProgress.addStatusChangedListener(this.task_statusChangedListener);

        PolylineOptions polylineOptions = new PolylineOptions();

        for (WaypointInProgress waypointInProgress : taskInProgress.getWaypointInProgressList()) {
            waypointInProgress.addStatusChangedListener(this.waypoint_statusChangedListener);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(waypointInProgress.getWaypoint().getLatitude(), waypointInProgress.getWaypoint().getLongitude()));
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            NumberFormat nf = new DecimalFormat();
            nf.setMaximumFractionDigits(1);
            nf.setMinimumFractionDigits(1);
            markerOptions.title(nf.format(waypointInProgress.getWaypoint().getAltitude()));
            markerList.add(map.addMarker(markerOptions));

            polylineOptions.add(new LatLng(waypointInProgress.getWaypoint().getLatitude(), waypointInProgress.getWaypoint().getLongitude()));
            if (polylineOptions.getPoints().size() == 2) {
                polylineOptions.color(Color.BLUE);
                polylineList.add(map.addPolyline(polylineOptions));
                polylineOptions = new PolylineOptions();
                polylineOptions.add(new LatLng(waypointInProgress.getWaypoint().getLatitude(), waypointInProgress.getWaypoint().getLongitude()));
            }

            for (ActionInProgress actionInProgress : waypointInProgress.getActionInProgressList()) {
                actionInProgress.addStatusChangedListener(this.action_statusChangedListener);
            }
        }
    }

    public void show() {
        for (Marker marker : this.markerList) {
            marker.setVisible(true);
        }
        for (Polyline polyline : this.polylineList) {
            polyline.setVisible(true);
        }
    }

    public void hide() {
        for (Marker marker : this.markerList) {
            marker.setVisible(false);
        }
        for (Polyline polyline : this.polylineList) {
            polyline.setVisible(false);
        }
    }

    public void clear() {
        for (Marker marker : this.markerList) {
            marker.remove();
        }
        for (Polyline polyline : this.polylineList) {
            polyline.remove();
        }
    }

    private TaskInProgress.StatusChangedListener task_statusChangedListener = new TaskInProgress.StatusChangedListener() {
        @Override
        public void onStatusChanged(final int taskID, final TaskInProgress.Status newStatus) {
            Handler handler = new Handler(UavApplication.getInstance().getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    on_task_statusChanged(taskID, newStatus);
                }
            });

        }
    };

    private WaypointInProgress.StatusChangedListener waypoint_statusChangedListener = new WaypointInProgress.StatusChangedListener() {
        @Override
        public void onStatusChanged(final int taskId, final int waypointID, final WaypointInProgress.Status newStatus) {
            Handler handler = new Handler(UavApplication.getInstance().getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    on_waypoint_statusChanged(taskId, waypointID, newStatus);
                }
            });

        }
    };

    private ActionInProgress.StatusChangedListener action_statusChangedListener = new ActionInProgress.StatusChangedListener() {
        @Override
        public void onStatusChanged(final int taskId, final int waypointId, final int actionID, final ActionInProgress.Status newStatus) {
            Handler handler = new Handler(UavApplication.getInstance().getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    on_action_statusChanged(taskId, waypointId, actionID, newStatus);
                }
            });
        }
    };


    private void on_task_statusChanged(int taskId, TaskInProgress.Status newStatus) {

    }

    private void on_waypoint_statusChanged(int taskId, int waypointId, WaypointInProgress.Status newStatus) {
        switch (newStatus) {
            case WAIT_FOR_EXECUTION:
                this.markerList.get(waypointId).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                if (waypointId > 0) {
                    this.polylineList.get(waypointId - 1).setColor(Color.BLUE);
                }
                break;
            case EXECUTION_FLYING_TO_WAYPOINT:
                this.markerList.get(waypointId).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                if (waypointId > 0) {
                    this.polylineList.get(waypointId - 1).setColor(Color.YELLOW);
                }
                break;
            case EXECUTION_DOING_ACTIONS:
                this.markerList.get(waypointId).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                if (waypointId > 0) {
                    this.polylineList.get(waypointId - 1).setColor(Color.GREEN);
                }
                break;
            case FINISHED:
                this.markerList.get(waypointId).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                if (waypointId > 0) {
                    this.polylineList.get(waypointId - 1).setColor(Color.GREEN);
                }
                break;
            case CANCELED:
                this.markerList.get(waypointId).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                if (waypointId > 0) {
                    this.polylineList.get(waypointId - 1).setColor(Color.RED);
                }
                break;
        }
    }

    private void on_action_statusChanged(int taskId, int waypointId, int actionID, ActionInProgress.Status newStatus) {

    }


}
