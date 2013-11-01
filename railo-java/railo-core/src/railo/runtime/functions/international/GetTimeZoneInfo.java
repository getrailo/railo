/**
 * Implements the CFML Function gettimezoneinfo
 */
package railo.runtime.functions.international;

import java.util.Calendar;
import java.util.TimeZone;

import railo.commons.date.JREDateTimeUtil;
import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.KeyConstants;

public final class GetTimeZoneInfo implements Function {

	
	public synchronized static railo.runtime.type.Struct call(PageContext pc ) {
		
        //Date date = ;
        TimeZone timezone = pc.getTimeZone();
        Calendar c = JREDateTimeUtil.getThreadCalendar(timezone);
        c.setTimeInMillis(System.currentTimeMillis());

    	int dstOffset=c.get(Calendar.DST_OFFSET);
        int total = c.get(Calendar.ZONE_OFFSET) / 1000 + dstOffset / 1000;
        total *= -1;
        int j = total / 60;
        int hour = total / 60 / 60;
        int minutes = j % 60;
        
        Struct struct = new StructImpl();
        struct.setEL("utcTotalOffset", new Double(total));
        struct.setEL("utcHourOffset", new Double(hour));
        struct.setEL("utcMinuteOffset", new Double(minutes));
        struct.setEL("isDSTon", (dstOffset > 0)?Boolean.TRUE:Boolean.FALSE);
        struct.setEL(KeyConstants._id, timezone.getID());
        
       
        return struct;
		
        //return new StructImpl();
	}
}