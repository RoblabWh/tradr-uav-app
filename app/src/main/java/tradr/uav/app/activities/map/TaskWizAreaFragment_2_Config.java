package tradr.uav.app.activities.map;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import tradr.uav.app.R;
import tradr.uav.app.model.uav.UAV;


public class TaskWizAreaFragment_2_Config extends Fragment {

    public static TaskWizAreaFragment_2_Config newInstance() {
        TaskWizAreaFragment_2_Config fragment = new TaskWizAreaFragment_2_Config();
        return fragment;
    }

    private View view;

    private TextView txtAltitude;
    private TextView txtOverlap;
    private TextView txtSpeed;
    private TextView txtGimbalPitch;

    private SeekBar seekBarAltitude;
    private SeekBar seekBarOverlap;
    private SeekBar seekBarSpeed;
    private SeekBar seekBarGimbalPitch;

    private Button btnNext;
    private Button btnBack;

    private InteractionListener listener;

    public TaskWizAreaFragment_2_Config() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setInteractionListener(InteractionListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_taskwiz_area_2_config, container, false);
        initUI();

        return view;
    }


    private void initUI() {

        txtAltitude    = (TextView) view.findViewById(R.id.txt_altitude);
        txtOverlap     = (TextView) view.findViewById(R.id.txt_overlap);
        txtSpeed       = (TextView) view.findViewById(R.id.txt_speed);
        txtGimbalPitch = (TextView) view.findViewById(R.id.txt_gimbal_pitch);

        seekBarAltitude = (SeekBar) view.findViewById(R.id.seekBar_altitude);
        seekBarOverlap = (SeekBar) view.findViewById(R.id.seekBar_overlap);
        seekBarGimbalPitch= (SeekBar) view.findViewById(R.id.seekBar_gimbal_pitch);
        seekBarSpeed = (SeekBar) view.findViewById(R.id.seekBar_speed);

        btnBack = (Button) view.findViewById(R.id.btn_back);
        btnNext = (Button) view.findViewById(R.id.btn_next);

        txtAltitude.setText(Integer.toString((Math.abs(UAV.MIN_ALTITUDE) + UAV.MAX_ALTITUDE) / 3));
        txtOverlap.setText("10");
        txtSpeed.setText(Integer.toString(UAV.MIN_FLIGHT_SPEED_M_PER_S));
        txtGimbalPitch.setText(Integer.toString((-1) * UAV.MAX_PITCH_ANGLE));

        seekBarAltitude.setProgress((Math.abs(UAV.MIN_ALTITUDE)+(Math.abs(UAV.MIN_ALTITUDE) + UAV.MAX_ALTITUDE) / 3));
        seekBarAltitude.setMax(UAV.MAX_ALTITUDE - UAV.MIN_ALTITUDE);
        seekBarOverlap.setMax(100 - 10);
        seekBarGimbalPitch.setMax((-1) * UAV.MIN_PITCH_ANGLE - (-1) * UAV.MAX_PITCH_ANGLE);
        seekBarSpeed.setMax(UAV.MAX_FLIGHT_SPEED_M_PER_S - UAV.MIN_FLIGHT_SPEED_M_PER_S);




        registerCallback();
    }

    private void registerCallback() {

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnBack_click((Button) v);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnNext_click((Button) v);
            }
        });

        seekBarAltitude.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                on_progress_changed(txtAltitude,progress,UAV.MIN_ALTITUDE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBarGimbalPitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                on_progress_changed(txtGimbalPitch,progress,(-1) * UAV.MAX_PITCH_ANGLE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                on_progress_changed(txtSpeed,progress,UAV.MIN_FLIGHT_SPEED_M_PER_S);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarOverlap.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                on_progress_changed(txtOverlap,progress,10);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void unregisterCallback() {
        btnBack.setOnClickListener(null);
        btnNext.setOnClickListener(null);
    }



    private void on_btnBack_click(Button btn) {
        if (listener != null) {
            listener.onBack();
        }
    }

    private void on_progress_changed(TextView view,int progress,int min) {
        view.setText(Integer.toString(progress+min));
    }

    private void on_btnNext_click(Button btn) {
        if (listener != null) {
            int overlap = Integer.parseInt(txtOverlap.getText().toString());
            float altitude = Float.parseFloat(txtAltitude.getText().toString());
            float speed = Float.parseFloat(txtSpeed.getText().toString());
            int gimbalPitch = Integer.parseInt(txtGimbalPitch.getText().toString());

            listener.onNext(overlap, altitude, speed, (-1) * gimbalPitch);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
        unregisterCallback();
    }


    public interface InteractionListener {

        void onNext(int overlap, float altitude, float speed, int gimbalPitch);
        void onBack();
    }

}
