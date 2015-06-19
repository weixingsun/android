package wsn.park.maps;

import java.util.ArrayList;
import java.util.List;

public class Mode {
	/*
	 0=normal:  not forcing to current position
	 1=navi:    forcing to current position
	 2=practice: not forcing to current position, adding sequential points
	 3=pick up home
	 4=pick up work
	*/
	public static final int NORMAL = 0;
	public static final int NAVI = 1;
	public static final int PRACTICE = 2;
	private static Mode mode;
	private Mode(){ }
	public static synchronized Mode getInstance( ) {
		if (mode == null) mode=new Mode();
		return mode;
	}
	public static int getID() {
		return ID;
	}
	public static void setID(int iD) {
		ID = iD;
	}
	//define a id for difference purposes in the map view page
	private static int ID=0;
	public static String getModeName(){
		String name = null;
		switch(ID){
		case NORMAL:	name="Normal";
		case NAVI:		name="Navi";
		case PRACTICE:	name="Practice";
				
		}
		return name; 
	}
}
