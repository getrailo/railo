package railo.commons.i18n;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.collections.map.ReferenceMap;

import railo.commons.date.TimeZoneConstants;
import railo.commons.io.IOUtil;
import railo.commons.io.res.Resource;
import railo.commons.lang.StringUtil;
import railo.runtime.config.Config;
import railo.runtime.engine.ThreadLocalPageContext;

public class FormatUtil {

	public static final short FORMAT_TYPE_DATE=1;
	public static final short FORMAT_TYPE_TIME=2;
	public static final short FORMAT_TYPE_DATE_TIME=3;
	public static final short FORMAT_TYPE_DATE_ALL=4;
 
	private final static Map<String,DateFormat[]> formats=new ReferenceMap(ReferenceMap.SOFT,ReferenceMap.SOFT);
	
	public static DateFormat[] getDateTimeFormats(Locale locale,TimeZone tz,boolean lenient) {

		String id="dt-"+locale.hashCode()+"-"+tz.getID()+"-"+lenient;
		DateFormat[] df=formats.get(id);
		if(df==null) {
			List<DateFormat> list=new ArrayList<DateFormat>();
			list.add(DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.FULL,locale));
			list.add(DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.LONG,locale));
			list.add(DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.MEDIUM,locale));
			list.add(DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.SHORT,locale));

			list.add(DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.FULL,locale));
			list.add(DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.LONG,locale));
			list.add(DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.MEDIUM,locale));
			list.add(DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.SHORT,locale));

			list.add(DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.FULL,locale));
			list.add(DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.LONG,locale));
			list.add(DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM,locale));
			list.add(DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT,locale));

			list.add(DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.FULL,locale));
			list.add(DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.LONG,locale));
			list.add(DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.MEDIUM,locale));
			list.add(DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT,locale));
			add24(list, locale);
			addCustom(list, locale, FORMAT_TYPE_DATE_TIME);
			df=list.toArray(new DateFormat[list.size()]);
			
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
		DateFormat[] df= formats.get(id);
		if(df==null) {
			List<DateFormat> list=new ArrayList<DateFormat>();
			list.add(DateFormat.getDateInstance(DateFormat.FULL,locale));
			list.add(DateFormat.getDateInstance(DateFormat.LONG,locale));
			list.add(DateFormat.getDateInstance(DateFormat.MEDIUM,locale));
			list.add(DateFormat.getDateInstance(DateFormat.SHORT,locale));
			addCustom(list, locale, FORMAT_TYPE_DATE);
			df=list.toArray(new DateFormat[list.size()]);
			
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
		DateFormat[] df= formats.get(id);
		if(df==null) {
			List<DateFormat> list=new ArrayList<DateFormat>();
			list.add(DateFormat.getTimeInstance(DateFormat.FULL,locale));
			list.add(DateFormat.getTimeInstance(DateFormat.LONG,locale));
			list.add(DateFormat.getTimeInstance(DateFormat.MEDIUM,locale));
			list.add(DateFormat.getTimeInstance(DateFormat.SHORT,locale));
			add24(list, locale);
			addCustom(list, locale, FORMAT_TYPE_TIME);
			df=list.toArray(new DateFormat[list.size()]);
			
			for(int i=0;i<df.length;i++){
				 df[i].setLenient(lenient);
				 df[i].setTimeZone(tz);
			}
			formats.put(id, df);
		}
		return df;
	}
	

	private static void add24(List<DateFormat> list,Locale locale) {
		
		// if found h:mm:ss a add H:mm:ss ...
		String p;
		int index;
		SimpleDateFormat sdf;
		DateFormat[] df=list.toArray(new DateFormat[list.size()]);
		for(int i=0;i<df.length;i++){
			if(df[i] instanceof SimpleDateFormat) {
				p=((SimpleDateFormat) df[i]).toPattern()+"";
				
				if(check(list,p,locale,"hh:mm:ss a","HH:mm:ss")) continue;
				if(check(list,p,locale,"h:mm:ss a","H:mm:ss")) continue;
				if(check(list,p,locale,"hh:mm a","HH:mm")) continue;
				if(check(list,p,locale,"h:mm a","H:mm")) continue;
				
				if(check(list,p,locale,"hh:mm:ssa","HH:mm:ss")) continue;
				if(check(list,p,locale,"h:mm:ssa","H:mm:ss")) continue;
				if(check(list,p,locale,"hh:mma","HH:mm")) continue;
				if(check(list,p,locale,"h:mma","H:mm")) continue;
				
				//if(check(list,p,locale,"HH:mm:ss","hh:mm:ss a")) continue;
				//if(check(list,p,locale,"H:mm:ss","h:mm:ss a")) continue;
				//if(check(list,p,locale,"HH:mm","hh:mm a")) continue;
				//if(check(list,p,locale,"H:mm","h:mm a")) continue;
			}
		}
	}
	
	private static boolean check(List<DateFormat> list, String p,Locale locale, String from, String to) {
		int index = p.indexOf(from);
		if(index!=-1) {
			p=StringUtil.replace(p, from, to, true);
			SimpleDateFormat sdf = new SimpleDateFormat(p,locale);
			if(!list.contains(sdf))list.add(sdf);
			return true;
		}
		return false;
	}


	private static void addCustom(List<DateFormat> list,Locale locale,short formatType) {
		// get custom formats from file
		Config config = ThreadLocalPageContext.getConfig();
		Resource dir=config.getConfigDir().getRealResource("locales");
		if(dir.isDirectory()) {
			String appendix="-datetime";
			if(formatType==FORMAT_TYPE_DATE)appendix="-date";
			if(formatType==FORMAT_TYPE_TIME)appendix="-time";
			
			Resource file = dir.getRealResource(locale.getLanguage()+"-"+locale.getCountry()+appendix+".df");
			if(file.isFile()) {
				try {
					String content=IOUtil.toString(file, (Charset)null);
					String[] arr = railo.runtime.type.util.ListUtil.listToStringArray(content, '\n');
					String line;
					SimpleDateFormat sdf;
					for(int i=0;i<arr.length;i++){
						line=arr[i].trim();
						if(StringUtil.isEmpty(line)) continue;
						sdf = new SimpleDateFormat(line,locale);
						if(!list.contains(sdf))list.add(sdf);
					}
					
				} 
				catch (Throwable t) {}
			}
		}
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
		DateFormat[] df= formats.get(id);
		if(df==null) {
			df= new SimpleDateFormat[]{
					  new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy",Locale.ENGLISH)
					 ,new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss a zzz",Locale.ENGLISH)
					 ,new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a",Locale.ENGLISH)	 
					 ,new SimpleDateFormat("MMM dd, yyyy HH:mm:ss",Locale.ENGLISH)	 
					 ,new SimpleDateFormat("MMMM d yyyy HH:mm:ssZ",Locale.ENGLISH)
					 ,new SimpleDateFormat("MMMM d yyyy HH:mm:ss",Locale.ENGLISH)
					 ,new SimpleDateFormat("MMMM d yyyy HH:mm",Locale.ENGLISH)
					 ,new SimpleDateFormat("EEE, MMM dd, yyyy HH:mm:ssZ",Locale.ENGLISH)
					 ,new SimpleDateFormat("EEE, MMM dd, yyyy HH:mm:ss",Locale.ENGLISH)
				     ,new SimpleDateFormat("EEEE, MMMM dd, yyyy H:mm:ss a zzz",Locale.ENGLISH)
					 ,new SimpleDateFormat("dd-MMM-yy HH:mm a",Locale.ENGLISH)
					 ,new SimpleDateFormat("dd-MMMM-yy HH:mm a",Locale.ENGLISH)
					  ,new SimpleDateFormat("EE, dd-MMM-yyyy HH:mm:ss zz",Locale.ENGLISH)
					  ,new SimpleDateFormat("EE, dd MMM yyyy HH:mm:ss zz",Locale.ENGLISH)
					,new SimpleDateFormat("EEE d, MMM yyyy HH:mm:ss zz",Locale.ENGLISH)
					 ,new SimpleDateFormat("dd-MMM-yyyy",Locale.ENGLISH)
					 ,new SimpleDateFormat("MMMM, dd yyyy HH:mm:ssZ",Locale.ENGLISH)
					 ,new SimpleDateFormat("MMMM, dd yyyy HH:mm:ss",Locale.ENGLISH)
					 ,new SimpleDateFormat("yyyy/MM/dd HH:mm:ss zz",Locale.ENGLISH)
					 ,new SimpleDateFormat("dd MMM yyyy HH:mm:ss zz",Locale.ENGLISH)
					 ,new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'ZZ (z)",Locale.ENGLISH)
					 ,new SimpleDateFormat("dd MMM, yyyy HH:mm:ss",Locale.ENGLISH)
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

	public static DateFormat[] getFormats(Locale locale,TimeZone tz,boolean lenient, short formatType) {
		if(FORMAT_TYPE_DATE_TIME==formatType)return getDateTimeFormats(locale,TimeZoneConstants.GMT,true);
		if(FORMAT_TYPE_DATE==formatType)return getDateFormats(locale,TimeZoneConstants.GMT,true);
		if(FORMAT_TYPE_TIME==formatType)return getTimeFormats(locale,TimeZoneConstants.GMT,true);
		
		DateFormat[] dt = getDateTimeFormats(locale,TimeZoneConstants.GMT,true);
		DateFormat[] d = getDateFormats(locale,TimeZoneConstants.GMT,true);
		DateFormat[] t = getTimeFormats(locale,TimeZoneConstants.GMT,true);
		
		DateFormat[] all=new DateFormat[dt.length+d.length+t.length];
		for(int i=0;i<dt.length;i++){
			all[i]=dt[i];
		}
		for(int i=0;i<d.length;i++){
			all[i+dt.length]=d[i];
		}
		for(int i=0;i<t.length;i++){
			all[i+dt.length+d.length]=t[i];
		}
		return getDateTimeFormats(locale,TimeZoneConstants.GMT,true);
	}

	public static String[] getSupportedPatterns(Locale locale, short formatType) {
		DateFormat[] _formats = getFormats(locale,TimeZoneConstants.GMT,true,formatType);
		String[] patterns=new String[_formats.length];
		for(int i=0;i<_formats.length;i++){
			if(!(_formats[i] instanceof SimpleDateFormat))return null; // all or nothing
			patterns[i]=((SimpleDateFormat)_formats[i]).toPattern();
		}
		
		return patterns;
	}
	
	
	
}
