package cat.app.sensor.db;

	import java.sql.Timestamp;

import cat.app.sensor.SensorData;
import cat.app.sensor.Sensors;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.hardware.Sensor;
import android.util.Log;

	public class DbHelper extends SQLiteOpenHelper {
		private static final String TAG = "AllSensors.DBhelper";
		static DbHelper dbHelper;
		private SQLiteDatabase db = null; 
		private final static String DATABASE_NAME="allsensors.db";
	    private final static int DATABASE_VERSION=1;
	    private final static String SENSOR_TABLE="sensor_data";
	    private final static String METRIC_TABLE="sensor_metric";
	    private final static String SERVER_TABLE="server_info";
	    //private final static String TABLE_ID="sensor_id";

	    private static String STR_CREATE;
	    //sensor_data(data_id,createtime,sensor_type,sensor_metric_seq,sensor_metric_data)
	    //sensor_metric(sensor_type,sensor_name,sensor_metric_seq,sensor_metric_name,sensor_metric_unit)
	    //server_info(server_ip,server_port,net_type,interval_min)
	    private DbHelper(Context context){
	    	super(context, DATABASE_NAME,null, DATABASE_VERSION);
	    }
	    public static DbHelper getInstance(Context context){
	    	if(dbHelper==null){
	    		dbHelper = new DbHelper(context);
	    	}
	    	return dbHelper;
	    }
	    public static DbHelper getInstance(){
	    	return dbHelper;
	    }
	    /*@Override  
	    public SQLiteDatabase getWritableDatabase() {
	        final SQLiteDatabase db;  
	        if(mDefaultWritableDatabase != null){  
	            db = mDefaultWritableDatabase;  
	        } else {  
	            db = super.getWritableDatabase();  
	        }  
	        return db;  
	    }*/
	    
	    @Override
	    public void onCreate(SQLiteDatabase db) {
	    	//recreateTables(db);
	    }
	    
	    private void recreateTables(SQLiteDatabase db) {
	    	Log.i(TAG,"recreate table:"+SENSOR_TABLE+","+SENSOR_TABLE+","+SERVER_TABLE);
	    	dropTable(SENSOR_TABLE);
	    	dropTable(METRIC_TABLE);
	    	dropTable(SERVER_TABLE);
	    	STR_CREATE = "CREATE TABLE IF NOT EXISTS "    //+DATABASE_NAME+"."
		    		+SENSOR_TABLE+ " ("
		    		+"data_id integer not null primary key autoincrement "
		    		+",createtime timestamp NOT NULL DEFAULT (datetime('now','localtime')) "
		    		+",sensor_type smallint, sensor_metric_seq smallint, sensor_metric_data double "
		    		+");";
	        db.execSQL(STR_CREATE);
	        STR_CREATE="CREATE INDEX sensor_data_index_1 on "+SENSOR_TABLE+ " (createtime, sensor_type);";
	        db.execSQL(STR_CREATE);
	        STR_CREATE = "CREATE TABLE IF NOT EXISTS "+METRIC_TABLE+ " ("    //DATABASE_NAME+"."+
	        		+"sensor_type smallint,sensor_name varchar(100),"
	        		+"sensor_metric_seq smallint,sensor_metric_name varchar(100), sensor_metric_unit varchar(10));";
	        db.execSQL(STR_CREATE);
	        STR_CREATE="CREATE INDEX sensor_metric_index_1 on "+METRIC_TABLE+ " (sensor_type, sensor_metric_seq);";
	        db.execSQL(STR_CREATE);
	        STR_CREATE = "CREATE TABLE IF NOT EXISTS "+SERVER_TABLE+ " ("
	        		+"server_ip varchar(20),server_port smallint,net_type varchar(10),interval_min integer);";
	        db.execSQL(STR_CREATE);
	        Log.i(TAG,"table recreated:"+SENSOR_TABLE+","+SENSOR_TABLE);
	        db.close();
		}
		@Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    	this.db = db;
	    	dropTable(SENSOR_TABLE);
	    	dropTable(METRIC_TABLE);
	        onCreate(db);
	    }
	    public void dropTable(String tableName){
	        String sql=" DROP TABLE IF EXISTS "+tableName+";VACUUM;";
	        db.execSQL(sql);
	    }
	    public String selectUnitById(int sensorType,int metricSeq)
	    {
	    	this.db = getReadableDatabase();
	    	//String[] columns = {"sensor_metric_unit"};
	        //Cursor cursor=mDefaultWritableDatabase.query(METRIC_TABLE, columns, null, null, null, null, null);
	    	String sql = "SELECT sensor_metric_unit FROM " +METRIC_TABLE+" where sensor_type="+sensorType+" and  metric_seq="+metricSeq;
	    	Cursor cursor = db.rawQuery(sql, null);
	    	if (cursor != null)
	        	cursor.moveToFirst();
	    	String name = cursor.getString(0);
	    	cursor.close();
	        db.close();
	        return name;
	    }
	    //(sensor_type,sensor_metric_seq,sensor_metric_data)
	    public long insert(int sensor_type, int sensor_metric_seq,double data)
	    {
	    	this.db = getWritableDatabase();
	        ContentValues cv=new ContentValues();
	        cv.put("sensor_type", sensor_type);
	        cv.put("sensor_metric_seq", sensor_metric_seq);
	        cv.put("sensor_metric_data", data);
	        //cv.put(SENSOR_NAME, sensor_name);
	        //cv.put("sensor_metric_unit", sensor_metric_unit);
	        //cv.put("sensor_metric_name", sensor_metric_name);
	        long row=db.insert(SENSOR_TABLE, null, cv);
	        return row;
	    }
	    public long insert(int sensor_type, String sensor_name, int sensor_metric_seq, String sensor_metric_name, String unit)
	    {
	        ContentValues cv=new ContentValues();
	        cv.put("sensor_type", sensor_type);
	        cv.put("sensor_metric_seq", sensor_metric_seq);
	        cv.put("sensor_name", sensor_name);
	        cv.put("sensor_metric_name", sensor_metric_name);
	        cv.put("sensor_metric_unit", unit);
	        return insert(METRIC_TABLE,cv);
	    }
	    public long insert(String tableName,ContentValues cv){
	    	return db.insert(tableName, null, cv);
	    }
	    
	    public void deleteOldSensorData(SQLiteDatabase db)
	    {
	    	//Log.i(TAG,"deleting: old data > 1 min before");
			//db = getWritableDatabase();
	    	String sql = "delete from sensor_data where createtime<datetime('now','localtime','-1 minute');";
	        String where="createtime<datetime('now','localtime','-1 minute')";
	        //datetime('now','+1 hour','-12 minute');
	        int a = db.delete(SENSOR_TABLE, where,null);
	        //db.close();
	        //Log.i(TAG,"deleting: "+a+" rows deleted");
	    }
	    /*public void delete(Timestamp time)
	    {
			this.db = getWritableDatabase();
	        String where="createtime<?";
	        String[] whereValue={time.toString()};
	        db.delete(SENSOR_TABLE, where, whereValue);
	        db.close();
	    }*/
	    
	    public void update(int id,String value)
	    {
	        String where="sensor_type=?";
	        String[] whereValue={Integer.toString(id)};
	        ContentValues cv=new ContentValues(); 
	        cv.put("sensor_name", value);
	        db.update(SENSOR_TABLE, cv, where, whereValue);
	    }

		public static void insertFloatData(int id,SensorData sd){
			for(int i=0;i<sd.fdata.length;i++){
				float data = sd.fdata[i];
				dbHelper.insert(id,i,data);
			}
		}
		public static void insertDoubleData(int id,SensorData sd){
			for(int i=0;i<sd.ddata.length;i++){
				double data = sd.ddata[i];
				dbHelper.insert(id,i,data);
			}
		}

		public void dbWriter(){
			db = getWritableDatabase();
			for (int id:Sensors.ids){
				SensorData sd = Sensors.saData.get(id);
				if(sd!=null){
					if(sd.object instanceof Sensor){
						DbHelper.insertFloatData(id,sd);
					}
					else if(sd.object instanceof String){
						DbHelper.insertDoubleData(id,sd);
					}
				}
			}
			deleteOldSensorData(db);
			db.close();
		}
		
		public void prepareMetricData(){
			this.db = getWritableDatabase();
			recreateTables(db);
			this.db = getWritableDatabase();
			//db.insert(sensor_type, sensor_name, sensor_metric_seq, sensor_metric_name, unit)
			this.insert(Sensor.TYPE_ACCELEROMETER, "Accelerometer",  0, "X", "m/s^2");
			this.insert(Sensor.TYPE_ACCELEROMETER, "Accelerometer",  1, "Y", "m/s^2");
			this.insert(Sensor.TYPE_ACCELEROMETER, "Accelerometer",  2, "Z", "m/s^2");
			this.insert(Sensor.TYPE_MAGNETIC_FIELD, "Magnetic Field",  0, "X", "uT");
			this.insert(Sensor.TYPE_MAGNETIC_FIELD, "Magnetic Field",  1, "Y", "uT");
			this.insert(Sensor.TYPE_MAGNETIC_FIELD, "Magnetic Field",  2, "Z", "uT");
			this.insert(Sensor.TYPE_GYROSCOPE, "Gyroscope",  0, "X", "radians/s");
			this.insert(Sensor.TYPE_GYROSCOPE, "Gyroscope",  1, "Y", "radians/s");
			this.insert(Sensor.TYPE_GYROSCOPE, "Gyroscope",  2, "Z", "radians/s");
			this.insert(Sensor.TYPE_LIGHT, "Light",  0, "", "lux");
			this.insert(Sensor.TYPE_LIGHT, "Light",  1, "", "lux");
			this.insert(Sensor.TYPE_LIGHT, "Light",  2, "", "lux");
			this.insert(Sensor.TYPE_PRESSURE, "Pressure",  0, "", "hPa");
			this.insert(Sensor.TYPE_PRESSURE, "Pressure",  1, "", "hPa");
			this.insert(Sensor.TYPE_PRESSURE, "Pressure",  2, "", "hPa");
			this.insert(Sensor.TYPE_PROXIMITY, "Proximity",  0, "", "cm");
			this.insert(Sensor.TYPE_PROXIMITY, "Proximity",  1, "", "cm");
			this.insert(Sensor.TYPE_PROXIMITY, "Proximity",  2, "", "cm");
			this.insert(Sensor.TYPE_GRAVITY, "Gravity",  0, "X", "m/s^2");
			this.insert(Sensor.TYPE_GRAVITY, "Gravity",  1, "Y", "m/s^2");
			this.insert(Sensor.TYPE_GRAVITY, "Gravity",  2, "Z", "m/s^2");
			this.insert(Sensor.TYPE_LINEAR_ACCELERATION, "Linear Acceleration",  0, "X", "m/s^2");
			this.insert(Sensor.TYPE_LINEAR_ACCELERATION, "Linear Acceleration",  1, "Y", "m/s^2");
			this.insert(Sensor.TYPE_LINEAR_ACCELERATION, "Linear Acceleration",  2, "Z", "m/s^2");
			//acceleration = gravity + linear-acceleration
			this.insert(Sensor.TYPE_ROTATION_VECTOR, "Rotation Vector",  0, "x*sin(¦È/2)", "");
			this.insert(Sensor.TYPE_ROTATION_VECTOR, "Rotation Vector",  1, "y*sin(¦È/2)", "");
			this.insert(Sensor.TYPE_ROTATION_VECTOR, "Rotation Vector",  2, "z*sin(¦È/2)", "");
			this.insert(Sensor.TYPE_ROTATION_VECTOR, "Rotation Vector",  3, "cos(¦È/2)",   "");
			this.insert(Sensor.TYPE_ROTATION_VECTOR, "Rotation Vector",  4, "accuracy", "radians");
			this.insert(Sensor.TYPE_ORIENTATION, "Orientation",  0, "Azimuth", "");
			this.insert(Sensor.TYPE_RELATIVE_HUMIDITY, "Humidity",  0, "Air", "percent");
			this.insert(Sensor.TYPE_AMBIENT_TEMPERATURE, "Temperature",  0, "Degree", "¡ãC");
			db.close();
		}
		//SERVER_TABLE(server_ip,server_port,net_type,interval_min)
		public void updateServerInfo(String ip, int port){
			this.db = getWritableDatabase();
			deleteServer(null);
			insertServer(ip,port,"UDP",10);
			db.close();
		}
		public void deleteServer(String ip)
	    {
	    	String where = ip==null?null:" server_ip="+ip+";";
	        int a = db.delete(SERVER_TABLE, where,null);
	        //db.close();
	        Log.i(TAG,"deleting from "+SERVER_TABLE+": "+a+" rows deleted");
	    }
		public long insertServer(String serverIp, int serverPort, String net, int intervalMin)
	    {
	        ContentValues cv=new ContentValues();
	        cv.put("server_ip", serverIp);
	        cv.put("server_port", serverPort);
	        cv.put("net_type", net);
	        cv.put("interval_min", intervalMin);
	        return insert(SERVER_TABLE,cv);
	    }
	}