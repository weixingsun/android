package cat.app.maps;

import cat.app.osmap.util.SavedOptions;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

public class OfflineDataDownloadManager {
    private DownloadManager downloadManager = null;
    private long lastDownloadId = 0;  
    public static final Uri CONTENT_URI = Uri.parse("content://downloads/my_downloads");
	//String apkUrl = "http://img.meilishuo.net/css/images/AndroidShare/Meilishuo_3.6.1_10006.apk";

	String urlMap = SavedOptions.MAPSFORGE_FILE_URL		+"nz.map";
	String urlRoute = SavedOptions.GH_ROUTE_DATA_URL	+"nz.zip";
	String pathMap = SavedOptions.MAPSFORGE_FILE_PATH	;
	String fileMap = "nz.map";
	String pathRoute = SavedOptions.GH_ROUTE_DATA_PATH;
	String fileRoute = "nz.zip";
	public void init(Activity act){
    	downloadManager = (DownloadManager)act.getSystemService(Context.DOWNLOAD_SERVICE);
		
	}
    public void download(String urlFile, String filePath, String fileName){
    	DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urlFile));
    	request.setDestinationInExternalPublicDir(filePath, fileName);
    	// request.setTitle("MeiLiShuo");
    	// request.setDescription("MeiLiShuo desc");
    	// request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
    	// request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
    	// request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
    	// request.setMimeType("application/cn.trinea.download.file");
    	// request.addRequestHeader(header, value);  //User-Agent£¬gzip
    	long downloadId = downloadManager.enqueue(request);
    }
    

}