
package tradr.uav.app.activities.main;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import dji.sdk.base.BaseProduct;
import dji.sdk.products.Aircraft;

import dji.sdk.sdkmanager.DJISDKManager;

import tradr.uav.app.R;
import tradr.uav.app.UavApplication;
import tradr.uav.app.utils.GeneralUtils;

/**
 * Created by tradr on 20.04.17.
 */

public class MainView {

    public static final String TAG = MainView.class.getName();




    protected MainActivity parentActivity;

    private RelativeLayout layout;

    private TextView titleTextView;

    private ActionBar actionBar;


    private TextView mTextConnectionStatus;
    private TextView mTextProduct;
    private TextView mTextModelAvailable;
    private Button mBtnOpen;
    //private Button mBtnDebug;
    private TextView mSDKVersion;

    private EditText txtIpAddress;

    private Handler mHandler;
    private Handler mHandlerUI;
    private HandlerThread mHandlerThread = new HandlerThread("Bluetooth");

    private BaseProduct mProduct;



    public MainView(MainActivity parentActivity)
    {
        this.parentActivity = parentActivity;

        initUI();
    }



    private void initUI() {
        Log.v(TAG, "initUI");

        this.parentActivity.setContentView(R.layout.activity_main);

        this.actionBar = parentActivity.getSupportActionBar();


        mTextConnectionStatus = (TextView) this.parentActivity.findViewById(R.id.text_connection_status);
        mTextModelAvailable   = (TextView) this.parentActivity.findViewById(R.id.text_model_available);
        mTextProduct          = (TextView) this.parentActivity.findViewById(R.id.text_product_info);
        mSDKVersion           = (TextView) this.parentActivity.findViewById(R.id.txt_sdk_version);
        mBtnOpen              = (Button)   this.parentActivity.findViewById(R.id.btn_open);
        //mBtnDebug              = (Button)   this.parentActivity.findViewById(R.id.btn_debug);

        txtIpAddress = (EditText) this.parentActivity.findViewById(R.id.txt_ipAddress);

        mBtnOpen.setOnClickListener(this.btnOpenListener);

        mBtnOpen.setEnabled(true);

        //mBtnDebug.setOnClickListener(this.btnDebugListener);
        //mBtnDebug.setEnabled(true);

        setupActionBar();


        Log.v("switch", "marke");

    }



    private OnClickListener btnDebugListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.v("switch", "switch");

            // @TODO why doubleclick check?
            if (GeneralUtils.isFastDoubleClick()) return;

            parentActivity.switchToDebugActivity();
        }

    };

    private OnClickListener btnOpenListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.v("switch", "switch");

            // @TODO why doubleclick check?
            if (GeneralUtils.isFastDoubleClick()) return;

            UavApplication.ipAddress = txtIpAddress.getText().toString();

            parentActivity.switchToMapActivity();
        }

    };


    private void setupActionBar() {

        if (null != actionBar) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.actionbar_custom);

            titleTextView = (TextView) (actionBar.getCustomView().findViewById(R.id.title_tv));
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fd0001")));
        }
    }


    /*
    @Override
    protected void onAttachedToWindow() {
        Log.d(TAG, "Comes into the onAttachedToWindow");
        refreshSDKRelativeUI();

        mHandlerThread.start();
        final long currentTime = System.currentTimeMillis();
        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                }
            }
        };
        mHandler.sendEmptyMessage(0);

        mHandlerUI = new Handler(Looper.getMainLooper());
        super.onAttachedToWindow();
    }



    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeCallbacksAndMessages(null);
        mHandlerUI.removeCallbacksAndMessages(null);
        mHandlerThread.quitSafely();
        mHandlerUI = null;
        mHandler = null;
        super.onDetachedFromWindow();
    }
    */

    private void updateVersion() {
        String version = null;
        if (mProduct != null) {
            version = mProduct.getFirmwarePackageVersion();
        }

        if (version == null) {
            mTextModelAvailable.setText("N/A"); //Firmware version:
        } else {
            mTextModelAvailable.setText(version); //"Firmware version: " +
        }
    }


    public void refreshSDKRelativeUI() {
        mSDKVersion.setText("DJI SDK Ver. " + DJISDKManager.getInstance().getSDKVersion());
        mProduct = UavApplication.getUav().getAircraftInstance();
        Log.d(TAG, "mProduct: " + (mProduct == null ? "null" : "unnull"));
        if (mProduct != null  && mProduct.isConnected()) {
            mBtnOpen.setEnabled(true);

            String str = mProduct instanceof Aircraft ? "DJIAircraft" : "DJIHandHeld";
            mTextConnectionStatus.setText("Status: " + str + " connected");
            updateVersion();

            if (null != mProduct.getModel()) {
                mTextProduct.setText("" + mProduct.getModel().getDisplayName());
            } else {
                mTextProduct.setText(R.string.product_information);
            }
        } else {
            mBtnOpen.setEnabled(true);

            mTextProduct.setText(R.string.product_information);
            mTextConnectionStatus.setText(R.string.connection_loose);
        }
    }
}