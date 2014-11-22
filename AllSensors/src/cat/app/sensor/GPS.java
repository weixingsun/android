package cat.app.sensor;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

public class GPS implements LocationListener {
	static GPS gps = new GPS();
	//private static boolean running = false;
	//public static boolean disabled = false;
	public static String STATUS_AVAILABLE = "available";
	public static String STATUS_LOCATING = "locating";
	public static String STATUS_OUT_OF_SERVICE = "outofservice";
	public static String STATUS_UNAVAILABLE = "unavailable";
	public static String STATUS_STOPPED = "stopped";
	public static String STATUS_DISABLED = "disabled";
	public static String status = STATUS_STOPPED;
	private static LocationManager locationManager;
	private static String provider;
	public static int virtualSensorId = Sensors.GPS;

	public static void generateGPSData(LocationManager lm) {
		// Get the location manager
		locationManager = lm;
		// Criteria to select location provider
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);
		if(location !=null){
		double[] data = new double[]{location.getLatitude(),location.getLongitude()};
		SensorData sd = new SensorData("GPS", data);
		Sensors.saData.append(virtualSensorId, sd);
		}
	}

	public static void start() {
		if(status.equals(GPS.STATUS_STOPPED)){  //running==false
			status=GPS.STATUS_LOCATING;
			locationManager.requestLocationUpdates(provider, 1000, 1, gps);
		}
	}

	public static void stop() {
		if(status.equals(GPS.STATUS_AVAILABLE)){
			status=GPS.STATUS_STOPPED;
			locationManager.removeUpdates(gps);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		double[] data = new double[] { location.getLatitude(),location.getLongitude() };
		SensorData sd = new SensorData("GPS", data);
		Sensors.saData.append(virtualSensorId, sd);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		if(status==LocationProvider.AVAILABLE){
            //System.out.println("当前GPS状态：可见的\n");  
			GPS.status=GPS.STATUS_AVAILABLE;
        }else if(status==LocationProvider.OUT_OF_SERVICE){  
        	//System.out.println("当前GPS状态：服务区外\n");
        	GPS.status=GPS.STATUS_OUT_OF_SERVICE;
        }else if(status==LocationProvider.TEMPORARILY_UNAVAILABLE){  
        	//System.out.println("当前GPS状态：暂停服务\n");  
        	GPS.status=STATUS_UNAVAILABLE;
        }
	}

	@Override
	public void onProviderEnabled(String provider) {
		//status="enabled";
	}

	@Override
	public void onProviderDisabled(String provider) {
		status=STATUS_DISABLED;
	}

}
