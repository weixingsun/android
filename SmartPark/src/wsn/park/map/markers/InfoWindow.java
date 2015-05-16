package wsn.park.map.markers;

import wsn.park.R;
import android.util.Log;

public class InfoWindow {
	private static final String tag = InfoWindow.class.getSimpleName();

	public static int getIconByManeuver(int mId){
		switch(mId){
		case Maneuver.EXIT_LEFT:
		case Maneuver.LEFT:			return R.drawable.ic_turn_left;
		case Maneuver.SLIGHT_LEFT:	return R.drawable.ic_slight_left;
		case Maneuver.SHARP_LEFT:   return R.drawable.ic_sharp_left;
		case Maneuver.EXIT_RIGHT:
		case Maneuver.RIGHT:    return R.drawable.ic_turn_right;
		case Maneuver.SLIGHT_RIGHT:   return R.drawable.ic_slight_right;
		case Maneuver.SHARP_RIGHT:    return R.drawable.ic_sharp_right;
		
		case Maneuver.STRAIGHT:		return R.drawable.ic_continue;
		case Maneuver.DESTINATION:	return R.drawable.ic_empty;
		case Maneuver.ROUNDABOUT1:  return R.drawable.ic_roundabout_1;
		case Maneuver.ROUNDABOUT2:  return R.drawable.ic_roundabout_2;
		case Maneuver.ROUNDABOUT3:  
		case Maneuver.ROUNDABOUT4:  return R.drawable.ic_roundabout;
default:
	Log.i(tag, "ManeuverID="+mId);
	return R.drawable.ic_empty;
		}
	}
}
/*
Maneuver Type Name 	Maneuver ID 	Description
NONE			0		No maneuver occurs here.
STRAIGHT		1		Continue straight.
BECOMES			2		No maneuver occurs here. Road name changes.
SLIGHT_LEFT		3		Make a slight left.
LEFT			4		Turn left.
SHARP_LEFT		5		Make a sharp left.
SLIGHT_RIGHT	6		Make a slight right.
RIGHT			7		Turn right.
SHARP_RIGHT		8		Make a sharp right.
STAY_LEFT		9		Stay left.
STAY_RIGHT		10		Stay right.
STAY_STRAIGHT	11		Stay straight.
UTURN			12		Make a U-turn.
UTURN_LEFT		13		Make a left U-turn.
UTURN_RIGHT		14		Make a right U-turn.
EXIT_LEFT		15		Exit left.
EXIT_RIGHT		16		Exit right.
RAMP_LEFT		17		Take the ramp on the left.
RAMP_RIGHT		18		Take the ramp on the right.
RAMP_STRAIGHT	19		Take the ramp straight ahead.
MERGE_LEFT 		20 		Merge left.
MERGE_RIGHT 	21 		Merge right.
MERGE_STRAIGHT	22		Merge.
ENTERING		23		Enter state/province.
DESTINATION		24		Arrive at your destination.
DESTINATION_LEFT	25		Arrive at your destination on the left.
DESTINATION_RIGHT	26		Arrive at your destination on the right.
ROUNDABOUT1		27		Enter the roundabout and take the 1st exit.
ROUNDABOUT2		28		Enter the roundabout and take the 2nd exit.
ROUNDABOUT3		29		Enter the roundabout and take the 3rd exit.
ROUNDABOUT4		30		Enter the roundabout and take the 4th exit.
ROUNDABOUT5		31		Enter the roundabout and take the 5th exit.
ROUNDABOUT6		32		Enter the roundabout and take the 6th exit.
ROUNDABOUT7		33		Enter the roundabout and take the 7th exit.
ROUNDABOUT8		34		Enter the roundabout and take the 8th exit.
TRANSIT_TAKE	35		Take a public transit bus or rail line.
TRANSIT_TRANSFER	36		Transfer to a public transit bus or rail line.
TRANSIT_ENTER		37		Enter a public transit bus or rail station
TRANSIT_EXIT		38		Exit a public transit bus or rail station
TRANSIT_REMAIN_ON	39		Remain on the current bus/rail car
*/