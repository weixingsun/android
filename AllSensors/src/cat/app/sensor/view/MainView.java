package cat.app.sensor.view;

import java.util.Timer;
import java.util.TimerTask;

import cat.app.sensor.*;
import cat.app.sensor.connect.GPS;
import cat.app.sensor.db.DbHelper;
import cat.app.sensor.net.udp.*;
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

public class MainView extends android.app.Activity implements
		OnItemSelectedListener {
	private static Context context;
	private final static String TAG = "AllSensors.MainView";
	/*
	 *private static int DisplayMessage=0x1;NetworkMessage=0x2;DatabaseMessage=0x3;
	 */
	BackgroundTimerTask btt = new BackgroundTimerTask();
	Timer timer = new Timer(true);
	private Spinner spinnerSensorType;
	private Spinner spinnerClientServer;
	private ArrayAdapter<String> adapterSensorType;
	private ArrayAdapter<String> adapterClientServer;
	private static SurfaceView surfaceView;
	private String[] selectType = new String[] { "Motion Sensors",
			"Position Sensors", "Environment Sensors", "Connect Modules" };
	private String[] selectCS = new String[] { "Client Mode", "Server Mode" };
	DisplayMetrics metrics;
	SensorManager sm;
	LocationManager locationManager;
	static DbHelper dbHelper;
	private static boolean serverMode = false;
	public static int SERVER_PORT = 6000;
	public static int MULTICAST_PORT = 4444;
	private static int UPDATE_INTERVAL = 10 * 1000;

	protected void onCreate(Bundle savedInstanceState) {
		MainView.context = getApplicationContext();
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
		dbHelper = DbHelper.getInstance(this);
		dbHelper.prepareMetricData();
		timer.schedule(btt, 1000, UPDATE_INTERVAL);
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
		if (Sensors.current != null) {
			GenericSensors.start();
		} else {
			GPS.start();
		}
	}

	private void setupSelect() {
		spinnerSensorType = (Spinner) findViewById(R.id.SpinnerSensorType);
		metrics = this.getResources().getDisplayMetrics();
		spinnerSensorType.setMinimumWidth((int) (metrics.widthPixels / 2));
		adapterSensorType = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, selectType);
		adapterSensorType
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerSensorType.setAdapter(adapterSensorType);
		spinnerSensorType.setVisibility(View.VISIBLE);
		spinnerSensorType.setOnItemSelectedListener(this);
		spinnerClientServer = (Spinner) findViewById(R.id.SpinnerCS);
		spinnerClientServer.setMinimumWidth((int) (metrics.widthPixels / 4));
		adapterClientServer = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, selectCS);
		adapterClientServer
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerClientServer.setAdapter(adapterClientServer);
		spinnerClientServer.setVisibility(View.VISIBLE);
		spinnerClientServer.setOnItemSelectedListener(this);

	}

	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if (arg0.getId() == R.id.SpinnerSensorType) {
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
		} else if (arg0.getId() == R.id.SpinnerCS) {
			if (arg2 == 0) {// client mode
				switchToClientMode();
			} else if (arg2 == 1) {// server mode
				switchToServerMode();
			}
		}
	}

	private void switchToServerMode() {
		serverMode = true;
	}

	private void switchToClientMode() {
		serverMode = false;
		startClientServerThreads();
	}

	private static void startClientServerThreads() {
		if (serverMode == false) {
			Intent i = new Intent(MainView.context, UDPClient.class);
			i.putExtra("multiport", MULTICAST_PORT);
			i.putExtra("port", SERVER_PORT);
			UDPClient.setTimeOut(UPDATE_INTERVAL - 2000);
			MainView.context.startService(i);
		}
	}

	private void switchToConnect() {
		Sensors.current = null;
		GenericSensors.stop();
		Sensors.initConnectModule();
		GPS.start();
	}

	private void switchToCommonSensor(int[] to) {
		Sensors.initGenericSensors();
		GenericSensors.switchTo(to);
		GenericSensors.start();
		GPS.stop();
	}

	public void onNothingSelected(AdapterView<?> arg0) {
	}

	private static void drawSurface() {
		Paint mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		// tv.setText("Sensors.saData="+Sensors.saData.size());
		SurfaceHolder sh = surfaceView.getHolder();
		Canvas mCanvas = sh.lockCanvas();
		if (mCanvas != null) {
			try {
				int cellHeight = 80;
				mCanvas.drawColor(Color.BLACK);
				// mCanvas.drawText("data="+Sensors.saData.size(), 50, 50,
				// mPaint);
				for (int i = 0; i < Sensors.currentPageSensors.size(); i++) {
					mPaint.setColor(Color.WHITE);
					int type = Sensors.currentPageSensors.get(i);
					SensorData sd = Sensors.saData.get(type);
					int y = i * cellHeight;
					if (sd == null) {
						drawEmptyData(mCanvas, mPaint, type, y, cellHeight);
					} else {
						drawData(mCanvas, mPaint, y, sd);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				sh.unlockCanvasAndPost(mCanvas);
			}
		}
	}

	private static void drawEmptyData(Canvas mCanvas, Paint mPaint, int type,
			int y, int cellHeight) {
		mPaint.setColor(Color.RED);
		if (!GPS.status.equals(GPS.STATUS_AVAILABLE) && type == Sensors.GPS) {
			mCanvas.drawText("GPS " + GPS.status, 10, y + 20, mPaint);
		} else {
			mCanvas.drawText("No data for:" + Sensors.findNameById(type), 10,
					y + 20, mPaint);
		}
		mPaint.setColor(Color.WHITE);
		mCanvas.drawLine(10, y + cellHeight, 400, y + cellHeight, mPaint);
	}

	private static void drawData(Canvas mCanvas, Paint mPaint, int y,
			SensorData sd) {
		//String name = null;
		int z = 0;
		if (sd.sensor != null) {
			//name = sd.sensor.getName();
			for (int j = 0; j < sd.fdata.length; j++) {
				mPaint.setColor(Sensors.colors[j]);
				String str = "value " + j + " =" + sd.fdata[j];
				mCanvas.drawText(str, 200, y + j * 20 + 20, mPaint);
			}
			z = y + sd.fdata.length * 20 + 10;
		} else {
			//name = sd.object.toString();
			for (int j = 0; j < sd.ddata.length; j++) {
				mPaint.setColor(Sensors.colors[j]);
				String str = "value " + j + " =" + sd.ddata[j];
				mCanvas.drawText(str, 200, y + j * 20 + 20, mPaint);
			}
			z = y + sd.ddata.length * 20 + 10;
		}
		mPaint.setColor(Color.WHITE);
		mCanvas.drawText(sd.getName(), 10, y + 20, mPaint);
		mPaint.setColor(Color.WHITE);
		mCanvas.drawLine(10, z, 400, z, mPaint);
	}

	private void ScheduleSelfCheck() {
		AlarmManager scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(getApplicationContext(),
				SensorServiceUnused.class);
		PendingIntent scheduledIntent = PendingIntent.getService(
				getApplicationContext(), 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		scheduler.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(), AlarmManager.INTERVAL_HOUR,
				scheduledIntent);
	}

	private void CancelScheduledSelfCheckService() {
		AlarmManager scheduler = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, SensorServiceUnused.class);
		PendingIntent scheduledIntent = PendingIntent.getService(
				getApplicationContext(), 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		scheduler.cancel(scheduledIntent);
	}

	static Runnable backgroundR = new Runnable() {
		@Override
		public void run() {
			/*
			 * Message message = new Message(); message.what = DisplayMessage;
			 * MainView.myHandler.sendMessage(message);
			 */
			drawSurface();
			dbHelper.dbWriter();
			startClientServerThreads();
		}
	};
	static Handler myHandler = new Handler();

	private static class BackgroundTimerTask extends TimerTask {
		@Override
		public void run() {
			myHandler.post(backgroundR);
		}
	}
}
/**
 * public void handleMessage(Message msg) { switch (msg.what) { case
 * DisplayMessage: //drawSurface(); break; case NetworkMessage:
 * //startClientThreads(); break; case DatabaseMessage: //dbHelper.dbWriter();
 * break; } super.handleMessage(msg); } Message message = new Message();
 * message.what = NetworkMessage; myHandler.sendMessage(message);
 * 
 * message.what = DisplayMessage; myHandler.sendMessage(message);
 * 
 * message.what = DatabaseMessage; myHandler.sendMessage(message);
 */
