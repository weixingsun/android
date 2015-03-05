package cat.app.xmpp.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cat.app.xmpp.Client;
import cat.app.xmpp.R;
import cat.app.xmpp.acct.*;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import org.jivesoftware.smack.packet.Message;

public class MessageAdapter extends BaseAdapter {
    private static final String tag = MessageAdapter.class.getSimpleName();
	//private List<Contact> list;
    //private Activity activity;
    private LayoutInflater inflater;
    ArrayList<HashMap<String,Object>> mData = new ArrayList<HashMap<String, Object>>();
    public MessageAdapter(Activity activity, List<Message> list){
    	
    	inflater = LayoutInflater.from(activity);
    	//this.list = list;
    	HashMap<String,Object> map = null;
		for(Message m:list){
			map = new HashMap<String,Object>();
			map.put("title", m.getBody());
			map.put("from", m.getSender());
			/*if(c.getStatus().equals(Contact.AVAILABLE)){
				map.put("img", R.drawable.light_green_16);
			}else{ //if(c.getStatus().equals(Contact.OFFLINE))
				map.put("img", R.drawable.light_gray_16);
			}*/
			map.put("info", m.getSendTime());
			mData.add(map);
		}
    }
    @Override  
    public View getView(int position, View convertView, ViewGroup parent){
    	MessageViewHolder holder = null;
    	if(convertView == null)  
        {
            holder = new MessageViewHolder();
            convertView = inflater.inflate(R.layout.messagelistitem, null);
            holder.img = (ImageView)convertView.findViewById(R.id.img);
            holder.title = (TextView)convertView.findViewById(R.id.tv);
            holder.info = (TextView)convertView.findViewById(R.id.info);
            convertView.setTag(holder);  
        }else{
            holder = (MessageViewHolder)convertView.getTag();
        }  
        //holder.img.setBackgroundResource((Integer)mData.get(position).get("img"));
        holder.title.setText((String)mData.get(position).get("title"));  
        holder.info.setText((String)mData.get(position).get("info"));  
        String from = (String)mData.get(position).get("from");
		if(!Client.SELF.equals(from)){
			//RelativeLayout contentLO = (RelativeLayout) convertView.findViewById(R.id.content_layout);
			//contentLO.setGravity(Gravity.RIGHT);
			 RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
							 RelativeLayout.LayoutParams.WRAP_CONTENT,
							 RelativeLayout.LayoutParams.WRAP_CONTENT);
					 lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);//Óë¸¸ÈÝÆ÷µÄ×ó²à¶ÔÆë
					 //lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);//Óë¸¸ÈÝÆ÷µÄÉÏ²à¶ÔÆë
			holder.title.setLayoutParams(lp);
			holder.info.setLayoutParams(lp);
			Log.i(tag, "self="+Client.SELF+",from="+from);
		}
        return convertView;
    }

	@Override
	public int getCount() {
		return mData.size();
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
    //ViewHolder¾²Ì¬Àà  
    static class MessageViewHolder
    {  
        public ImageView img;
        public TextView title;
        public TextView info;
    }  
}  