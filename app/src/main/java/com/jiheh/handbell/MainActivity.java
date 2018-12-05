package com.jiheh.handbell;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

// Choose a direction
// Choose a pitch
// Add songs

public class MainActivity extends Activity implements SensorEventListener {
    private ImageView clapper;
    private TextView speed;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastUpdateTime = 0;
    private float last_x;
    private static final int SHAKE_THRESHOLD = 300;

    private SpringAnimation springAnim;
    private float clapperFinalPos;

    private SoundPool soundPool;
    private int lowG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clapper = findViewById(R.id.clapper);
        speed = findViewById(R.id.velocity);

        setupAccelerometer();
        setupSoundPool();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (springAnim == null || clapperFinalPos == 0.0f) {
            createSpringAnimation();
        }

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long timeDiff = currentTime - lastUpdateTime;

            if (timeDiff > 100) {
                float x = sensorEvent.values[0];
                float velocity = (last_x - x) / timeDiff * 10000;

                if (Math.abs(velocity) > SHAKE_THRESHOLD) {
                    ringBell(velocity);
                }

                lastUpdateTime = currentTime;
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
        soundPool.release();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setupAccelerometer();
        setupSoundPool();
    }

    private void setupAccelerometer() {
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void setupSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool
                    .Builder()
                    .setMaxStreams(10)
                    .build();

        } else {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        }

        lowG = soundPool.load(this, R.raw.low_g, 1);
    }

    private void createSpringAnimation() {
        clapperFinalPos = clapper.getX();

        springAnim = new SpringAnimation(clapper, DynamicAnimation.X);
        SpringForce springForce = new SpringForce();

        springForce.setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);
        springForce.setStiffness(SpringForce.STIFFNESS_LOW);
        springForce.setFinalPosition(clapperFinalPos);

        springAnim.setSpring(springForce);
    }

    private void ringBell(float currentVelocity) {
        speed.setText("" + currentVelocity);

        float MAX_SOUND_VELOCITY = 800f;
        float volume = Math.abs(currentVelocity) / MAX_SOUND_VELOCITY;
        soundPool.play(lowG, volume, volume, 1, 0, 1);

        springAnim.setStartVelocity(currentVelocity * 10);
        springAnim.start();
    }
}
