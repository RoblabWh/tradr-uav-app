package tradr.uav.app.trash;

import android.view.View;

/**
 * Created by tradr on 11.05.17.
 */

public class OldMapActivity {
    public void on_btnLocate_click(View view) {
        //mapWidget.updateDroneLocation(new LatLng(droneLocationLat, droneLocationLng));
        //mapGoToDrone();
    }

    public void on_btnAddMode_click(View view) {
        //enableDisableAdd();
    }

    public void on_btnClear_click(View view) {
        //this.clearWaypointMission();
        //waypointList.clear();
    }

    public void on_btnConfig_click(View view)
    {
        //showSettingDialog();
    }

    public void on_btnUpload_click(View view) {
        //uploadWayPointMission();
    }

    public void on_btnStart_click(View view) {
        //this.startWaypointMission();
    }

    public void on_btnStop_click(View view) {
        //this.stopWaypointMission();
    }


    private void showSettingDialog(){
        //WaypointSetting waypointSetting = new WaypointSetting(this);

        //waypointSetting.show();

        /*
        altitude = waypointSetting.getAltitude();
        speed = waypointSetting.getSpeed();
        actionAfterFinished = waypointSetting.getActionAfterFinished();
        headingMode = waypointSetting.getHeadingMode();

        configWayPointMission();
        */
    }


    /*
    private void enableDisableAdd(){
        if (isAddToolActive == false) {
            isAddToolActive = true;
            btnAddMode.setText("Exit");
        }else{
            isAddToolActive = false;
            btnAddMode.setText("Add");
        }
    }
    */


    /*********************************************************************/
    /*    W a y p o i n t   M i s s i o n   O p e r a t o r   ( W M O )  */
    /*********************************************************************/

    public void clearWaypointMission() {
        /*
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mapWidget.clearMapMarker();
            }

        });

        //waypointMissionBuilder.waypointList(waypointList);
        mapWidget.updateDroneLocation(new LatLng(droneLocationLat, droneLocationLng));
        */
    }
}
