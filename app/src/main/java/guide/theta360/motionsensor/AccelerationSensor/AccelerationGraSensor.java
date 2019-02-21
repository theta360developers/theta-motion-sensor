package guide.theta360.motionsensor.AccelerationSensor;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AccelerationGraSensor implements SensorEventListener {

    private static final float FILTER = 0.1f; // フィルタ係数

    private float[] currentGravityValues      = {0.0f, 0.0f, 0.0f};
    private float[] currentAccelerationValues = {0.0f, 0.0f, 0.0f};

    public synchronized float getX() {
        return this.currentAccelerationValues[0];
    }

    public synchronized float getY() {
        return this.currentAccelerationValues[1];
    }

    public synchronized float getZ() {
        return this.currentAccelerationValues[2];
    }

    public AccelerationGraSensor(SensorManager sensorManager) {
        // イベントリスナーの登録
        Sensor s = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // ローパスフィルタで重力値を抽出
            currentGravityValues[0] = event.values[0] * FILTER + currentGravityValues[0] * (1.0f - FILTER);
            currentGravityValues[1] = event.values[1] * FILTER + currentGravityValues[1] * (1.0f - FILTER);
            currentGravityValues[2] = event.values[2] * FILTER + currentGravityValues[2] * (1.0f - FILTER);

            synchronized (this) {
                // 重力値の除去
                currentAccelerationValues[0] = event.values[0] - currentGravityValues[0];
                currentAccelerationValues[1] = event.values[1] - currentGravityValues[1];
                currentAccelerationValues[2] = event.values[2] - currentGravityValues[2];
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}