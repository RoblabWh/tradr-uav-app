package tradr.uav.app.activities.map;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import tradr.uav.app.R;
import tradr.uav.app.UavApplication;
import tradr.uav.app.model.map.MapWidget;
import tradr.uav.app.model.map.TaskMapMarkerLayer;
import tradr.uav.app.model.uav.UAV;
import tradr.uav.app.model.uav.UAVFlightController;
import tradr.uav.app.model.task.Task.Task;
import tradr.uav.app.model.task.TaskInProgress.TaskInProgress;
import tradr.uav.app.services.common.TradrService;
import tradr.uav.app.utils.GPSUtils;
import tradr.uav.app.utils.ToastUtils;


public class MapActivity extends Activity {

    protected static final String TAG = "uavapp";


    enum STATE {
        LOAD_MAP,
        NORMAL,
        MENU,
        TAP_TO_FLY,
        TASK_WIZARD_AREA,
        TASK_WIZARD_CIRCLE,
        TASK_RECEIVED,
        TASK_UPLOAD,
        TASK_OPERATION
    }


    private STATE state;


    /* View */
    private TextView lblConnection;

    private MapFragment mapFragment;
    private MapWidget mapWidget;

    private Button btnMenu;
    private Button btnFPV;
    private Button btnViewtype;


    /* Fragments */
    private FragmentManager fragmentManager;

    private FPVFragment fpvFragment;

    private MenuFragment menuFragment;
    private TaskReceivedFragment taskReceivedFragment;
    private TaskWizCircleFragment taskWizCircleFragment;
    private TaskWizAreaFragment taskWizAreaFragment;
    private TaskUploadFragment taskUploadFragment;
    private TaskOperationFragment taskOperationFragment;



    /* Model */
    private UAV uav;

    //private UAVTaskOperator UAVTaskOperator;
    private Task task;
    private TaskInProgress taskInProgress;
    private TaskMapMarkerLayer taskMapMarkerLayer;



    private IntentFilter intentFilter;
    private BroadcastReceiver broadcastReceiver;




