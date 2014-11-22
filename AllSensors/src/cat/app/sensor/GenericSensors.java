package cat.app.sensor;

import android.hardware.*;

public class GenericSensors implements SensorEventListener{

	static GenericSensors gsensors = new GenericSensors();
	public static String STATUS_RUNNING = "running";
	public static String STATUS_STOPPED = "stopped";
	public static String status = STATUS_STOPPED;
	
	public static SensorManager sensorManager;
	public static void init(SensorManager sm){
		sensorManager=sm;
	}
	private void collectData(SensorEvent event) {
		SensorData sd = new SensorData(event.sensor, event.values);
		Sensors.saData.append(event.sensor.getType(), sd);
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		collectData(event);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	public static void generateSensorData(SensorManager lm) {}


	public static void start() {
		if(status.equals(STATUS_RUNNING)){  //running==false
			status=STATUS_STOPPED;
		}
		registerSensors(Sensors.current);
	}
	
	public static void stop() {
		if(status.equals(STATUS_RUNNING)){
			status=STATUS_STOPPED;
		}
		sensorManager.unregisterListener(gsensors);
	}
	public static void stop(int[] toStop) {
		for(int id:toStop){
			Sensor s = sensorManager.getDefaultSensor(id);
			sensorManager.unregisterListener(gsensors,s);
		}
		
	}
	private static void registerSensors(int[] sensors) {
		Sensors.init();
		for (int i : sensors) {
			Sensor s = sensorManager.getDefaultSensor(i);
			if (s != null) {
				sensorManager.registerListener(gsensors, s, Sensors.refreshRate);
				Sensors.currentSupportedSensors.add( s.getType());
			}
		}
	}
	
	public static void switchTo(int[] newSensors){
		if(Sensors.current!=null){
		Sensors.toStop=Sensors.current;
		GenericSensors.stop(Sensors.toStop);
		}
		Sensors.current=newSensors;
	}
}
