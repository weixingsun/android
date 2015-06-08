package wsn.park.model;

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
	private SavedPlace place;
	private List<ParkingPlace> parkingPlaces;
	private GeoPoint hintPoint;
	private GeoPoint endPoint;
	public SavedPlace getPlace() {
		return place;
	}
	public void setPlace(SavedPlace place) {
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
}
