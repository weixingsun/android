package cat.app.gmap.model;

import com.google.android.gms.maps.model.LatLng;

public class MarkerPoint {

	private int seq;
	private String title;
	private String comment;
	private LatLng latlng;
	//private Icon icon;

	public MarkerPoint(int seq,String title,String comment,LatLng latlng){
		this.seq=seq;
		this.title=title;
		this.comment=comment;
		this.latlng=latlng;
	}

	public int getCityCode() {
		return seq;
	}

	public void setCityCode(int seq) {
		this.seq = seq;
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
