package tradr.uav.app.model.task.Operator;

import android.support.annotation.Nullable;

/**
 * Created by Artur Leinweber on 13.06.17.
 */

public interface Operator {
    void startMission(@Nullable CallBack callBack);
    void resumeMission(@Nullable CallBack callBack);
    void pauseMission(@Nullable CallBack callBack);
    void stopMission(@Nullable CallBack callBack);

    public interface CallBack {
        void onResult(Exception e);
    }
}
