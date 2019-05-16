package tradr.uav.app.activities.map;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import tradr.uav.app.R;
import tradr.uav.app.model.map.MapWidget;


public class TaskWizAreaFragment_1_Polygon extends Fragment {


    public static TaskWizAreaFragment_1_Polygon newInstance(MapWidget mapWidget) {
        TaskWizAreaFragment_1_Polygon fragment = new TaskWizAreaFragment_1_Polygon();
        fragment.setMapWidget(mapWidget);
        return fragment;
    }



    private View view;

    private Button btnDeleteLastPoint;
    private Button btnFinished;

    private InteractionListener listener;

    private MapWidget mapWidget;

    private List<LatLng> polygon;



    public TaskWizAreaFragment_1_Polygon() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        polygon = new ArrayList<LatLng>(30);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_taskwiz_area_1_polygon, container, false);

        initUI();

        return view;
    }


    private void initUI() {

        btnDeleteLastPoint = (Button) view.findViewById(R.id.btn_delete_last_point);
        btnFinished        = (Button) view.findViewById(R.id.btn_finished);

        btnDeleteLastPoint.setEnabled(false);
        btnFinished.setEnabled(false);

        registerCallback();
    }

    private void registerCallback() {

        btnDeleteLastPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnDeleteLastPoint_click((Button) v);
            }
        });
        btnFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnFinished_click((Button) v);
            }
        });
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

    private void on_btnDeleteLastPoint_click(Button btn) {
        mapWidget.clearMapMarker();
        polygon.remove(polygon.size() - 1);
        mapWidget.drawWaypoints(polygon);

        if (polygon.isEmpty()) {
            btnDeleteLastPoint.setEnabled(false);
        }

        if (polygon.size() < 3) {
            btnFinished.setEnabled(false);
        }
    }

    private void on_btnFinished_click(Button btn) {
        listener.onFinished(polygon);
    }

    private void on_mapWidget_click(LatLng pos) {
        mapWidget.clearMapMarker();
        polygon.add(pos);
        mapWidget.drawWaypoints(polygon);

        if (!polygon.isEmpty()) {
            btnDeleteLastPoint.setEnabled(true);
        }

        if (polygon.size() >= 3) {
            btnFinished.setEnabled(true);
        }
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

        void onFinished(List<LatLng> polygon);
    }
}
