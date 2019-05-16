package tradr.uav.app.activities.map;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import tradr.uav.app.R;


public class MenuFragment extends Fragment {

    public enum MENU_OPTION {
        CIRCLE,
        AREA,
        FREE
    }

    public static MenuFragment newInstance() {
        MenuFragment fragment = new MenuFragment();
        return fragment;
    }




    private View view;

    private Button btnCancel;

    private Button btnCircle;
    private Button btnArea;
    private Button btnFree;

    private Button btnConfig;

    private InteractionListener listener;



    public MenuFragment() {

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

        view = inflater.inflate(R.layout.fragment_menu, container, false);

        initUI();

        return view;
    }


    private void initUI() {

        btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        btnCircle = (Button) view.findViewById(R.id.btn_circle);
        btnArea   = (Button) view.findViewById(R.id.btn_area);
        btnFree   = (Button) view.findViewById(R.id.btn_free);

        btnConfig = (Button) view.findViewById(R.id.btn_config);

        registerCallback();
    }

    private void registerCallback() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnCancel_click((Button) v);
            }
        });

        btnCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnCircle_click((Button) v);
            }
        });

        btnArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnArea_click((Button) v);
            }
        });

        btnFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnFree_click((Button) v);
            }
        });

        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                on_btnConfig_click((Button) v);
            }
        });
    }

    private void on_btnCancel_click(Button btn) {
        if (listener != null) {
            listener.onCanceled();
        }
    }

    private void on_btnCircle_click(Button btn) {
        if (listener != null) {
            listener.onOptionSelected(MENU_OPTION.CIRCLE);
        }
    }

    private void on_btnArea_click(Button btn) {
        if (listener != null) {
            listener.onOptionSelected(MENU_OPTION.AREA);
        }
    }

    private void on_btnFree_click(Button btn) {
        if (listener != null) {
            listener.onOptionSelected(MENU_OPTION.FREE);
        }
    }

    private void on_btnConfig_click(Button btn) {
        if (listener != null) {
            listener.onConfigButtonClicked();
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
    }







    public interface InteractionListener {
        void onCanceled();

        void onOptionSelected(MENU_OPTION selectedOption);

        void onConfigButtonClicked();
    }

}
