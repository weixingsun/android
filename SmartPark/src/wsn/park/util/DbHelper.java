package wsn.park.util;

import java.util.ArrayList;
import java.util.List;

import wsn.park.model.SavedPlace;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Location;
import android.util.Log;

	public class DbHelper extends SQLiteOpenHelper {
		private static final String tag = DbHelper.class.getSimpleName();
		static DbHelper dbHelper;
		private SQLiteDatabase db = null;
		//private int maxHistoryPlaceID;
		private final static String DATABASE_NAME="osmap.db";
	    private final static int DATABASE_VERSION=1;
	    private final static String GPS_TABLE="gps_data";
	    private final static String HISTORY_TABLE="history_place"; //just local history
	    private final static String MY_PLACE_TABLE="my_place"; // upload to cloud
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
	    	Log.i(tag,"recreate table:"+GPS_TABLE);
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
	    	
	    	//id, address_name, admin, country_code, lat,lng
	    	STR_CREATE = "CREATE TABLE IF NOT EXISTS "    //+DATABASE_NAME+"."
		    		+HISTORY_TABLE+ " ("
				    +"id integer not null primary key AUTOINCREMENT, "
		    		+"name varchar(100) not null, "
		    		+"admin varchar(50), "
		    		+"country_code varchar(5) not null, "
		    		+"lat double NOT NULL, lng double NOT NULL"
		    		+");";
	    	this.db.execSQL(STR_CREATE);
	    	//id, address_name, admin, country_code, lat,lng, machine_code, user_name, special
	    	STR_CREATE = "CREATE TABLE IF NOT EXISTS "    //+DATABASE_NAME+"."
		    		+MY_PLACE_TABLE+ " ("
				    +"create_time DATETIME not null, "
		    		+"name varchar(80) not null, "
		    		+"admin varchar(50), "
		    		+"country_code varchar(5) not null, " 
		    		+"lat double NOT NULL, lng double NOT NULL, "
		    		+"machine_code varchar(50) NOT NULL,"
		    		+"user_name varchar(50) NOT NULL,"
		    		+"special integer NOT NULL"
		    		+");";
	    	this.db.execSQL(STR_CREATE);
	    	
	    	this.db.close();
		}
	    public int getMaxID(String table) {
	    	this.db = getReadableDatabase();
	    	String sql = "SELECT max(id) FROM " +table;
	    	Cursor cursor = db.rawQuery(sql, null);
	    	if (cursor != null)
	        	cursor.moveToFirst();
	    	int id = 0;
	    	try{
	    	  id = cursor.getInt(0);
	    	}catch(Exception e){
	    		Log.i(tag, "Error:"+e.getMessage()+",sql="+sql);
	    	}
	    	cursor.close();
	        //db.close();
	        //this.db=null;
	        return id;
	    }
	    public List<SavedPlace> getHistoryPlaces() {
			List<SavedPlace> list = new ArrayList<SavedPlace>();
	    	this.db = getReadableDatabase();
	    	String sql = "SELECT name,admin,lat,lng FROM " +HISTORY_TABLE+" order by id desc";
	    	Cursor cursor = db.rawQuery(sql, null);
	    	if (cursor.moveToFirst()){
	    		do{
	    			String name= cursor.getString(0);
	    			String admin= cursor.getString(1);
	    			double lat= cursor.getDouble(2);
	    			double lng= cursor.getDouble(3);
	    			SavedPlace sp = new SavedPlace(name,admin,lat,lng);
	    			//Log.i(tag, "name="+name+",admin="+admin+",lat="+lat+",lng="+lng);
	    			list.add(sp);
	    		}while(cursor.moveToNext());
	    	}
	    	cursor.close();
	        //db.close();
	        return list;
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
	    		Log.i(tag, "Error:"+e.getMessage()+",sql="+sql);
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
	        Log.w(tag, "insertSettings:"+name+"="+value);
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
	        //Log.w(TAG, "updateSettings:"+name+"="+value);
	    }
	    public void changeSettings(String name, String value){
	    	if(getSettings(name)==null){
	    		this.insertSettings(name, value);
	    	}else{
	    		this.updateSettings(name, value);
	    	}
	    }
	    //id, address_name, admin, country_code, lat,lng, machine_code, user_name, special
		public List<SavedPlace> getSavedPlaces() {
			List<SavedPlace> list = new ArrayList<SavedPlace>();
		    	this.db = getReadableDatabase();
		    	String sql = "SELECT name,admin,lat,lng FROM " +MY_PLACE_TABLE+" where special=0";
		    	Cursor cursor = db.rawQuery(sql, null);
		    	if (cursor.moveToFirst()){
		    		do{
		    			String name= cursor.getString(0);
		    			String admin= cursor.getString(1);
		    			double lat= cursor.getDouble(2);
		    			double lng= cursor.getDouble(3);
		    			SavedPlace sp = new SavedPlace(name,admin,lat,lng);
		    			//Log.i(tag, "name="+name+",admin="+admin+",lat="+lat+",lng="+lng);
		    			list.add(sp);
		    		}while(cursor.moveToNext());
		    	}
		    	cursor.close();
		        //db.close();
		    
			return list;
		}
		public SavedPlace[] getSavedPlaceNames() {
			List<SavedPlace> list =getSavedPlaces();
			SavedPlace[] array = list.toArray(new SavedPlace[list.size()]);
			//Log.w(tag, "array="+array[0].getName()+", "+array[1].getName());
			return array;
		}
		public SavedPlace[] getHistoryPlaceNames() {
			List<SavedPlace> list =getHistoryPlaces();
			SavedPlace[] array = list.toArray(new SavedPlace[list.size()]);
			//Log.w(tag, "array="+array[0].getName()+", "+array[1].getName());
			return array;
		}
		
		//id, address_name, admin, country_code, lat,lng
		public void addHistoryPlace(Address addr) {
			String name = GeoOptions.getAddressName(addr);
	    	if(this.db==null)
	    	this.db = getWritableDatabase();
	        ContentValues cv=new ContentValues();
	        //cv.put("id", id);
	        cv.put("name", name);
	        cv.put("admin", addr.getSubAdminArea());
	        cv.put("country_code", addr.getCountryCode());
	        cv.put("lat", addr.getLatitude());
	        cv.put("lng", addr.getLongitude());
	        long row=db.insert(HISTORY_TABLE, null, cv);
	        Log.w(tag, "insertHistory:"+name);
			
		}
		//id, address_name, admin, country_code, lat,lng
		public void addMyPlace(Address addr, int special) {
			String name = GeoOptions.getAddressName(addr);
	    	if(this.db==null)
	    	this.db = getWritableDatabase();
	        ContentValues cv=new ContentValues();
	        //cv.put("id", id);
	        cv.put("name", name);
	        cv.put("admin", addr.getSubAdminArea());
	        cv.put("country_code", addr.getCountryCode());
	        cv.put("lat", addr.getLatitude());
	        cv.put("lng", addr.getLongitude());
	        cv.put("special", special);
	        long row=db.insert(MY_PLACE_TABLE, null, cv);
	        Log.w(tag, "insertMyPlace:"+name+",special="+special);
		}
		public int deleteHistoryPlaces() {
	    	if(this.db==null)
	    		this.db = this.getWritableDatabase();
	        String where="1="+1;
	        int a = db.delete(MY_PLACE_TABLE, where,null);
	        //db.close();
	        return a;
	    }
	}