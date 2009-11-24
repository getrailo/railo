package railo.commons.date;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import railo.commons.lang.StringUtil;
import railo.runtime.exp.ExpressionException;
import railo.runtime.type.List;

public class TimeZoneUtil {

	private static final Map IDS=new HashMap();
	
	static {
		String[] ids=TimeZone.getAvailableIDs();
		for(int i=0;i<ids.length;i++){
			IDS.put(ids[i].toLowerCase(), TimeZone.getTimeZone(ids[i]));
		}
		IDS.put("jvm", TimeZone.getDefault());
		IDS.put("default", TimeZone.getDefault());
		IDS.put("", TimeZone.getDefault());
		//IDS.put(TimeZoneConstants.CEST.getID().toLowerCase(), TimeZoneConstants.CEST);
	}
	
	
	/**
	 * return the string format of the Timezone
	 * @param timezone
	 * @return
	 */
	public static String toString(TimeZone timezone){
		return timezone.getID();
	}

	private static String getSupportedTimeZonesAsString() {
		return List.arrayToList(TimeZone.getAvailableIDs(),", ");
	}
	
	/**
	 * translate timezone string format to a timezone
	 * @param strTimezone
	 * @return
	 */
	public static TimeZone toTimeZone(String strTimezone,TimeZone defaultValue){
		strTimezone=StringUtil.replace(strTimezone.trim().toLowerCase(), " ", "", false);
		TimeZone tz = (TimeZone) IDS.get(strTimezone);
		if(tz!=null) return tz;
		return defaultValue;
	}
	
	public static TimeZone toTimeZone(String strTimezone) throws ExpressionException{
		TimeZone tz = toTimeZone(strTimezone, null);
		if(tz!=null) return tz;
		throw new ExpressionException("can't cast value ("+strTimezone+") to a TimeZone","supported TimeZones are:"+getSupportedTimeZonesAsString());
	}
}
