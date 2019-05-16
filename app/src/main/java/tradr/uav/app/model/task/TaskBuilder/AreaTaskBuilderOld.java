package tradr.uav.app.model.task.TaskBuilder;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.geometry.Point;

import java.util.ArrayList;
import java.util.List;

import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointAction;
import dji.common.mission.waypoint.WaypointActionType;
import tradr.uav.app.model.uav.UAV;
import tradr.uav.app.utils.GPSUtils;
import tradr.uav.app.utils.MissionUtil;
import tradr.uav.app.utils.ToastUtils;

/**
 * Created by Artur Leinweber on 08.05.17.
 */

public class AreaTaskBuilderOld extends TaskBuilderOld implements Airworthy {


    /**
     *  List of the coordinates from the polygon.
     */
    private List<LatLng> polygonPoints;


    /**
     *  List of the meander for the polygon.
     */
    private List<LatLng> meanderPoints;

    /**
     *  List of the meander points that are in the polygon.
     */
    private List<LatLng> meanderPolygon;

    /**
     * Angle of the gimbal when looking at the bottom
     */
    private short bottomViewAngle;


    private List<Integer> cameraPosition;


    /**
     * Overlap
     */
    private int overlap;

    /**
     * Constructor
     */
    public AreaTaskBuilderOld() {
     super();
        polygonPoints = new ArrayList<>();
        meanderPoints = new ArrayList<>();
        meanderPolygon = new ArrayList<>();
        cameraPosition = new ArrayList<>();

        cameraPosition.add(-60);
        this.overlap = 50;
        this.bottomViewAngle = -90;
    }


    /**
     * Calculates a Meander in WGS84 for the mission.
     */
    private void generateWaypoints() {

        //Log.wtf("meander","Two Corner Points");
        Log.d("meander","Two Corner Points ...");
        /* Two Corner points Achtung DER ALGO IST INVERTIERETERT !!!*/
        /*
        LatLng bottomRightCorner = new LatLng(GPSUtils.maxLatitude(this.polygonPoints),GPSUtils.minLongitude(this.polygonPoints));
        LatLng topLeftCorner = new LatLng(GPSUtils.minLatitude(this.polygonPoints),GPSUtils.maxLongitude(this.polygonPoints));
        */

        Point bottomRightCorner = GPSUtils.toPoint(new LatLng(GPSUtils.maxLatitude(this.polygonPoints),GPSUtils.minLongitude(this.polygonPoints)));
        Point topLeftCorner = GPSUtils.toPoint(new LatLng(GPSUtils.minLatitude(this.polygonPoints),GPSUtils.maxLongitude(this.polygonPoints)));

        //Log.wtf("meander","Size of the two site in x and y direction from the rectangle");
        Log.d("meander","Size of the two site in x and y direction from the rectangle");
        /* Size of the two site in x and y direction from the rectangle */
        double distanceXAxis = Math.abs(topLeftCorner.x - bottomRightCorner.x);
        double distanceYAxis = Math.abs(topLeftCorner.y - bottomRightCorner.y);


        //Log.wtf("meander","Distance between two points");
        Log.d("meander","Distance between two points");
        /* Distance in meter between two points */
        double distance = MissionUtil.getDistance(((double) this.overlap) / 100.0, UAV.APERATURE_ANGLE_IN_RAD, super.getAltitude());

        ToastUtils.setResultToToast("Distance: " + distance);

        /* Create meander */
        for (double y = 0.0; y <= distanceYAxis; y += distance) {
            for (double x = 0.0; x <= distanceXAxis; x += distance) {
                this.meanderPoints.add(GPSUtils.toLatLng(new Point(topLeftCorner.x + x, topLeftCorner.y - y)));
            }

            y += distance;

            for (double x = distanceXAxis; x >= 0.0; x -= distance) {
                this.meanderPoints.add(GPSUtils.toLatLng(new Point(topLeftCorner.x + x, topLeftCorner.y - y)));
            }
        }
        //11*11 -1
        //double resolutionXAxis = distanceXAxis / 5.0;
        //double resolutionYAxis = distanceYAxis / 5.0;


        Log.wtf("meander","Create meander START LOOP");
        /* Create meander */
        /*
        for(double y = 0; y <= distanceYAxis; y+= resolutionYAxis) {
            for (double x = 0; x <= distanceXAxis; x += resolutionXAxis) {
                this.meanderPoints.add(
                        new LatLng(
                                topLeftCorner.latitude + x,
                                topLeftCorner.longitude - y
                                ));
            }

            y += resolutionYAxis;

            for (double x = distanceXAxis; x >= 0; x -= resolutionXAxis) {
                this.meanderPoints.add(
                        new LatLng(
                                topLeftCorner.latitude + x,
                                topLeftCorner.longitude - y
                                ));
            }
        }
        */


        Log.wtf("meander","Create meander END LOOP");

        Log.wtf("meander","Get points that are in the polygon");

        /* Get points that are in the polygon*/
        polygonPoints.add(polygonPoints.get(0));

        ToastUtils.setResultToToast("meanderPoints:" + Integer.toString(meanderPoints.size()));

        //meanderPolygon = GPSUtils.pointsInPolygone(meanderPoints,polygonPoints);

        ToastUtils.setResultToToast("meanderPolygon:" + Integer.toString(meanderPolygon.size()));

        //super.generatedWaypoints.clear();
        for(LatLng ll : meanderPolygon) {
            super.generatedWaypoints.add(new Waypoint(ll.latitude,ll.longitude,super.getAltitude()));
        }


        /*for(LatLng ll : meanderPoints) {
            super.generatedWaypoints.add(new Waypoint(ll.latitude,ll.longitude,super.getAltitude()));
        }
        */

    }

