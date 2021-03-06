package wsn.park.navi.task;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.util.GeoPoint;

import wsn.park.LOC;
import wsn.park.NET;
import wsn.park.maps.OSM;
import wsn.park.model.ParkingPlace;

import android.location.Address;
import android.util.Log;
import android.widget.Toast;

public class ParkingAPI {
	private static ParkingAPI singleton;
	private ParkingAPI(){ }
	public static synchronized ParkingAPI getInstance( ) {
		if (singleton == null)
			singleton=new ParkingAPI();
		return singleton;
   }
    private static final String tag = ParkingAPI.class.getSimpleName();
    private OSM osm = OSM.getInstance();
    private String URL;
    private List<ParkingPlace> searchResult;
	public List<ParkingPlace> search(GeoPoint point,int range) {
		URL = getSelectURL(point,range);
		if(!osm.net.isNetworkConnected()){
			Toast.makeText(osm.act, "Please enable network firstly", Toast.LENGTH_LONG).show();
		}
		NetDataTask t = new NetDataTask();
		t.execute(URL);
		return null;
	}
	//http://servicedata.vhostall.com/wsn/wsn_park_select.php?lat=-43.525827&lng=172.584113
    public String getSelectURL(GeoPoint point,int range) {
        //String region = "&country="+LOC.countryCode;
        String url = "http://servicedata.vhostall.com/wsn/wsn_park_select.php?lat="
        			+point.getLatitude()+"&lng="+point.getLongitude()+"&range="+range;
        //Log.i(tag,"Parking URL === " + url);
        //sql=SELECT id,status,lat,lng,operator,type,admin,country,SQRT(POW(-43.525827-lat,2)+POW(172.584213-lng,2))*100000 as comment 
        //FROM wsn_parking_space_info where lat between -43.535827 and -43.515827 and lng between 172.574213 and 172.594213
        return url;
    }
	public List<ParkingPlace> getSearchResult() {
		return searchResult;
	}
	public void setSearchResult(List<ParkingPlace> searchResult) {
		this.searchResult = searchResult;
	}
}
