package railo.commons.i18n;

import java.text.DateFormat;
import java.util.Locale;
import java.util.Map;

import railo.commons.lang.SoftHashMap;

public class FormatUtil {

	private final static Map formats=new SoftHashMap();
	
	public static DateFormat[] getDateTimeFormats(Locale locale,boolean lenient) {

		String id="dt-"+locale.hashCode()+"-"+lenient;
		DateFormat[] df=(DateFormat[]) formats.get(id);
		if(df==null) {
			df= new DateFormat[]{
					DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.FULL,locale),
	                DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.LONG,locale),
	                DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.MEDIUM,locale),
	                DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.SHORT,locale),

	                DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.FULL,locale),
	                DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.LONG,locale),
	                DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.MEDIUM,locale),
	                DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.SHORT,locale),

	                DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.FULL,locale),
	                DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.LONG,locale),
	                DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM,locale),
	                DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT,locale),

	                DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.FULL,locale),
	                DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.LONG,locale),
	                DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.MEDIUM,locale),
	                DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT,locale)
        	};
			for(int i=0;i<df.length;i++){
			 df[i].setLenient(lenient);
			}
			
			formats.put(id, df);
		}
		
		return df;
	}
	public static DateFormat[] getDateFormats(Locale locale,boolean lenient) {
		String id="d-"+locale.hashCode()+"-"+lenient;
		DateFormat[] df=(DateFormat[]) formats.get(id);
		if(df==null) {
			df= new DateFormat[]{
                DateFormat.getDateInstance(DateFormat.FULL,locale),
                DateFormat.getDateInstance(DateFormat.LONG,locale),
                DateFormat.getDateInstance(DateFormat.MEDIUM,locale),
                DateFormat.getDateInstance(DateFormat.SHORT,locale)
        	};

			for(int i=0;i<df.length;i++){
			 df[i].setLenient(lenient);
			}
			formats.put(id, df);
		}
		return df;
	}
	public static DateFormat[] getTimeFormats(Locale locale,boolean lenient) {
		String id="t-"+locale.hashCode()+"-"+lenient;
		DateFormat[] df=(DateFormat[]) formats.get(id);
		if(df==null) {
			df= new DateFormat[]{
	                DateFormat.getTimeInstance(DateFormat.FULL,locale),
	                DateFormat.getTimeInstance(DateFormat.LONG,locale),
	                DateFormat.getTimeInstance(DateFormat.MEDIUM,locale),
	                DateFormat.getTimeInstance(DateFormat.SHORT,locale)
        	};

			for(int i=0;i<df.length;i++){
			 df[i].setLenient(lenient);
			}
			formats.put(id, df);
		}
		return df;
	}
	
}
