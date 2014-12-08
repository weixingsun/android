package cat.app.sensor;

import org.json.JSONArray;
import org.json.JSONObject;

import android.hardware.Sensor;

public class SensorData {

	private int id;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Sensor sensor;
	private String name;
	public static float[] fdata;
	public static double[] ddata;
	public SensorData(int type,String s,double[] d){
		this.id=type;
		this.name=s;
		this.ddata=d;
	}
	public SensorData(Sensor s, float[] f){
		this.id = s.getType();
		this.sensor=s;
		this.name=s.getName();
		this.fdata=f;
	}
	public JSONObject getJson(){
		try{
			JSONObject o = new JSONObject();
			//SensorData sd = Sensors.saData.get(getId());
			o.put("id", getId());
			o.put("name", this.getName());
			o.put("data", fdata==null?toString(ddata):toString(fdata));
			return o;
		}catch(Exception e){
		}
		return null;
	}
	private static String toString(float[] data){
		String s = "";
		for(float f:data){
			s+=f+",";
		}
		return s.substring(0, s.length()-1);
	}
	private static String toString(double[] data){
		String s = "";
		for(double f:data){
			s+=f+",";
		}
		return s.substring(0, s.length()-1);
	}
}
