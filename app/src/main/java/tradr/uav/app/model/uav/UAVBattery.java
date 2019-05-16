package tradr.uav.app.model.uav;

import dji.common.battery.BatteryState;
import dji.sdk.battery.Battery;
import dji.sdk.products.Aircraft;

/**
 * Created by tradr on 29.10.17.
 */

public class UAVBattery {
    private Battery battery;
    private Aircraft aircraft;

    private float batteryStatus;

    public UAVBattery(Aircraft aircraft) {
        this.aircraft = aircraft;
        this.battery = aircraft.getBattery();

        this.battery.setStateCallback(new BatteryState.Callback() {
            @Override
            public void onUpdate(BatteryState batteryState) {
                on_battery_update(batteryState);
            }
        });
    }

    private void on_battery_update(BatteryState batteryState) {
        this.batteryStatus = (float) batteryState.getChargeRemainingInPercent();
    }

    public Battery getBattery() {
        return battery;
    }

    public float getBatteryStatus() {
        return batteryStatus;
    }
}