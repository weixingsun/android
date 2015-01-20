package cat.app.osmap.ui;

import java.io.File;
import java.text.DecimalFormat;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cat.app.maps.BaseActivity;
import cat.app.maps.MapOptions;
import cat.app.osmap.R;
import cat.app.osmap.util.RouteOptions;
import cat.app.osmap.util.SavedOptions;
import cat.app.osmap.util.ZIP;
import cn.trinea.android.common.util.DownloadManagerPro;
import cn.trinea.android.common.util.PreferencesUtils;

/**
 * DownloadManagerUI
 * 
 * @author <a href="http://sun.vhostall.com/" target="_blank">Sun</a> 2015-1-15
 */
public class DownloadManagerUI extends BaseActivity {

    public static String     DOWNLOAD_FOLDER_NAME;
    public static String     DOWNLOAD_FILE_NAME;
    public static String     DOWNLOAD_FILE_DESC;
    public static String     COUNTRY_CODE;

    //public static final String   FILE_URL              = "http://servicedata.vhostall.com/route/nz.zip";
    public static String     FILE_URL;//              = "http://servicedata.vhostall.com/route/nz.zip";
    public static final String     KEY_NAME_DOWNLOAD_ID = "downloadId";
	protected static final String  tag = DownloadManagerUI.class.getSimpleName();

    //private Button                 downloadButton;
    private ProgressBar            downloadProgress;
    private TextView               downloadTip;
    private TextView               downloadSize;
    private TextView               downloadPrecent;
    private Button                 downloadCancel;
    //private Button				   downloadDone;
    private String					phase				= "prepared";

    private DownloadManager        downloadManager;
    private DownloadManagerPro     downloadManagerPro;
    private long                   downloadId           = 0;

    private MyHandler              handler;

