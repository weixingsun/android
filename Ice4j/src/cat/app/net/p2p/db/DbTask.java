package cat.app.net.p2p.db;

import android.os.AsyncTask;

public class DbTask extends AsyncTask<String, Void, String>{
	private static final String tag = DbTask.class.getSimpleName();
	String host;
	String msg;
	public DbTask(String host, String msg) {
		this.host = host;
		this.msg = msg;
	}
	@Override
	protected String doInBackground(String... params) {
		DbHelper.getInstance().insertMsg(host, msg);
		return null;
	}

}
