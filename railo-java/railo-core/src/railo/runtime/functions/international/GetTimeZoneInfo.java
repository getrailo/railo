/**
 * Implements the Cold Fusion Function gettimezoneinfo
 */
package railo.runtime.functions.international;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;

public final class GetTimeZoneInfo implements Function {

	private static String id="";
	private static Calendar calendar;
	
	public synchronized static railo.runtime.type.Struct call(PageContext pc ) {
		
        //Date date = ;
        TimeZone timezone = pc.getTimeZone();
		
       
        synchronized(id) {
        	if(!id.equals(timezone.getID())) {
        		id=timezone.getID();
        		calendar = Calendar.getInstance(timezone);
        	}
            else calendar.clear();
        	
        	calendar.setTime(new Date());
        }
        
    	int dstOffset=calendar.get(Calendar.DST_OFFSET);
        int total = calendar.get(Calendar.ZONE_OFFSET) / 1000 + dstOffset / 1000;
        total *= -1;
        int j = total / 60;
        int hour = total / 60 / 60;
        int minutes = j % 60;
        
        Struct struct = new StructImpl();
        struct.setEL("utcTotalOffset", new Double(total));
        struct.setEL("utcHourOffset", new Double(hour));
        struct.setEL("utcMinuteOffset", new Double(minutes));
        struct.setEL("isDSTon", (dstOffset > 0)?Boolean.TRUE:Boolean.FALSE);
        struct.setEL("id", timezone.getID());
        
       
        return struct;
		
        //return new StructImpl();
	}
}