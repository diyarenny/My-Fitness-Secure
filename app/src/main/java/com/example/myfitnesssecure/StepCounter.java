package com.example.myfitnesssecure;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StepCounter extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

    }

    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

    }


    @Override
    public final void onSensorChanged(SensorEvent event){
        //reading from the sensor

        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            Log.i("STEP1", "Detected");
        }
        float steps = event.values[0];
        Log.i("STEP", String.valueOf(steps));
        TextView textView = (TextView) findViewById(R.id.stepNum);
        textView.setText("Steps = " + steps);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause(){

        super.onPause();
        sensorManager.unregisterListener(this);
    }

}
