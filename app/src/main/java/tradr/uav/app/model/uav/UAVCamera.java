package tradr.uav.app.model.uav;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;

import dji.common.camera.SettingsDefinitions;
import dji.common.camera.SystemState;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.log.DJILog;
import dji.sdk.camera.Camera;
import dji.sdk.camera.MediaManager;
import dji.sdk.products.Aircraft;

/**
 * Created by tradr on 29.10.17.
 */

public class UAVCamera {
    private Camera camera;

    public UAVCamera(Aircraft aircraft) {
        camera = aircraft.getCamera();

        camera.setSystemStateCallback(new SystemState.Callback() {
            @Override
            public void onUpdate(@NonNull SystemState systemState) {
                Log.d("CAM_STATE", systemState.getMode().toString());
            }
        });
    }

    public Camera getCamera() {
        return camera;
    }

    public void shootPhoto() {
        /*Shoot a Photo*/
        camera.startShootPhoto(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                DJILog.e("CameraMode.SHOOT_PHOTO ...", djiError == null ? "null" : djiError.getDescription());
            }
        });
    }

    public void configCamera() {

        /*Set the Camera Mode*/
        camera.setMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                DJILog.e("setMode CameraMode.SHOOT_PHOTO ...", djiError == null ? "null" : djiError.getDescription());
            }
        });

        /*Set the type of shooting Photos*/
        camera.setShootPhotoMode(SettingsDefinitions.ShootPhotoMode.SINGLE, new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                DJILog.e("setShootPhotoMode CameraMode.SHOOT_PHOTO ...", djiError == null ? "null" : djiError.getDescription());
            }
        });

        /*Set Auto Focus to Auto*/
        if(camera.isAdjustableFocalPointSupported()) {
            camera.setFocusMode(SettingsDefinitions.FocusMode.AUTO, null);
        }
    }

    public void restoreCameraFactorySettings() {
        camera.restoreFactorySettings(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                DJILog.e("Restore Factory Settings ...", djiError == null ? "null" : djiError.getDescription());
            }
        });
    }

    public void getImage() {

    }

    public void downloadLastPhoto() {
        File destDir = new File(Environment.getExternalStorageDirectory().getPath() + "/DJI_Images/");
        if (camera != null) {
            camera.setMode(SettingsDefinitions.CameraMode.MEDIA_DOWNLOAD, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        Log.d("UAV_FILE", "Mode changed");
                    } else {
                        Log.d("UAV_FILE", "Mode change error: " + djiError.getDescription());
                    }
                }
            });
            MediaManager manager = camera.getMediaManager();
                /*PlaybackManager manger = camera.getPlaybackManager();
                manger.unselectAllFiles();
                manger.toggleFileSelectionAtIndex(0);
                manger.downloadSelectedFiles(destDir, new PlaybackManager.FileDownloadCallback() {
                    @Override
                    public void onStart() {
                        Log.d("UAV_FILE", "download startet");
                    }

                    @Override
                    public void onEnd() {
                        Log.d("UAV_FILE", "download finished");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("UAV_FILE", "download error: " + e.getMessage());
                    }

                    @Override
                    public void onProgressUpdate(int i) {
                        Log.d("UAV_FILE", "loading... " + i + "%");
                    }
                });
                */


        } else {
            Log.d("UAV_FILE", "camera null");
        }
    }
}