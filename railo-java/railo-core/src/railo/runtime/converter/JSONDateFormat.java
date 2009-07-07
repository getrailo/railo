package railo.runtime.converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.collections.map.ReferenceMap;

public class JSONDateFormat {

	private static ReferenceMap map=new ReferenceMap();
	//private static DateFormat format=null;
	private static Locale locale=Locale.ENGLISH;
	
	public synchronized static  String format(Date date) {
		/*
		Locale locale = Locale.ENGLISH;
		PageContext pc = ThreadLocalPageContext.get();
		if(pc!=null)locale=pc.getLocale();
		DateFormat format = (DateFormat) map.get(locale.toString());
		*/
		DateFormat format = (DateFormat) map.get(locale);
		
		
		if(format==null){
			format=new SimpleDateFormat("MMMM, dd yyyy hh:mm:ss",locale);
			map.put(locale.toString(), format);
		}
		
		return format.format(date);
	}
}
