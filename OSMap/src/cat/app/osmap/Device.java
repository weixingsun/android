package cat.app.osmap;

import cat.app.osmap.ui.DelayedTextWatcher;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

public class Device {
	Activity act;
	public void init(Activity act){
		this.act=act;
		setText();
		closeKeyBoard();
	}
	private void setText() {
		EditText inputAddress = (EditText) act.findViewById(R.id.inputAddress);
		inputAddress.addTextChangedListener(new DelayedTextWatcher(2000) {
			@Override
			public void afterTextChangedDelayed(Editable s) {
				//GoogleSearchByAddressNameTask task = new GoogleSearchByAddressNameTask(map, inputAddress.getText().toString());
				//task.execute();
			}
		});
		inputAddress.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == 66) { // Enter
					//GoogleSearchByAddressNameTask task = new GoogleSearchByAddressNameTask(map, inputAddress.getText().toString());
					//task.execute();
					closeKeyBoard();
				}
				return false;
			}
		});
	}

		public void closeKeyBoard() {
			EditText inputAddress = (EditText) act.findViewById(R.id.inputAddress);
			InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(inputAddress.getWindowToken(), 0);
		}

}
