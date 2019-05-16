package tradr.uav.app.activities.debug;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.view.View.OnClickListener;


import tradr.uav.app.R;
import tradr.uav.app.services.common.TradrService;

public class DebugActivtity extends AppCompatActivity {


    Button btnConnect;
    Button btnDisconnect;
    private OnClickListener btnConnectListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            startConnection();
        }
    };

    private OnClickListener btnDisconnectListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            stopConnection();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);


        btnConnect = (Button) this.findViewById(R.id.btn_connect);

        btnConnect.setOnClickListener(btnConnectListener);

        btnDisconnect = (Button) this.findViewById(R.id.btn_disconnect);

        btnDisconnect.setOnClickListener(btnDisconnectListener);

        btnConnect.setEnabled(true);
        btnDisconnect.setEnabled(false);

    }

    private void startConnection() {


        startService(new Intent(this, TradrService.class));
        btnConnect.setEnabled(false);
        btnDisconnect.setEnabled(true);


    }

    private void stopConnection() {

        stopService(new Intent(this, TradrService.class));
        btnConnect.setEnabled(true);
        btnDisconnect.setEnabled(false);
    }

}
