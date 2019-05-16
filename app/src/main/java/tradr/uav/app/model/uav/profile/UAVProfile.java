package tradr.uav.app.model.uav.profile;

import android.content.res.Resources;
import android.support.annotation.Nullable;

import java.io.InputStream;

import dji.common.product.Model;
import dji.sdk.products.Aircraft;
import tradr.uav.app.R;
import tradr.uav.app.UavApplication;

/**
 * Created by tradr on 27.02.18.
 */

public class UAVProfile {

    private boolean iFrameNeccessary;

    private byte[] iFrame;

    private boolean fixedSize;

    private int size;

    public UAVProfile(Aircraft aircraft) {
        Resources res = UavApplication.getInstance().getResources();

        switch (aircraft.getModel()) {
            case PHANTOM_3_STANDARD:
                this.init(true, res.openRawResource(R.raw.dji_iframe_1280x720_ins), false, 0);
                break;
            case PHANTOM_3_ADVANCED:
                this.init(true, res.openRawResource(R.raw.dji_iframe_960x720_3s), false, 0);
                break;
            case PHANTOM_3_PROFESSIONAL:
                this.init(true, res.openRawResource(R.raw.dji_iframe_1280x720_3s), false, 0);
                break;
            case Phantom_3_4K:
                this.init(true, res.openRawResource(R.raw.dji_iframe_1280x720_ins), false, 0);
                break;


            case PHANTOM_4:
                this.init(true, res.openRawResource(R.raw.dji_iframe_1280x720_p4), false, 0);
                break;
            case PHANTOM_4_ADV:
                this.init(true, res.openRawResource(R.raw.dji_iframe_1280x720_p4), false, 0);
                break;
            case PHANTOM_4_ADVANCED:
                this.init(true, res.openRawResource(R.raw.dji_iframe_1280x720_p4), false, 0);
                break;
            case PHANTOM_4_PRO:
                this.init(true, res.openRawResource(R.raw.dji_iframe_1280x720_p4), false, 0);
                break;


            case MAVIC_PRO:
                this.init(false, (InputStream) null, true, 2032);
                break;


            case INSPIRE_1:
                this.init(false, res.openRawResource(R.raw.dji_iframe_1280x720_ins), false, 0);
                break;
            case INSPIRE_1_RAW:
                this.init(false, res.openRawResource(R.raw.dji_iframe_1280x720_ins), false, 0);
                break;
            case INSPIRE_1_PRO:
                this.init(false, res.openRawResource(R.raw.dji_iframe_1280x720_ins), false, 0);
                break;


            case INSPIRE_2:
                this.init(false, res.openRawResource(R.raw.iframe_1280x720_ins), false, 0);
                break;


            case MATRICE_100:
                this.init(false, res.openRawResource(R.raw.iframe_1024x768_wm100), false, 0);
                break;
            case MATRICE_200:
                this.init(false, res.openRawResource(R.raw.iframe_1280x720_wm220), false, 0);
                break;
            case MATRICE_210:
                this.init(false, res.openRawResource(R.raw.iframe_1280x720_wm220), false, 0);
                break;
            case MATRICE_210_RTK:
                this.init(false, res.openRawResource(R.raw.iframe_1280x720_wm220), false, 0);
                break;
            case MATRICE_600:
                this.init(false, res.openRawResource(R.raw.iframe_1920x1440_wm620), false, 0);
                break;
            case MATRICE_600_PRO:
                this.init(false, res.openRawResource(R.raw.iframe_1920x1440_wm620), false, 0);
                break;
        }

    }

    public UAVProfile(boolean iFrameNeccessary, byte[] iFrame, boolean fixedSize, int size) {
        this.init(iFrameNeccessary, iFrame, fixedSize, size);
    }

    public UAVProfile(boolean iFrameNeccessary, InputStream iFrameStream, boolean fixedSize, int size) {
        this.init(iFrameNeccessary, iFrameStream, fixedSize, size);
    }

    private void init(boolean iFrameNeccessary, byte[] iFrame, boolean fixedSize, int size) {
        this.iFrameNeccessary = iFrameNeccessary;
        this.iFrame = iFrame;
        this.fixedSize = fixedSize;
        this.size = size;
    }

    private void init(boolean iFrameNeccessary, InputStream iFrameStream, boolean fixedSize, int size) {
        byte[] iFrame;

        if (iFrameStream != null) {
            try {
                iFrame = new byte[iFrameStream.available()];
                iFrameStream.read(iFrame, 0, iFrameStream.available());
            } catch (Exception e) {
                iFrame = null;
            }
        } else {
            iFrame = null;
        }

        this.init(iFrameNeccessary, iFrame, fixedSize, size);
    }

    public boolean isIFrameNeccessary() {
        return this.iFrameNeccessary;
    }

    public byte[] getIFrame() {
        return this.iFrame;
    }

    public boolean isFixedSize() {
        return this.fixedSize;
    }

    public int getSize() {
        return this.size;
    }
}
