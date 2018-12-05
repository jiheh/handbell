package com.jiheh.handbell;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastUpdateTime = 0;
    private float last_x;
    private static final int SHAKE_THRESHOLD = 300;

    private ImageView clapper;
    private SpringAnimation springAnim;
    private SpringForce springForce;
    private float clapperFinalPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Accelerometer setup
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        clapper = findViewById(R.id.clapper);
        springAnim = new SpringAnimation(clapper, DynamicAnimation.X);

        springForce = new SpringForce();
        springForce.setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);
        springForce.setStiffness(SpringForce.STIFFNESS_LOW);

        springAnim.setSpring(springForce);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (clapperFinalPos == 0.0f) {
            clapperFinalPos = clapper.getX();
            springForce.setFinalPosition(clapperFinalPos);
        }

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdateTime > 100) {
                long timeDiff = currentTime - lastUpdateTime;
                lastUpdateTime = currentTime;

                float velocity = (last_x - x) / timeDiff * 10000;

                if (Math.abs(velocity) > SHAKE_THRESHOLD) {
                    springAnim.setStartVelocity(velocity * 10);
                    springAnim.start();
                }

                last_x = x;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Best practice to unregister sensor when app hibernates
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
