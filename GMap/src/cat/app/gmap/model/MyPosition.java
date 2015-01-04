package cat.app.gmap.model;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import cat.app.gmap.Util;
import cat.app.gmap.nav.Step;

public class MyPosition {

	public MyPosition(Location loc) {
		myLatLng= new LatLng(loc.getLatitude(),loc.getLongitude());
	}
	
	public static MyPosition getInstance(){
		return INSTANCE;
	}
	public final static MyPosition INSTANCE = new MyPosition();
	private MyPosition() {
	}
	int currentStepIndex=0;
	Step step;
	public List<Step> steps = new ArrayList<Step>();
	LatLng myLatLng;
	float toCurrentStart;
	float toCurrentEnd;
	private static final String tag = MyPosition.class.getSimpleName();
	public int getCurrentStepIndex() {
		return currentStepIndex;
	}
	public void setCurrentStepIndex(int currentStepIndex) {
		this.currentStepIndex = currentStepIndex;
	}
	public LatLng getMyLatLng() {
		return myLatLng;
	}
	public void setMyLatLng(LatLng myLatLng) {
		this.myLatLng = myLatLng;
	}
	public void setMyLatLng(Location loc) {
		this.myLatLng = new LatLng(loc.getLatitude(),loc.getLongitude());
	}
	public float getToCurrentEnd() {
		return toCurrentEnd;
	}
	public void setToCurrentEnd(float toCurrentEnd) {
		this.toCurrentEnd = toCurrentEnd;
	}
	public float getToCurrentStart() {
		return toCurrentStart;
	}
	public void setToCurrentStart(float toCurrentStart) {
		this.toCurrentStart = toCurrentStart;
	}
	
	public void updateDistances(){
		if(steps.size()>0){
			this.step = steps.get(currentStepIndex);
			this.toCurrentStart  = Util.getDistance(myLatLng, step.getStartLocation());
			this.toCurrentEnd  = Util.getDistance(myLatLng, step.getEndLocation());
		}
	}

	public void clear() {
		steps.clear();
		currentStepIndex=0;
	}

}
