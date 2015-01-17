package cat.app.navi.task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.util.GeoPoint;

import cat.app.maps.OSM;
import cat.app.osmap.LOC;
import cat.app.osmap.util.GeoOptions;
import android.location.Address;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class GeocoderTask extends
        AsyncTask<String, Void, String> {
    private static final String TAG = GeocoderTask.class.getSimpleName();
    Geocoders gc;
    String mode = null;
    String purpose;
    OSM osm;
    String searchByPoint = "point";
    GeoPoint position;
    Address foundAddr;
    String searchByName = "name";
    String address;
    List<Address> list;
    String provider = GeoOptions.getGeocoder();
    
    public GeocoderTask(OSM osm,String address) {
    	this.address=address;
    	this.osm = osm;
    	gc = new Geocoders(osm.act);
        this.mode = searchByName;
    }
    public GeocoderTask(OSM osm,GeoPoint position) {
    	this.position=position;
    	this.osm = osm;
    	gc = new Geocoders(osm.act);
    	//this.type = type;
        this.mode = searchByPoint;
    }
    public GeocoderTask(OSM osm, GeoPoint point, String purpose) {
		this(osm,point);
		this.purpose=purpose;
	}
	@Override  
    public String doInBackground(String... params) {
    	if(mode.equals(searchByName)){
			list = gc.getFromLocationName(provider,this.address);
			//Log.i(TAG, provider+",found_list.size="+list.size());
		}else if (mode.equals(searchByPoint)){
			foundAddr = gc.getFromLocation(provider,position);
			//Log.i(TAG, provider+",foundAddr="+foundAddr);
		}
        return null;
    }
  
    @Override  
    protected void onPostExecute(String ret) {
        super.onPostExecute(ret);
        if(mode.equals(searchByName)){
        		osm.dv.fillList(list);
        		osm.suggestPoints = list;
        		//activity.map.updateMarker(marker,foundPoint);
        		//activity.map.activity.openPopup(marker,type);
        		//activity.map.addRouteMarker(foundPoint);
        }else if(mode.equals(searchByPoint)){
    		if(LOC.countryCode == null){
    			if(foundAddr!=null)
    				LOC.countryCode=foundAddr.getCountryCode();
    			Log.i(TAG, "==================country_code="+LOC.countryCode);
    		}
    		Log.w(TAG, "foundAddr.code="+foundAddr.getCountryCode()+","+this.purpose);
    		if(foundAddr != null && !this.purpose.equals("countryCode")){
    			foundAddr.setLatitude(position.getLatitude());
    			foundAddr.setLongitude(position.getLongitude());
    			osm.mks.updateRouteMarker(foundAddr);
    		}
			//Log.i(TAG, "GeoCoderTask.foundAddr="+foundAddr.getFeatureName());
        }
    }
   
}