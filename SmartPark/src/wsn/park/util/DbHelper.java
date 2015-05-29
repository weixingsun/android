package wsn.park.util;

import java.util.ArrayList;
import java.util.List;

import wsn.park.map.markers.SavedPlace;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Location;
import android.util.Log;

	public class DbHelper extends SQLiteOpenHelper {
		private static final String TAG = DbHelper.class.getSimpleName();
		static DbHelper dbHelper;
		private SQLiteDatabase db = null; 
		private final static String DATABASE_NAME="osmap.db";
	    private final static int DATABASE_VERSION=1;
	    private final static String GPS_TABLE="gps_data";
	    private final static String SETTINGS_TABLE="settings";
	    private static String STR_CREATE;
	    //gps_data(data_id,lat,lng,country_code)	//id=0, lastknownlocation,
	    private DbHelper(Context context){
	    	super(context, DATABASE_NAME,null, DATABASE_VERSION);
	    	this.createTables();
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
	    
	    private void createTables() {
	    	this.db = getWritableDatabase();
	    	Log.i(TAG,"recreate table:"+GPS_TABLE);
	    	//dropTable(GPS_TABLE,db);
	    	STR_CREATE = "CREATE TABLE IF NOT EXISTS "    //+DATABASE_NAME+"."
		    		+GPS_TABLE+ " ("
		    		+"data_id integer not null primary key, "
		    		+"lat double NOT NULL, lng double NOT NULL, "
		    		+"country_code varchar(3) "
		    		+");";
	    	this.db.execSQL(STR_CREATE);

	    	//settings(name,)  //name=map_vendor,travel_mode,route,geocode
	    	STR_CREATE = "CREATE TABLE IF NOT EXISTS "    //+DATABASE_NAME+"."
		    		+SETTINGS_TABLE+ " ("
		    		+"name varchar(10) not null primary key, "
		    		+"value varchar(20) NOT NULL"
		    		+");";
	    	this.db.execSQL(STR_CREATE);
	    	this.db.close();
		}
		@Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    	this.db = db;
	    	dropTable(GPS_TABLE,db);
	        onCreate(db);
	    }
	    public void dropTable(String tableName,SQLiteDatabase db){
	        String sql=" DROP TABLE IF EXISTS "+tableName+";VACUUM;";
	        db.execSQL(sql);
	    }
	    public String getCountryCode() {
	    	this.db = getReadableDatabase();
	    	String sql = "SELECT country_code FROM " +GPS_TABLE+" where data_id=0";
	    	Cursor cursor = db.rawQuery(sql, null);
	    	if (cursor != null)
	        	cursor.moveToFirst();
	    	String name = null;
	    	try{
	    	  name = cursor.getString(0);
	    	}catch(Exception e){
	    		Log.i(TAG, "Error:"+e.getMessage()+",sql="+sql);
	    	}
	    	cursor.close();
	        db.close();
	        this.db=null;
	        return name;
	    }
	    public void addCountryCode(String countryCode) {
	    	insertGPS(0, -1,-1,countryCode);
	    }
	    public void updateCountryCode(String countryCode) {
	    	if(getCountryCode()!=null){
	    		updateOnlyCountryCode(countryCode);
	    	}else{
	    		addCountryCode(countryCode);
	    	}
	    }
	    public void updateOnlyCountryCode(String countryCode) {
	    	if(this.db==null)
	    		this.db = this.getWritableDatabase();
	        String where="data_id=?";
	        String[] whereValue={Integer.toString(0)};
	        ContentValues cv=new ContentValues();
	        cv.put("country_code", countryCode);
	        db.update(GPS_TABLE, cv, where, whereValue);
	    }
	    
	    //(sensor_type,sensor_metric_seq,sensor_metric_data)
	    private long insertGPS(int id, double lat,double lng)
	    {
	        String countryCode = CountryCode.getByLatLng(lat, lng);
	        return insertGPS(id, lat,lng,countryCode);
	    }
	    private long insertGPS(int id, double lat,double lng,String countryCode)
	    {
	    	if(this.db==null)
	    	this.db = getWritableDatabase();
	        ContentValues cv=new ContentValues();
	        cv.put("data_id", id);
	        cv.put("lat", lat);
	        cv.put("lng", lng);
	        cv.put("country_code", countryCode);
	        long row=db.insert(GPS_TABLE, null, cv);
	        return row;
	    }
	    
	    public int deleteOldData(SQLiteDatabase db,int id) {
	    	if(this.db==null)
	    		this.db = this.getWritableDatabase();
	        String where="data_id="+id;
	        int a = db.delete(GPS_TABLE, where,null);
	        //db.close();
	        return a;
	    }
	    
	    private void updateGPS(int id,double lat,double lng, String countryCode) {
	    	if(this.db==null)
	    		this.db = this.getWritableDatabase();
	        String where="data_id=?";
	        String[] whereValue={Integer.toString(id)};
	        ContentValues cv=new ContentValues();
	        cv.put("lat", lat);
	        cv.put("lng", lng);
	        cv.put("country_code", countryCode);
	        db.update(GPS_TABLE, cv, where, whereValue);
	    }
	    private void updateGPS(int id,double lat,double lng) {
	        String countryCode = CountryCode.getByLatLng(lat, lng);
	        this.updateGPS(id, lat, lng, countryCode);
	    }
	    public void updateGPS(int id,Location loc){
	    	if(getCountryCode()==null){
	    		this.insertGPS(id, loc.getLatitude(), loc.getLongitude());
	    	}else{
	    		this.updateGPS(id, loc.getLatitude(), loc.getLongitude());
	    	}
	    }
	    public String getSettings(String name) {
	    	this.db = getReadableDatabase();
	    	String parsedName = name.replaceAll("\'","\'\'");
	    	String sql = "SELECT value FROM " +SETTINGS_TABLE+" where name='"+parsedName+"'";
	    	Cursor cursor = db.rawQuery(sql, null);
	    	if (cursor != null)
	        	cursor.moveToFirst();
	    	String value = null;
	    	try{
	    		value = cursor.getString(0);
	    	}catch(Exception e){
	    		//Log.i(TAG, "Error:"+e.getMessage()+",sql="+sql);
	    	}
	    	cursor.close();
	        db.close();
	        this.db=null;
	        return value;
	    }
	    private long insertSettings(String name, String value){
	    	if(this.db==null)
	    	this.db = getWritableDatabase();
	        ContentValues cv=new ContentValues();
	        cv.put("name", name);
	        cv.put("value", value);
	        long row=db.insert(SETTINGS_TABLE, null, cv);
	        Log.w(TAG, "insertSettings.row="+row);
	        return row;
	    }
	    private void updateSettings(String name, String value) {
	    	if(this.db==null)
	    		this.db = this.getWritableDatabase();
	        String where="name=?";
	        String[] whereValue={name};
	        ContentValues cv=new ContentValues();
	        cv.put("value", value);
	        db.update(SETTINGS_TABLE, cv, where, whereValue);
	    }
	    public void changeSettings(String name, String value){
	    	if(getSettings(name)==null){
	    		this.insertSettings(name, value);
	    	}else{
	    		this.updateSettings(name, value);
	    	}
	    }
	    //id, name, admin, lat,lng, machine_code, user_name
		public List<SavedPlace> getSavedPlaces() {
			List<SavedPlace> list = new ArrayList<SavedPlace>();
			//
			return list;
		}
		public String[] getSavedPlaceNames() {
			List<String> list_name = new ArrayList<String>();
			for(SavedPlace addr : getSavedPlaces()){
				list_name.add(addr.getName()); //feature_name
				//addr.getAdmin();
			}
			String[] stringArray = list_name.toArray(new String[list_name.size()]);
			return stringArray;
		}
	}