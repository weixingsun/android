package cat.app.gmap.nav;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import cat.app.gmap.MainActivity;
import cat.app.gmap.Util;
import cat.app.gmap.task.TextToSpeechTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

public class FindMyStepTask extends AsyncTask<LatLng, Void, String> {
	MainActivity act;
	private static final String TAG = "GMap.NaviTask";
	public FindMyStepTask(MainActivity act) {
		super();
		this.act = act;
		this.steps = act.gMap.steps;
		currentStepIndex=0;
	}
	public int currentStepIndex;
	//public List<Route> routes;
	public List<Step> steps;
	public static List<String> stepHintStrings = new ArrayList<String>();
	public static List<String> stepHintFiles = new ArrayList<String>();
	
	public Step findNextStep(){
		if(currentStepIndex+1==steps.size()){
			return null;
		}
		currentStepIndex++;
		return steps.get(currentStepIndex);
	}
	public boolean isInStep(Step step, LatLng loc){
		boolean geodesic = false;
		return PolyUtil.isLocationOnPath(loc, step.getPoints(), geodesic,10); //tolerance=10 meters
	}

	public boolean isInPolylines(List<Polyline> polylines, LatLng loc){
		List<LatLng> ll = new ArrayList<LatLng>();
		for(Polyline pl: polylines){
			ll.addAll(pl.getPoints());
		}
		return isInPointList(ll,loc); //tolerance=10 meters
	}
	public boolean isInPolyline(Polyline polyline, LatLng loc){
		return isInPointList(polyline.getPoints(),loc);
	}
	public boolean isInPointList(List<LatLng> points, LatLng loc){
		boolean geodesic = true;
		return PolyUtil.isLocationOnPath(loc, points, geodesic,10); //tolerance=10 meters
	}
	public boolean isInPointList(List<LatLng> points, LatLng loc,int tolerance){
		boolean geodesic = true;
		return PolyUtil.isLocationOnPath(loc, points, geodesic,tolerance); //tolerance=10 meters
	}
	@Override
	protected String doInBackground(LatLng... params) {
		Step step = steps.get(currentStepIndex);
		boolean isIn= false;
		if(currentStepIndex==0)
			isIn = isInPointList(step.getPoints(),params[0],100);
		else
			isIn = isInPointList(step.getPoints(),params[0]);
		
		if(isIn){
			return step.getHtmlInstructions();
		}else{
			Log.w(TAG, "Find next step......................");
			while((step = findNextStep())!=null){
				if(isInPointList(step.getPoints(),params[0])){
					return step.getHtmlInstructions();
				}
			}
		}
		return null;
	}

	@Override
    protected void onPostExecute(String instruction) {
		//http://translate.google.com/translate_tts?tl=en&q=Hello%20World
		//String hint=Html.fromHtml(instruction).toString();
		act.gMap.currentStepIndex = this.currentStepIndex;
    }
}
