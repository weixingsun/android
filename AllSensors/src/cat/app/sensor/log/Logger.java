package cat.app.sensor.log;

import java.util.Arrays;

import cat.app.sensor.Sensors;

public class Logger {

	public static void init(){
		Sensors.logSensors=Arrays.asList(Sensors.ids);
	}
}
