package cat.app.gmap;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import cat.app.gmap.model.MarkerPoint;
import cat.app.gmap.nav.Step;
import cat.app.gmap.task.GoogleSearchByPointTask;
import cat.app.gmap.task.UploadTask;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.location.Location;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

public class Util {

	public static final int REQ_CODE_SPEECH_INPUT = 2;
	
	public static final String NAV_DRIVING   = "Driving";
	public static final String NAV_WALKING   = "Walking";
	public static final String NAV_BICYCLING = "Bicycling";
	public static final String NAV_TRANSIT   = "Transit";
	
	public static final String MAP_SATELLITE  = "Satellite";
	public static final String MAP_TERRAIN    = "Terrain";
	public static final String MAP_TRAFFIC    = "Traffic";
	public static final String MAP_NORMAL     = "Normal";
	
	public static final String ROAD_POLICE_CAR     = "Police Car";
	public static final String ROAD_MONITOR_CAMERA = "Monitor Camera";
	public static final String ROAD_CLOSED_ROAD    = "Closed Road";
	public static final String ROAD_MEDICAL_EMERGENCY = "Medical Emergency";

	public static final long LOCATION_UPDATE_INTERVAL = 1000 * 10; //10 seconds

	public static final double hintBeforeTurn = 20;

	private static final String TAG = "Util";
	static String baseDir = Environment.getExternalStorageDirectory() + "/GMap/routes/hint/";
	
	/**
     * 在给定的图片的右上角加上联系人数量。数量用红色表示
     * @param icon 给定的图片
     * @return 带联系人数量的图片
     */
    public static Bitmap generatorSequencedIcon(Bitmap markerIcon,int seq){
    	
    	Bitmap contactIcon=Bitmap.createBitmap(markerIcon.getWidth(), markerIcon.getHeight(), Config.ARGB_8888);
    	Canvas canvas=new Canvas(contactIcon);
    	
    	//拷贝图片
    	Paint iconPaint=new Paint();
    	iconPaint.setDither(true);//防抖动
    	iconPaint.setFilterBitmap(true);//用来对Bitmap进行滤波处理，这样，当你选择Drawable时，会有抗锯齿的效果
    	Rect src=new Rect(0, 0, markerIcon.getWidth(), markerIcon.getHeight());
    	Rect dst=new Rect(0, 0, markerIcon.getWidth(), markerIcon.getHeight());
    	canvas.drawBitmap(markerIcon, src, dst, iconPaint);
    	
    	//启用抗锯齿和使用设备的文本字距
    	Paint countPaint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DEV_KERN_TEXT_FLAG);
    	countPaint.setColor(Color.RED);
    	countPaint.setTextSize(20f);
    	countPaint.setTypeface(Typeface.DEFAULT_BOLD);
    	canvas.drawText(String.valueOf(seq), markerIcon.getWidth()-37, 35, countPaint);
    	return contactIcon;
    }
    
	public static void closeKeyBoard(MainActivity activity) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(activity.inputAddress.getWindowToken(), 0);
	}
	
	public static float getDistance(LatLng oldPosition  ,LatLng newPosition){
		float[] results = new float[1];
		Location.distanceBetween(oldPosition.latitude, oldPosition.longitude,
		                newPosition.latitude, newPosition.longitude, results);
		return results[0];
	}
	public static boolean isFileExist(String filepath){
		File file = new File(filepath);
	    return file.exists();
	}
	public static void createFolder(File folder) {
    	if (!folder.mkdirs()) {
    		Log.i("Util", "failed to create dir:"+folder.getAbsolutePath());
    	}
	}

    /*public void showMarkers(){
    	LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;

    	Iterator<Entry<String, Marker>> iter = markers.entrySet().iterator();
    	while(iter.hasNext()){
    		Entry<String,Marker> entry = iter.next();
    		//String key = entry.getKey();
    		Marker mk = entry.getValue();
    		if(bounds.contains(mk.getPosition()))
        	{
        	    mk.setVisible(true);
        	}else{
        		//mk.remove();
        		mk.setVisible(false);
        	}
    	}
    }
	protected String getLastMarkerId() {
    	Iterator<Entry<String, MarkerPoint>> iter = markerpoints.entrySet().iterator();
    	String key = null;
    	while(iter.hasNext()){
    		Entry<String,MarkerPoint> entry = iter.next();
    		key = entry.getKey();
    	}
		return key;
	}
    */

	public static String createVoiceFileName(String type,int currentStepIndex) {
		return baseDir+type+"_"+currentStepIndex+".mp3";
	}

	public static void uploadRemind(MainActivity act,LatLng point,int type, String reporter) {
		//type(1:police)(2:cctv)
		(new UploadTask(act, point,type,reporter)).execute();
		//String url = "http://servicedata.net76.net/insert.php?";  //lat=0&lng=0&type=0&reporter=name
		//String params= "lat="+lat+"&lng="+lng+"&type="+type+"&reporter=admin";
		//time auto-gen
		//Log.i("GMap.Util.upload", url+params);
	}
	public static String removeHTMLTags(String html){
		return Html.fromHtml(html).toString().trim();
	}
	public static int getTimezoneOffsetMS(){
		Calendar cal =  Calendar.getInstance();
		int tzOffsetMS = -(cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET));
		Log.i("Util", "tzOffsetMS--------("+tzOffsetMS+")");
		return tzOffsetMS;
	}
	public static int getTimezoneOffsetHour(){
		return getTimezoneOffsetMS()/(1000*60*60);
	}

