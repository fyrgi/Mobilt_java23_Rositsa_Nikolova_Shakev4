package com.example.mobilt_java23_rositsa_nikolova_shakev4;


import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope, lightSensor;

    private TextView accelerometerText, gyroscopeText, lightText;
    private ImageView imageView;

    private float rotationValue = 0;

    // To fine tune the acceleration. Higher value for hard shakes
    private static final float THRESHOLD = 20.0f;
    // 1-second delay between shake detections
    private static final long COOLDOWN = 1000;
    private long lastShakeTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI-components
        accelerometerText = findViewById(R.id.accelerometerText);
        gyroscopeText = findViewById(R.id.gyroscopeText);
        lightText = findViewById(R.id.lightText);
        imageView = findViewById(R.id.imageView);

        // Sensor Manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Initialize sensors
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        // Register the sensors
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                handleAccelerometer(sensorEvent.values);
                break;
            case Sensor.TYPE_GYROSCOPE:
                handleGyroscope(sensorEvent.values);
                break;
            case Sensor.TYPE_LIGHT:
                handleLightSensor(sensorEvent.values);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, lightSensor);
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, gyroscope);
    }

    @Override
    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this, lightSensor);
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, gyroscope);
    }


    private void handleAccelerometer(float[] values) {
        String accelerometerData = String.format("Accelerometer\nX: %.2f, Y: %.2f, Z: %.2f", values[0], values[1], values[2]);
        accelerometerText.setText(accelerometerData);

        float x = values[0];
        float y = values[1];
        float z = values[2];
        double magnitude = Math.sqrt(x * x + y * y + z * z);

        // Check if magnitude exceeds the threshold
        if (magnitude > THRESHOLD) {
            long currentTime = System.currentTimeMillis();
            // Ensure a cooldown period between shakes
            if (currentTime - lastShakeTime > COOLDOWN) {
                lastShakeTime = currentTime;
                Toast.makeText(this, "Device shaken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleGyroscope(float[] values) {
        String gyroscopeData = String.format("Gyroscope\nX: %.2f, Y: %.2f, Z: %.2f", values[0], values[1], values[2]);
        gyroscopeText.setText(gyroscopeData);

        // Rotate the image based on the data from the gyroscope's Z value
        rotationValue += values[2] * 10;
        imageView.setRotation(rotationValue);
    }

    private void handleLightSensor(float[] values) {
        int maxBrightness = 255;
        // Update the text view with the current light level
        String lightData = String.format("Light Level: %.2f lx", values[0]);
        lightText.setText(lightData);
        Log.d("InitialValue", "The initialValue of values[0] is "+ values[0]);
        // Calculate brightness based on light sensor values
        int brightness = (int) Math.abs(values[0]) * 10; // Scale by 10 to make it more sensitive
        if (brightness > maxBrightness) {
            brightness = maxBrightness; // Ensure it doesn't exceed the maximum brightness
        }

        // Update background color (use parent layout for visibility)
        LinearLayout layout = findViewById(R.id.main);
        // Change the background color based on the intake of data from the Light sensor
        layout.setBackgroundColor(Color.rgb(brightness, 255 - brightness, 200));
    }
}

