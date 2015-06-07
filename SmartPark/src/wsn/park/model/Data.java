package wsn.park.model;

import java.util.List;

public class Data {
	private static Data singleton;
	private Data(){ }
	public static synchronized Data getInstance( ) {
		if (singleton == null)
			singleton=new Data();
		return singleton;
	}
	private List<ParkingPlace> parkingPlaces;
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
}
