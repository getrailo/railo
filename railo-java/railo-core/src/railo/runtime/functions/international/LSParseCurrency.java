/**
 * Implements the Cold Fusion Function lsparsecurrency
 */
package railo.runtime.functions.international;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.WeakHashMap;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.i18n.LocaleFactory;
import railo.runtime.op.Caster;


public final class LSParseCurrency implements Function {
	
	private static WeakHashMap currFormatter=new WeakHashMap();
	private static WeakHashMap numbFormatter=new WeakHashMap();

	public static String call(PageContext pc , String string) throws PageException {
		return Caster.toString(toDoubleValue(pc.getLocale(),string));
	}
	public static String call(PageContext pc , String string,String strLocale) throws PageException {
		return Caster.toString(toDoubleValue(LocaleFactory.getLocale(strLocale),string));
	}
	
	public static synchronized double toDoubleValue(Locale locale,String str) throws PageException {
		
		NumberFormat cnf=getCurrencyInstance(locale);
		cnf.setParseIntegerOnly(false);
		try {
			return cnf.parse(str).doubleValue();
		} catch (ParseException e) {
			
			NumberFormat nf=getInstance(locale);
			str=str.replaceFirst(cnf.getCurrency().getCurrencyCode(),"").trim();
			try {
				return nf.parse(str).doubleValue();
			} catch (ParseException e1) {
				throw Caster.toPageException(e1);
			}
		}
	}
	
	private static NumberFormat getInstance(Locale locale) {
		Object o=numbFormatter.get(locale);
		if(o!=null) return (NumberFormat) o;
		
		NumberFormat nf=NumberFormat.getInstance(locale);
		numbFormatter.put(locale,nf);
		return nf;
	}

	private static NumberFormat getCurrencyInstance(Locale locale) {
		Object o = currFormatter.get(locale);
		if(o!=null) return (NumberFormat) o;
		
		NumberFormat nf=NumberFormat.getCurrencyInstance(locale);
		currFormatter.put(locale,nf);
		return nf;
	}
}