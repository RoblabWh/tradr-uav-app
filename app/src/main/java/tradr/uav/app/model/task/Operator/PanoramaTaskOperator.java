package tradr.uav.app.model.task.Operator;

import android.support.annotation.Nullable;

/**
 * Created by Artur Leinweber on 13.06.17.
 */

public abstract class PanoramaTaskOperator implements Operator {
    @Override
    public abstract void startMission(@Nullable CallBack callBack);

    @Override
    public abstract void resumeMission(@Nullable CallBack callBack);

    @Override
    public abstract void pauseMission(@Nullable CallBack callBack);

    @Override
    public abstract void stopMission(@Nullable CallBack callBack);
}
