package wsn.park.ui;

import wsn.park.R;
import wsn.park.model.SavedPlace;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaceAdapter extends ArrayAdapter<SavedPlace>{

    Context context;
    int layoutResourceId;   
    SavedPlace data[] = null;
	private String tag = PlaceAdapter.class.getSimpleName();
   
    public PlaceAdapter(Context context, int layoutResourceId, SavedPlace[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PlaceHolder holder = null;
       
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
           
            holder = new PlaceHolder();
            //holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
           
            row.setTag(holder);
        }
        else
        {
            holder = (PlaceHolder)row.getTag();
        }
       
        SavedPlace p = data[position]; //position start with 0
        holder.place = p;
        //Log.i(tag , "item="+p.getName());
        holder.txtTitle.setText(p.getName());
        //holder.imgIcon.setImageResource(weather.icon);
        return row;
    }

    static class PlaceHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
        SavedPlace place;
    }
}