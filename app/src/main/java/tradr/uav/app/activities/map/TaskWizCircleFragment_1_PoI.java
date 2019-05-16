package tradr.uav.app.activities.map;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import tradr.uav.app.R;
import tradr.uav.app.model.map.MapWidget;


public class TaskWizCircleFragment_1_PoI extends Fragment {


    public static TaskWizCircleFragment_1_PoI newInstance(MapWidget mapWidget) {
        TaskWizCircleFragment_1_PoI fragment = new TaskWizCircleFragment_1_PoI();
        fragment.setMapWidget(mapWidget);
        return fragment;
    }



    View view;


    private InteractionListener listener;

    private MapWidget mapWidget;



    public TaskWizCircleFragment_1_PoI() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_taskwiz_circ_1_poi, container, false);

        initUI();

        return view;
    }


    private void initUI() {

        registerCallback();
    }

    private void registerCallback() {

        mapWidget.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                on_mapWidget_click(latLng);
            }
        });
    }

    private void unregisterCallback() {
        mapWidget.setOnMapClickListener(null);
    }


    private void on_mapWidget_click(LatLng pos) {
        //Log.d("pos", "circle_1");
        listener.onFinished(pos);
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
        unregisterCallback();
    }

    private void setMapWidget(MapWidget mapWidget) {
        this.mapWidget = mapWidget;
    }



    public interface InteractionListener {

        void onFinished(LatLng pos);
    }
}
