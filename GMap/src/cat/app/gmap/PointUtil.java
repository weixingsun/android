package cat.app.gmap;

import java.util.HashMap;

import com.google.android.gms.maps.model.LatLng;

public class PointUtil {
    //Beijing (39.915291, 116.396860)
    //Shanghai(31.192609, 121.431577)
    //Sydney (-33.867, 151.206)
	//Christchurch(-43.5320544,172.6362254)
	private String countryCode;
	private String areaCode;
	private String cityCode;
	private static HashMap<String,MarkerPoint> city = new HashMap<String,MarkerPoint>();
	//public void init()
	static {
		MarkerPoint bj = new MarkerPoint("BJS","Beijing","The capital city of China",new LatLng(39.915291, 116.396860));
		MarkerPoint sh = new MarkerPoint("SHA","Shanghai","A modern city of China",new LatLng(31.192609, 121.431577));
		MarkerPoint sn = new MarkerPoint("SYD","Sydney","The biggest city of Australia",new LatLng(-33.867, 151.206));
		MarkerPoint ch = new MarkerPoint("CHC","Christchurch","A garden city of New Zealand",new LatLng(-43.5320544,172.6362254));
		city.put(bj.getCityCode(), bj);
		city.put(sh.getCityCode(), sh);
		city.put(sn.getCityCode(), sn);
		city.put(ch.getCityCode(), ch);
	}
	public static MarkerPoint findCity(String code){
		return city.get(code);
	}
}
