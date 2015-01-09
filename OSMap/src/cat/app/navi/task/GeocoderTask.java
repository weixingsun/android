package cat.app.navi.task;

import java.io.IOException;
import java.util.List;

import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.util.GeoPoint;

import cat.app.maps.OSM;
import android.location.Address;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class GeocoderTask extends
        AsyncTask<String, Void, String> {
    private static final String TAG = GeocoderTask.class.getSimpleName();
    Geocoders gc;
    String mode = null;
    //int type;
    OSM osm;
    String searchByPoint = "point";
    GeoPoint position;
    Address foundAddr;
    String searchByName = "name";
    String address;
    List<Address> list;
    String provider;
    
    public GeocoderTask(OSM osm,String provider,String address) {
    	this.address=address;
    	this.osm = osm;
    	this.provider = provider;
    	gc = new Geocoders(osm.act);
        this.mode = searchByName;
    }
    public GeocoderTask(OSM osm,String provider,GeoPoint position) {
    	this.position=position;
    	this.osm = osm;
    	gc = new Geocoders(osm.act);
    	//this.type = type;
        this.mode = searchByPoint;
    }
    @Override  
    public String doInBackground(String... params) {
    	if(mode.equals(searchByName)){
			list = gc.getFromLocationName("google",this.address);
			Log.i(TAG, provider+",found_list.size="+list.size());
		}else if (mode.equals(searchByPoint)){
			foundAddr = gc.getFromLocation("google",position);
			Log.i(TAG, provider+",foundAddr="+foundAddr);
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
    		if(osm.loc.countryCode == null){
    			osm.loc.countryCode=foundAddr.getCountryCode();
    			Log.i(TAG, "==================country_code="+osm.loc.countryCode);
    		}
    		if(foundAddr == null){
    			Log.i(TAG, "address from Point not found");
    		}
        }
    }
   
}