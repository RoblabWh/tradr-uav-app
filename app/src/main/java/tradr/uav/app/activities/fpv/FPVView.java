package tradr.uav.app.activities.fpv;

import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import dji.common.camera.SettingsDefinitions;

import tradr.uav.app.R;

/**
 * Created by tradr on 02.05.17.
 */

public class FPVView {

    private static final String TAG = FPVActivity.class.getName();



    private FPVActivity activity;

    private TextureView videoSurface;

    private Button btnCapture;
    private Button btnPhotoMode;
    private Button btnVideoMode;
    private ToggleButton btnRecord;
    private TextView txtRecordingTime;

    private Button btnMap;




    public FPVView(FPVActivity parentActivity) {
        activity = parentActivity;

        initUI();

        initVideoSurface();
    }



    /************************************************/
    /*                   VIEW                       */
    /************************************************/

    private void initUI() {

        activity.setContentView(R.layout.activity_fpv);

        videoSurface     = (TextureView)  activity.findViewById(R.id.video_previewer_surface);

        txtRecordingTime = (TextView)     activity.findViewById(R.id.timer);

        btnCapture       = (Button)       activity.findViewById(R.id.btn_capture);
        btnRecord        = (ToggleButton) activity.findViewById(R.id.btn_record);
        btnPhotoMode     = (Button)       activity.findViewById(R.id.btn_shoot_photo_mode);
        btnVideoMode     = (Button)       activity.findViewById(R.id.btn_record_video_mode);

        btnMap           = (Button)       activity.findViewById(R.id.btn_map);

        txtRecordingTime.setVisibility(View.INVISIBLE);

        registerCallbackMethods();
    }

    private void registerCallbackMethods() {
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnCapture_click((Button) v);
            }
        });
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnRecord_click((Button) v);
            }
        });
        btnPhotoMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnPhotoMode_click((Button) v);
            }
        });
        btnVideoMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnVideoMode_click((Button) v);
            }
        });

        btnRecord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                on_btnRecord_checkedChanged(buttonView, isChecked);
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnMap_click((Button) v);
            }
        });
    }

    public void initVideoSurface() {

        if (null != videoSurface) {
            videoSurface.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {

                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    on_videoSurface_available(surface, width, height);
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                    on_videoSurface_sizeChanged(surface, width, height);
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    return on_videoSurface_destroyed(surface);
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                    on_videoSurface_updated(surface);
                }
            });
        }

    }




    private void on_videoSurface_available(SurfaceTexture surface, int width, int height) {
        Log.e(TAG, "onSurfaceTextureAvailable");
        activity.registerSurface(surface, width, height);
    }

    private void on_videoSurface_sizeChanged(SurfaceTexture surface, int width, int height) {
        Log.e(TAG, "onSurfaceTextureSizeChanged");
        activity.registerSurface(surface, width, height);
    }

    private boolean on_videoSurface_destroyed(SurfaceTexture surface) {
        Log.e(TAG,"onSurfaceTextureDestroyed");
        activity.unregisterSurface();
        return false;
    }

    private void on_videoSurface_updated(SurfaceTexture surface) {
    }





    private void on_btnCapture_click(Button btn) {
        activity.captureAction();
    }

    private void on_btnPhotoMode_click(Button btn) {
        activity.switchCameraMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO);
    }

    private void on_btnVideoMode_click(Button btn) {
        activity.switchCameraMode(SettingsDefinitions.CameraMode.RECORD_VIDEO);
    }

    private void on_btnRecord_click(Button btn) {

    }

    private void on_btnRecord_checkedChanged(CompoundButton btn, boolean isChecked) {
        if (isChecked) {
            activity.startRecord();
        } else {
            activity.stopRecord();
        }
    }

    private void on_btnMap_click(Button btn) {
        activity.switchToMapActivity();
    }




    private void onReturn(View view) {
        Log.e(TAG, "onReturn");
        activity.finish();
    }



    public void showRecordTime() {
        txtRecordingTime.setVisibility(View.VISIBLE);
    }

    public void hideRecordTime() {
        txtRecordingTime.setVisibility(View.INVISIBLE);
    }

    public void updateRecordTime(int recordTime) {
        int minutes = (recordTime % 3600) / 60;
        int seconds = recordTime % 60;

        final String timeString = String.format("%02d:%02d", minutes, seconds);

        txtRecordingTime.setText(timeString);
    }
}
