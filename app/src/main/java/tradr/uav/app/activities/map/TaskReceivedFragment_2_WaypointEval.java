package tradr.uav.app.activities.map;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import tradr.uav.app.R;


public class TaskReceivedFragment_2_WaypointEval extends Fragment {

    public static TaskReceivedFragment_2_WaypointEval newInstance() {
        TaskReceivedFragment_2_WaypointEval fragment = new TaskReceivedFragment_2_WaypointEval();
        return fragment;
    }

    private View view;

    private Button btnAccept;
    private Button btnCancel;

    private InteractionListener listener;

    public TaskReceivedFragment_2_WaypointEval() {
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

        view = inflater.inflate(R.layout.fragment_taskreceived_2_waypointseval, container, false);
        initUI();

        return view;
    }


    private void initUI() {


        btnAccept = (Button) view.findViewById(R.id.btn_accept);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);


        registerCallback();
    }

    private void registerCallback() {

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnAccept_click((Button) v);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnCancel_click((Button) v);
            }
        });

    }

    private void unregisterCallback() {
        btnAccept.setOnClickListener(null);
        btnCancel.setOnClickListener(null);
    }



    private void on_btnAccept_click(Button btn) {
        if (listener != null) {
            listener.onAccepted();
        }
    }

    private void on_btnCancel_click(Button btn) {
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
        void onCanceled();
    }

}
