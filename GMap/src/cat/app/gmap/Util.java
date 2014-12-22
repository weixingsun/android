package cat.app.gmap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.view.inputmethod.InputMethodManager;

public class Util {

	public static final int REQ_CODE_SPEECH_INPUT = 2;
	
	public static final String NAV_DRIVING = "Driving";
	public static final String NAV_WALKING = "Walking";
	public static final String NAV_BIKING  = "Biking";
	public static final String NAV_BUS     = "Bus";
	
	public static final String MAP_SATELLITE  = "Satellite";
	public static final String MAP_TERRAIN    = "Terrain";
	public static final String MAP_TRAFFIC    = "Traffic";
	public static final String MAP_NORMAL     = "Normal";
	
	public static final String ROAD_POLICE_CAR     = "Police Car";
	public static final String ROAD_MONITOR_CAMERA = "Monitor Camera";
	public static final String ROAD_CLOSED_ROAD    = "Closed Road";
	public static final String ROAD_MEDICAL_EMERGENCY = "Medical Emergency";
	
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
}
