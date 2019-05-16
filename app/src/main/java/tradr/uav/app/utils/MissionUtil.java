package tradr.uav.app.utils;

/**
 * Created by Artur Leinweber on 09.05.17.
 */

public class MissionUtil {
    public static double getDistance(double overlap, double apertureAngle, double altitude) {
        return 2.0 * altitude * Math.tan(apertureAngle / 2.0) * (1.0 - overlap);
    }
}
