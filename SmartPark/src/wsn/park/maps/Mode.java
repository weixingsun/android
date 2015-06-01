package wsn.park.maps;

public class Mode {
	private static Mode mode;
	private Mode(){ }
	public static synchronized Mode getInstance( ) {
		if (mode == null) mode=new Mode();
		return mode;
	}
	//define a id for difference purposes in the map view page
	public static int ID=0; 
	/*
	 0=normal:  not forcing to current position
	 1=navi:    forcing to current position
	 2=practice: not forcing to current position, adding sequential points
	 3=pick up home
	 4=pick up work
	*/
	
	
}
