package com.android.myapplication.ui;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.myapplication.R;

public class LauncherActivity extends AppCompatActivity {


    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        if (handler == null) {
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(LauncherActivity.this, MainActivity.class));
                    overridePendingTransition(R.anim.screen_zoom_in, R.anim.screen_zoom_out);
                }
            }, 200);
        }
    }

    @Override
    protected void onDestroy() {
        handler = null;
        super.onDestroy();
    }
}
