package com.example.getorientation;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView txtAzimuth, txtPitch, txtRoll;
    SensorManager sensorManager;
    Sensor magSensor, accSensor;
    SensorEventListener listener;

    private float[] magValues ,accValues;
    @Override
    protected void onPause(){
        MainActivity.super.onPause();
        Toast.makeText(getApplicationContext(),"-180",Toast.LENGTH_SHORT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainActivity.super.onPause();
        Toast.makeText(getApplicationContext(), "-180", Toast.LENGTH_SHORT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtAzimuth = findViewById(R.id.txtAzimuth);
        txtPitch = findViewById(R.id.txtpitch);
        txtRoll = findViewById(R.id.txtRoll);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                switch (sensorEvent.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        accValues = sensorEvent.values.clone(); break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        magValues = sensorEvent.values.clone(); break;
                }

                if (magValues != null && accValues != null) {
                    // 1) 회전(Rotation) 행렬과 경사(Inclination) 행렬 얻기
                    float[] R = new float[16]; //얻고자 하는 회전 행렬 (장비의 방향을 계산할 때 이용)
                    float[] I = new float[16]; //얻고자 하는 경사 행렬 (장비의 경사 각도를 계산할 때 이용)
                    SensorManager.getRotationMatrix(R, I, accValues, magValues);
                    // 2) 회전행렬로부터 방향 얻기
                    float[] values = new float[3];
                    SensorManager.getOrientation(R, values);

                    if((int) radian2Degree(values[0]) == 180) {

                        Toast.makeText(MainActivity.this, "180", Toast.LENGTH_SHORT);

                    } else if((int) radian2Degree(values[0]) == -180) {

                        Toast.makeText(MainActivity.this, "-180", Toast.LENGTH_SHORT);

                    }


                    txtAzimuth.setText("Azimuth: " + (int) radian2Degree(values[0]));

                    txtPitch.setText("Pitch: " + (int) radian2Degree(values[1]));

                    txtRoll.setText("Roll: " + (int) radian2Degree(values[2]));

                }




            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {        }
        };
        sensorManager.registerListener(listener, magSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(listener, accSensor, sensorManager.SENSOR_DELAY_UI);

    }

    private Object radian2Degree(float radian) {
        return radian * 180 / (float)Math.PI;
    }
};
