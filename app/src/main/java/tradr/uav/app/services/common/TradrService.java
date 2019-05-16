package tradr.uav.app.services.common;

import android.app.IntentService;
import android.content.Intent;

import android.util.Log;

import tradr.uav.app.model.task.Task.Task;

public class TradrService extends IntentService {

    public static final String FLAG_TASK_RECEIVED = "TASK_RECEIVED";

    public static final String FLAG_TASK_CONTROLLED = "TASK_CONTROLLED";


    private static TradrService tradrService;

    public static TradrService getInstance() {
        return tradrService;
    }


    private TradrUavInterfaceClient client;

    public TradrService() {

        super("TRADR Service");

        TradrService.tradrService = this;
    }



    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();

        Log.d("Marke", "before new Client");
        client = new TradrUavInterfaceClient();

        client.addTaskListener(new TradrUavInterfaceClient.TaskListener() {
            @Override
            public void onTaskReceived(Task task) {
                Log.d("Task", "Task will be send");

                Intent taskReceivedIntent = new Intent(FLAG_TASK_RECEIVED);
                taskReceivedIntent.putExtra("tradr.uav.app.model.task.Task.Task", task);
                //TradrService.this.sendBroadcast(taskReceivedIntent);
                //LocalBroadcastManager.getInstance(TradrService.getInstance()).sendBroadcast(taskReceivedIntent);
                getApplicationContext().sendBroadcast(taskReceivedIntent);
            }

            @Override
            public void onTaskControlled() {
                Intent taskControlledIntent = new Intent(FLAG_TASK_CONTROLLED);
                TradrService.this.sendBroadcast(taskControlledIntent);
            }
        });
        Log.d("Marke", "before connect");
        client.connect();

        this.getMainLooper().loop();
    }





}
