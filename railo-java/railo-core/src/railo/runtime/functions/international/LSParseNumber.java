package railo.runtime.functions.international;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.WeakHashMap;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

/**
 * Implements the Cold Fusion Function lsparsecurrency
 */
public final class LSParseNumber implements Function {
	
	private static WeakHashMap whm=new WeakHashMap();
	
	public static double call(PageContext pc , String string) throws PageException {
		return toDoubleValue(pc.getLocale(),string);
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
		try {
			return nf.parse(str).doubleValue();
		} catch (ParseException e) {
			throw Caster.toPageException(e);
		}
	}
}