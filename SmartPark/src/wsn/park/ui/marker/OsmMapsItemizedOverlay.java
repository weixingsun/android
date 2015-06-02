package wsn.park.ui.marker;

import java.util.ArrayList;

import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import wsn.park.model.SavedPlace;

import android.graphics.Point;

public class OsmMapsItemizedOverlay extends ItemizedIconOverlay<OverlayItem>
{
	private SavedPlace sp;
    private ArrayList<OverlayItem> mItemList = new ArrayList<OverlayItem>();
    private int temp = 0;	//default is temp marker.
    public OsmMapsItemizedOverlay(ArrayList<OverlayItem> pList,
            OnItemGestureListener<OverlayItem> listener, ResourceProxy pResourceProxy){
        super(pList, listener, pResourceProxy);
        mItemList = pList;
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
    public void removeOverlay(OverlayItem aOverlayItem)
    {
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

	public SavedPlace getSp() {
		return sp;
	}

	public void setSp(SavedPlace sp) {
		this.sp = sp;
	}

}
