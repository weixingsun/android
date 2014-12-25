package cat.app.gmap.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class DrawPoint extends Overlay{

	GeoPoint p;
	
	public DrawPoint(LatLng point){
	}
	@Override
    public boolean draw(Canvas canvas, MapView mapView, 
    boolean shadow, long when) 
    {
        super.draw(canvas, mapView, shadow);                   

        //---translate the GeoPoint to screen pixels---
        Point screenPts = new Point();
        mapView.getProjection().toPixels(p, screenPts);
        //--------------draw circle----------------------            

        Point pt=mapView.getProjection().toPixels(p,screenPts);

        Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(0x30000000);
        circlePaint.setStyle(Style.FILL_AND_STROKE);
        canvas.drawCircle(screenPts.x, screenPts.y, 50, circlePaint);           

        //---add the marker---
        //Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.pin);            
        //canvas.drawBitmap(bmp, screenPts.x, screenPts.y-bmp.getHeight(), null);
        super.draw(canvas,mapView,shadow);

        return true;

    }
}
