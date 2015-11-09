package ru.nekit.android.periodiccall;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.nekit.android.periodiccall.service.GCMPeriodicService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERIOD = 1000;
    private static final String GSM_TASK_TAG = "myScan|1";

    private Handler handler;
    private Runnable runnable;
    private ScheduledFuture<?> executorFuture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.stop_button).setOnClickListener(this);
        findViewById(R.id.stop_button2).setOnClickListener(this);
        findViewById(R.id.stop_button3).setOnClickListener(this);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Log.v("ru.nekit.android.vtag", "call via Handler");
                handler.postDelayed(this, PERIOD);
            }
        };
        handler.postDelayed(runnable, PERIOD);

        //
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                Log.v("ru.nekit.android.vtag", "call via Executor");
            }
        };
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executorFuture = executor.scheduleWithFixedDelay(runnable2, PERIOD, PERIOD, TimeUnit.MILLISECONDS);

        long periodSecs = 1L;
        long flexSecs = 0;

        PeriodicTask mPeriodicTask = new PeriodicTask.Builder()
                .setService(GCMPeriodicService.class)
                .setPeriod(periodSecs)
                .setFlex(flexSecs)
                .setTag(GSM_TASK_TAG)
                .setPersisted(true)
                .setRequiredNetwork(com.google.android.gms.gcm.Task.NETWORK_STATE_ANY)
                .setRequiresCharging(false)
                .setUpdateCurrent(true)
                .build();


        GcmNetworkManager.getInstance(this).schedule(mPeriodicTask);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stop_button:

                handler.removeCallbacks(runnable);

                break;

            case R.id.stop_button2:

                executorFuture.cancel(true);

                break;

            case R.id.stop_button3:

                GcmNetworkManager.getInstance(this).cancelTask(GSM_TASK_TAG, GCMPeriodicService.class);

                break;
        }


    }
}

