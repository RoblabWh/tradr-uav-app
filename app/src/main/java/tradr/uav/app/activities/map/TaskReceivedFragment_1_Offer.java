package tradr.uav.app.activities.map;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import tradr.uav.app.R;
import tradr.uav.app.model.map.MapWidget;


public class TaskReceivedFragment_1_Offer extends Fragment {


    public static TaskReceivedFragment_1_Offer newInstance(MapWidget mapWidget) {
        TaskReceivedFragment_1_Offer fragment = new TaskReceivedFragment_1_Offer();
        return fragment;
    }



    private View view;

    private Button btnAccept;
    private Button btnCancel;

    private InteractionListener listener;





    public TaskReceivedFragment_1_Offer() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_taskreceived_1_offer, container, false);

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


    private void on_btnAccept_click(Button btn) {
        listener.onAccepted();
    }

    private void on_btnCancel_click(Button btn) {
        listener.onCanceled();
    }




    public void setInteractionListener(InteractionListener listener) {
        this.listener = listener;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }




    public interface InteractionListener {

        void onCanceled();
        void onAccepted();
    }
}
