package wsn.park.util;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.util.GeoPoint;

import wsn.park.model.Place;
import wsn.park.model.SavedPlace;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Location;
import android.provider.Settings.Secure;
import android.util.Log;

	public class DbHelper extends SQLiteOpenHelper {
		private static final String tag = DbHelper.class.getSimpleName();
		static DbHelper dbHelper;
		private SQLiteDatabase setting_db = null;
		private SQLiteDatabase poi_db = null;
		private final static String POI_DB_NAME="nz.db";
		private final static String SETTING_DB_NAME="osmap.db";
	    private final static int DATABASE_VERSION=1;
	    private final static String GPS_TABLE="gps_data";
	    private final static String HISTORY_TABLE="history_place"; //just local history
	    public final static String MY_PLACE_TABLE="my_place"; // upload to cloud
	    private final static String SETTINGS_TABLE="settings";
	    private final static String POI_TABLE = "poi";
	    private static String STR_CREATE;

		private String android_id; 
	    //gps_data(data_id,lat,lng,country_code)	//id=0, lastknownlocation,
	    private DbHelper(Context context){
	    	super(context, SETTING_DB_NAME,null, DATABASE_VERSION);
	    	android_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID); 
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
	    @Override
	    public void onCreate(SQLiteDatabase db) {
	    	//recreateTables(db);
	    }
	    
	    private void createTables() {
	    	this.setting_db = getWritableDatabase();
	    	Log.i(tag,"recreate table:"+GPS_TABLE);
	    	//dropTable(GPS_TABLE,db);
	    	STR_CREATE = "CREATE TABLE IF NOT EXISTS "    //+DATABASE_NAME+"."
		    		+GPS_TABLE+ " ("
		    		+"data_id integer not null primary key, "
		    		+"lat double NOT NULL, lng double NOT NULL, " //float(10,7)
		    		+"country_code varchar(3) "
		    		+");";
	    	this.setting_db.execSQL(STR_CREATE);

	    	//settings(name,)  //name=map_vendor,travel_mode,route,geocode
	    	STR_CREATE = "CREATE TABLE IF NOT EXISTS "    //+DATABASE_NAME+"."
		    		+SETTINGS_TABLE+ " ("
		    		+"name varchar(10) not null primary key, "
		    		+"value varchar(20) NOT NULL"
		    		+");";
	    	this.setting_db.execSQL(STR_CREATE);
	    	
	    	//id, address_name, admin, country_code, lat,lng
	    	STR_CREATE = "CREATE TABLE IF NOT EXISTS "    //+DATABASE_NAME+"."
		    		+HISTORY_TABLE+ " ("
				    +"id integer not null primary key AUTOINCREMENT, "
		    		+"name varchar(100) not null, "
		    		+"admin varchar(50), "
		    		+"country_code varchar(5) not null, "
		    		+"lat double NOT NULL, lng double NOT NULL"
		    		+");";
	    	this.setting_db.execSQL(STR_CREATE);
	    	//id, address_name, admin, country_code, lat,lng, machine_code, user_name, special(10-normal, 11-home,12-work)
	    	STR_CREATE = "CREATE TABLE IF NOT EXISTS "    //+DATABASE_NAME+"."
		    		+MY_PLACE_TABLE+ " ("
				    //+"create_time TIMESTAMP DEFAULT (datetime('now','localtime')) , "  //CURRENT_TIMESTAMP is in GMT
				    +"id integer not null primary key AUTOINCREMENT, "
		    		+"name varchar(80) not null, "
		    		+"admin varchar(50), "
		    		+"country_code varchar(5) not null, " 
		    		+"lat double NOT NULL, lng double NOT NULL, "
		    		+"machine_code varchar(50) NOT NULL,"
		    		+"user_name varchar(50),"
		    		+"special integer NOT NULL"
		    		+");";
	    	//DECIMAL(10,7)
	    	this.setting_db.execSQL(STR_CREATE);
	    	
	    	this.setting_db.close();
		}
	    public int getMaxID(String table) {
	    	this.setting_db = getReadableDatabase();
	    	String sql = "SELECT max(id) FROM " +table;
	    	Cursor cursor = setting_db.rawQuery(sql, null);
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
	    	this.setting_db = getReadableDatabase();
	    	String sql = "SELECT name,admin,lat,lng,country_code FROM " +HISTORY_TABLE+" order by id desc";
	    	Cursor cursor = setting_db.rawQuery(sql, null);
	    	if (cursor.moveToFirst()){
	    		do{
	    			String name= cursor.getString(0);
	    			String admin= cursor.getString(1);
	    			double lat= cursor.getDouble(2);
	    			double lng= cursor.getDouble(3);
	    			String country_code = cursor.getString(4);
	    			SavedPlace sp = new SavedPlace(name,admin,lat,lng,country_code);
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
	    	this.setting_db = db;
	    	dropTable(GPS_TABLE,db);
	        onCreate(db);
	    }
	    public void dropTable(String tableName,SQLiteDatabase db){
	        String sql=" DROP TABLE IF EXISTS "+tableName+";VACUUM;";
	        db.execSQL(sql);
	    }
	    public String getLastPosition() {
	    	this.setting_db = getReadableDatabase();
	    	String sql = "SELECT country_code,lat,lng FROM " +GPS_TABLE+" where data_id=0";
	    	Cursor cursor = setting_db.rawQuery(sql, null);
	    	if (cursor != null)
	        	cursor.moveToFirst();
	    	String name = null;
	    	try{
	    	  String code = cursor.getString(0);
	    	  double lat = cursor.getDouble(1);
	    	  double lng = cursor.getDouble(2);
	    	  name = code+","+lat+","+lng;
	    	}catch(Exception e){
	    		Log.i(tag, "Error:"+e.getMessage()+",sql="+sql);
	    	}
	    	cursor.close();
	        setting_db.close();
	        this.setting_db=null;
	        return name;
	    }
	    public void addLastPosition(String pos) {
	        String[] posArr = pos.split(",");
	    	insertGPS(0, Double.valueOf(posArr[1]),Double.valueOf(posArr[2]),posArr[0]);
	    }
	    public void updateCountryCode(String countryCode) {
	    	if(getLastPosition()!=null){
	    		updateLastPosition(countryCode);
	    	}else{
	    		addLastPosition(countryCode);
	    	}
	    }
	    //updateGPS(int id,double lat,double lng, String countryCode)
	    public void updateLastPosition(String pos) { //pos=nz,0,0
	    	String[] posArr = pos.split(",");
	    	updateGPS(0,Double.valueOf(posArr[1]),Double.valueOf(posArr[2]),posArr[0]);
	    }
	    
		//(sensor_type,sensor_metric_seq,sensor_metric_data)
	    private long insertGPS(int id, double lat,double lng)
	    {
	        String countryCode = CountryCode.getByLatLng(lat, lng);
	        return insertGPS(id, lat,lng,countryCode);
	    }
	    private long insertGPS(int id, double lat,double lng,String countryCode)
	    {
	    	if(this.setting_db==null)
	    	this.setting_db = getWritableDatabase();
	        ContentValues cv=new ContentValues();
	        cv.put("data_id", id);
	        cv.put("lat", lat);
	        cv.put("lng", lng);
	        cv.put("country_code", countryCode);
	        long row=setting_db.insert(GPS_TABLE, null, cv);
	        return row;
	    }
	    
	    public int deleteOldData(SQLiteDatabase db,int id) {
	    	if(this.setting_db==null)
	    		this.setting_db = this.getWritableDatabase();
	        String where="data_id="+id;
	        int a = db.delete(GPS_TABLE, where,null);
	        //db.close();
	        return a;
	    }
	    
	    private void updateGPS(int id,double lat,double lng, String countryCode) {
	    	if(this.setting_db==null)
	    		this.setting_db = this.getWritableDatabase();
	        String where="data_id=?";
	        String[] whereValue={Integer.toString(id)};
	        ContentValues cv=new ContentValues();
	        cv.put("lat", lat);
	        cv.put("lng", lng);
	        cv.put("country_code", countryCode);
	        setting_db.update(GPS_TABLE, cv, where, whereValue);
	    }
	    private void updateGPS(int id,double lat,double lng) {
	        String countryCode = CountryCode.getByLatLng(lat, lng);
	        this.updateGPS(id, lat, lng, countryCode);
	    }
	    public void updateGPS(int id,Location loc){
	    	if(getLastPosition()==null){
	    		this.insertGPS(id, loc.getLatitude(), loc.getLongitude());
	    	}else{
	    		this.updateGPS(id, loc.getLatitude(), loc.getLongitude());
	    	}
	    }
	    public String getSettings(String name) {
	    	this.setting_db = getReadableDatabase();
	    	String parsedName = name.replaceAll("\'","\'\'");
	    	String sql = "SELECT value FROM " +SETTINGS_TABLE+" where name='"+parsedName+"'";
	    	Cursor cursor = setting_db.rawQuery(sql, null);
	    	if (cursor != null)
	        	cursor.moveToFirst();
	    	String value = null;
	    	try{
	    		value = cursor.getString(0);
	    	}catch(Exception e){
	    		//Log.i(TAG, "Error:"+e.getMessage()+",sql="+sql);
	    	}
	    	cursor.close();
	        setting_db.close();
	        this.setting_db=null;
	        return value;
	    }
	    private long insertSettings(String name, String value){
	    	if(this.setting_db==null)
	    	this.setting_db = getWritableDatabase();
	        ContentValues cv=new ContentValues();
	        cv.put("name", name);
	        cv.put("value", value);
	        long row=setting_db.insert(SETTINGS_TABLE, null, cv);
	        Log.w(tag, "insertSettings:"+name+"="+value);
	        return row;
	    }
	    private void updateSettings(String name, String value) {
	    	if(this.setting_db==null)
	    		this.setting_db = this.getWritableDatabase();
	        String where="name=?";
	        String[] whereValue={name};
	        ContentValues cv=new ContentValues();
	        cv.put("value", value);
	        setting_db.update(SETTINGS_TABLE, cv, where, whereValue);
	        //Log.w(TAG, "updateSettings:"+name+"="+value);
	    }
	    public void changeSettings(String name, String value){
	    	if(getSettings(name)==null){
	    		this.insertSettings(name, value);
	    	}else{
	    		this.updateSettings(name, value);
	    	}
	    }
	    //my_place(id, address_name, admin, country_code, lat,lng, machine_code, user_name, special)
	    //special=10 normal
	    //special=11 home
	    //special=12 work
		public List<SavedPlace> getSavedPlaces(String condition) {
			List<SavedPlace> list = new ArrayList<SavedPlace>();
		    	this.setting_db = getReadableDatabase();
		    	String sql = "SELECT name,admin,lat,lng,country_code,special,id FROM " +MY_PLACE_TABLE+" where "+condition+" order by id desc";
		    	Cursor cursor = setting_db.rawQuery(sql, null);
		    	if (cursor.moveToFirst()){
		    		do{
		    			String name= cursor.getString(0);
		    			String admin= cursor.getString(1);
		    			double lat= cursor.getDouble(2);
		    			double lng= cursor.getDouble(3);
		    			String country_code = cursor.getString(4);
		    			int special = cursor.getInt(5);
		    			int id = cursor.getInt(6);
		    			//int id,String name,String admin,double lat,double lng,String countryCode,String machine,String user,int special
		    			SavedPlace sp = new SavedPlace(id,name,admin,lat,lng,country_code,null,null,special);
		    			//Log.i(tag, "name="+name+",admin="+admin+",lat="+lat+",lng="+lng);
		    			list.add(sp);
		    		}while(cursor.moveToNext());
		    	}
		    	cursor.close();
		        //db.close();
			return list;
		}
		public SavedPlace[] getSavedPlaceNames() {
			List<SavedPlace> list =getSavedPlaces("special="+Place.NORMAL);
			SavedPlace[] array = list.toArray(new SavedPlace[list.size()]);
			//Log.w(tag, "array="+array[0].getName()+", "+array[1].getName());
			return array;
		}
		public SavedPlace getSavedPlace(GeoPoint gp) {
			List<SavedPlace> list =getSavedPlaces("lat="+gp.getLatitude()+" and lng="+gp.getLongitude());
			return list.get(0);
		}
		public SavedPlace[] getHistoryPlaceNames() {
			List<SavedPlace> list =getHistoryPlaces();
			SavedPlace[] array = list.toArray(new SavedPlace[list.size()]);
			//Log.w(tag, "array="+array[0].getName()+", "+array[1].getName());
			return array;
		}
		
		//id, address_name, admin, country_code, lat,lng
		public void addHistoryPlace(SavedPlace addr) {
			String name = addr.getName();//GeoOptions.getAddressName(addr);
	    	if(this.setting_db==null)
	    	this.setting_db = getWritableDatabase();
	        ContentValues cv=new ContentValues();
	        //cv.put("id", id);
	        cv.put("name", name);
	        cv.put("admin", addr.getAdmin());
	        cv.put("country_code", addr.getCountryCode());
	        cv.put("lat", addr.getLat());
	        cv.put("lng", addr.getLng());
	        long row=setting_db.insert(HISTORY_TABLE, null, cv);
	        Log.w(tag, "insertHistory:"+name);
			
		}
		//id, address_name, admin, country_code, lat,lng
		public int addMyPlace(SavedPlace addr) {
			//String name = GeoOptions.getAddressName(addr);
	    	if(this.setting_db==null)
	    	this.setting_db = getWritableDatabase();
	        ContentValues cv=new ContentValues();
	        cv.put("id", addr.getId());
	        cv.put("name", addr.getBriefName());
	        cv.put("admin", addr.getAdmin());
	        cv.put("country_code", addr.getCountryCode());
	        cv.put("lat", addr.getLat());
	        cv.put("lng", addr.getLng());
	        cv.put("machine_code", android_id);
	        //cv.put("user_name", user_name);
	        cv.put("special", addr.getSpecial());
	        long row=setting_db.insert(MY_PLACE_TABLE, null, cv);
	        Log.w(tag, "insertMyPlace:"+addr.getBriefName()+"id="+addr.getId()+",special="+addr.getSpecial());
	        return addr.getId();
		}
		public int deleteHistoryPlaces() {
	    	if(this.setting_db==null)
	    		this.setting_db = this.getWritableDatabase();
	        String where="1=1";
	        int a = setting_db.delete(MY_PLACE_TABLE, where,null);
	        //db.close();
	        return a;
	    }
		public int deleteMyPlaces(int id) {
	    	if(this.setting_db==null)
	    		this.setting_db = this.getWritableDatabase();
	        String where="id="+id;
	        int a = setting_db.delete(MY_PLACE_TABLE, where,null);
	        //db.close();
	        Log.i(tag, "deleted "+a+" rows: "+where);
	        return a;
	    }
		public List<SavedPlace> getAllSavedPlaces(){
			return getSavedPlaces("1=1");
		}
		public String getPoiDbName(String cc){
			return cc+".db";
		}
		public String getPoiDbPath(String cc){
			return SavedOptions.sdcard+"/"+SavedOptions.POI_FILE_PATH+this.getPoiDbName(cc);
		}
		public void testPoiDb(){
			int size = getPOIs("peppers").size();
			Log.w(tag, "POI test:"+size);
		}
	    public List<SavedPlace> getPOIs(String name) {
			List<SavedPlace> list = new ArrayList<SavedPlace>();
			if(this.poi_db==null){
			String dbFile = SavedOptions.sdcard +"/"+SavedOptions.POI_FILE_PATH+ POI_DB_NAME;
	    	this.poi_db = SQLiteDatabase.openDatabase(dbFile, null, SQLiteDatabase.OPEN_READONLY);
			}
	    	String sql = "SELECT lat,lng,pname FROM " +POI_TABLE+" where pname match '"+name+"' limit 0,10"; //,admin,country_code
	    	Cursor cursor = poi_db.rawQuery(sql, null);
	    	if (cursor.moveToFirst()){
	    		do{
	    			double lat= cursor.getDouble(0);
	    			double lng= cursor.getDouble(1);
	    			String fullName= cursor.getString(2);
	    			String admin= "nz"; //cursor.getString(3);
	    			String country_code = "nz"; //cursor.getString(4);
	    			SavedPlace sp = new SavedPlace(fullName,admin,lat,lng,country_code);
	    			//Log.i(tag, "name="+name+",admin="+admin+",lat="+lat+",lng="+lng);
	    			list.add(sp);
	    		}while(cursor.moveToNext());
	    	}
	    	cursor.close();
	        return list;
	    }

	}