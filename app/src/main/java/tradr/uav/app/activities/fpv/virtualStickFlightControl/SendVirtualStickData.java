package tradr.uav.app.activities.fpv.virtualStickFlightControl;

import java.util.TimerTask;

import dji.common.error.DJIError;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import tradr.uav.app.utils.ToastUtils;

/**
 * Created by Artur Leinweber on 12.06.17.
 */

public class SendVirtualStickData extends TimerTask {

    private FlightController flightController;
    private FlightControlData flightControlData;
    private onSendVirtualStickFlightControlData listener;

    public SendVirtualStickData(FlightController flightController) {
        super();
        this.flightController = flightController;
    }

    @Override
    public void run() {
        if (flightController != null && flightControlData != null) {
            flightController.sendVirtualStickFlightControlData(flightControlData
                    , new CommonCallbacks.CompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if(djiError == null) {
                                listener.onFinished();
                            }else{
                                listener.onCanceled(djiError);
                            }
                        }
                    }
            );
        }else {
            ToastUtils.setResultToToast("DEBUG \n run(): (flightController || flightControlData) == null");
        }
    }



    public void setOnSendVirtualStickFlightControlDataListener(onSendVirtualStickFlightControlData listener) {
        this.listener = listener;
    }

    public void setFlightControlData(FlightControlData flightControlData) {
        this.flightControlData = flightControlData;
    }


    public interface onSendVirtualStickFlightControlData {
        void onFinished();
        void onCanceled(DJIError djiError);
    }


}
