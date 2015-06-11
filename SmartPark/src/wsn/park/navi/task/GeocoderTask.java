package wsn.park.navi.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.util.GeoPoint;

import wsn.park.LOC;
import wsn.park.maps.OSM;
import wsn.park.model.SavedPlace;
import wsn.park.ui.marker.OsmMapsItemizedOverlay;
import wsn.park.util.GeoOptions;
import wsn.park.util.SavedOptions;

import android.location.Address;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
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
    List<SavedPlace> splist;
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
    		splist = GeoOptions.getSavedPlaceFromAddress(list);
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
        	if(list!=null && splist.size()>0){
        		osm.dv.fillList(splist);
        		osm.suggestPoints = splist;
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
    			SavedPlace sp = GeoOptions.getMyPlace(foundAddr);
    			OsmMapsItemizedOverlay pin = osm.mks.updateDestinationOverlay(sp);
    			osm.dv.openPlacePopup(pin);
    			osm.mks.updateDestinationOverlay(sp);
    		}
        }
    }
   
}