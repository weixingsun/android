package cat.app.gmap.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MarkerPoint {

	private String id;
	private int seq;
	private String title;
	private String comment;
	private LatLng latlng;
	//private Icon icon;


	public MarkerPoint(String id,int seq,String title,String comment,LatLng latlng){
		this.id=id;
		this.seq=seq;
		this.title=title;
		this.comment=comment;
		this.latlng=latlng;
	}
	public MarkerPoint(Marker mk){
		this.id=mk.getId();
		this.seq=0;
		this.title=mk.getTitle();
		this.comment=mk.getSnippet();
		this.latlng=mk.getPosition();
	}
	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
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

	public LatLng getLatLng() {
		return latlng;
	}

	public void setLatlng(LatLng latlng) {
		this.latlng = latlng;
	}

	public String getId() {
		return id;
	}

	private void setId(String id) {
		this.id = id;
	}
}
