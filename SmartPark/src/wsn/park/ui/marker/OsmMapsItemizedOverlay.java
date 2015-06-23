package wsn.park.ui.marker;

import java.util.ArrayList;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapView;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import wsn.park.R;
import wsn.park.maps.OSM;
import wsn.park.model.Place;
import wsn.park.model.SavedPlace;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;

public class OsmMapsItemizedOverlay extends ItemizedIconOverlay<OverlayItem>
{
	private static final String tag = OsmMapsItemizedOverlay.class.getSimpleName();
	private Place sp;
	private static ResourceProxy rp=(ResourceProxy) new DefaultResourceProxyImpl(OSM.getInstance().act);
    private static ArrayList<OverlayItem> mItemList = new ArrayList<OverlayItem>();
    //private int temp = 0;	//default is temp marker.
//	static OnItemGestureListener<OverlayItem> listener = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>(){
//		  @Override
//		  public boolean onItemSingleTapUp(final int index, final OverlayItem item){
//
//				return true; // We 'handled' this event.
//		  }
//		  @Override
//		  public boolean onItemLongPress(final int index, final OverlayItem item){
//		      return true;
//		  }
//		};
    public OsmMapsItemizedOverlay(){
        super(mItemList, null, rp);
    }

    public void addOverlay(OverlayItem aOverlayItem)
    {
        mItemList.add(aOverlayItem);
        populate();
    }
    public OverlayItem firstOverlay()
    {
        return mItemList.get(0);
        //populate();
    }
    public static OverlayItem secondOverlay()
    {
        return mItemList.get(1);
        //populate();
    }
    public void changeIcon(Drawable icon){
    	firstOverlay().setMarker(icon);
    }
    public void secondIcon(Drawable icon){
    	secondOverlay().setMarker(icon);
    }

	public void hideSecondIcon() {
		// TODO Auto-generated method stub
		secondOverlay().getDrawable().setAlpha(0);
	}
    public void removeOverlay(OverlayItem aOverlayItem) {
        mItemList.remove(aOverlayItem);
        populate();
    }

    @Override
    protected OverlayItem createItem(int i)
    {
        return mItemList.get(i);
    }

    @Override
    public int size(){
        if (mItemList != null)
            return mItemList.size();
        else
            return 0;
    }

    @Override
    public boolean onSnapToItem(int arg0, int arg1, Point arg2, IMapView arg3)
    {
        return false;
    }

	public Place getSp() {
		return sp;
	}

	public void setSp(Place sp) {
		this.sp = sp;
	}
	  @Override
	  public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
			OSM osm = OSM.getInstance();
			//osm.dv.openPlacePopup(sp);
			//item.getHeight()	//item.getWidth()
			Drawable icon = osm.act.getResources().getDrawable( R.drawable.marker_sky_44 );
			icon.setAlpha(255);
			//secondOverlay().setMarker(icon);
			Log.w(tag, "Tap("+e.getX()+","+e.getY()+"): value("+e.getXPrecision()+","+e.getYPrecision()+")");
	  	return true;
	  }

}