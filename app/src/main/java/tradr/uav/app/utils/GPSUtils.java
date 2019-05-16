package tradr.uav.app.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.geometry.Bounds;
import com.google.maps.android.projection.SphericalMercatorProjection;
import com.google.maps.android.geometry.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tradr on 21.04.17.
 */


public class GPSUtils {
private static final double mWorldWidth = 6371.0;
    public static boolean checkGpsCoordination(double latitude, double longitude) {
        return (latitude > -90 && latitude < 90 && longitude > -180 && longitude < 180) && (latitude != 0f && longitude != 0f);
    }

    public static double calculateDistanceInMeters(LatLng point1,LatLng point2) {
        return SphericalUtil.computeDistanceBetween(point1,point2);
    }

    //https://github.com/googlemaps/android-maps-utils/blob/master/library/src/com
    // /google/maps/android/projection/SphericalMercatorProjection.java
    public static Point toPoint(final LatLng latLng) {
        final double x = latLng.longitude / 360 + .5;
        final double siny = Math.sin(Math.toRadians(latLng.latitude));
        final double y = 0.5 * Math.log((1 + siny) / (1 - siny)) / -(2 * Math.PI) + .5;

        return new Point(x * mWorldWidth, y * mWorldWidth);
    }


    public static LatLng toLatLng(com.google.maps.android.geometry.Point point) {
        final double x = point.x / mWorldWidth - 0.5;
        final double lng = x * 360;

        double y = .5 - (point.y / mWorldWidth);
        final double lat = 90 - Math.toDegrees(Math.atan(Math.exp(-y * 2 * Math.PI)) * 2);

        return new LatLng(lat, lng);
    }




    public static double calculateDistanceBetweenWGS84(LatLng latLng1, LatLng latLng2) {
        Point p1 = GPSUtils.toPoint(latLng1);
        Point p2 = GPSUtils.toPoint(latLng2);

        double x1 = p1.x;
        double y1 = p1.y;

        double x2 = p2.x;
        double y2 = p2.y;

        return Math.sqrt(Math.pow((x2-x1),2) + Math.pow((y2-y1),2));
    }

    // Latitude: Breite (0 Grad Equator)
    // Logitude: Länge (0 Grad Greenwitch)

    static public double minLongitude(List<LatLng> points) {
        double minLongitude = 360.0;
        for (LatLng point : points) {
            if (point.longitude < minLongitude) {
                minLongitude = point.longitude;
            }
        }
        return minLongitude;
    }

    static public double maxLongitude(List<LatLng> points) {
        double maxLongitude = 0.0;
        for (LatLng point : points) {
            if (point.longitude > maxLongitude) {
                maxLongitude = point.longitude;
            }
        }
        return maxLongitude;
    }

    static public double minLatitude(List<LatLng> points) {
        double minLatitude = 90.0;
        for (LatLng point : points) {
            if (point.latitude < minLatitude) {
                minLatitude = point.latitude;
            }
        }
        return minLatitude;
    }

    static public double maxLatitude(List<LatLng> points) {
        double maxLatitude = -90.0;
        for (LatLng point : points) {
            if (point.latitude > maxLatitude) {
                maxLatitude = point.latitude;
            }
        }
        return maxLatitude;
    }


    static public List<LatLng> pointsInPolygone(List<LatLng> points, List<LatLng> polygone) {
        ArrayList resultPoints = new ArrayList(points.size());
        for (LatLng point : points) {
            if (pointInPolygon(polygone, point)) {
                resultPoints.add(point);
            }
        }
        return resultPoints;
    }


    static public boolean pointInPolygon(List<LatLng> polygon, LatLng point) {
        boolean result = false;
        polygon.set(0, polygon.get(polygon.size() - 1));
        for (int i = 0; i < polygon.size() - 1; i++) {
            result = !(result ^ testVecProduct(point, polygon.get(i), polygon.get(i + 1)));
        }
        return result;
    }

    static public boolean testVecProduct(LatLng a, LatLng b, LatLng c) {
        // x = Latitude
        // y = Longitude


        if (b.longitude > c.longitude) {
            LatLng tmp;

            tmp = b;
            b = c;
            c = tmp;
        }

        if (a.longitude <= b.longitude || a.longitude > c.longitude) {
            return true;
        }

        double delta = (b.latitude - a.latitude) * (c.longitude - a.longitude)
                - (b.longitude - a.longitude) * (c.latitude - a.latitude);

        if (delta > 0) {
            return false;
        } else {
            return true;
        }

    }

    static public double angleBetweenToWGS84(LatLng point1, LatLng point2) {
        double distanceLongitude = (point2.longitude - point1.longitude);

        double y = Math.sin(distanceLongitude) * Math.cos(point2.latitude);
        double x = Math.cos(point1.latitude) * Math.sin(point2.latitude) - Math.sin(point1.latitude)
                 * Math.cos(point2.latitude) * Math.cos(distanceLongitude);

        double angle = Math.atan2(y, x);

        angle = Math.toDegrees(angle);
        angle = (angle + 360.0) % 360.0;
        angle = 360.0 - angle; // count degrees counter-clockwise - remove to make clockwise

        angle = angle > 180 ? angle - 360.0 : angle;

        return angle;
    }
}