    /**
     * Checks that the waypoints were calculated
     */
    @Override
    protected void checkForGeneratedWaypoints() {
            meanderPoints.clear();
            meanderPolygon.clear();
            super.generatedWaypoints.clear();
            this.generateWaypoints();
    }

    /**
     * Adds a waypoint to the mission. This is used for creating a polygon.
     * @param polygonPoints LatLng Coordinates
     */
    public void addPolygonPoints(List<LatLng> polygonPoints) {
        this.polygonPoints = polygonPoints;
    }


    /**
     * Rotates the gimbal's pitch. The actionParam value should be in range [-90, 0] degrees.
     * Starts to shoot a photo. The actionParam for the waypoint action will be ignored.
     * The maximum time set to execute this waypoint action is 6 seconds. If the time while
     * executing the waypoint action goes above 6 seconds, the aircraft will stop executing
     * the waypoint action and will move on to the next waypoint action, if one exists.
     * @param gimbalPitchAngles Array of Angles for the Gimbal that should
     *                          be approached for taking photos
     */
    public void setCameraPositions(Integer... gimbalPitchAngles) {
        cameraPosition.clear();
        for(Integer i : gimbalPitchAngles) {
            this.cameraPosition.add(i);
        }
    }


    @Override
    protected void generateWaypointAction() {
        waypointActions.clear();

        waypointActions.add(new WaypointAction(WaypointActionType.GIMBAL_PITCH,this.cameraPosition.get(0)));
        int x = -180;
        for (int i = 0; i < 4 ;i++) {
            waypointActions.add(new WaypointAction(WaypointActionType.ROTATE_AIRCRAFT,x += 90));
            waypointActions.add(new WaypointAction(WaypointActionType.START_TAKE_PHOTO,NOTHING));
        }

        waypointActions.add(new WaypointAction(WaypointActionType.GIMBAL_PITCH,bottomViewAngle));
        waypointActions.add(new WaypointAction(WaypointActionType.START_TAKE_PHOTO,NOTHING));

    }



    /**
     * Sets the overlap for the taken picture.
     * @param overlap Overlap
     */
    public void setOverlap(int overlap) {
        this.overlap = overlap;
    }


    /**
     * Returns the overlap for the taken picture.
     * @return int overlap
     */
    public int getOverlap() {
        return this.overlap;
    }

    /**
     * Sets the bottom view angle for the gimbal.
     * @param bottomViewAngle short Bottom view angle
     */
    public void setBottomViewAngle(short bottomViewAngle) {
        this.bottomViewAngle = bottomViewAngle;
    }

    /**
     * Return the bottom view angle for the gimbal.
     * @return Bottom view angle
     */
    public short getBottomViewAngle() {
        return this.bottomViewAngle;
    }


}
