package cat.app.xmpp.db;

import cat.app.xmpp.acct.Account;
import cat.app.xmpp.evt.PopulateSettingsEvent;
import de.greenrobot.event.EventBus;
import android.os.AsyncTask;
import android.util.Log;

public class DbTask extends AsyncTask<String, Void, String>{
	private static final String tag = DbTask.class.getSimpleName();
	public static final String SETTINGS = "settings";
	public static final String GET_SETTINGS = "get_settings";
	public static final String ACCOUNT = "account";
	public static final String INIT = "init";
	public static final String MSG = "msg";
	String sender;
	String msg;
	String type;
	String settingsName;
	String settingsValue;
	Account account;
	public DbTask(String type, String sender, String msg) {
		this.type = type;
		this.sender = sender;
		this.msg = msg;
	}
	public DbTask(String type, Account account) {
		this.type = type;
		this.account = account;
	}
	public DbTask(String type, String settingsName) {
		this.type = type;
		this.settingsName = settingsName;
	}
	public DbTask(String type) {
		this.type = type;
	}
	@Override
	protected String doInBackground(String... params) {
		try{
			if(type.equals(MSG)){
				DbHelper.getInstance().insertMsg(sender, msg);
			}else if(type.equals(SETTINGS)){
				DbHelper.getInstance().changeSettings(sender, msg);
			}else if(type.equals(ACCOUNT)){
				DbHelper.getInstance().changeAccount(account);
			}else if(type.equals(INIT)){
				DbHelper.getInstance().init();
			}else if(type.equals(GET_SETTINGS)){
				settingsValue = DbHelper.getInstance().getSettings(this.settingsName);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	@Override
    protected void onPostExecute(String useless) {
		if(type.equals(GET_SETTINGS)){
		EventBus.getDefault().post(new PopulateSettingsEvent(settingsName,settingsValue));
		}
	}
}