    private DownloadChangeObserver downloadObserver;
    private CompleteReceiver       completeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.download_manager_demo);

        Bundle extras = getIntent().getExtras();
        String fileName=null;
        if(extras!=null) {
        	fileName =extras.getString("file");
        }
        DOWNLOAD_FILE_NAME = fileName;
        COUNTRY_CODE = DOWNLOAD_FILE_NAME.split("\\.")[0];
        if(fileName.endsWith(".zip")){
        	DOWNLOAD_FOLDER_NAME = SavedOptions.GH_ROUTE_DATA_PATH; 
        	FILE_URL = RouteOptions.GH_ROUTE_URL+fileName;
        }else if(fileName.endsWith(".map")){
        	DOWNLOAD_FOLDER_NAME = SavedOptions.MAPSFORGE_FILE_PATH;
        	FILE_URL = MapOptions.MF_ROUTE_URL+fileName;
        } 
        handler = new MyHandler();
        downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        downloadManagerPro = new DownloadManagerPro(downloadManager);

        // see android mainfest.xml, accept minetype of cn.trinea.download.file
        Intent intent = getIntent();
        if (intent != null) {
            /**
             * below android 4.2, intent.getDataString() is file:///storage/sdcard1/Trinea/MeLiShuo.apk<br/>
             * equal or above 4.2 intent.getDataString() is content://media/external/file/29669
             */
            Uri data = intent.getData();
            if (data != null) {
                Toast.makeText(context, data.toString(), Toast.LENGTH_LONG).show();
            }
        }

        initView();
        initData();

        downloadObserver = new DownloadChangeObserver();
        completeReceiver = new CompleteReceiver();
        /** register download success broadcast **/
        registerReceiver(completeReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onResume() {
        super.onResume();
        /** observer download change **/
        getContentResolver().registerContentObserver(DownloadManagerPro.CONTENT_URI, true, downloadObserver);
        updateView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(downloadObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(completeReceiver);
    }

    private void initView() {
        //downloadButton = (Button)findViewById(R.id.download_button);
        downloadCancel = (Button)findViewById(R.id.download_cancel);
        downloadProgress = (ProgressBar)findViewById(R.id.download_progress);
        downloadTip = (TextView)findViewById(R.id.download_tip);
        //downloadTip.setText(getString(R.string.tip_download_file) + Environment.getExternalStoragePublicDirectory(DOWNLOAD_FOLDER_NAME));
        downloadSize = (TextView)findViewById(R.id.download_size);
        downloadPrecent = (TextView)findViewById(R.id.download_precent);
    }

    private void initData() {
        /**
         * get download id from preferences.<br/>
         * if download id bigger than 0, means it has been downloaded, then query status and show right text;
         */
        downloadId = PreferencesUtils.getLong(context, KEY_NAME_DOWNLOAD_ID);
        updateView();
        downloadCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                File folder = Environment.getExternalStoragePublicDirectory(DOWNLOAD_FOLDER_NAME);
                if (!folder.exists() || !folder.isDirectory()) {
                    folder.mkdirs();
                }
                Log.e(tag, "file="+DOWNLOAD_FOLDER_NAME+"/"+DOWNLOAD_FILE_NAME+",url="+FILE_URL);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(FILE_URL));
                request.setDestinationInExternalPublicDir(DOWNLOAD_FOLDER_NAME, DOWNLOAD_FILE_NAME);
                request.setTitle(getString(R.string.download_title));
                request.setDescription(DOWNLOAD_FILE_NAME);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setVisibleInDownloadsUi(false);
                // request.allowScanningByMediaScanner();
                // request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                // request.setShowRunningNotification(false);
                // request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
                // request.setMimeType("application/cat.app.download.file");
                downloadId = downloadManager.enqueue(request);
                /** save download id to preferences **/
                PreferencesUtils.putLong(context, KEY_NAME_DOWNLOAD_ID, downloadId);
                updateView();
                DownloadManagerUI.this.phase = "downloading";
            }
        });
    }

    /**
     * install app
     * 
     * @param context
     * @param filePath
     * @return whether apk exist
     */
    public static boolean install(Context context, String filePath) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        if (file != null && file.length() > 0 && file.exists() && file.isFile()) {
            i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            return true;
        }
        return false;
    }

    class DownloadChangeObserver extends ContentObserver {
        public DownloadChangeObserver() {
            super(handler);
        }
        @Override
        public void onChange(boolean selfChange) {
            updateView();
        }
    }

    class CompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            /**
             * get the id of download which have download success, if the id is my id and it's status is successful,
             * then install it
             **/
        	DownloadManagerUI.this.phase = "downloaded";
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (completeDownloadId == downloadId) {
                initData();
                updateView();
                if (downloadManagerPro.getStatusById(downloadId) == DownloadManager.STATUS_SUCCESSFUL) {
                	//Log.e(tag, "file="+DOWNLOAD_FILE_NAME);
                    // if download successful, unzip
                    if(DOWNLOAD_FILE_NAME.endsWith(".zip")){
                    	//Log.e(tag, "zip="+DOWNLOAD_FILE_NAME);
                    	//String countryCode = DOWNLOAD_FILE_NAME.split("\\.")[0];
                    	String folderName = Environment.getExternalStoragePublicDirectory(DOWNLOAD_FOLDER_NAME).getAbsolutePath();
                    	String zipFile = folderName +"/"+ DOWNLOAD_FILE_NAME;
                    	String unzipLocation = folderName +"/";
                    	//Log.e(tag, "unzipping: "+zipFile+", folder="+unzipLocation);
                    	ZIP d = new ZIP(zipFile, unzipLocation);
                    	d.unzip();
                    	File zip = new File(zipFile);
                    	zip.delete();
                    }
                }
            }
        }
    };

    public void updateView() {
        int[] bytesAndStatus = downloadManagerPro.getBytesAndStatus(downloadId);
        handler.sendMessage(handler.obtainMessage(0, bytesAndStatus[0], bytesAndStatus[1], bytesAndStatus[2]));
    }

    /**
     * MyHandler
     * 
     * @author wsun 2015-1-18
     */
    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    int status = (Integer)msg.obj;
                    if (isDownloading(status)) {
                        downloadProgress.setVisibility(View.VISIBLE);
                        downloadProgress.setMax(0);
                        downloadProgress.setProgress(0);
                        //downloadButton.setVisibility(View.GONE);
                        downloadSize.setVisibility(View.VISIBLE);
                        downloadPrecent.setVisibility(View.VISIBLE);
                        downloadCancel.setVisibility(View.VISIBLE);
                        changeIcon(R.drawable.icon_stop);

                        downloadCancel.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                downloadManager.remove(downloadId);
                                updateView();
                                changeIcon(R.drawable.icon_download);
                            }
                        });
                        if (msg.arg2 < 0) {
                            downloadProgress.setIndeterminate(true);
                            downloadPrecent.setText("0%");
                            downloadSize.setText("0M/0M");
                        } else {
                            downloadProgress.setIndeterminate(false);
                            downloadProgress.setMax(msg.arg2);
                            downloadProgress.setProgress(msg.arg1);
                            downloadPrecent.setText(getNotiPercent(msg.arg1, msg.arg2));
                            downloadSize.setText(getAppSize(msg.arg1) + "/" + getAppSize(msg.arg2));
                        }
                    } else {
                        downloadProgress.setVisibility(View.VISIBLE);
                        downloadProgress.setMax(0);
                        downloadProgress.setProgress(0);
                        //downloadButton.setVisibility(View.VISIBLE);
                        downloadSize.setVisibility(View.VISIBLE);
                        downloadPrecent.setVisibility(View.VISIBLE);
                        downloadCancel.setVisibility(View.VISIBLE);

                        //Log.e(tag, "status="+status+",done="+DownloadManager.STATUS_SUCCESSFUL);
                        if (status == DownloadManager.STATUS_FAILED) {
                            //downloadButton.setText(getString(R.string.app_status_download_fail));
                        } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            //downloadButton.setText(getString(R.string.app_status_downloaded));
                        } else {
                            //downloadButton.setText(getString(R.string.app_status_download));
                        }
                        if(DownloadManagerUI.this.phase.equals("prepared")){
                        	changeUIBeforeDownload();
                        } else if(DownloadManagerUI.this.phase.equals("downloaded") ){
                    		changeUIAfterDownload();
                    	}
                    }
                    break;
            }
        }
    }
    public void changeUIBeforeDownload(){
        downloadPrecent.setText("");
        String title=null;
        if(DOWNLOAD_FILE_NAME.endsWith("zip")){
        	title = "Download Route File: "+COUNTRY_CODE.toUpperCase();
        }else{
        	title = "Download Map File: "+COUNTRY_CODE.toUpperCase();
        }
        downloadSize.setText(title);
        //changeIcon(R.drawable.icon_download);
        //downloadCancel.setOnClickListener(new OnClickListener() {
        //    @Override
        //    public void onClick(View v) { }
        //});
    }
    public void changeUIAfterDownload(){
        downloadPrecent.setText("");
        downloadSize.setText(DOWNLOAD_FILE_DESC+" Downloaded");
        changeIcon(R.drawable.icon_correct);
        downloadCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) { }
        });
    }
    public void changeIcon(int resId){
        Drawable myIcon = DownloadManagerUI.this.getResources().getDrawable(resId);
        downloadCancel.setCompoundDrawablesWithIntrinsicBounds(null, null, null, myIcon);
    }
    static final DecimalFormat DOUBLE_DECIMAL_FORMAT = new DecimalFormat("0.##");

    public static final int    MB_2_BYTE             = 1024 * 1024;
    public static final int    KB_2_BYTE             = 1024;

    /**
     * @param size
     * @return
     */
    public static CharSequence getAppSize(long size) {
        if (size <= 0) {
            return "0M";
        }

        if (size >= MB_2_BYTE) {
            return new StringBuilder(16).append(DOUBLE_DECIMAL_FORMAT.format((double)size / MB_2_BYTE)).append("M");
        } else if (size >= KB_2_BYTE) {
            return new StringBuilder(16).append(DOUBLE_DECIMAL_FORMAT.format((double)size / KB_2_BYTE)).append("K");
        } else {
            return size + "B";
        }
    }

    public static String getNotiPercent(long progress, long max) {
        int rate = 0;
        if (progress <= 0 || max <= 0) {
            rate = 0;
        } else if (progress > max) {
            rate = 100;
        } else {
            rate = (int)((double)progress / max * 100);
        }
        return new StringBuilder(16).append(rate).append("%").toString();
    }

    public static boolean isDownloading(int downloadManagerStatus) {
        return downloadManagerStatus == DownloadManager.STATUS_RUNNING
                || downloadManagerStatus == DownloadManager.STATUS_PAUSED
                || downloadManagerStatus == DownloadManager.STATUS_PENDING;
    }
}
