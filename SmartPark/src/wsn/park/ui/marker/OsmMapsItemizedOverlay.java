package wsn.park.ui.marker;

import java.util.ArrayList;

import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import android.graphics.Point;

public class OsmMapsItemizedOverlay extends ItemizedIconOverlay<OverlayItem>
{
    private ArrayList<OverlayItem> mItemList = new ArrayList<OverlayItem>();

    public OsmMapsItemizedOverlay(ArrayList<OverlayItem> pList,
            ItemizedIconOverlay.OnItemGestureListener<OverlayItem> pOnItemGestureListener, ResourceProxy pResourceProxy)
    {
        super(pList, pOnItemGestureListener, pResourceProxy);
        mItemList = pList;
        // TODO Auto-generated constructor stub
    }

    public void addOverlay(OverlayItem aOverlayItem)
    {
        mItemList.add(aOverlayItem);
        populate();
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
    public int size()
    {
        if (mItemList != null)
            return mItemList.size();
        else
            return 0;
    }

    @Override
    public boolean onSnapToItem(int arg0, int arg1, Point arg2, IMapView arg3)
    {
        // TODO Auto-generated method stub
        return false;
    }

}
