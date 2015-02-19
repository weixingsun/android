package cat.app.net.p2p.util;

import org.osmdroid.util.GeoPoint;

public class CountryCode {

	//NZ.lat(-33,-47)
	//NZ.lng(165,179)
	//CN.lat(5,55)
	//CN.lng(73,134)
	public static String getByLatLng(double lat, double lng){
		if(		lat< -33 && lat > -47 
			&&  lng< 179 && lng > 165 ){
			return "nz";
		}else if (lat<55 && lat > 5
			&&  lng> 134 && lng > 73  ){
			return "cn";
		}
		return null;
	}
	public static String getByGeoPoint(GeoPoint p){
		return getByLatLng(p.getLatitude(),p.getLongitude());
	}
}
