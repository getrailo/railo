
package railo.runtime.i18n;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import railo.runtime.exp.ExpressionException;
import railo.runtime.type.List;


/**
 * Factory to create Locales by Cold Fusion rules
 */
public final class LocaleFactory {
	//private static Pattern localePattern = Pattern.compile("^\\s*([^\\s\\(]+)\\s*(\\(\\s*([^\\s\\)]+)\\s*\\))?\\s*$");
	private static Pattern localePattern = Pattern.compile("^\\s*([^\\(]+)\\s*(\\(\\s*([^\\)]+)\\s*\\))?\\s*$");
	private static Pattern localePattern2 = Pattern.compile("^([a-z]{2})_([a-z]{2,3})$");
	private static Pattern localePattern3 = Pattern.compile("^([a-z]{2})_([a-z]{2,3})_([a-z]{2,})$");
	
	private static Map locales=new LinkedHashMap();
	private static Map localeAlias=new LinkedHashMap();
	
	private static String list;
	static {
		Locale[] ls = Locale.getAvailableLocales();
		
		
		String key;
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<ls.length;i++) {
			key=ls[i].getDisplayName(Locale.US).toLowerCase();
			locales.put(key, ls[i]);
			if(key.indexOf(',')!=-1){
				key=ls[i].toString();
				//print.ln(key);
				
			}
			if(i>0)sb.append(",");
			sb.append(key);
		}
		list=sb.toString();
		
		
		
