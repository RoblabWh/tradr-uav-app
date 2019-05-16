package tradr.uav.app.model.uav;

import android.support.annotation.NonNull;

import dji.common.error.DJIError;
import dji.common.gimbal.GimbalMode;
import dji.common.gimbal.GimbalState;
import dji.common.gimbal.Rotation;
import dji.common.gimbal.RotationMode;
import dji.common.util.CommonCallbacks;
import dji.log.DJILog;
import dji.sdk.gimbal.Gimbal;
import dji.sdk.products.Aircraft;

/**
 * Created by tradr on 29.10.17.
 */

public class UAVGimbal {
    private Gimbal gimbal;

    public UAVGimbal(Aircraft aircraft) {
        gimbal = aircraft.getGimbal();
    }

    public Gimbal getGimbal() {
        return gimbal;
    }

    /**
     * Rotate gimbal's pitch with an angle.
     * @param angle from -90 degrees (Gimbal looks to earth)
     *              to +30 degrees (Gimbal looks to heaven)
     */
    public void setPitchAngle(float angle) {

        if(angle <= -90) {
            angle = -90;
        }else if (angle > 30) {
            angle = 30;
        }

        Rotation.Builder rotationBuilder = new Rotation.Builder();
        rotationBuilder.pitch(angle);
        rotationBuilder.mode(RotationMode.ABSOLUTE_ANGLE);
        rotationBuilder.time(2);
        Rotation rotation = rotationBuilder.build();

        gimbal.setMotorEnabled(true, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                DJILog.e("Gimbal motor enabling ...", djiError == null ? "null" : djiError.getDescription());
            }
        });

        gimbal.setMode(GimbalMode.FREE, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                DJILog.e("Set Gimbal mode to FREE ...", djiError == null ? "null" : djiError.getDescription());
            }
        });

        gimbal.rotate(rotation, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                DJILog.e("Moving Gimbal ...", djiError == null ? "null" : djiError.getDescription());
            }
        });
    }

    /**
     * Starts calibrating the gimbal. The product should be stationary (not flying, or being held)
     * and horizontal during calibration. For gimbal's with adjustable payloads, the payload
     * should be present and balanced before doing a calibration.
     */
    public void calibrateGimbal() {
        gimbal.startCalibration(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                DJILog.e("Calibrating Gimbal ...", djiError == null ? "null" : djiError.getDescription());
            }
        });
    }

    /**
     * Resets the gimbal. The behaviors are product-dependent.
     * Osmo series (e.g. Osmo, Osmo Pro): The gimbal's pitch and yaw will be set
     * to the origin, which is the standard position for the gimbal. Phantom series
     * (e.g. Phantom 3 Professional, Phantom 4 series): The first call sets gimbal to
     * point down vertically to the earth. The second call sets gimbal to the standard position.
     * Other products (e.g. Inspire 1): Only the gimbal's pitch will the set to the origin.
     */
    public void resetGimbal() {
        gimbal.reset(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if (djiError != null) {
                    DJILog.e("Resetting Gimbal to home position ... ", djiError.getDescription());
                }
            }
        });
    }

    /**
     * Information about gimbal's pitch, roll, and yaw angles.
     * @return Information about gimbal's pitch, roll, and yaw angles.
     */
    public String getGimbalPitchRollYaw() {

        final StringBuffer stringBuffer = new StringBuffer();

        gimbal.setStateCallback(new GimbalState.Callback() {

            @Override
            public void onUpdate(@NonNull GimbalState gimbalState) {
                stringBuffer.delete(0, stringBuffer.length());

                stringBuffer.append("PitchInDegrees: ").
                        append(gimbalState.getAttitudeInDegrees().getPitch()).append("\n");
                stringBuffer.append("RollInDegrees: ").
                        append(gimbalState.getAttitudeInDegrees().getRoll()).append("\n");
                stringBuffer.append("YawInDegrees: ").
                        append(gimbalState.getAttitudeInDegrees().getYaw()).append("\n");

            }
        });

        return stringBuffer.toString();
    }

}
