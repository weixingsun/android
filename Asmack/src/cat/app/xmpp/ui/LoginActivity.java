package cat.app.xmpp.ui;

import cat.app.xmpp.ConnectionService;
import cat.app.xmpp.R;
import cat.app.xmpp.R.id;
import cat.app.xmpp.R.layout;
import cat.app.xmpp.db.DbTask;
import cat.app.xmpp.evt.LoginEvent;
import cat.app.xmpp.evt.PopulateSettingsEvent;
import de.greenrobot.event.EventBus;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
 
public class LoginActivity extends Activity {

	Button loginBtn;
	EditText login_text;
	EditText password_text;
	ProgressDialog loginDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

		EventBus.getDefault().register(this);
		login_text = (EditText) this.findViewById(R.id.login_text);
		password_text = (EditText) this.findViewById(R.id.password_text);
        loginBtn = (Button) findViewById(R.id.login_button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                connect();
            }
        });
        DbTask task = new DbTask(DbTask.GET_SETTINGS,PopulateSettingsEvent.LAST_LOGIN);
		task.execute();

        task = new DbTask(DbTask.GET_SETTINGS,PopulateSettingsEvent.LAST_PASSWORD);
		task.execute();
    }
    public void onEventMainThread(LoginEvent event) {
        if(event.getStatus().equals(LoginEvent.SUCCESS)){
        	loginDialog.dismiss();
        	EventBus.getDefault().unregister(this);
        	finish();
        }else if(event.getStatus().equals(LoginEvent.FAIL)){
    		
    	}
	}
    public void onEventMainThread(PopulateSettingsEvent event) {
        if(event.getSettingsName().equals(PopulateSettingsEvent.LAST_LOGIN)){
        	this.login_text.setText(event.getSettingsValue());
        }else if(event.getSettingsName().equals(PopulateSettingsEvent.LAST_PASSWORD)){
        	this.password_text.setText(event.getSettingsValue());
        }
	}
	public void connect() {
		loginDialog = ProgressDialog.show(this,"Connecting...", "Please wait...", false);
		startRecieverService();
		loginDialog.show();
	}

	public void startRecieverService() {
		String login = login_text.getText().toString();
		Intent intent = new Intent(this, ConnectionService.class);
		intent.putExtra("host", login.split("@")[1]);
		intent.putExtra("user", login.split("@")[0]);
		intent.putExtra("pswd", password_text.getText().toString());
		startService(intent);
	}
}