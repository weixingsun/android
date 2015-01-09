package cat.app.navi.google;

import java.util.List;

import org.osmdroid.util.GeoPoint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;

public class Step {
    private Distance distance;
    private Duration duration;
    private GeoPoint endLocation;
    private GeoPoint startLocation;
    private String htmlInstructions;
    private String maneuver;
    private String travelMode;
    private List<GeoPoint> points;
    private String startHint;
    private String endHint;

    public List<GeoPoint> getPoints() {
        return points;
    }

    public void setPoints(List<GeoPoint> points) {
        this.points = points;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public GeoPoint getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(GeoPoint endLocation) {
        this.endLocation = endLocation;
    }

    public GeoPoint getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(GeoPoint startLocation) {
        this.startLocation = startLocation;
    }

    public String getHtmlInstructions() {
        return htmlInstructions;
    }

    public void setHtmlInstructions(String htmlInstructions) {
        this.htmlInstructions = htmlInstructions;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

	public String getManeuver() {
		return maneuver;
	}

	public void setManeuver(String maneuver) {
		this.maneuver = maneuver;
	}

	public String getStartHint() {
		return startHint;
	}

	public void setStartHint(String startHint) {
		this.startHint = startHint;
	}

	public String getEndHint() {
		return endHint;
	}

	public void setEndHint(String endHint) {
		this.endHint = endHint;
	}
}