/*	public static String getHint(Step step) {
    	String hintHTML = step.getHtmlInstructions();
		return getDistanceHint(step)+Util.removeHTMLTags(hintHTML);
	}*/
	public static String getHint(List<Step> steps, int index) {
		if(index==steps.size()){return "";}
    	String currHintHTML = steps.get(index).getHtmlInstructions();
		return getDistanceHint(steps,index)+Util.removeHTMLTags(currHintHTML);
	}
	public static String getDistanceHint(List<Step> steps, int index) {
    	String distance = "in "+steps.get(index).getDistance().getText()+", ";
		return distance;
	}
	public static void reOrgHints(List<Step> steps) {
    	for(int i=0;i<steps.size();i++){
    		Step s =steps.get(i);
    		String str = "in "+s.getDistance().getText()+ " , ";
    		if(i<steps.size()-1) {
    			String hint = Util.removeHTMLTags(steps.get(i+1).getHtmlInstructions());
    			str+=removeDestination(hint);
    			String endHint = steps.get(i+1).getManeuver()==null?hint:steps.get(i+1).getManeuver();
    			s.setEndHint(endHint);
    		}else{ //proceed last step
    			String hint = Util.removeHTMLTags(steps.get(i).getHtmlInstructions());
    			str+=getDestination(hint);
    			String endHint = steps.get(i).getManeuver()==null?hint:steps.get(i).getManeuver();
    			s.setEndHint(getDestination(endHint));
    		}
    		s.setStartHint(str);
    	}
	}
	private static String getDestination(String hint){
		int index = hint.indexOf("Destination");
		if(index>0){
			return hint.substring(index).trim();
		}else{
			return hint;
		}
	}
	private static String removeDestination(String hint){
		int index = hint.indexOf("Destination");
		if(index>0){
			return hint.substring(0,index).trim();
		}else{
			return hint;
		}
	}
	/*

    	//Destination
    	Step lastStep = steps.get(steps.size()-1);
    	String hint = Util.removeHTMLTags(lastStep.getHtmlInstructions());
    	int index = hint.indexOf("Destination");
    	//Log.i(TAG, "lastStep.html"+lastStep.getHtmlInstructions());
    	if(index>0){
    		Step prev = steps.get(steps.size()-2);
    		prev.setStartHint(prev.getStartHint().substring(0,prev.getStartHint().indexOf("Destination")));
	    	String lastHint = lastStep.getHtmlInstructions().substring(index);
	    	lastStep.setStartHint(lastStep.getStartHint()+lastHint);
    	}
	 * */
}
