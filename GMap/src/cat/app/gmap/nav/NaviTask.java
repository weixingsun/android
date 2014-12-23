package cat.app.gmap.nav;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.location.Location;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import cat.app.gmap.MainActivity;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

public class NaviTask extends AsyncTask<LatLng, Void, String> {
	MainActivity act;
	private static final String TAG = "GMap.NaviTask";
	public NaviTask(MainActivity act) {
		super();
		this.act = act;
	}
	static Route currentRoute;
	static Leg currentLeg;
	public static Step currentStep;
	static List<Route> routes;
	public static void init(List<Route> routes){
		NaviTask.routes = routes;
		currentRoute=routes.get(0);
		currentLeg=currentRoute.getLegs().get(0);
		currentStep=currentLeg.getSteps().get(0);
	}
	public static Route findNextRoute(){
		int currentRouteIndex = routes.indexOf(currentRoute);
		if(currentRouteIndex+1==routes.size()){
			return null;
		}
		return routes.get(currentRouteIndex+1);
	}

	public static Leg findNextLeg(){
		List<Leg> legs = currentRoute.getLegs();
		int currentLegIndex = legs.indexOf(currentLeg);
		if(currentLegIndex+1==legs.size()){ //no more leg in current route, find in next route
			Route nextR = findNextRoute();
			if(nextR==null){ //no more route
				return null;
			}else{
				return nextR.getLegs().get(0);
			}
		}
		return legs.get(currentLegIndex+1);
	}
	public static Step findNextStep(){
		List<Step> steps = currentLeg.getSteps();
		int currentStepIndex = steps.indexOf(currentStep);
		if(currentStepIndex+1==steps.size()){ //no more step in current leg, find in next leg
			Leg nextL = findNextLeg();
			if(nextL==null){ //no more leg
				if(findNextRoute()!=null)
					return findNextRoute().getLegs().get(0).getSteps().get(0);
				else
					return null;
			}else{
				return nextL.getSteps().get(0);
			}
		}
		return steps.get(currentStepIndex+1);
	}
	public static boolean isInStep(Step step, LatLng loc){
		boolean geodesic = false;
		return PolyUtil.isLocationOnPath(loc, step.getPoints(), geodesic,10); //tolerance=10 meters
	}

	public static boolean isInPolylines(List<Polyline> polylines, LatLng loc){
		List<LatLng> ll = new ArrayList<LatLng>();
		for(Polyline pl: polylines){
			ll.addAll(pl.getPoints());
		}
		return isInPointList(ll,loc); //tolerance=10 meters
	}
	public static boolean isInPolyline(Polyline polyline, LatLng loc){
		return isInPointList(polyline.getPoints(),loc);
	}
	public static boolean isInPointList(List<LatLng> points, LatLng loc){
		boolean geodesic = true;
		return PolyUtil.isLocationOnPath(loc, points, geodesic,10); //tolerance=10 meters
	}
	public static boolean isInPointList(List<LatLng> points, LatLng loc,int tolerance){
		boolean geodesic = true;
		return PolyUtil.isLocationOnPath(loc, points, geodesic,tolerance); //tolerance=10 meters
	}
	@Override
	protected String doInBackground(LatLng... params) {
		boolean isIn = isInPointList(currentStep.getPoints(),params[0],100);
		if(isIn){
			return currentStep.getHtmlInstructions();
		}else{//continue to search in next step
			Log.i(TAG, "Not in first step");
			while(findNextStep()!=null){
				currentStep = findNextStep();
				if(isInPointList(currentStep.getPoints(),params[0])){
					return currentStep.getHtmlInstructions();
				}
			}
			currentStep=null; //consider to redraw route
		}
		return null;
	}

	@Override
    protected void onPostExecute(String instruction) {
		//http://translate.google.com/translate_tts?tl=en&q=Hello%20World
		String hint=Html.fromHtml(instruction).toString();
        Toast.makeText(act, hint, Toast.LENGTH_SHORT).show();
    }
}
