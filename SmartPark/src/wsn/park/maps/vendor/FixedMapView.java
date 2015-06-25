package wsn.park.maps.vendor;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;

import wsn.park.R;
import wsn.park.maps.OSM;
import wsn.park.ui.marker.PlaceMarker;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public final class FixedMapView extends MapView {
	private List<PlaceMarker> allMarkers;
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
    public void setAllMarkers(List<PlaceMarker> list){
    	allMarkers=list;
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                if (moveCount > 0) {
                    moveCount--;
                    Log.d(tag,"Ignored move event");//random jump when quick pinch
                    return true;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                moveCount = IGNORE_MOVE_COUNT;
                break;
            case MotionEvent.ACTION_DOWN:
            	int x = (int) ev.getX();
            	int y = (int) ev.getY();
            	Point p = new Point();
            	p.set(x, y);
            	//GeoPoint gp = (GeoPoint)getProjection().fromPixels((int)x, (int)y);
            	//GeoPoint gp1 = (GeoPoint)getProjection().fromPixels((int)x-50, (int)y-50);
            	//GeoPoint gp2 = (GeoPoint)getProjection().fromPixels((int)x+50, (int)y+50);
            	//select * from poi where (lat between gp1.lat and gp2.lat) and (lng between gp1.lng and gp2.lng) order by (lat+lng -gp.lat-gp.lng) asc limit 1
            	//Point p = pointFromGeoPoint(gp, this);
            	if(OSM.getInstance().mks.tempMarker!=null){
                	Point pTemp = getProjection().toPixels(OSM.getInstance().mks.tempMarker.getPosition(), p);//will change p value
                	p.set(x, y);
                	if(comparePoint(p,pTemp,50)){
            			OSM.getInstance().dv.openPlacePopup(OSM.getInstance().mks.tempMarker.getPlace());
                	}
                	p.set(x, y);
            	}
            	if(allMarkers!=null)
            	for (PlaceMarker pm:allMarkers){
            		Point p2 = getProjection().toPixels(pm.getPosition(), p);//will change p value
            		Point press = new Point();
            		press.set(x, y);
            		if(comparePoint(press,p2,50)){
            			//Log.w(tag, "pressed("+m.getPosition()+")");
            			//Drawable icon = this.getContext().getResources().getDrawable( R.drawable.marker_sky_80 );
            			//pm.setIcon(icon);
            			//this.invalidate();
            			OSM.getInstance().mks.updateTargetMarker(pm);
            			OSM.getInstance().dv.openPlacePopup(pm.getPosition());
            			break;
            		}
            	}
        }
        return super.onTouchEvent(ev);
    }
    private boolean comparePoint(Point p1,Point p2, int dist){
    	int xx = Math.abs(p1.x-p2.x);
    	int yy = Math.abs(p1.y-p2.y);
		//Log.w(tag, "point("+p1.x+","+p1.y+") - ("+p2.x+","+p2.y+")");
    	if(xx<dist && yy<dist) return true;
    	return false;
    }
    /**
     * 
     * @param x  view coord relative to left
     * @param y  view coord relative to top
     * @param vw MapView
     * @return GeoPoint
     */
    private GeoPoint geoPointFromScreenCoords(int x, int y, MapView vw){
        if (x < 0 || y < 0 || x > vw.getWidth() || y > vw.getHeight()){
            return null; // coord out of bounds
        }
        // Get the top left GeoPoint
        Projection projection = vw.getProjection();
        GeoPoint geoPointTopLeft = (GeoPoint) projection.fromPixels(0, 0);
        Point topLeftPoint = new Point();
        // Get the top left Point (includes osmdroid offsets)
        projection.toPixels(geoPointTopLeft, topLeftPoint);
        // get the GeoPoint of any point on screen 
        GeoPoint rtnGeoPoint = (GeoPoint) projection.fromPixels(x, y);
        return rtnGeoPoint;
    }
    /**
     * 
     * @param gp GeoPoint
     * @param vw Mapview
     * @return a 'Point' in screen coords relative to top left
     */
    private Point pointFromGeoPoint(GeoPoint gp, MapView vw){
        Point rtnPoint = new Point();
        Projection projection = vw.getProjection();
        projection.toPixels(gp, rtnPoint);
        // Get the top left GeoPoint
        GeoPoint geoPointTopLeft = (GeoPoint) projection.fromPixels(0, 0);
        Point topLeftPoint = new Point();
        // Get the top left Point (includes osmdroid offsets)
        projection.toPixels(geoPointTopLeft, topLeftPoint);
        rtnPoint.x-= topLeftPoint.x; // remove offsets
        rtnPoint.y-= topLeftPoint.y;
        if (rtnPoint.x > vw.getWidth() || rtnPoint.y > vw.getHeight() || 
                rtnPoint.x < 0 || rtnPoint.y < 0){
            return null; // gp must be off the screen
        }
        return rtnPoint;
    }
}
