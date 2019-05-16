package tradr.uav.app.services.common;

import android.os.Handler;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by tradr on 09.08.17.
 */

public class EasyTimer {

    private int delay;

    private boolean isRunning;

    private Handler handler;

    private Runnable tickCallback;




    public EasyTimer(final int delay) {
        this.delay = delay;
        this.isRunning = false;
        this.handler = new Handler();

        this.tickCallback = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    Log.d("Marke", "Timer rings intern");
                    for (TimerListener listener : timerListenerSet) {
                        listener.onRinging();
                    }
                    if (isRunning) {
                        handler.postDelayed(this, delay);
                    }
                }
            }
        };

        this.timerListenerSet = new HashSet<TimerListener>();
    }


    public void start() {
        if (!isRunning) {
            isRunning = true;
            handler.postDelayed(tickCallback, delay);
        }
    }

    public void stop() {
        if (isRunning) {
            isRunning = false;
        }
    }




    private Set<TimerListener> timerListenerSet;

    public interface TimerListener {
        void onRinging();
    }

    public void addTimerListener(TimerListener timerListener) {
        this.timerListenerSet.add(timerListener);
    }

    public void removeTimerListener(TimerListener timerListener) {
        this.timerListenerSet.remove(timerListener);
    }

}
