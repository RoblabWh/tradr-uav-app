package tradr.uav.app.model.uav.profile;

import android.content.res.Resources;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import dji.common.product.Model;
import dji.sdk.products.Aircraft;
import tradr.uav.app.R;
import tradr.uav.app.UavApplication;
import tradr.uav.app.model.uav.UAV;

/**
 * Created by tradr on 27.02.18.
 */

public class UAVProfileManager {

    private Map<Model, UAVProfile> profiles;

    public UAVProfileManager() {
        this.profiles = new HashMap<>();

        UAVProfile profile;
        Resources res = UavApplication.getInstance().getResources();


        /* Phantom 3 */

        profile = new UAVProfile(true, res.openRawResource(R.raw.dji_iframe_1280x720_ins), false, 0);
        this.profiles.put(Model.PHANTOM_3_STANDARD, profile);

        profile = new UAVProfile(true, res.openRawResource(R.raw.dji_iframe_1280x720_ins), false, 0);
        this.profiles.put(Model.PHANTOM_3_ADVANCED, profile);

        profile = new UAVProfile(true, res.openRawResource(R.raw.dji_iframe_1280x720_ins), false, 0);
        this.profiles.put(Model.PHANTOM_3_PROFESSIONAL, profile);

        profile = new UAVProfile(true, res.openRawResource(R.raw.dji_iframe_1280x720_ins), false, 0);
        this.profiles.put(Model.Phantom_3_4K, profile);



        /* Phantom 4 */

        profile = new UAVProfile(true, res.openRawResource(R.raw.dji_iframe_1280x720_p4), false, 0);
        this.profiles.put(Model.PHANTOM_4, profile);

        profile = new UAVProfile(true, res.openRawResource(R.raw.dji_iframe_1280x720_p4), false, 0);
        this.profiles.put(Model.PHANTOM_4_ADV, profile);

        profile = new UAVProfile(true, res.openRawResource(R.raw.dji_iframe_1280x720_p4), false, 0);
        this.profiles.put(Model.PHANTOM_4_ADVANCED, profile);

        profile = new UAVProfile(true, res.openRawResource(R.raw.dji_iframe_1280x720_p4), false, 0);
        this.profiles.put(Model.PHANTOM_4_PRO, profile);



        /* Mavic */

        profile = new UAVProfile(false, (InputStream) null, true, 2032);
        this.profiles.put(Model.MAVIC_PRO, profile);



        /* Inspire 1 */

        profile = new UAVProfile(false, res.openRawResource(R.raw.dji_iframe_1280x720_ins), false, 0);
        this.profiles.put(Model.INSPIRE_1, profile);

        profile = new UAVProfile(false, res.openRawResource(R.raw.dji_iframe_1280x720_ins), false, 0);
        this.profiles.put(Model.INSPIRE_1_RAW, profile);

        profile = new UAVProfile(false, res.openRawResource(R.raw.dji_iframe_1280x720_ins), false, 0);
        this.profiles.put(Model.INSPIRE_1_PRO, profile);



        /* Inspire 2 */

        profile = new UAVProfile(false, res.openRawResource(R.raw.iframe_1280x720_ins), false, 0);
        this.profiles.put(Model.INSPIRE_2, profile);



        /* Matrice */

        profile = new UAVProfile(false, res.openRawResource(R.raw.iframe_1024x768_wm100), false, 0);
        this.profiles.put(Model.MATRICE_100, profile);

        profile = new UAVProfile(false, res.openRawResource(R.raw.iframe_1280x720_wm220), false, 0);
        this.profiles.put(Model.MATRICE_200, profile);

        profile = new UAVProfile(false, res.openRawResource(R.raw.iframe_1280x720_wm220), false, 0);
        this.profiles.put(Model.MATRICE_210, profile);

        profile = new UAVProfile(false, res.openRawResource(R.raw.iframe_1280x720_wm220), false, 0);
        this.profiles.put(Model.MATRICE_210_RTK, profile);

        profile = new UAVProfile(false, res.openRawResource(R.raw.iframe_1920x1440_wm620), false, 0);
        this.profiles.put(Model.MATRICE_600, profile);

        profile = new UAVProfile(false, res.openRawResource(R.raw.iframe_1920x1440_wm620), false, 0);
        this.profiles.put(Model.MATRICE_600_PRO, profile);

    }


    public UAVProfile getUavProfile(Model model) {
        return this.profiles.get(model);
    }
}
