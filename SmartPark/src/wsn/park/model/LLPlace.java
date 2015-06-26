package wsn.park.model;

import org.osmdroid.util.GeoPoint;

import wsn.park.LOC;
import wsn.park.maps.OSM;

public class LLPlace implements Place {
	//private String name,admin,countryCode;
	private double lat,lng;
	private int id,type;
	private boolean star;
	public LLPlace(GeoPoint gp) {
		this.lat=gp.getLatitude();
		this.lng=gp.getLongitude();
	}
	@Override
	public String getName() {
		return "Unamed("+this.lat+","+this.lng+")";
	}

	@Override
	public GeoPoint getPosition() {
		return new GeoPoint(lat,lng);
	}

	@Override
	public String getAdmin() {
		return "";
	}

	@Override
	public String getCountryCode() {
		return LOC.countryCode;
	}

	@Override
	public double getLat() {
		return lat;
	}

	@Override
	public double getLng() {
		return lng;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id=id;
	}

	@Override
	public boolean isStar() {
		return star;
	}

	@Override
	public void setStar(boolean star) {
		this.star=star;
	}
	@Override
	public void setType(int type) {
		this.type=type;
	}
	@Override
	public int getType() {
		return type;
	}
}
