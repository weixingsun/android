package wsn.park.ui;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class VoiceSuggestListAdapter extends ArrayAdapter<String> {


    private Context mContext;
    private int id;
    private List <String>items ;

    public VoiceSuggestListAdapter(Context context, int textViewResourceId , List<String> list ) 
    {
        super(context, textViewResourceId, list);           
        mContext = context;
        id = textViewResourceId;
        items = list ;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent)
    {
        View mView = v ;
        if(mView == null){
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = vi.inflate(id, null);
        }

        TextView text = (TextView) mView.findViewById(android.R.id.text1);

        if(items.get(position) != null )
        {
            text.setTextColor(Color.BLACK);
            text.setText(items.get(position));
            text.setTextSize(12);
            //text.setBackgroundColor(Color.RED); 
            int color = Color.argb( 200, 64, 64, 64 );
            text.setBackgroundColor( color );

        }

        return mView;
    }

}