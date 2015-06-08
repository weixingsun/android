package wsn.park.model;

import org.osmdroid.util.GeoPoint;

public interface Place {

	public String getName();
	public GeoPoint getPosition();
	public String getAdmin();
	public String getCountryCode();
	public double getLat();
	public double getLng();
	public int getId();
	public void setId(int i);
}
