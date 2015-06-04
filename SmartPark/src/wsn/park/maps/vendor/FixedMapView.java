package wsn.park.maps.vendor;
import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.views.MapView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
 
 
public final class FixedMapView extends MapView {
 
    private static final int IGNORE_MOVE_COUNT = 2;
    private int moveCount = 0;
	private String tag = FixedMapView.class.getSimpleName();
 
 
    public FixedMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }  
 
    public FixedMapView(Context context, int tileSizePixels,
			ResourceProxy resourceProxy, MapTileProviderBase aTileProvider) {
    	super(context, tileSizePixels,resourceProxy,aTileProvider);
	}

	@Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                if (moveCount > 0) {
                    moveCount--;
                    Log.d(tag ,"Ignored move event");
                    return true;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                moveCount = IGNORE_MOVE_COUNT;
                break;
        }
        return super.onTouchEvent(ev);
    }  
}
