package cat.app.net.p2p.cloud;

public class DB {

	public static void insertCloud(String hostname, String localSdp) {
		// TODO Auto-generated method stub
		
	}

	public static void createTable(){
		String sql = "CREATE TABLE IF NOT EXISTS tbl_p2p ("
  +"hostname varchar(50) COLLATE utf8_unicode_ci NOT NULL,"
  +"sdp varchar(1000) COLLATE utf8_unicode_ci NOT NULL,"
  +"create_time timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',"
  +"last_update_time timestamp NOT NULL  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
  +"group_id int(11) NOT NULL default 0,"
  +"PRIMARY KEY (hostname));";
	}
	public static void conn(){
		
	}
}
