package railo.commons.lang;


public class FormatUtil {
	
	/**
	 * convert given time in nanoseconds to a flaoting point number in milliseconds reduced to max 3 digits on the right site
	 * @param ns
	 * @return
	 */
	public static double formatNSAsMSDouble(long ns){
		if(ns>=100000000L) return (ns/1000000L);
		if(ns>=10000000L) return ((ns/100000L))/10D;
		if(ns>=1000000L) return ((ns/10000L))/100D;
		return ((ns/1000L))/1000D;
	}
}
