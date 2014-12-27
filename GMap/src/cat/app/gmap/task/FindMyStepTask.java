package cat.app.gmap.task;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import cat.app.gmap.MainActivity;
import cat.app.gmap.Util;
import cat.app.gmap.nav.PolyUtil;
import cat.app.gmap.nav.Step;

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
	public List<Step> steps;
	
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

	@Override
	protected String doInBackground(LatLng... params) {
		if(steps==null || steps.size()<1){return null;}
		//isInPointList(step.getPoints(),params[0],30);
		for (int i=act.gMap.currentStepIndex;i<steps.size();i++){
			if(isInStep(steps.get(i), params[0])){ //�������10�ף�����Ϊ������·�ϣ���������Ѱ��
				act.gMap.onRoad=true;
				act.gMap.currentStepIndex=i;
				break;
			}
			if(i==steps.size()-1 && act.gMap.currentStepIndex<i){
				Toast.makeText(act, "FindMyStepTask", Toast.LENGTH_LONG).show();
			}
		}//�Ȳ������ػ���·�����
		return null;
	}

	@Override
    protected void onPostExecute(String instruction) {
		//http://translate.google.com/translate_tts?tl=en&q=Hello%20World
		//String hint=Html.fromHtml(instruction).toString();
    }
}
