package tradr.uav.app.model.task.TaskBuilder;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import dji.common.mission.waypoint.WaypointMission;

/**
 * Created by Artur Leinweber on 08.05.17.
 */

public interface Airworthy {
    public List<LatLng> getWaypoints();
    public WaypointMission generateWaypointTask();
}
