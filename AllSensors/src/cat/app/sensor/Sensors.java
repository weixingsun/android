package cat.app.sensor;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cat.app.sensor.db.CacheIdName;
import cat.app.sensor.db.DbHelper;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;
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
/* Connect: GSM/NFC/Bluetooth/WiFi/GPS/Camera
 * Motion: Accelerometer/Orientation/Gravity/Rotation/Gyroscope
 * Environment: Sound/Light/Magnetic/Temperature
 * SENSOR_DELAY_FASTEST 20ms
 * SENSOR_DELAY_GAME 40ms
 * SENSOR_DELAY_UI   90ms
 * SENSOR_DELAY_NORMAL 230ms
 * 
 */
public class Sensors {
	private final static String TAG = "AllSensors.Sensors";
	public static int refreshRate = SensorManager.SENSOR_DELAY_NORMAL;
	public static SparseArray<SensorData> saData = new SparseArray<SensorData>();
	//public static SparseArray<Sensor> supportedSensors = new SparseArray<Sensor>();
	public static List<Integer> currentPageSensors = new ArrayList<Integer>();
	//public static Map<Integer,String> currentPageSensorMetrics = new HashMap<Integer,String>();
	//public static Map<Integer,String> currentPageSensorUnits = new HashMap<Integer,String>();
	public static List<int[]> logSensors = new ArrayList<int[]>(); //all supported
	public static int[] current;
	public static int[] toStop;
	public static int[] colors = new int[] { Color.RED, Color.GREEN, Color.BLUE,
		Color.YELLOW, Color.CYAN, Color.GRAY,Color.WHITE };
	public static String[] motionNames = new String[]{
		Sensor.STRING_TYPE_ACCELEROMETER,
		Sensor.STRING_TYPE_GRAVITY,
		Sensor.STRING_TYPE_GYROSCOPE,
		Sensor.STRING_TYPE_LINEAR_ACCELERATION,
		Sensor.STRING_TYPE_ROTATION_VECTOR,
		};
	public static int[] motions = new int[]{
			Sensor.TYPE_ACCELEROMETER,
			Sensor.TYPE_MAGNETIC_FIELD,
			Sensor.TYPE_GRAVITY,
			Sensor.TYPE_GYROSCOPE,
			Sensor.TYPE_LINEAR_ACCELERATION,
			Sensor.TYPE_ROTATION_VECTOR,
			};
			/*TYPE_GAME_ROTATION_VECTOR
			 *TYPE_SIGNIFICANT_MOTION
			 *TYPE_STEP_COUNTER
			 *TYPE_STEP_DETECTOR*/
	public static String[] positionNames = new String[]{
		Sensor.STRING_TYPE_ORIENTATION,
		Sensor.STRING_TYPE_GAME_ROTATION_VECTOR,
		Sensor.STRING_TYPE_GEOMAGNETIC_ROTATION_VECTOR,
		Sensor.STRING_TYPE_PROXIMITY
	};
	public static int[] positions = new int[]{
			Sensor.TYPE_ORIENTATION,
			Sensor.TYPE_GAME_ROTATION_VECTOR,
			Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR,
			Sensor.TYPE_PROXIMITY
			};
	public static String[] environmentNames = new String[]{
		Sensor.STRING_TYPE_LIGHT,
		Sensor.STRING_TYPE_PRESSURE,
		Sensor.STRING_TYPE_RELATIVE_HUMIDITY,
		Sensor.STRING_TYPE_AMBIENT_TEMPERATURE
	};
	public static int[] environments = new int[]{
			Sensor.TYPE_LIGHT,
			Sensor.TYPE_PRESSURE,
			Sensor.TYPE_RELATIVE_HUMIDITY,
			Sensor.TYPE_AMBIENT_TEMPERATURE
			};
	/** Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED **/
	public static int[] connects = new int[]{
		-10,		//GSM or CDMA
		-20,		//GPS
		-30,		//Bluetooth
		-40,		//WiFi
		-50,		//NFC
		-60,		//
		};

	/**  merge id arrays into one array **/
	public static int[] merge(int[] array1, int[] array2){
		int[] array1and2 = new int[array1.length + array2.length];
		System.arraycopy(array1, 0, array1and2, 0, array1.length);
		System.arraycopy(array2, 0, array1and2, array1.length, array2.length);
		return array1and2;
	}
	public static Object[] merge(Object[] array1, Object[] array2){
		Object[] array1and2 = new Object[array1.length + array2.length];
		System.arraycopy(array1, 0, array1and2, 0, array1.length);
		System.arraycopy(array2, 0, array1and2, array1.length, array2.length);
		return array1and2;
	}
	public static    int[]   ids= merge(merge(motions,    positions),    environments);
	public static Object[] names= merge(merge(motionNames,positionNames),environmentNames);
	public static String findNameById(int sensorId){
		return CacheIdName.getSensorNameById(sensorId);
	}
	private static List<Integer> convertToList(int[] array){
		List<Integer> intList = new ArrayList<Integer>();
		for (int index = 0; index < array.length; index++)
	    {
	        intList.add(array[index]);
	    }
		return intList;
	}
	/** Virtual Sensor Id of GPS **/
	public static int GPS = -20;
	public static void init(){
		currentPageSensors.clear();
	}
	public static void initConnectModule(){
		currentPageSensors.clear();
		currentPageSensors.add(GPS);
	}
	public static void initGenericSensors(){
		currentPageSensors.clear();
	}
	public static JSONObject getJson(){

		JSONArray array = new JSONArray();
		for(int i = 0; i < saData.size(); i++) {
		   int key = saData.keyAt(i);
		   SensorData sd = saData.get(key);
		   JSONObject o = sd.getJson();
		   array.put(o);
		}
		JSONObject json = new JSONObject();
		try {
			json.put("sensors", array);
		} catch (JSONException e) {
			Log.w(TAG, "create json object failed:"+e.getMessage());
		}
		return json;
	}
}
