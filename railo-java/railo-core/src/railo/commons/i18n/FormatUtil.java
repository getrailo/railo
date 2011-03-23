package railo.commons.i18n;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.collections.map.ReferenceMap;


public class FormatUtil {
 
	private final static Map<String,DateFormat[]> formats=new ReferenceMap(ReferenceMap.SOFT,ReferenceMap.SOFT);
	
	public static DateFormat[] getDateTimeFormats(Locale locale,TimeZone tz,boolean lenient) {

		String id="dt-"+locale.hashCode()+"-"+tz.getID()+"-"+lenient;
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
				df[i].setTimeZone(tz);
			}
			
			formats.put(id, df);
		}
		
		return df;
	}
	public static DateFormat[] getDateFormats(Locale locale,TimeZone tz,boolean lenient) {
		String id="d-"+locale.hashCode()+"-"+tz.getID()+"-"+lenient;
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
				df[i].setTimeZone(tz);
			}
			formats.put(id, df);
		}
		return df;
	}
	

	/**
	 * CFML Supported LS Formats
	 * @param locale
	 * @param tz
	 * @param lenient
	 * @return
	 */
	public static DateFormat[] getCFMLFormats(TimeZone tz,boolean lenient) {
		String id="cfml-"+Locale.ENGLISH.hashCode()+"-"+tz.getID()+"-"+lenient;
		DateFormat[] df=(DateFormat[]) formats.get(id);
		if(df==null) {
			df= new SimpleDateFormat[]{
					  new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy",Locale.ENGLISH)
						 ,new SimpleDateFormat("MMM dd, yyyy H:mm:ss a",Locale.ENGLISH)
						 
					 ,new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss a zzz",Locale.ENGLISH)
					 ,new SimpleDateFormat("MMMM d yyyy HH:mm:ss",Locale.ENGLISH)
					 ,new SimpleDateFormat("MMMM d yyyy HH:mm",Locale.ENGLISH)
					 ,new SimpleDateFormat("EEE, MMM dd, yyyy HH:mm:ss",Locale.ENGLISH)
					 ,new SimpleDateFormat("EEEE, MMMM dd, yyyy h:mm:ss a zzz",Locale.ENGLISH)
					 ,new SimpleDateFormat("dd-MMM-yy HH:mm a",Locale.ENGLISH)
					 ,new SimpleDateFormat("dd-MMMM-yy HH:mm a",Locale.ENGLISH)
					 ,new SimpleDateFormat("EE, dd MMM yyyy HH:mm:ss zz",Locale.ENGLISH)
					 ,new SimpleDateFormat("EEE d, MMM yyyy HH:mm:ss zz",Locale.ENGLISH)
					 ,new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH)
						 ,new SimpleDateFormat("MMMM, dd yyyy hh:mm:ss",Locale.ENGLISH)
						 ,new SimpleDateFormat("yyyy/MM/dd hh:mm:ss zz",Locale.ENGLISH)
					 //,new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",Locale.ENGLISH)
				};

			for(int i=0;i<df.length;i++){
				df[i].setLenient(lenient);
				df[i].setTimeZone(tz);
			}
			formats.put(id, df);
		}
		return df;
	}
	
	
	
	
	
	public static DateFormat[] getTimeFormats(Locale locale,TimeZone tz,boolean lenient) {
		String id="t-"+locale.hashCode()+"-"+tz.getID()+"-"+lenient;
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
				 df[i].setTimeZone(tz);
			}
			formats.put(id, df);
		}
		return df;
	}
}
