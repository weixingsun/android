package cat.app.navi.google;

import org.osmdroid.util.GeoPoint;


public class Bound {
    private GeoPoint northEast;
    private GeoPoint southWest;
    public GeoPoint getNorthEast() {
        return northEast;
    }
    public void setNorthEast(GeoPoint northEast) {
        this.northEast = northEast;
    }
    public GeoPoint getSouthWest() {
        return southWest;
    }
    public void setSouthWest(GeoPoint southWest) {
        this.southWest = southWest;
    }
}