		localeAlias.put("chinese (china)", Locale.CHINA);
		localeAlias.put("chinese (hong kong)",new Locale("zh","HK"));
        localeAlias.put("chinese (taiwan)",new Locale("zho","TWN"));
        localeAlias.put("dutch (belgian)",new Locale("nl","BE"));
        localeAlias.put("dutch (belgium)",new Locale("nl","BE"));
        localeAlias.put("dutch (standard)",new Locale("nl","NL"));
        localeAlias.put("english (australian)",new Locale("en","AU"));
        localeAlias.put("english (australia)",new Locale("en","AU"));
        localeAlias.put("english (canadian)",new Locale("en","CA"));
        localeAlias.put("english (canadia)",new Locale("en","CA"));
        localeAlias.put("english (new zealand)",new Locale("en","NZ"));
        localeAlias.put("english (uk)",new Locale("en","GB"));
        localeAlias.put("english (united kingdom)",new Locale("en","GB"));
        localeAlias.put("english (us)",new Locale("en","US"));
        localeAlias.put("french (belgium)",new Locale("fr","BE"));
        localeAlias.put("french (belgian)",new Locale("fr","BE"));
		localeAlias.put("french (canadian)",new Locale("fr","CA"));
		localeAlias.put("french (canadia)",new Locale("fr","CA"));
        localeAlias.put("french (standard)",new Locale("fr","FRA"));
        localeAlias.put("french (swiss)",new Locale("fr","CH"));
        localeAlias.put("german (austrian)",new Locale("de","AT"));
        localeAlias.put("german (austria)",new Locale("de","AT"));
        localeAlias.put("german (standard)",new Locale("de","DE"));
        localeAlias.put("german (swiss)",new Locale("de","CH"));
        localeAlias.put("italian (standard)",new Locale("it","IT"));
        localeAlias.put("italian (swiss)",new Locale("it","CH"));
        localeAlias.put("japanese",new Locale("ja","JP"));  
        localeAlias.put("korean",Locale.KOREAN);
        localeAlias.put("norwegian (bokmal)",new Locale("no","NO"));
        localeAlias.put("norwegian (nynorsk)",new Locale("no","NO"));
        localeAlias.put("portuguese (brazilian)",new Locale("pt","BR"));
        localeAlias.put("portuguese (brazil)",new Locale("pt","BR"));
		localeAlias.put("portuguese (standard)",new Locale("pt","PT"));
        localeAlias.put("rhaeto-romance (swiss)",new Locale("rm","CH"));
        locales.put("rhaeto-romance (swiss)",new Locale("rm","CH"));
        localeAlias.put("spanish (modern)",new Locale("es","ES"));
        localeAlias.put("spanish (standard)",new Locale("es","ES"));
        localeAlias.put("swedish",new Locale("sv","SE"));
        
	}
	
	private LocaleFactory(){}
	
    /**
     * @param strLocale
     * @param defaultValue 
     * @return return locale match to String
     */
    public static Locale getLocale(String strLocale, Locale defaultValue) {
        try {
            return getLocale(strLocale);
        } catch (ExpressionException e) {
            return defaultValue;
        }
    }
	
	
	/**
	 * @param strLocale
	 * @return return locale match to String
	 * @throws ExpressionException
	 */
	public static Locale getLocale(String strLocale) throws ExpressionException {
		String strLocaleLC = strLocale.toLowerCase().trim();
		Locale l=(Locale) locales.get(strLocaleLC);
		if(l!=null) return l;
		
		l=(Locale) localeAlias.get(strLocaleLC);
		if(l!=null) return l;

		Matcher matcher = localePattern2.matcher(strLocaleLC);
		if(matcher.find()) {
			int len=matcher.groupCount();
			if(len==2) {
				String lang=matcher.group(1).trim();
				String country=matcher.group(2).trim();
				Locale locale=new Locale(lang,country);
				
				try {
					locale.getISO3Language();
					localeAlias.put(strLocaleLC, locale);
					return locale;
				}
				catch(Exception e) {}
			}
		}
		
		matcher = localePattern3.matcher(strLocaleLC);
		if(matcher.find()) {
			int len=matcher.groupCount();
			if(len==3) {
				String lang=matcher.group(1).trim();
				String country=matcher.group(2).trim();
				String variant=matcher.group(3).trim();
				Locale locale=new Locale(lang,country,variant);
				
				try {
					locale.getISO3Language();
					localeAlias.put(strLocaleLC, locale);
					return locale;
				}
				catch(Exception e) {}
			}
		}
			
		
		matcher=localePattern.matcher(strLocaleLC);
		if(matcher.find()) {
			int len=matcher.groupCount();

			if(len==3) {
				
				String lang=matcher.group(1).trim();
				String country=matcher.group(3);
				if(country!=null)country=country.trim();
				 Object objLocale=null;
				 
				if(country!=null) objLocale=locales.get(lang.toLowerCase()+" ("+(country.toLowerCase())+")");
				else objLocale=locales.get(lang.toLowerCase());
				if(objLocale!=null)return (Locale)objLocale;
				
				Locale locale;
				if(country!=null)locale=new Locale(lang.toUpperCase(),country.toLowerCase());
				else locale=new Locale(lang);
				
				try {
					locale.getISO3Language();
				}
				catch(Exception e) {
					if(strLocale.indexOf('-')!=-1) return getLocale(strLocale.replace('-', '_'));
					throw new ExpressionException("unsupported Locale ["+strLocale+"]","supported Locales are:"+getSupportedLocalesAsString());
				}
				localeAlias.put(strLocaleLC, locale);
				return locale;

			}
		}
		

		throw new ExpressionException("can't cast value ("+strLocale+") to a Locale","supported Locales are:"+getSupportedLocalesAsString());
	}
	

	private static String getSupportedLocalesAsString() {
		//StringBuffer sb=new StringBuffer();
	    // TODO chnge from ArryObject to string
		return List.arrayToList((String[])locales.keySet().toArray(new String[locales.size()]),",");
		
	}

	/**
	 * @param locale
	 * @return cast a Locale to a String 
	 */
	public static String toString(Locale locale) {
		String lang=locale.getLanguage();
		String country=locale.getCountry();
		
		synchronized(localeAlias){
			Iterator it = localeAlias.entrySet().iterator();
			Map.Entry entry;
			while(it.hasNext()) {
				entry=(Entry) it.next();
				//Object qkey=it.next();
				Locale curr=(Locale) entry.getValue();
				if(lang.equals(curr.getLanguage()) && country.equals(curr.getCountry())) {
					return entry.getKey().toString();
				}
			}
		}
		return locale.getDisplayName(Locale.ENGLISH);
	}
	
    /**
     * @return Returns the locales.
     */
    public static Map getLocales() {
        return locales;
    }
    public static String getLocaleList() {
        return list;
    }
}