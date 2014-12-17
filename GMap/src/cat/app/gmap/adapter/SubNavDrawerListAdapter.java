package cat.app.gmap.adapter;

import java.util.ArrayList;
import java.util.List;

import cat.app.gmap.model.NavDrawerItem;
import cat.app.gmap.model.RowItem;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class SubNavDrawerListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	public ArrayList<String> arr;
	private String TAG = "GMap.SubNavDrawerListAdapter";

    List<RowItem> rowItem;
    public SubNavDrawerListAdapter(Context context, List<RowItem> listItem) {
        this.context = context;
        rowItem = listItem;
        Log.d("const", "const");
    }

    @Override
	public int getCount() {
        Log.d("count", "count");
        return rowItem.size();
	}
	@Override
	public Object getItem(int position) {
        Log.d("item", "item");
        return rowItem.get(position);
	}
	@Override
	public long getItemId(int position) {
        Log.d("const", "item id");
        return rowItem.indexOf(getItem(position));
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("const", "getview");
        LayoutInflater inflater = (LayoutInflater)context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        RowItem rowitem = (RowItem)getItem(position);
        TextView textForTitle;
        ImageView imgForImage;
        /*convertView = inflater.inflate(R.layout.list_item, null);
        textForTitle = (TextView)convertView.findViewById(R.id.textview);
        imgForImage = (ImageView)convertView.findViewById(R.id.imageview);
        textForTitle.setText(rowitem.getTitle());
        imgForImage.setImageResource(rowitem.getImageId());*/

        return convertView;
	}
}
