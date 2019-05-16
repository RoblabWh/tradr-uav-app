package tradr.uav.app.trash;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import dji.common.mission.waypoint.WaypointMissionFinishedAction;
import dji.common.mission.waypoint.WaypointMissionHeadingMode;
import tradr.uav.app.R;
import tradr.uav.app.utils.GeneralUtils;

/**
 * Created by tradr on 21.04.17.
 */



public class WaypointSetting {

    protected static final String TAG = "uavapp";

    Activity parentActivity;

    View view;

    TextView   tvAltitude;
    RadioGroup rgSpeed;
    RadioGroup rgActionAfterFinished;
    RadioGroup rgHeading;

    AlertDialog alertDialog;

    private float altitude;
    private float speed;
    private WaypointMissionFinishedAction actionAfterFinished;
    private WaypointMissionHeadingMode headingMode;




    public WaypointSetting(Activity parentActivity)
    {
        this.parentActivity = parentActivity;

        /*
        this.parentActivity.setContentView(R.layout.dialog_waypointsetting);

        this.tvAltitude            = (TextView)   this.parentActivity.findViewById(R.id.altitude);
        this.rgSpeed               = (RadioGroup) this.parentActivity.findViewById(R.id.speed);
        this.rgActionAfterFinished = (RadioGroup) this.parentActivity.findViewById(R.id.actionAfterFinished);
        this.rgHeading             = (RadioGroup) this.parentActivity.findViewById(R.id.heading);
        */


        this.view = parentActivity.getLayoutInflater().inflate(R.layout.dialog_waypointsetting, null);

        this.tvAltitude            = (TextView)   this.view.findViewById(R.id.altitude);
        this.rgSpeed               = (RadioGroup) this.view.findViewById(R.id.speed);
        this.rgActionAfterFinished = (RadioGroup) this.view.findViewById(R.id.actionAfterFinished);
        this.rgHeading             = (RadioGroup) this.view.findViewById(R.id.heading);



        this.altitude            = 100.0f;
        this.speed               = 10.0f;
        this.actionAfterFinished = WaypointMissionFinishedAction.NO_ACTION;
        this.headingMode         = WaypointMissionHeadingMode.AUTO;



        rgSpeed.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.lowSpeed){
                    speed = 3.0f;
                } else if (checkedId == R.id.MidSpeed){
                    speed = 5.0f;
                } else if (checkedId == R.id.HighSpeed){
                    speed = 10.0f;
                }
            }

        });



        rgActionAfterFinished.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, "Select finish action");

                if (checkedId == R.id.finishNone){
                    actionAfterFinished = WaypointMissionFinishedAction.NO_ACTION;
                } else if (checkedId == R.id.finishGoHome){
                    actionAfterFinished = WaypointMissionFinishedAction.GO_HOME;
                } else if (checkedId == R.id.finishAutoLanding){
                    actionAfterFinished = WaypointMissionFinishedAction.AUTO_LAND;
                } else if (checkedId == R.id.finishToFirst){
                    actionAfterFinished = WaypointMissionFinishedAction.GO_FIRST_WAYPOINT;
                }
            }
        });



        rgHeading.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, "Select heading");

                if (checkedId == R.id.headingNext) {
                    headingMode = WaypointMissionHeadingMode.AUTO;
                } else if (checkedId == R.id.headingInitDirec) {
                    headingMode = WaypointMissionHeadingMode.USING_INITIAL_DIRECTION;
                } else if (checkedId == R.id.headingRC) {
                    headingMode = WaypointMissionHeadingMode.CONTROL_BY_REMOTE_CONTROLLER;
                } else if (checkedId == R.id.headingWP) {
                    headingMode = WaypointMissionHeadingMode.USING_WAYPOINT_HEADING;
                }
            }
        });
    }



    public void show()
    {
        AlertDialog.Builder alertDialogBuilder;

        alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(parentActivity, R.style.Theme_AppCompat));

        alertDialogBuilder.setTitle("Settings");

        alertDialogBuilder.setView(view);

        alertDialogBuilder.setPositiveButton("Finish",new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                String altitudeString = tvAltitude.getText().toString();
                altitude = Integer.parseInt(GeneralUtils.nulltoIntegerDefalt(altitudeString));
                Log.e(TAG, "altitude " + altitude);
                Log.e(TAG, "speed " + speed);
                Log.e(TAG, "mFinishedAction " + actionAfterFinished);
                Log.e(TAG, "mHeadingMode " + headingMode);
            }

        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }

        });

        Log.v("call config", "marke");

        alertDialog = alertDialogBuilder.create();



        alertDialog.show();

    }




    public float getSpeed()
    {
        return speed;
    }

    public float getAltitude()
    {
        return altitude;
    }

    public WaypointMissionFinishedAction getActionAfterFinished()
    {
        return actionAfterFinished;
    }

    public WaypointMissionHeadingMode getHeadingMode()
    {
        return headingMode;
    }

}
