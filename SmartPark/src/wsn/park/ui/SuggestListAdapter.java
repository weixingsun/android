package wsn.park.ui;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SuggestListAdapter extends SimpleAdapter{

	//private int[] colors = { Color.GRAY, Color.LTGRAY };
    private int[] colors = new int[]{0x30FF0000, 0x300000FF};

    public SuggestListAdapter(Context context,
                    List<? extends Map<String, ?>> data, int resource,
                    String[] from, int[] to) {
            super(context, data, resource, from, to);
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		int colorPos = position%colors.length;
		view.setBackgroundColor(colors[colorPos]);
		TextView tv = (TextView) view.findViewById(android.R.id.text1);
		tv.setTextColor(Color.BLACK);
		//tv.setBackgroundColor(colors[colorPos]);
		tv.setTextSize(18);
		return view;
	}
}