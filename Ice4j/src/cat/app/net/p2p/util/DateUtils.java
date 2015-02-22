package cat.app.net.p2p.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {

    public static String formatTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Calendar c = Calendar.getInstance();
        return sdf.format(c.getTime());
    }
}
