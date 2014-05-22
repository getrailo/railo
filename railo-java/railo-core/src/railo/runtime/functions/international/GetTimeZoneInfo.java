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

	private static final long serialVersionUID = -5462276373169138909L;
	
	public synchronized static railo.runtime.type.Struct call(PageContext pc ) {
		return call(pc,null);
	}
	public synchronized static railo.runtime.type.Struct call(PageContext pc, TimeZone tz) {
		if(tz==null) tz=pc.getTimeZone();
		
        //Date date = ;
        Calendar c = JREDateTimeUtil.getThreadCalendar(tz);
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
        struct.setEL(KeyConstants._id, tz.getID());
        
       
        return struct;
		
        //return new StructImpl();
	}
}