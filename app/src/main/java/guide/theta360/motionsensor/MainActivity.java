/**
 * Copyright 2018 Ricoh Company, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package guide.theta360.motionsensor;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import guide.theta360.motionsensor.AccelerationSensor.AccelerationGraSensor;

import guide.theta360.motionsensor.R;

import guide.theta360.motionsensor.task.TakePictureTask;

import com.theta360.pluginlibrary.activity.PluginActivity;
import com.theta360.pluginlibrary.callback.KeyCallback;
import com.theta360.pluginlibrary.receiver.KeyReceiver;
import com.theta360.pluginlibrary.values.LedColor;
import com.theta360.pluginlibrary.values.LedTarget;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends PluginActivity {
    private TakePictureTask.Callback mTakePictureTaskCallback = new TakePictureTask.Callback() {
        @Override
        public void onTakePicture(String fileUrl) {

        }
    };


    // specific to sensor tutorial
    private SensorManager graSensorManager;
    private AccelerationGraSensor accelerationGraSensor;

    private static final int ACCELERATION_INTERVAL_PERIOD = 1000;
    private Timer timer;
    private static final float ACCELERATION_THRESHOLD = 3.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //加速度を取れる状態に設定
        graSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerationGraSensor = new AccelerationGraSensor(graSensorManager);

        // Set enable to close by pluginlibrary, If you set false, please call close() after finishing your end processing.
        setAutoClose(true);
        // Set a callback when a button operation event is acquired.
        setKeyCallback(new KeyCallback() {
            @Override
            public void onKeyDown(int keyCode, KeyEvent event) {
                if (keyCode == KeyReceiver.KEYCODE_CAMERA) {
                    /*
                     * To take a static picture, use the takePicture method.
                     * You can receive a fileUrl of the static picture in the callback.
                     */
                    new TakePictureTask(mTakePictureTaskCallback).execute();
                }

            }

            @Override
            public void onKeyUp(int keyCode, KeyEvent event) {
                /**
                 * You can control the LED of the camera.
                 * It is possible to change the way of lighting, the cycle of blinking, the color of light emission.
                 * Light emitting color can be changed only LED3.
                 */
                notificationLedBlink(LedTarget.LED3, LedColor.BLUE, 1000);
            }

            @Override
            public void onKeyLongPress(int keyCode, KeyEvent event) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 加速度のログを出力
                Log.d("accelerX", String.valueOf(accelerationGraSensor.getX()));
                Log.d("accelerY", String.valueOf(accelerationGraSensor.getY()));
                Log.d("accelerZ", String.valueOf(accelerationGraSensor.getZ()));

                if (Math.abs(accelerationGraSensor.getX()) > ACCELERATION_THRESHOLD ||
                        Math.abs(accelerationGraSensor.getY()) > ACCELERATION_THRESHOLD ||
                        Math.abs(accelerationGraSensor.getZ()) > ACCELERATION_THRESHOLD) {
                    new TakePictureTask(mTakePictureTaskCallback).execute();
                }
//
            }
        }, 0, ACCELERATION_INTERVAL_PERIOD);
    }

    @Override
    protected void onPause() {
        // Do end processing
        //close();

        super.onPause();
        timer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (graSensorManager != null) {
            // イベントリスナーの解除
            graSensorManager.unregisterListener(accelerationGraSensor);
        }
    }
}
