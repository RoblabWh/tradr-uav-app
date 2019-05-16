package tradr.uav.app.activities.map;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import tradr.uav.app.R;


public class TaskWizAreaFragment_3_WaypointsEval extends Fragment {

    public static TaskWizAreaFragment_3_WaypointsEval newInstance() {
        TaskWizAreaFragment_3_WaypointsEval fragment = new TaskWizAreaFragment_3_WaypointsEval();
        return fragment;
    }



    private View view;


    private Button btnYes;
    private Button btnNo;

    private InteractionListener listener;

    public TaskWizAreaFragment_3_WaypointsEval() {
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

        view = inflater.inflate(R.layout.fragment_taskwiz_area_3_waypointseval, container, false);

        initUI();

        return view;
    }

    private void initUI() {

        btnNo = (Button) view.findViewById(R.id.btn_no);
        btnYes = (Button) view.findViewById(R.id.btn_yes);

        registerCallback();
    }

    private void registerCallback() {

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnNo_click((Button) v);
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnYes_click((Button) v);
            }
        });
    }

    private void unregisterCallback() {
        btnNo.setOnClickListener(null);
        btnYes.setOnClickListener(null);
    }


    private void on_btnNo_click(Button btn) {
        if (listener != null) {
            listener.onRefused();
        }
    }

    private void on_btnYes_click(Button btn) {
        if (listener != null) {
            listener.onAccepted();
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

        void onAccepted();
        void onRefused();
    }

}
