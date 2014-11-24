package cat.app.sensor;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;

public class SensorServiceUnused extends Service implements SensorEventListener {
	//private static final String DEBUG_TAG = "SensorService";

	private SensorManager sensorManager = null;
	private Sensor sensor = null;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, sensor,
				SensorManager.SENSOR_DELAY_NORMAL);
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// do nothing
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// grab the values and timestamp
		new SensorEventLoggerTask().execute(event);
		// stop the sensor and service
		sensorManager.unregisterListener(this);
		stopSelf();
	}

	private class SensorEventLoggerTask extends
			AsyncTask<SensorEvent, Void, Void> {
		@Override
		protected Void doInBackground(SensorEvent... events) {
			//SensorEvent event = events[0];
			// log the value
			return null;
		}
	}

}
