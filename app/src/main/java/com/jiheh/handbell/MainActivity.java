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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

// Choose a direction
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
    private int currentPitch;
    private int middleC;
    private int middleD;
    private int middleE;
    private int middleF;
    private int middleG;
    private int middleA;
    private int middleB;
    private int highC;
    private int highD;
    private int highE;
    private int highF;
    private int highG;

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
                    .setMaxStreams(12)
                    .build();

        } else {
            soundPool = new SoundPool(12, AudioManager.STREAM_MUSIC, 1);
        }

//        Attempt to accomplish programmatically
//        LinearLayout pitchList = findViewById(R.id.listOfPitches);
//
//        Field[] pitches = R.raw.class.getFields();
//        for (int idx = 0; idx < pitches.length; idx++) {
//            String pitchName = pitches[idx].getName();
//            final int pitchId = getResources().getIdentifier("raw/" + pitchName, null, this.getPackageName());
//
//            TextView pitchView = new TextView(this);
//            pitchView.setText(getString(R.string.pitch_name, pitchName.toUpperCase().charAt(pitchName.length() - 1)));
//            pitchList.addView(pitchView);
//
//            soundPool.load(this, pitchId, 1);
//            pitchView.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    changePitch(middleC);
//                }
//            });
//        }

        middleC = soundPool.load(this, R.raw.note_01_c, 1);
        middleD = soundPool.load(this, R.raw.note_02_d, 1);
        middleE = soundPool.load(this, R.raw.note_03_e, 1);
        middleF = soundPool.load(this, R.raw.note_04_f, 1);
        middleG = soundPool.load(this, R.raw.note_05_g, 1);
        middleA = soundPool.load(this, R.raw.note_06_a, 1);
        middleB = soundPool.load(this, R.raw.note_07_b, 1);
        highC = soundPool.load(this, R.raw.note_08_c, 1);
        highD = soundPool.load(this, R.raw.note_09_d, 1);
        highE = soundPool.load(this, R.raw.note_10_e, 1);
        highF = soundPool.load(this, R.raw.note_11_f, 1);
        highG = soundPool.load(this, R.raw.note_12_g, 1);

        currentPitch = middleC;

        findViewById(R.id.note_01_c).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { changePitch(middleC); }
        });
        findViewById(R.id.note_02_d).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { changePitch(middleD); }
        });
        findViewById(R.id.note_03_e).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { changePitch(middleE); }
        });
        findViewById(R.id.note_04_f).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { changePitch(middleF); }
        });
        findViewById(R.id.note_05_g).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { changePitch(middleG); }
        });
        findViewById(R.id.note_06_a).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { changePitch(middleA); }
        });
        findViewById(R.id.note_07_b).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { changePitch(middleB); }
        });
        findViewById(R.id.note_08_c).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { changePitch(highC); }
        });
        findViewById(R.id.note_09_d).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { changePitch(highD); }
        });
        findViewById(R.id.note_10_e).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { changePitch(highE); }
        });
        findViewById(R.id.note_11_f).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { changePitch(highF); }
        });
        findViewById(R.id.note_12_g).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { changePitch(highG); }
        });
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
        speed.setText("" + Math.round(currentVelocity));

        float MAX_SOUND_VELOCITY = 1300f;
        float volume = Math.abs(currentVelocity) / MAX_SOUND_VELOCITY;
        soundPool.play(currentPitch, volume, volume, 1, 0, 1);

        springAnim.setStartVelocity(currentVelocity * 10);
        springAnim.start();
    }

    public void changePitch(int pitchId) {
        currentPitch = pitchId;
    }
}
