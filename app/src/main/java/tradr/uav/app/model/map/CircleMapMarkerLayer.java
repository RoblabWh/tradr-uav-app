package tradr.uav.app.model.map;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import tradr.uav.app.utils.GPSUtils;

/**
 * Created by tradr on 29.10.17.
 */

public class CircleMapMarkerLayer {

    private GoogleMap map;

    private LatLng centerPoint;
    private double radius;

    public CircleMapMarkerLayer(GoogleMap map) {
        this.map = map;
        this.centerPoint = null;
        this.radius = 0.0;
    }

    public void setCenterPoint(LatLng centerPoint) {
        this.centerPoint = centerPoint;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setRadius(LatLng radiusPoint) {
        this.radius = GPSUtils.calculateDistanceBetweenWGS84(this.centerPoint, radiusPoint);
    }

    public void drawCircle() {

    }
}
