package cat.app.xmpp.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cat.app.xmpp.R;
import cat.app.xmpp.acct.Contact;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactAdapter extends BaseAdapter {
    //private List<Contact> list;
    //private Activity activity;
    private LayoutInflater inflater;
    ArrayList<HashMap<String,Object>> cData = new ArrayList<HashMap<String, Object>>();
    public ContactAdapter(Activity activity, List<Contact> list){
    	
    	inflater = LayoutInflater.from(activity);
    	//this.list = list;
    	HashMap<String,Object> map = null;
		for(Contact c:list){
			map = new HashMap<String,Object>();
			map.put("title", c.getName());
			//map.put("info", c.getStatus());
			if(c.getStatus().equals(Contact.AVAILABLE)){
				map.put("img", R.drawable.light_green_16);
			}else{ //if(c.getStatus().equals(Contact.OFFLINE))
				map.put("img", R.drawable.light_gray_16);
			}
			//map.put("info", "no message");
			cData.add(map);
		}
    }
    @Override  
    public View getView(int position, View convertView, ViewGroup parent){
    	ContactViewHolder holder = null;
    	if(convertView == null)  
        {  
            holder = new ContactViewHolder();
            convertView = inflater.inflate(R.layout.contactlistitem, null);
            holder.img = (ImageView)convertView.findViewById(R.id.img);
            holder.title = (TextView)convertView.findViewById(R.id.tv);
            holder.info = (TextView)convertView.findViewById(R.id.info);
            convertView.setTag(holder);  
        }else{
            holder = (ContactViewHolder)convertView.getTag();
        }  
        holder.img.setBackgroundResource((Integer)cData.get(position).get("img"));
        holder.title.setText((String)cData.get(position).get("title"));
        if((Integer)cData.get(position).get("img")==R.drawable.light_green_16){
        	holder.title.setTextColor(Color.BLUE);
        }else{
        	holder.title.setTextColor(Color.GRAY);
        }
        holder.info.setText((String)cData.get(position).get("info"));  
		
		
        return convertView;
    }

	@Override
	public int getCount() {
		return cData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}  
    //ViewHolderæ≤Ã¨¿‡  
    static class ContactViewHolder
    {  
        public ImageView img;  
        public TextView title;  
        public TextView info;  
    }  
}  