package ch.epfl.malogouryduroslan.mysportapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends WearableActivity {

    private TextView mTextView;
    private ConstraintLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textView);
        mTextView.setText("Hello world from watch");

        mLayout = findViewById(R.id.container);
        // Enables Always-on
        setAmbientEnabled();

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = null;
                message = intent.getStringExtra()

            }
        });
    }

    @Override
    public void onEnterAmbient (Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onExitAmbient () {
        super.onExitAmbient();
        updateDisplay();
    }

    private void updateDisplay() {
        if(isAmbient())
            mLayout.setBackgroundColor(getResources().getColor(
                    android.R.color.black,getTheme()));
        else
            mLayout.setBackgroundColor(getResources().getColor(
                    android.R.color.white,getTheme()));
    }
}
