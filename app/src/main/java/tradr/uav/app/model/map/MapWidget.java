package tradr.uav.app.model.map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import tradr.uav.app.R;
import tradr.uav.app.UavApplication;
import tradr.uav.app.model.task.TaskInProgress.TaskInProgress;
import tradr.uav.app.utils.GPSUtils;

/**
 * Created by tradr on 11.05.17.
 */

public class MapWidget {

    public enum MAP_TYPE {
        ROADS,
        TERRAIN,
        SATELLITE,
        HYBRIDE
    }

    private GoogleMap map;

    private Activity parentActivity;

    private Marker droneMapMarker;




    public MapWidget(GoogleMap googleMapsWidget, Activity parentActivity) {
        this.map = googleMapsWidget;
        this.parentActivity = parentActivity;
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setCompassEnabled(false);
    }


    public void setOnMapClickListener(GoogleMap.OnMapClickListener onMapClickListener) {
        this.map.setOnMapClickListener(onMapClickListener);
    }

    public void mapGoTo(LatLng pos, float zoom) {
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(pos, zoom);
        map.moveCamera(cu);
    }

    public void setMapType(MAP_TYPE mapType) {
        switch (mapType) {
            case ROADS:
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case TERRAIN:
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case SATELLITE:
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case HYBRIDE:
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;


            default:
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
        }
    }

    public void switchMapType() {
        switch (map.getMapType()) {
            case GoogleMap.MAP_TYPE_NONE:
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;


            case GoogleMap.MAP_TYPE_NORMAL:
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case GoogleMap.MAP_TYPE_TERRAIN:
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case GoogleMap.MAP_TYPE_SATELLITE:
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case GoogleMap.MAP_TYPE_HYBRID:
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;


            default:
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
        }
    }


    public Marker markWaypointOnMap(LatLng point) {
        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(point);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        return map.addMarker(markerOptions);
    }

    public void clearMapMarker() {
        droneMapMarker = null;
        map.clear();
    }

    public Circle drawCircOnMap(LatLng pos, double radius, int strokeWidth, int strokeColor, int fillColor) {
        CircleOptions circOptions = new CircleOptions();

        circOptions.center(pos);
        circOptions.strokeWidth(strokeWidth);
        circOptions.strokeColor(strokeColor);
        circOptions.fillColor(fillColor);
        circOptions.radius(radius);

        return map.addCircle(circOptions);
    }

    public List<Marker> drawWaypoints(List<LatLng> waypoints) {

        List<Marker> marker = new ArrayList<>(50);

        PolylineOptions lineOptions = new PolylineOptions();

        for (LatLng waypoint : waypoints) {
            MarkerOptions markerOptions = new MarkerOptions();

            markerOptions.position(waypoint);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            marker.add(map.addMarker(markerOptions));

            lineOptions.add(waypoint);

            if (lineOptions.getPoints().size() > 1) {
                lineOptions.color(Color.WHITE);

                map.addPolyline(lineOptions);
                lineOptions = new PolylineOptions();
                lineOptions.add(waypoint);
            }

        }

        return marker;
    }

    public Polygon drawPolygon(List<LatLng> polygon) {
        PolygonOptions polygonOptions = new PolygonOptions();

        polygonOptions.strokeColor(Color.WHITE);

        for (LatLng point : polygon) {
            polygonOptions.add(point);
        }

        return map.addPolygon(polygonOptions);
    }

    public List<Polyline> drawRoute(List<LatLng> waypoints) {
        List<Polyline> polyline = new ArrayList<>(50);

        for (int i = 0; i < waypoints.size() - 1; i++) {
            LatLng waypoint1 = waypoints.get(i);
            LatLng waypoint2 = waypoints.get(i + 1);

            PolylineOptions polylineOptions = new PolylineOptions();

            polylineOptions.color(Color.WHITE);

            polylineOptions.add(waypoint1);
            polylineOptions.add(waypoint2);

            polyline.add(map.addPolyline(polylineOptions));
        }

        return polyline;
    }



    public void setDroneLocation(final LatLng droneLocation, final double flightHeight, final double yaw) {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateDroneLocation(droneLocation, flightHeight, yaw);
            }
        });
    }

    public void updateDroneLocation(LatLng droneLocation, double flightHeight, double yaw) {

        if (GPSUtils.checkGpsCoordination(droneLocation.latitude, droneLocation.longitude)) {

            if (droneMapMarker == null) {
                initDroneLocation(droneLocation, flightHeight, yaw);
            } else {
                droneMapMarker.setPosition(droneLocation);
                droneMapMarker.setRotation((float) yaw);
                droneMapMarker.setTitle(String.valueOf(flightHeight));
            }
        }

    }

    private void initDroneLocation(LatLng droneLocation, double flightHeight, double yaw) {

        Bitmap icon = BitmapFactory.decodeResource(parentActivity.getResources(), R.drawable.aircraft);
        Bitmap smallIcon = Bitmap.createScaledBitmap(icon, 160, 160, false);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallIcon));
        markerOptions.anchor(0.5f, 0.5f);

        markerOptions.position(droneLocation);
        markerOptions.rotation((float) yaw);
        markerOptions.title(String.valueOf(flightHeight));

        droneMapMarker = map.addMarker(markerOptions);
    }



    public TaskMapMarkerLayer registerTaskInProgress(TaskInProgress taskInProgress) {
        return new TaskMapMarkerLayer(this.map, taskInProgress);
    }


}
