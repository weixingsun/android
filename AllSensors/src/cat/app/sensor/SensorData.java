package cat.app.sensor;

public class SensorData {

	//Sensor sensor;
	public Object object;
	public float[] fdata;
	public double[] ddata;
	public SensorData(Object o,float[] d){
		this.object=o;
		this.fdata=d;
	}
	public SensorData(Object o,double[] d){
		this.object=o;
		this.ddata=d;
	}
}
