package tradr.uav.app.model.uav;

import android.graphics.SurfaceTexture;
import android.util.Log;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import dji.common.product.Model;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.products.Aircraft;
import tradr.uav.app.R;
import tradr.uav.app.UavApplication;
import tradr.uav.app.model.uav.profile.UAVProfile;

/**
 * Created by tradr on 29.10.17.
 */




public class UAVLiveStream {
    private VideoFeeder.VideoFeed videoFeed;
    private Aircraft aircraft;
    private UAVProfile profile;


    public UAVLiveStream(Aircraft aircraft) {
        this.aircraft = aircraft;

        this.profile = new UAVProfile(this.aircraft);

        videoFeed = VideoFeeder.getInstance().getPrimaryVideoFeed();

        videoFeed.setCallback(new VideoFeeder.VideoDataCallback() {
            @Override
            public void onReceive(byte[] bytes, int i) {
                if (profile.isFixedSize()) {
                    emit_h264Listener_dataReceived(Arrays.copyOfRange(bytes, 0, profile.getSize()), profile.getSize());
                } else {
                    emit_h264Listener_dataReceived(Arrays.copyOfRange(bytes, 0, i), i);
                }

            }
        });


        this.h264ListenerSet = new HashSet<H264Listener>();

    }


    public boolean isIframeNecessary() {
        return profile.isIFrameNeccessary();
    }

    public byte[] getIframe() {
        return profile.getIFrame();
    }


    /* H264Listener */
    private Set<H264Listener> h264ListenerSet;

    public interface H264Listener {
        void onDataReceived(byte[] data, int size);
    }

    public void addH264Listener(H264Listener h264Listener) {
        synchronized (this.h264ListenerSet) {
            this.h264ListenerSet.add(h264Listener);
            Log.d("H264ListenerAdress", "add: " + h264Listener.toString());
        }
    }

    public void removeH264Listener(H264Listener h264Listener) {
        synchronized (this.h264ListenerSet) {
            this.h264ListenerSet.remove(h264Listener);
            Log.d("H264ListenerAdress", "remove: " + h264Listener.toString());
        }
    }

    private void emit_h264Listener_dataReceived(byte[] data, int size) {
        synchronized (this.h264ListenerSet) {
            for (H264Listener listener : this.h264ListenerSet) {
                listener.onDataReceived(data, size);
            }
        }
    }

}
