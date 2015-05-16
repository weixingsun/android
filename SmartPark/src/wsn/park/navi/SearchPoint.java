package wsn.park.navi;

import android.location.Address;

public class SearchPoint {

	private int seq;	//waypoint sequence
	private int type; // 1 police, 2 camera, 3 medical
	private Address addr;
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Address getAddr() {
		return addr;
	}
	public void setAddr(Address addr) {
		this.addr = addr;
	}
	
}
