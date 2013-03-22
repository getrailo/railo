package railo.commons.i18n;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.WeakHashMap;

/**
 * 
 */
public final class DateFormatPool {
    
    private final static Map data=new WeakHashMap();
    
    /**
     * pool for formated dates
     * @param locale
     * @param timeZone
     * @param pattern
     * @param date
     * @return date matching given values
     */
    public static synchronized String format(Locale locale, TimeZone timeZone, String pattern,Date date) {
        String key=locale.toString()+'-'+timeZone.getID()+'-'+pattern;
        Object obj=data.get(key);
        if(obj!=null) {
            return ((SimpleDateFormat)obj).format(date);
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern,locale);
        sdf.setTimeZone(timeZone);
        data.put(key,sdf);
        return sdf.format(date);
    }

    /**
     * pool for formated dates
     * @param locale
     * @param pattern
     * @param date
     * @return date matching given values
     */
    public static synchronized String format(Locale locale, String pattern,Date date) {
        String key=locale.toString()+'-'+pattern;
        
        Object obj=data.get(key);
        if(obj!=null) {
            return ((SimpleDateFormat)obj).format(date);
        }//print.ln(key);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern,locale);
        data.put(key,sdf);
        return sdf.format(date);
    }

    /**
     * pool for formated dates
     * @param pattern
     * @param date
     * @return date matching given values
     */
    public static synchronized String format(String pattern,Date date) {
        Object obj=data.get(pattern);
        if(obj!=null) {
            return ((SimpleDateFormat)obj).format(date);
        }//print.ln(pattern);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        data.put(pattern,sdf);
        return sdf.format(date);
    }
    
}