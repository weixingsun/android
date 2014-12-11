package cat.app.gmap;

import java.util.HashMap;

import com.google.android.gms.maps.model.LatLng;

public class Position {
    //Beijing (39.915291, 116.396860)
    //Shanghai(31.192609, 121.431577)
    //Sydney (-33.867, 151.206)
	//Christchurch(-43.5320544,172.6362254)
	private String countryCode;
	private String areaCode;
	private String cityCode;
	private static HashMap<String,LatLng> city = new HashMap<String,LatLng>();
	
	public void init(){
		city.put("BJS", new LatLng(39.915291, 116.396860));
		city.put("SHA", new LatLng(31.192609, 121.431577));
		city.put("SYD", new LatLng(-33.867, 151.206));
		city.put("CHC", new LatLng(-43.5320544,172.6362254));
	}
	public static LatLng findLatLng(String code){
		return city.get(code);
	}
}
