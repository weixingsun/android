package wsn.park.model;

import org.osmdroid.util.GeoPoint;

public interface Place {

	public static final int NONE = 0;
	public static final int PARK = 9;	//temp place, do not save it to bookmark
	public static final int NORMAL = 10;
	public static final int HOME = 11;
	public static final int WORK = 12;
	
	public String getName();
	public GeoPoint getPosition();
	public String getAdmin();
	public String getCountryCode();
	public double getLat();
	public double getLng();
	public int getId();
	public void setId(int i);
	public void setType(int type);
	public int getType();
	public boolean isStar();
	public void setStar(boolean star);
}
