package cat.app.gmap;

import com.google.android.gms.maps.model.LatLng;

public class MarkerPoint {

	private String cityCode;
	private String title;
	private String comment;
	private LatLng latlng;
	//private Icon icon;

	public MarkerPoint(String code,String title,String comment,LatLng latlng){
		this.cityCode=code;
		this.title=title;
		this.comment=comment;
		this.latlng=latlng;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public LatLng getLatlng() {
		return latlng;
	}

	public void setLatlng(LatLng latlng) {
		this.latlng = latlng;
	}
}
