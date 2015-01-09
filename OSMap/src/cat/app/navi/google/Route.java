package cat.app.navi.google;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.osmdroid.bonuspack.overlays.Polyline;

import android.content.Context;

public class Route implements Serializable {

    private static final long serialVersionUID = 1L;
    private Bound bounds;
    private String copyrights;
    private List<Leg> legs;
    private Polyline overviewPolyLine;
    private String summary;

    public Route(Context context) {
        legs = new ArrayList<Leg>();
    }

    public Bound getBounds() {
        return bounds;
    }

    public void setBounds(Bound bounds) {
        this.bounds = bounds;
    }

    public String getCopyrights() {
        return copyrights;
    }

    public void setCopyrights(String copyrights) {
        this.copyrights = copyrights;
    }

    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }

    public void addLeg(Leg leg) {
        this.legs.add(leg);
    }

    public Polyline getOverviewPolyLine() {
        return overviewPolyLine;
    }

    public void setOverviewPolyLine(Polyline overviewPolyLine) {
        this.overviewPolyLine = overviewPolyLine;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

	public List<Step> getSteps() {
		List<Step> steps = new ArrayList<Step>();
		for(Leg l:getLegs()){
			for(Step s:l.getSteps()){
				steps.add(s);
			}
		}
		return steps;
	}

}
