package cat.app.sensor;

import java.util.*;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.SparseArray;

/*
 * 传感器类型常量 内部整数值 中文名称 
 * Sensor.TYPE_ACCELEROMETER 1 加速度传感器
 * Sensor.TYPE_MAGNETIC_FIELD 2 磁力传感器 
 * Sensor.TYPE_ORIENTATION 3 方向传感器
 * Sensor.TYPE_GYROSCOPE 4 陀螺仪传感器 
 * Sensor.TYPE_LIGHT 5 环境光照传感器
 * Sensor.TYPE_PRESSURE 6 压力传感器 
 * Sensor.TYPE_TEMPERATURE 7 温度传感器
 * Sensor.TYPE_PROXIMITY 8 距离传感器
 */
// Connect: GSM/NFC/Bluetooth/WiFi/GPS/Camera
// Motion: Accelerometer/Orientation/Gravity/Rotation/Gyroscope
// Environment: Sound/Light/Magnetic/Temperature

// SENSOR_DELAY_FASTEST 20ms
// SENSOR_DELAY_GAME 40ms
// SENSOR_DELAY_UI   90ms
// SENSOR_DELAY_NORMAL 230ms
public class Sensors {
	public static int[] colors = new int[] { Color.RED, Color.GREEN, Color.BLUE,
		Color.YELLOW, Color.CYAN, Color.GRAY,Color.WHITE };

	public static int refreshRate = SensorManager.SENSOR_DELAY_NORMAL;
	public static SparseArray<SensorData> saData = new SparseArray<SensorData>();
	//public static SparseArray<Sensor> supportedSensors = new SparseArray<Sensor>();
	public static List<Integer> currentSupportedSensors = new ArrayList<Integer>();
	public static int[] current;
	public static int[] toStop;
	public static int[] motions = new int[]{
			Sensor.TYPE_ACCELEROMETER,
			Sensor.TYPE_GRAVITY,
			Sensor.TYPE_GYROSCOPE,
			Sensor.TYPE_LINEAR_ACCELERATION,
			Sensor.TYPE_ROTATION_VECTOR,
			};
			/*TYPE_GAME_ROTATION_VECTOR
			 *TYPE_SIGNIFICANT_MOTION
			 *TYPE_STEP_COUNTER
			 *TYPE_STEP_DETECTOR*/
	public static int[] environments = new int[]{
			Sensor.TYPE_LIGHT,
			Sensor.TYPE_PRESSURE,
			Sensor.TYPE_RELATIVE_HUMIDITY,
			Sensor.TYPE_AMBIENT_TEMPERATURE
			};
	public static int[] positions = new int[]{
			Sensor.TYPE_ORIENTATION,
			Sensor.TYPE_MAGNETIC_FIELD,
			Sensor.TYPE_GAME_ROTATION_VECTOR,
			Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR,
			Sensor.TYPE_PROXIMITY
			};
	public static int[] connects = new int[]{
		-10,		//GSM or CDMA
		-20,		//GPS
		-30,		//Bluetooth
		-40,		//WiFi
		-50,		//NFC
		-60,		//
		};
	public static int[] connect1 = new int[]{
		-20,		//GPS
		};
	/** Virtual Sensor Id of GPS **/
	public static int GPS = -20;
	public static void init(){
		currentSupportedSensors.clear();
	}
	public static void initConnectModule(){
		currentSupportedSensors.clear();
		currentSupportedSensors.add(GPS);
	}
	public static void initGenericSensors(){
		currentSupportedSensors.clear();
	}
	
}
