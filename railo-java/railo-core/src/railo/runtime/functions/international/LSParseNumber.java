package railo.runtime.functions.international;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.WeakHashMap;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.i18n.LocaleFactory;

/**
 * Implements the Cold Fusion Function lsparsecurrency
 */
public final class LSParseNumber implements Function {
	
	private static WeakHashMap whm=new WeakHashMap();

	public static double call(PageContext pc , String string) throws PageException {
		return toDoubleValue(pc.getLocale(),string);
	}
	
	public static double call(PageContext pc , String string,String strLocale) throws PageException {
		return toDoubleValue(LocaleFactory.getLocale(strLocale),string);
	}
	
	
	public synchronized static double toDoubleValue(Locale locale,String str) throws PageException {
		Object o=whm.get(locale);
		NumberFormat nf=null;
		if(o==null) {
			nf=NumberFormat.getInstance(locale);
			whm.put(locale,nf);
		}
		else {
			nf=(NumberFormat) o;
		}
		str=optimze(str.toCharArray());
		
		ParsePosition pp = new ParsePosition(0);
        Number result = nf.parse(str, pp);
		
        if (pp.getIndex() < str.length()) {
            throw new ExpressionException("can't parse number [" + str + "] against locale ["+LocaleFactory.toString(locale)+"]");
        }
        return result.doubleValue();
		
	}
	
	
	private static String optimze(char[] carr) {
		StringBuffer sb=new StringBuffer();
		char c;
		for(int i=0;i<carr.length;i++){
			c=carr[i];
			if(!Character.isWhitespace(c) && c!='+')sb.append(carr[i]);
		}
		
		return sb.toString();
	}

	
	
}