    public MapActivity() {

        /* Android INTENTS */
        intentFilter = new IntentFilter();
        intentFilter.addAction(UavApplication.FLAG_CONNECTION_CHANGE);
        intentFilter.addAction(TradrService.FLAG_TASK_RECEIVED);
        intentFilter.addAction(TradrService.FLAG_TASK_CONTROLLED);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                Log.d("Task", "before switch");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (intent.getAction()) {
                            case UavApplication.FLAG_CONNECTION_CHANGE:
                                onProductConnectionChange();
                                break;
                            case TradrService.FLAG_TASK_RECEIVED:
                                Log.d("Task", "before methode");
                                on_tradrService_taskReceived((Task) intent.getSerializableExtra("tradr.uav.app.model.task.Task.Task"));
                                break;
                            case TradrService.FLAG_TASK_CONTROLLED:
                                on_tradrService_taskControlled();
                                break;
                        }
                    }
                });

            }
        };

    }




    /**********************************************************************/
    /*                A c t i v i t y  -  S t a t e                       */
    /**********************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        fragmentManager = getFragmentManager();

        uav = UavApplication.getUav();

        //UAVTaskOperator = uav.taskOperator;

        initUI();


        registerReceiver(broadcastReceiver, intentFilter);

        final Intent tradrServiceIntent = new Intent(MapActivity.this, TradrService.class);
        getApplicationContext().startService(tradrServiceIntent);

        if (uav.isAircraftConnected()) {
            registerUavListener();
        }

    }

    @Override
    protected void onResume(){
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause(){
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        //unregisterReceiver(broadcastReceiver);
        //unbindService(mConnection);
        super.onDestroy();
    }



    public void switchToFPVActivity() {
        startActivity(new Intent(this, tradr.uav.app.activities.fpv.FPVActivity.class));
    }




    /*********************************************************************/
    /*                        U A V   S t a t e                          */
    /*********************************************************************/

    private void on_app_connectivityChanged(boolean isConnected) {
        refreshConnectionLabel(isConnected);

    }

    private void onProductConnectionChange()
    {
        refreshConnectionLabel(true);
    }




    /**********************************************************************/
    /*                                 V I E W                            */
    /**********************************************************************/

    /* init */

    private void initUI() {

        this.setContentView(R.layout.activity_map);

        lblConnection = (TextView) this.findViewById(R.id.lbl_connection);

        btnMenu     = (Button) this.findViewById(R.id.btn_menu);
        btnFPV      = (Button) this.findViewById(R.id.btn_fpv);
        btnViewtype = (Button) this.findViewById(R.id.btn_viewtype);

        btnMenu.setEnabled(false);
        btnViewtype.setEnabled(false);

        refreshConnectionLabel(uav.isAircraftConnected());


        mapFragment = (MapFragment) this.getFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                on_map_ready(googleMap);
            }
        });

        btnFPV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnFPV_click(v);
            }
        });
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnMenu_click(v);
            }
        });
        btnViewtype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnViewtype_click(v);
            }
        });

        uav.addDroneConnectionListener(new UAV.DroneConnectionListener() {
            @Override
            public void onConnectivityChanged(final boolean isConnected) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        on_uav_connectionChanged(isConnected);
                    }
                });
            }
        });

        if (uav.isAircraftConnected()) {
            registerUavListener();

            showFPVFragment();
        }

        initState();

    }


    private void registerUavListener() {

        uav.flightController.addDronePoseListener(new UAVFlightController.DronePoseListener() {
            @Override
            public void onPoseChanged(final double latitude, final double longitude, final double altitude, final double roll, final double pitch, final double yaw, final double velX, final double velY, final double velZ) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        on_uav_poseChanged(latitude, longitude, altitude, roll, pitch, yaw);
                    }
                });
            }
        });

    }


    private void refreshConnectionLabel(boolean isConnected) {
        if (isConnected) {
            lblConnection.setText("connected");
            lblConnection.setTextColor(getColor(R.color.green));
        } else {
            lblConnection.setText("disconnected");
            lblConnection.setTextColor(getColor(R.color.red));
        }
    }



    private void showFPVFragment() {
        this.fpvFragment = FPVFragment.newInstance();



        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fpv, this.fpvFragment);
        fragmentTransaction.commit();
    }


    private void showMenuFragment() {
        menuFragment = MenuFragment.newInstance();

        menuFragment.setInteractionListener(new MenuFragment.InteractionListener() {
            @Override
            public void onCanceled() {
                on_menuFragment_canceled();
            }

            @Override
            public void onOptionSelected(MenuFragment.MENU_OPTION selectedOption) {
                on_menuFragment_optionSelected(selectedOption);
            }

            @Override
            public void onConfigButtonClicked() {
                on_menuFragment_configButtonClicked();
            }
        });

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.side_bar, menuFragment);
        fragmentTransaction.commit();
    }

    private void showTaskReceivedFragment(Task task) {
        taskReceivedFragment = TaskReceivedFragment.newInstance(mapWidget, task);

        taskReceivedFragment.setInteractionListener(new TaskReceivedFragment.InteractionListener() {
            @Override
            public void canceled() {
                on_taskReceivedFragment_canceled();
            }

            @Override
            public void finished(Task task) {
                on_taskReceivedFragment_finished(task);
            }
        });


        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.side_bar, taskReceivedFragment);
        fragmentTransaction.commit();
    }

    private void showTaskWizCircleFragment() {
        taskWizCircleFragment = TaskWizCircleFragment.newInstance(mapWidget);

        taskWizCircleFragment.setInteractionListener(new TaskWizCircleFragment.InteractionListener() {
            @Override
            public void canceled() {
                on_taskWizCircleFragment_canceled();
            }

            @Override
            public void finished(Task task) {
                on_taskWizCircleFragment_finished(task);
            }
        });

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.side_bar, taskWizCircleFragment);
        fragmentTransaction.commit();
    }

    private void showTaskWizAreaFragment() {
        taskWizAreaFragment = TaskWizAreaFragment.newInstance(mapWidget);

        taskWizAreaFragment.setInteractionListener(new TaskWizAreaFragment.InteractionListener() {
            @Override
            public void canceled() {
                on_taskWizAreaFragment_canceled();
            }

            @Override
            public void finished(Task task) {
                on_taskWizAreaFragment_finished(task);
            }
        });

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.side_bar, taskWizAreaFragment);
        fragmentTransaction.commit();
    }

    private void showTaskUploadFragment() {
        taskUploadFragment = TaskUploadFragment.newInstance(this.mapWidget, this.task);

        taskUploadFragment.setInteractionListener(new TaskUploadFragment.InteractionListener() {
            @Override
            public void canceled() {
                on_taskUploadFragment_canceled();
            }

            @Override
            public void finished(TaskInProgress taskInProgress) {
                on_taskUploadFragment_finished(taskInProgress);
            }
        });

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.side_bar, taskUploadFragment);
        fragmentTransaction.commit();
    }

    private void showTaskOperationFragment() {
        taskOperationFragment = TaskOperationFragment.newInstance(mapWidget);

        taskOperationFragment.setInteractionListener(new TaskOperationFragment.InteractionListener() {
            @Override
            public void canceled() {
                on_taskOperationFragment_canceled();
            }

            @Override
            public void finished() {
                on_taskOperationFragment_finished();
            }
        });

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.side_bar, taskOperationFragment);
        fragmentTransaction.commit();
    }

    private void hideFPVFragment() {
        //fpvFragment.setInteractionListener(null);

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fpvFragment);
        fragmentTransaction.commit();

        fpvFragment = null;
    }

    private void hideMenuFragment() {
        menuFragment.setInteractionListener(null);

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(menuFragment);
        fragmentTransaction.commit();

        menuFragment = null;
    }

    private void hideTaskWizCircleFragment() {
        taskWizCircleFragment.setInteractionListener(null);

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(taskWizCircleFragment);
        fragmentTransaction.commit();

        taskWizCircleFragment = null;
    }

    private void hideTaskWizAreaFragment() {
        taskWizAreaFragment.setInteractionListener(null);

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(taskWizAreaFragment);
        fragmentTransaction.commit();

        taskWizAreaFragment = null;
    }

    private void hideTaskReceivedFragment() {
        taskReceivedFragment.setInteractionListener(null);

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(taskReceivedFragment);
        fragmentTransaction.commit();

        taskReceivedFragment = null;
    }

    private void hideTaskUploadFragment() {
        taskUploadFragment.setInteractionListener(null);

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(taskUploadFragment);
        fragmentTransaction.commit();

        taskUploadFragment = null;
    }

    private void hideTaskOperationFragment() {
        taskOperationFragment.setInteractionListener(null);

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(taskOperationFragment);
        fragmentTransaction.commit();

        taskOperationFragment = null;
    }



    /* Fragments Callback */

    private void on_menuFragment_canceled() {

        switchState(STATE.NORMAL);
    }

    private void on_menuFragment_optionSelected(MenuFragment.MENU_OPTION selectedOption) {

        switch (selectedOption) {
            case CIRCLE:
                switchState(STATE.TASK_WIZARD_CIRCLE);
                break;
            case AREA:
                switchState(STATE.TASK_WIZARD_AREA);
                break;
            case FREE:
                break;
        }

    }

    private void on_menuFragment_configButtonClicked() {
        startActivity(new Intent(this, tradr.uav.app.activities.config.ActionConfiguratorActivity.class));
    }

    private void on_taskWizCircleFragment_canceled() {
        switchState(STATE.NORMAL);
    }

    private void on_taskWizCircleFragment_finished(Task task) {
        this.task = task;

        switchState(STATE.TASK_UPLOAD);
    }

    private void on_taskWizAreaFragment_canceled() {
        switchState(STATE.NORMAL);
    }

    private void on_taskWizAreaFragment_finished(Task task) {
        this.task = task;

        switchState(STATE.TASK_UPLOAD);
    }

    private void on_taskReceivedFragment_canceled() { switchState(STATE.NORMAL); }

    private void on_taskReceivedFragment_finished(Task task) {
        this.task = task;

        switchState(STATE.TASK_UPLOAD);
    }

    private void on_taskUploadFragment_canceled() {
        switchState(STATE.NORMAL);
    }

    private void on_taskUploadFragment_finished(TaskInProgress taskInProgress) {
        switchState(STATE.TASK_OPERATION, taskInProgress);
    }

    private void on_taskOperationFragment_canceled() {
        switchState(STATE.NORMAL);
    }

    private void on_taskOperationFragment_finished() {
        switchState(STATE.NORMAL);
    }




    private void on_TRADR_taskReceived() {

    }




    private void initState(Object... param) {
        stateTransition_init_to_loadMap();
        state = STATE.LOAD_MAP;
    }

    private void switchState(STATE newState, Object... param) {
        switch (state) {
            case LOAD_MAP:
                if (newState == STATE.NORMAL) {
                    stateTransition_loadMap_to_normal();
                    state = STATE.NORMAL;
                } else {
                    ToastUtils.setResultToToast("State transition not possible");
                }
                break;


            case NORMAL:
                if (newState == STATE.MENU) {
                    stateTransition_normal_to_menu();
                    state = STATE.MENU;
                } else if (newState == STATE.TASK_RECEIVED) {
                    stateTransition_normal_to_taskReceived((Task) param[0]);
                    state = STATE.TASK_RECEIVED;
                } else {
                    ToastUtils.setResultToToast("State transition not possible");
                }
                break;


            case MENU:
                if (newState == STATE.TASK_WIZARD_CIRCLE) {
                    stateTransition_menu_to_taskWizardCircle();
                    state = STATE.TASK_WIZARD_CIRCLE;
                } else if (newState == STATE.TASK_WIZARD_AREA) {
                    stateTransition_menu_to_taskWizardArea();
                    state = STATE.TASK_WIZARD_AREA;
                } else if (newState == STATE.NORMAL) {
                    stateTransition_menu_to_normal();
                    state = STATE.NORMAL;
                } else {
                    ToastUtils.setResultToToast("State transition not possible");
                }
                break;


            case TAP_TO_FLY:
                break;


            case TASK_WIZARD_CIRCLE:
                if (newState == STATE.TASK_UPLOAD) {
                    stateTransition_taskWizardCircle_to_taskUpload();
                    state = STATE.TASK_UPLOAD;
                } else if (newState == STATE.NORMAL) {
                    stateTransition_taskWizardCircle_to_normal();
                    state = STATE.NORMAL;
                } else {
                    ToastUtils.setResultToToast("State transition not possible");
                }
                break;

            case TASK_WIZARD_AREA:
                if (newState == STATE.TASK_UPLOAD) {
                    stateTransition_taskWizardArea_to_taskUpload();
                    state = STATE.TASK_UPLOAD;
                } else if (newState == STATE.NORMAL) {
                    stateTransition_taskWizardArea_to_normal();
                    state = STATE.NORMAL;
                } else {
                    ToastUtils.setResultToToast("State transition not possible");
                }
                break;


            case TASK_RECEIVED:
                if (newState == STATE.TASK_UPLOAD) {
                    stateTransition_taskReceived_to_taskUpload();
                    state = STATE.TASK_UPLOAD;
                } else if (newState == STATE.NORMAL) {
                    stateTransition_taskReceived_to_normal();
                    state = STATE.NORMAL;
                } else {
                    ToastUtils.setResultToToast("State transition not possible");
                }
                break;


            case TASK_UPLOAD:
                if (newState == STATE.TASK_OPERATION) {
                    stateTransition_taskUpload_to_taskOperation((TaskInProgress) param[0]);
                    state = STATE.TASK_OPERATION;
                } else if (newState == STATE.NORMAL) {
                    stateTransition_taskUpload_to_normal();
                    state = STATE.NORMAL;
                } else {
                    ToastUtils.setResultToToast("State transition not possible");
                }
                break;


            case TASK_OPERATION:
                if (newState == STATE.NORMAL) {
                    stateTransition_taskOperation_to_normal();
                    state = STATE.NORMAL;
                } else {
                    ToastUtils.setResultToToast("State transition not possible");
                }
                break;

        }
    }

    private void stateTransition_init_to_loadMap() {
        btnMenu.setEnabled(false);
        btnMenu.setVisibility(View.INVISIBLE);
        btnViewtype.setEnabled(false);
        btnViewtype.setVisibility(View.INVISIBLE);
    }

    private void stateTransition_loadMap_to_normal() {
        btnMenu.setEnabled(true);
        btnMenu.setVisibility(View.VISIBLE);
        btnFPV.setEnabled(true);
        btnFPV.setVisibility(View.VISIBLE);
        btnViewtype.setEnabled(true);
        btnViewtype.setVisibility(View.VISIBLE);
    }

    private void stateTransition_normal_to_menu() {
        btnFPV.setEnabled(false);
        btnFPV.setVisibility(View.INVISIBLE);

        showMenuFragment();
    }

    private void stateTransition_normal_to_taskReceived(Task task) {
        btnFPV.setEnabled(false);
        btnFPV.setVisibility(View.INVISIBLE);

        showTaskReceivedFragment(task);
    }

    private void stateTransition_menu_to_taskWizardArea() {
        showTaskWizAreaFragment();
    }

    private void stateTransition_menu_to_taskWizardCircle() {
        showTaskWizCircleFragment();
    }

    private void stateTransition_menu_to_normal() {
        btnFPV.setEnabled(true);
        btnFPV.setVisibility(View.VISIBLE);

        hideMenuFragment();
    }

    private void stateTransition_taskWizardArea_to_taskUpload() {
        showTaskUploadFragment();
    }

    private void stateTransition_taskWizardArea_to_normal() {
        btnFPV.setEnabled(true);
        btnFPV.setVisibility(View.VISIBLE);

        mapWidget.clearMapMarker();

        hideTaskWizAreaFragment();
    }

    private void stateTransition_taskWizardCircle_to_taskUpload() {
        showTaskUploadFragment();
    }

    private void stateTransition_taskWizardCircle_to_normal() {
        btnFPV.setEnabled(true);
        btnFPV.setVisibility(View.VISIBLE);

        mapWidget.clearMapMarker();

        hideTaskWizCircleFragment();
    }

    private void stateTransition_taskReceived_to_taskUpload() { showTaskUploadFragment(); }

    private void stateTransition_taskReceived_to_normal() {
        btnFPV.setEnabled(true);
        btnFPV.setVisibility(View.VISIBLE);

        mapWidget.clearMapMarker();

        hideTaskReceivedFragment();
    }

    private void stateTransition_taskUpload_to_taskOperation(TaskInProgress taskInProgress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mapWidget.clearMapMarker();
            }
        });

        this.taskInProgress = taskInProgress;
        this.taskMapMarkerLayer = this.mapWidget.registerTaskInProgress(this.taskInProgress);

        showTaskOperationFragment();
    }

    private void stateTransition_taskUpload_to_normal() {
        btnFPV.setEnabled(true);
        btnFPV.setVisibility(View.VISIBLE);

        mapWidget.clearMapMarker();

        hideTaskUploadFragment();
    }

    private void stateTransition_taskOperation_to_normal() {
        btnFPV.setEnabled(true);
        btnFPV.setVisibility(View.VISIBLE);

        mapWidget.clearMapMarker();

        hideTaskOperationFragment();
    }




    /* Widgets Callback */

    private void on_map_ready(GoogleMap map) {
        if (mapWidget == null) {
            mapWidget = new MapWidget(map, this);
            btnViewtype.setEnabled(true);
            setUpMap();
            switchState(STATE.NORMAL);
        }

        LatLng homePos = new LatLng(50.748953, 7.205702); // Fraunhofer IAIS Sankt Augustin
        mapWidget.mapGoTo(homePos, 18.0f);
        mapWidget.setMapType(MapWidget.MAP_TYPE.SATELLITE);

        mapGoToDrone();
    }

    private void on_mapWindget_click(LatLng pos) {


    }



    private void on_btnMenu_click(View view) {
        switchState(STATE.MENU);
    }

    private void on_btnFPV_click(View view)
    {
        this.switchToFPVActivity();
    }

    private void on_btnViewtype_click(View view) {
        mapWidget.switchMapType();
    }



    public void onReturn(View view) {
        Log.d(TAG, "onReturn");
        this.finish();
    }




    /* Map */

    private void setUpMap() {

        mapWidget.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng pos) {
                on_mapWindget_click(pos);
            }
        });
    }

    public void mapGoToDrone() {
        if (uav.isAircraftConnected()) {
            LatLng pos = uav.flightController.getPosition();
            float zoom = 18.0f;
            if (GPSUtils.checkGpsCoordination(pos.latitude, pos.longitude)) {
                mapWidget.mapGoTo(pos, zoom);
            }
        }
    }



    /* Aircraft */

    private void on_uav_connectionChanged(boolean isConnected) {
        refreshConnectionLabel(isConnected);

        if (uav.isAircraftConnected()) {
            registerUavListener();

            showFPVFragment();
        }

        if (mapWidget != null) {
            mapGoToDrone();
        }
    }

    private void on_uav_poseChanged(double latitude, double longitude, double altitude, double roll, double pitch, double yaw) {
        if (mapWidget != null) {
            this.mapWidget.setDroneLocation(new LatLng(latitude, longitude), altitude, yaw);
        }
    }


    /********************************************************
     *              T R A D R  -  S e r v i c e             *
     ********************************************************/

    private ServiceConnection mConnection;

    private void on_tradrService_taskReceived(Task task) {
        Log.d("Task", "Task Received");
        this.task = task;
        switchState(STATE.TASK_RECEIVED, task);
    }

    private void on_tradrService_taskControlled() {

    }
}
