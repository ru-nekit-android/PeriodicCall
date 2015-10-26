package ru.nekit.android.periodiccall;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERIOD = 1000;
    private Handler handler;
    private Runnable runnable;
    private ScheduledThreadPoolExecutor executor;
    private ScheduledFuture<?> executorFuture;
    private Runnable runnable2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.stop_button).setOnClickListener(this);

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
        runnable2 = new Runnable() {
            @Override
            public void run() {
                Log.v("ru.nekit.android.vtag", "call via Executor");
            }
        };
        executor = new ScheduledThreadPoolExecutor(1);
        executorFuture = executor.scheduleWithFixedDelay(runnable2, PERIOD, PERIOD, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onClick(View v) {
        handler.removeCallbacks(runnable);
        executorFuture.cancel(true);
    }
}

