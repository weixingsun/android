package wsn.park.navi.task;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.util.GeoPoint;

import wsn.park.LOC;
import wsn.park.model.ParkingPlace;

import android.location.Address;
import android.util.Log;

public class ParkingAPI {
    private static final String tag = ParkingAPI.class.getSimpleName();
    private String URL;
    private List<ParkingPlace> searchResult;
    public ParkingAPI(){
    }
	public List<ParkingPlace> search(GeoPoint point,int range) {
		this.URL = getLocationURL(point,range);
		List<ParkingPlace> list = new ArrayList<ParkingPlace>();
		return null;
	}
    public String getLocationURL(GeoPoint point,int range) {
        //String region = "&country="+LOC.countryCode;
        String url = "https://servicedata.vhostall.com/api/parking.php?lat="+point.getLatitude()+"&lng="+point.getLongitude()+"&range="+range;
        Log.i(tag,"Parking URL === " + url);
        return url;
    }
	public List<ParkingPlace> getSearchResult() {
		return searchResult;
	}
	public void setSearchResult(List<ParkingPlace> searchResult) {
		this.searchResult = searchResult;
	}
}
