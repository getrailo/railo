/**
 * Implements the CFML Function lscurrencyformat
 */
package railo.runtime.functions.international;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public final class LSCurrencyFormat implements Function {

	private static final long serialVersionUID = -3173006221339130136L;

	public static String call(PageContext pc , Object number) throws PageException {
		return format(toDouble(number), "local", pc.getLocale());
	}
	public static String call(PageContext pc , Object number, String type) throws PageException {
		return format( toDouble(number), type, pc.getLocale());
	}
	public static String call(PageContext pc , Object number, String type,Locale locale) throws PageException {
		return format(toDouble(number), type, locale==null?pc.getLocale():locale);
	}
	
	public static String format( double number, String type,Locale locale) throws ExpressionException {
		if(StringUtil.isEmpty(type)) return local(locale,number);
		type=type.trim().toLowerCase();
		
		if(type.equals("none")) 				return none(locale,number);
		else if(type.equals("local"))			return local(locale,number);
		else if(type.equals("international"))	return international(locale,number);
		else {
			throw new ExpressionException("invalid type for function lsCurrencyFormat","types are: local, international or none");
		}
		
	}

	public static String none(Locale locale, double number) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
        return StringUtil.replace(nf.format(number),nf.getCurrency().getSymbol(locale),"",false).trim();
	}
	
	public static String local(Locale locale, double number) {
		return NumberFormat.getCurrencyInstance(locale).format(number);	
	}
	
	public static String international(Locale locale, double number) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
        Currency currency = nf.getCurrency();
        
        String str = StringUtil.replace(
        			nf.format(number),
        			nf.getCurrency().getSymbol(locale),
        			"",false).trim();
        
        return currency.getCurrencyCode()+" "+str;
        
        /*return StringUtil.replace(
        			nf.format(number),
        			nf.getCurrency().getSymbol(locale),
        			currency.getCurrencyCode(),false).trim();*/
        
	}
	
	public static double toDouble(Object number) throws PageException {
		if(number instanceof String && ((String)number).length()==0) return 0d;
		return Caster.toDoubleValue(number);
	}
    
    /*private static String removeCurrencyFromPatterns(String pattern) {
        return pattern.replace('ﾤ',' ');
    }*/
	
	
	/*public static void main(String[] args) throws Exception {

        print.ln(international(Locale.US,123456));
        print.ln(local(Locale.US,123456));
        print.ln(none(Locale.US,123456));
        
	}*/
}