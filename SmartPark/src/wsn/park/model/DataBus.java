package wsn.park.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.osmdroid.util.GeoPoint;

public class DataBus {
	private static DataBus singleton;
	private DataBus(){ }
	public static synchronized DataBus getInstance( ) {
		if (singleton == null)
			singleton=new DataBus();
		return singleton;
	}
	private GeoPoint myPoint;
	private long findMyStepTime=0;
	private long redrawTime=0;
	private Place place;
	private List<ParkingPlace> parkingPlaces;
	private GeoPoint hintPoint;
	private GeoPoint endPoint;
	private static List<Integer> playedList = new ArrayList<Integer>();
	public Place getPlace() {
		return place;
	}
	public void setPlace(Place place) {
		this.place = place;
	}
	public List<ParkingPlace> getParkingPlaces() {
		return parkingPlaces;
	}
	public void setParkingPlaces(List<ParkingPlace> parkingPlaces) {
		this.parkingPlaces = parkingPlaces;
	}
	public ParkingPlace[] getParkingPlaceNames() {
		ParkingPlace[] placesArr = new ParkingPlace[parkingPlaces.size()];
		return parkingPlaces.toArray(placesArr);
	}
	public GeoPoint getHintPoint() {
		return hintPoint;
	}
	public void setHintPoint(GeoPoint hintPoint) {
		this.hintPoint = hintPoint;
	}
	public void setEndPoint(GeoPoint endPoint) {
		this.endPoint = endPoint;
	}
	public GeoPoint getEndPoint() {
		return endPoint;
	}
	public static void setPlayedId(int id,int dist){
		playedList.add(id*10000+dist);
	}
	public static void clearPlayedList(){
		playedList.clear();
	}
	public static boolean isPlayed(int id,int dist){
		return playedList.contains(id*10000+dist);
	}
	public long getFindingMS() {
		if(this.findMyStepTime==0) 
			findMyStepTime= System.currentTimeMillis();
		return findMyStepTime;
	}
	public void setFindMyStepTime(long ts) {
		this.findMyStepTime = ts;
	}
	public long getRedrawTime() {
		if(this.redrawTime==0) 
			redrawTime= System.currentTimeMillis();
		return redrawTime;
	}
	public void setRedrawTime(long ts) {
		this.redrawTime = ts;
	}
	public GeoPoint getMyPoint() {
		return myPoint;
	}
	public void setMyPoint(GeoPoint myPoint) {
		this.myPoint = myPoint;
	}
}
