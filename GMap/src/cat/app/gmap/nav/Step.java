package cat.app.gmap.nav;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.model.LatLng;

public class Step {
    private Distance distance;
    private Duration duration;
    private LatLng endLocation;
    private LatLng startLocation;
    private String htmlInstructions;
    private String maneuver;
    private String travelMode;
    private List<LatLng> points;
    private String startHint;
    private String endHint;

    public List<LatLng> getPoints() {
        return points;
    }

    public void setPoints(List<LatLng> points) {
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

    public LatLng getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(LatLng endLocation) {
        this.endLocation = endLocation;
    }

    public LatLng getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(LatLng startLocation) {
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