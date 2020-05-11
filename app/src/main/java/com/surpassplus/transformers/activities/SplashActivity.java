package com.surpassplus.transformers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.surpassplus.transformers.R;
import com.surpassplus.transformers.util.PreferencesHandler;

public class SplashActivity extends AppCompatActivity {

    public static final long SPLASH_WAIT_TIME = 3000;
    private PreferencesHandler mPreferencesHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);


        initUi();
    }

    /**
     * Initializes splash ui and add small delay before navigating to BranchContainerActivity
     */
    private void initUi() {
        mPreferencesHandler = PreferencesHandler.getInstance(SplashActivity.this);
        Thread waitThread = new Thread() {

            @Override
            public void run() {
                try {
                    sleep(SPLASH_WAIT_TIME);
                    Intent intent = new Intent(getApplicationContext(), TransformerContainerActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        waitThread.start();
    }

}
