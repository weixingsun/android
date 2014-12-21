package cat.app.gmap.nav;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

public class Navi {
	static Route currentRoute;
	static Leg currentLeg;
	public static Step currentStep;
	static List<Route> routes;
	public static void init(List<Route> routes){
		Navi.routes = routes;
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
		boolean geodesic = false;
		List<LatLng> ll = new ArrayList<LatLng>();
		for(Polyline pl: polylines){
			ll.addAll(pl.getPoints());
		}
		return PolyUtil.isLocationOnPath(loc, ll, geodesic,10); //tolerance=10 meters
	}
}
