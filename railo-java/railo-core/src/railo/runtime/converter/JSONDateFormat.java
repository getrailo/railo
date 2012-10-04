package railo.runtime.converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.collections.map.ReferenceMap;

import railo.runtime.engine.ThreadLocalPageContext;

public class JSONDateFormat {

	private static ReferenceMap map=new ReferenceMap();
	//private static DateFormat format=null;
	private static Locale locale=Locale.ENGLISH;
	
	public synchronized static  String format(Date date, TimeZone tz) {
		tz=ThreadLocalPageContext.getTimeZone(tz);
		String id=locale.hashCode()+"-"+tz.getID();
		DateFormat format = (DateFormat) map.get(id);
		if(format==null){
			format=new SimpleDateFormat("MMMM, dd yyyy HH:mm:ss Z",locale);
			format.setTimeZone(tz);
			map.put(id, format);
		}
		
		return format.format(date);
	}
}
