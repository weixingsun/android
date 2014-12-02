package cat.app.sensor.db;

import android.util.SparseArray;
import cat.app.sensor.SensorData;

public class CacheIdName {

	static SparseArray<String> cachedData = new SparseArray<String>();
	public static String getSensorNameById(int sensorId) {
		if(cachedData.size()==0 || cachedData.get(sensorId)==null){
			return DbHelper.getInstance().selectSensorNameById(sensorId);
		}else{
			return cachedData.get(sensorId);
		}
	}

}
