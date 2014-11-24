package cat.app.sensor.db;

	import cat.app.sensor.SensorData;
import cat.app.sensor.Sensors;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.hardware.Sensor;

	public class DbHelper extends SQLiteOpenHelper {

		static DbHelper dbHelper;
		private SQLiteDatabase mDefaultWritableDatabase = null; 
		private final static String DATABASE_NAME="allsensors.db";
	    private final static int DATABASE_VERSION=1;
	    private final static String SENSOR_TABLE="sensor_data";
	    private final static String METRIC_TABLE="sensor_metric";
	    //private final static String TABLE_ID="sensor_id";

	    private static String STR_CREATE;
	    private final static String SENSOR_NAME="sensor_name";
	    private final static String SENSOR_TYPE="sensor_type";
	    //static SQLiteDatabase readDb;
	    //static SQLiteDatabase writeDb;
	    private DbHelper(Context context){
	    	super(context, DATABASE_NAME,null, DATABASE_VERSION);
	    }
	    public static DbHelper getInstance(Context context){
	    	if(dbHelper==null){
	    		dbHelper = new DbHelper(context);
	    	}
	    	return dbHelper;
	    }
	    @Override  
	    public SQLiteDatabase getWritableDatabase() {  
	        final SQLiteDatabase db;  
	        if(mDefaultWritableDatabase != null){  
	            db = mDefaultWritableDatabase;  
	        } else {  
	            db = super.getWritableDatabase();  
	        }  
	        return db;  
	    }  
	    //(data_id,datetime,sensor_type,sensor_name,sensor_metric_name,sensor_metric_seq,sensor_metric_unit,sensor_metric_data,comments)
	    @Override
	    public void onCreate(SQLiteDatabase db) {
	    	this.mDefaultWritableDatabase = db;
	    	STR_CREATE = "CREATE TABLE IF NOT EXISTS "    //+DATABASE_NAME+"."
		    		+SENSOR_TABLE+ " ("
		    		+"data_id integer not null primary key autoincrement "
		    		+",datetime timestamp NOT NULL DEFAULT (datetime('now','localtime')) "
		    		+",sensor_type smallint, sensor_metric_seq smallint, sensor_metric_data double "
		    		//+",sensor_name varchar(100),sensor_metric_name varchar(100),sensor_metric_unit varchar(10) "
		    		//+",comments varchar(200) DEFAULT null"
		    		+");";
	        db.execSQL(STR_CREATE);
	        STR_CREATE = "CREATE TABLE IF NOT EXISTS "+METRIC_TABLE+ " ("    //DATABASE_NAME+"."+
	        		+"sensor_type smallint,sensor_name varchar(100),"
	        		+"sensor_metric_seq smallint,sensor_metric_name varchar(100), sensor_metric_unit varchar(10));";
	        db.execSQL(STR_CREATE);
    		//readDb=getReadableDatabase();
    		//writeDb=mDefaultWritableDatabase;
	        prepareData(db);
	    }

	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    	this.mDefaultWritableDatabase = db;
	        String sql=" DROP TABLE IF EXISTS "+SENSOR_TABLE;
	        db.execSQL(sql);
	        onCreate(db);
	    }

	    public Cursor select(SQLiteDatabase readDb)
	    {
	        Cursor cursor=readDb.query(SENSOR_TABLE, null, null, null, null, null,  SENSOR_NAME+" desc");
	        return cursor;
	    }
	    //(sensor_type,sensor_metric_seq,sensor_metric_data)
	    public long insert(int sensor_type, int sensor_metric_seq,double data)
	    {
	    	this.mDefaultWritableDatabase = getWritableDatabase();
	        ContentValues cv=new ContentValues();
	        cv.put(SENSOR_TYPE, sensor_type);
	        cv.put("sensor_metric_seq", sensor_metric_seq);
	        cv.put("sensor_metric_data", data);
	        //cv.put(SENSOR_NAME, sensor_name);
	        //cv.put("sensor_metric_unit", sensor_metric_unit);
	        //cv.put("sensor_metric_name", sensor_metric_name);
	        long row=mDefaultWritableDatabase.insert(SENSOR_TABLE, null, cv);
	        return row;
	    }
	    public long insert(int sensor_type, String sensor_name, int sensor_metric_seq, String sensor_metric_name, String unit)
	    {
	        ContentValues cv=new ContentValues();
	        cv.put(SENSOR_TYPE, sensor_type);
	        cv.put("sensor_metric_seq", sensor_metric_seq);
	        cv.put(SENSOR_NAME, sensor_name);
	        cv.put("sensor_metric_name", sensor_metric_name);
	        cv.put("sensor_metric_unit", unit);
	        long row=mDefaultWritableDatabase.insert(METRIC_TABLE, null, cv);
	        return row;
	    }
	    
	    public void delete(int id)
	    {
	        String where=SENSOR_TYPE+"=?";
	        String[] whereValue={Integer.toString(id)};
	        mDefaultWritableDatabase.delete(SENSOR_TABLE, where, whereValue);
	    }
	    
	    public void update(int id,String value)
	    {
	        String where=SENSOR_TYPE+"=?";
	        String[] whereValue={Integer.toString(id)};
	        ContentValues cv=new ContentValues(); 
	        cv.put(SENSOR_NAME, value);
	        mDefaultWritableDatabase.update(SENSOR_TABLE, cv, where, whereValue);
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

		public static void dbWriter(){
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
		}
		public void prepareData(SQLiteDatabase db){
			this.mDefaultWritableDatabase = getWritableDatabase();
			//db.insert(sensor_type, sensor_name, sensor_metric_seq, sensor_metric_name, unit)
			this.insert(Sensor.TYPE_ACCELEROMETER, "Accelerometer",  0, "X", "m/s^2");
			this.insert(Sensor.TYPE_ACCELEROMETER, "Accelerometer",  1, "Y", "m/s^2");
			this.insert(Sensor.TYPE_ACCELEROMETER, "Accelerometer",  2, "Z", "m/s^2");
		}
	}