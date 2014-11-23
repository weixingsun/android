package cat.app.sensor.view;

import cat.app.sensor.*;
import android.location.LocationManager;
import android.os.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.hardware.*;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.util.*;
import android.view.*;

public class MainView extends android.app.Activity implements OnItemSelectedListener {

	protected static final int GUIUPDATEIDENTIFIER = 0x101;
	private Spinner spinner;
	private ArrayAdapter<String> adapter;
	private static SurfaceView surfaceView;
	//private static TextView tv;
	private String[] select = new String[] { "Motion Sensors","Position Sensors", "Environment Sensors", "Connect Modules" };
	DisplayMetrics metrics;
	SensorManager sm;
	LocationManager locationManager;
	Thread thread;
	static Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
             switch (msg.what) {
                  case MainView.GUIUPDATEIDENTIFIER:
                       break;
             }   
             super.handleMessage(msg);
        }
   };
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		setupSelect();
		sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		GenericSensors.init(sm);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		GPS.generateGPSData(locationManager);
		Sensors.initConnectModule();
		surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
		//tv=(TextView) findViewById(R.id.Textview00);
		thread = new Thread(new MyDisplayThread());
		thread.start();
	}

	public void onDestroy() {
		super.onDestroy();
		Cleanup();
	}

	public void onStop() {
		super.onStop();
		Cleanup();
	}

	public void onPause() {
		super.onPause();
		Cleanup();
	}

	public void Cleanup() {
		GenericSensors.stop();
		GPS.stop();
		
	}

	public void onResume() {
		super.onResume();
		if(Sensors.current!=null){
			GenericSensors.start();
		}else{
			GPS.start();
		}
		//if(thread.isInterrupted()||!thread.isAlive())
		//	thread.start();
	}

	private void setupSelect() {
		//tv0 = (TextView) findViewById(R.id.Textview00);
		spinner = (Spinner) findViewById(R.id.Spinner01);
		metrics = this.getResources().getDisplayMetrics();
		spinner.setMinimumWidth((int) (metrics.widthPixels / 2));
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, select);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setVisibility(View.VISIBLE);
		spinner.setOnItemSelectedListener(this);
	}

	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// Toast.makeText(arg1,"test",Toast.LENGTH_LONG).show();
		if (arg2 == 0) {// motion sensors
			switchToCommonSensor(Sensors.motions);
		}
		if (arg2 == 1) {// position sensors
			switchToCommonSensor(Sensors.positions);
		}
		if (arg2 == 2) {// environment sensors
			switchToCommonSensor(Sensors.environments);
		}
		if (arg2 == 3) {// connection modules
			switchToConnect();
		}
	}
	private void switchToConnect(){
		Sensors.current = null;
		GenericSensors.stop();
		Sensors.initConnectModule();
		GPS.start();
	}
	private void switchToCommonSensor(int[] to){
		Sensors.initGenericSensors();
		GenericSensors.switchTo(to) ;
		GenericSensors.start();
		GPS.stop();
	}
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	private static void drawSurface() {
		Paint mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		//tv.setText("Sensors.saData="+Sensors.saData.size());
		SurfaceHolder sh = surfaceView.getHolder();
		Canvas mCanvas = sh.lockCanvas();
		if (mCanvas != null) {
			try {
				int cellHeight = 80;
				mCanvas.drawColor(Color.BLACK);
				//mCanvas.drawText("data="+Sensors.saData.size(), 50, 50, mPaint);
				for (int i = 0; i < Sensors.currentSupportedSensors.size(); i++) {
					mPaint.setColor(Color.WHITE);
					int type =Sensors.currentSupportedSensors.get(i);
					SensorData sd = Sensors.saData.get(type);
					int y = i* cellHeight;
					if (sd == null){
						mPaint.setColor(Color.RED);
						if(!GPS.status.equals(GPS.STATUS_AVAILABLE)&&type==Sensors.GPS){
							mCanvas.drawText("GPS "+GPS.status, 10, y+20, mPaint);
						}else{
							mCanvas.drawText("No data for:"+Sensors.findNameById(type), 10, y+20, mPaint);
						}
						mPaint.setColor(Color.WHITE);
						mCanvas.drawLine(10, y+cellHeight, 400, y+cellHeight, mPaint);
					} else {
						String name = null;
						int z=0;
						if(sd.object instanceof Sensor) {
							name=((Sensor)sd.object).getName();
							for (int j = 0; j < sd.fdata.length; j++) {
								mPaint.setColor(Sensors.colors[j]);
								String str ="value " + j + " =" + sd.fdata[j];
								mCanvas.drawText(str,200, y + j * 20 + 20, mPaint);
							}
							z = y + sd.fdata.length * 20 + 10;
						}
						else if(sd.object instanceof String){
							name=sd.object.toString();
							for (int j = 0; j < sd.ddata.length; j++) {
								mPaint.setColor(Sensors.colors[j]);
								String str ="value " + j + " =" + sd.ddata[j];
								mCanvas.drawText(str,200, y + j * 20 + 20, mPaint);
							}
							z = y + sd.ddata.length * 20 + 10;
						}
						mPaint.setColor(Color.WHITE);
						mCanvas.drawText(name, 10, y + 20, mPaint);
						mPaint.setColor(Color.WHITE);
						mCanvas.drawLine(10, z, 400, z, mPaint);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				sh.unlockCanvasAndPost(mCanvas);
			}
		}
	}

	private void ScheduleSelfCheck() {
		AlarmManager scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(getApplicationContext(), SensorService.class);
		PendingIntent scheduledIntent = PendingIntent.getService(
				getApplicationContext(), 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		scheduler.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(), AlarmManager.INTERVAL_HOUR,
				scheduledIntent);
	}

	private void CancelScheduledSelfCheckService() {
		AlarmManager scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, SensorService.class);
		PendingIntent scheduledIntent = PendingIntent.getService(
				getApplicationContext(), 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		scheduler.cancel(scheduledIntent);
	}

	static class MyDisplayThread implements Runnable {
        public void run() {  
            while (!Thread.currentThread().isInterrupted()) {
                 Message message = new Message();   
                 message.what = MainView.GUIUPDATEIDENTIFIER;
                 MainView.myHandler.sendMessage(message);
                 try {
                      Thread.sleep(1000);    
                      drawSurface();
                 } catch (InterruptedException e) {   
                      Thread.currentThread().interrupt();
                 }   
            }
       }
        public static void interrupt(){
        	Thread.currentThread().interrupt();
        }
	}
}
