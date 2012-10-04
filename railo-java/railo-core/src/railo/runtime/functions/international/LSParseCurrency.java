/**
 * Implements the CFML Function lsparsecurrency
 */
package railo.runtime.functions.international;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.WeakHashMap;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.i18n.LocaleFactory;
import railo.runtime.op.Caster;


public final class LSParseCurrency implements Function {

	private static final long serialVersionUID = -7023441119083818436L;
	private static WeakHashMap currFormatter=new WeakHashMap();
	private static WeakHashMap numbFormatter=new WeakHashMap();

	public static String call(PageContext pc , String string) throws PageException {
		return Caster.toString(toDoubleValue(pc.getLocale(),string,false));
	}
	public static String call(PageContext pc , String string,String strLocale) throws PageException {
		return Caster.toString(toDoubleValue(strLocale==null?pc.getLocale():LocaleFactory.getLocale(strLocale),string,false));
	}

	public static synchronized double toDoubleValue(Locale locale,String str) throws PageException {
		return toDoubleValue(locale, str, false);
	}
	
	public static synchronized double toDoubleValue(Locale locale,String str, boolean strict) throws PageException {
		str=str.trim();
		NumberFormat cnf=getCurrencyInstance(locale);
		cnf.setParseIntegerOnly(false);
		try {
			return cnf.parse(str).doubleValue();
		} 
		catch (ParseException e) {
			String stripped=str.replaceFirst(cnf.getCurrency().getCurrencyCode(),"").trim();
			NumberFormat nf=getInstance(locale);
			
			ParsePosition pp = new ParsePosition(0);
			double d = nf.parse(stripped,pp).doubleValue();
			if (pp.getIndex() == 0 || (strict && stripped.length()!=pp.getIndex())) 
	            throw new ExpressionException("Unparseable number [" + str + "]");
			
			return d;
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