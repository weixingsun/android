package cat.app.xmpp.ui;

import cat.app.xmpp.R;
import cat.app.xmpp.evt.UIEvent;
import de.greenrobot.event.EventBus;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ContactClickListener implements OnItemClickListener {

	private Activity activity;
	public ContactClickListener(Activity act){
		this.activity = act;
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		LinearLayout ll = (LinearLayout) view;
		TextView tv = (TextView) ll.findViewById(R.id.tv);
		String contactName = tv.getText().toString();
		changeSubMenu(R.array.action_type_items,contactName);
	}

	private void changeSubMenu(int arrayId, final String contactName){
		String[] subSettingsStr = activity.getResources().getStringArray(arrayId);
		ArrayAdapter<String> childAdapter = new ArrayAdapter<String>(activity,R.layout.action_list_item, subSettingsStr);
		ListView subSettings = (ListView) activity.findViewById(R.id.left_drawer_child);
		subSettings.setAdapter(childAdapter);
		subSettings.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView tv = (TextView)view;
				String name=tv.getText().toString();
				if(name.equals("Chat")){
					TextView recipient = (TextView) activity.findViewById(R.id.toTV);
					recipient.setText(contactName);
					EventBus.getDefault().post(new UIEvent(UIEvent.DRAWER,UIEvent.CLOSE));
				}
			}
		});
	}
}
