package wsn.park.navi.task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.util.GeoPoint;

import wsn.park.LOC;
import wsn.park.maps.OSM;
import wsn.park.util.GeoOptions;
import wsn.park.util.SavedOptions;

import android.location.Address;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class GeocoderTask extends
        AsyncTask<String, Void, String> {
    private static final String TAG = GeocoderTask.class.getSimpleName();
    Geocoders gc;
    String purpose;
    OSM osm;
    String searchBy ;
    GeoPoint position;
    Address foundAddr;
    String ByPoint = "point";
    String ByName = "name";
    String address;
    List<Address> list;
    String provider = SavedOptions.selectedGeo;
    
    public GeocoderTask(OSM osm,String address) {
    	this.address=address;
    	this.osm = osm;
    	gc = new Geocoders(osm.act);
        this.searchBy = ByName;
    }
    public GeocoderTask(OSM osm,GeoPoint position) {
    	this.position=position;
    	this.osm = osm;
    	gc = new Geocoders(osm.act);
    	//this.type = type;
        this.searchBy = ByPoint;
    }
    public GeocoderTask(OSM osm, GeoPoint point, String purpose) {
		this(osm,point);
		this.purpose=purpose;
	}
	@Override  
    public String doInBackground(String... params) {
    	if(searchBy.equals(ByName)){
			//list = gc.getFromLocationName(provider,this.address);
    		list = gc.getFromLocationName(provider,this.address);
    		//GoogleSearchByAddressNameTask task = new GoogleSearchByAddressNameTask(osm,this.address);
			//task.execute();
		}else if (searchBy.equals(ByPoint)){
			foundAddr = gc.getFromLocation(provider,position);
			//Log.i(TAG, provider+",foundAddr="+foundAddr);
		}
        return null;
    }
  
    @Override  
    protected void onPostExecute(String ret) {
        super.onPostExecute(ret);
        if(searchBy.equals(ByName)){
        	if(list!=null && list.size()>0){
        		osm.dv.fillList(list);
        		osm.suggestPoints = list;
        	}
    		//activity.map.updateMarker(marker,foundPoint);
    		//activity.map.activity.openPopup(marker,type);
    		//activity.map.addRouteMarker(foundPoint);
        }else if(searchBy.equals(ByPoint)){
    		if(LOC.countryCode == null){
    			if(foundAddr!=null)
    				LOC.countryCode=foundAddr.getCountryCode();
    			Log.i(TAG, "==================country_code="+LOC.countryCode);
    		}
    		//Log.w(TAG, "foundAddr.code="+foundAddr.getCountryCode()+","+this.purpose);
    		if(foundAddr != null && !this.purpose.equals("countryCode")){
    			foundAddr.setLatitude(position.getLatitude());
    			foundAddr.setLongitude(position.getLongitude());
    			//osm.mks.updateRouteMarker(foundAddr);
    			osm.mks.updatePointOverlay(GeoOptions.getMyPlace(foundAddr));
    		}
			//Log.i(TAG, "GeoCoderTask.foundAddr="+foundAddr.getFeatureName());
        }
    }
   
}