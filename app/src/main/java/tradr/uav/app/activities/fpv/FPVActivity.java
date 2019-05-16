package tradr.uav.app.activities.fpv;

//ToDo: MVC bzw. Interprozesscommunication

/************************************************/
/*                 ANDROID SDK                  */
/************************************************/
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/************************************************/
/*                 DJI SDK                      */
/************************************************/
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import dji.common.camera.SettingsDefinitions;
import dji.common.camera.SystemState;
import dji.common.error.DJIError;
import dji.common.product.Model;
import dji.common.util.CommonCallbacks;
import dji.sdk.base.BaseProduct;
import dji.sdk.camera.Camera;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;

/************************************************/
/*                   TRADR                      */
/************************************************/
import tradr.uav.app.R;
import tradr.uav.app.UavApplication;
import tradr.uav.app.model.uav.UAV;
import tradr.uav.app.model.uav.UAVLiveStream;
import tradr.uav.app.utils.ToastUtils;


/************************************************/
/*                 FPVActivity                  */
/************************************************/
public class FPVActivity extends Activity {

    private static final String TAG = FPVActivity.class.getName();

    private FragmentManager fragmentManager;

    private FPVView view;

    private UAV uav;

    private PanoramaActionFragment panoramaActionFragment;

    private DJICodecManager codecManager;

    private Camera camera;

    private UAVLiveStream liveStream;

    private Handler handler;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        uav = UavApplication.getUav();

        view = new FPVView(this);

        handler = new Handler();

        // Start load PhotoAction Fragment
        fragmentManager = getFragmentManager();

        panoramaActionFragment = PanoramaActionFragment.newInstance();

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.photo_action_frame, panoramaActionFragment);
        //fragmentTransaction.remove(panoramaActionFragment);

        fragmentTransaction.commit();

        //fragmentTransaction.show(panoramaActionFragment);

        if (uav.isAircraftConnected()) {
            camera = uav.camera.getCamera();

            liveStream = uav.liveStream;

            initCamera();
        }
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
        startPreviewer();
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        stopPreviewer();
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        stopPreviewer();
        super.onDestroy();
    }




    private void initCamera() {
        if (camera != null) {

            camera.setSystemStateCallback(new SystemState.Callback() {
                @Override
                public void onUpdate(SystemState state) {
                    on_camera_systemStateUpdate(state);
                }
            });

        }
    }

    private void on_camera_systemStateUpdate(SystemState state) {
        if (null != state) {

            final int recordTime = state.getCurrentVideoRecordingTimeInSeconds();
            final boolean isVideoRecording = state.isRecording();

            this.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    view.updateRecordTime(recordTime);

                    if (isVideoRecording){
                        view.showRecordTime();
                    }else
                    {
                        view.hideRecordTime();
                    }
                }
            });
        }
    }


    public void captureAction(){

        if (camera != null) {

            camera.setShootPhotoMode(SettingsDefinitions.ShootPhotoMode.SINGLE, new CommonCallbacks.CompletionCallback(){
                @Override
                public void onResult(DJIError error) {
                    on_camera_captureStep1(error);
                }
            });
        }
    }

    private void on_camera_captureStep1(DJIError error) {
        if (null == error) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    on_camera_captureStep2();
                }
            }, 2000);
        }
    }

    private void on_camera_captureStep2() {
        camera.startShootPhoto(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {
                on_camera_captureStep3(error);
            }
        });
    }

    private void on_camera_captureStep3(DJIError error) {
        if (error == null) {
            ToastUtils.showToast(this, "take photo: success");
        } else {
            ToastUtils.showToast(this, error.getDescription());
        }
    }


    public void startRecord(){

        if (camera != null) {
            camera.startRecordVideo(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError error)
                {
                    on_camera_recordStarted(error);
                }
            }); // Execute the startRecordVideo API
        }
    }

    private void on_camera_recordStarted(DJIError error) {
        if (error == null) {
            ToastUtils.showToast(this, "Record video: success");
        } else {
            ToastUtils.showToast(this, error.getDescription());
        }
    }


    public void stopRecord(){

        if (camera != null) {
            camera.stopRecordVideo(new CommonCallbacks.CompletionCallback() {

                @Override
                public void onResult(DJIError error)
                {
                    on_camera_recordStoped(error);
                }
            }); // Execute the stopRecordVideo API
        }

    }

    private void on_camera_recordStoped(DJIError error) {
        if(error == null) {
            ToastUtils.showToast(this, "Stop recording: success");
        } else {
            ToastUtils.showToast(this, error.getDescription());
        }
    }



    public void switchCameraMode(SettingsDefinitions.CameraMode cameraMode) {

        Camera camera = uav.camera.getCamera();
        if (camera != null) {
            camera.setMode(cameraMode, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError error) {
                    on_camera_modeChanged(error);
                }
            });
        }
    }

    private void on_camera_modeChanged(DJIError error) {
        if (error == null) {
            ToastUtils.showToast(this, "Switch Camera Mode Succeeded");
        } else {
            ToastUtils.showToast(this, error.getDescription());
        }
    }




    public void registerSurface(SurfaceTexture surface, int width, int height) {
        unregisterSurface();

        codecManager = new DJICodecManager(this, surface, width, height);
    }

    public void unregisterSurface() {
        if (codecManager != null) {
            codecManager.cleanSurface();
            codecManager = null;
        }
    }


    private UAVLiveStream.H264Listener dataReceivedListener = new UAVLiveStream.H264Listener() {
        @Override
        public void onDataReceived(byte[] data, int size) {
            /*
            Phantom 3 Advanced (4:3)    dji_iframe_960x720_3s0_3s.h264
            Phantom 3 Advanced (16:9)   dji_iframe_1280x720_3s.h264h
            Phantom 3 Pro               dji_iframe_1280x720_ins.h264
            Matrice 100                 dji_iframe_1280x720_ins.h264
            Inspire 1                   dji_iframe_1280x720_ins.h264
            Phantom 4                   dji_iframe_1280x720_p4.h264
            Mavic Pro                   is sent by hardware (no injection necessary)
            */

            Log.d("frame size", "size: " + String.valueOf(size) + "    length: " + String.valueOf(data.length));

            if (codecManager != null) {
                codecManager.sendDataToDecoder(data, size);
            }
        }
    };


    private void startPreviewer() {

        BaseProduct product = uav.getAircraftInstance();

        if (product == null || !product.isConnected()) {
            ToastUtils.showToast(this, "disconnected...");
        } else {
            view.initVideoSurface();

            if (liveStream != null) {
                liveStream.addH264Listener(this.dataReceivedListener);
            }
        }
    }

    private void stopPreviewer() {
        if (this.liveStream != null) {
            this.liveStream.removeH264Listener(this.dataReceivedListener);
        }
    }





    public void switchToMapActivity() {

        this.finish();
    }

}