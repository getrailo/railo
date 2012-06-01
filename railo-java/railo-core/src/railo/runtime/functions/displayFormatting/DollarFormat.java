/**
 * Implements the CFML Function dollarformat
 */
package railo.runtime.functions.displayFormatting;

import java.util.Locale;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.CasterException;
import railo.runtime.ext.function.Function;
import railo.runtime.functions.international.LSCurrencyFormat;
import railo.runtime.op.Caster;

public final class DollarFormat implements Function {
	public static String call(PageContext pc , String strDollar) throws CasterException {
		if(StringUtil.isEmpty(strDollar)) strDollar="0";//"$0,00";
		return LSCurrencyFormat.local(Locale.US, Caster.toDoubleValue(strDollar));
		/*try {
			return "$"+Caster.toDecimal(Caster.toDoubleValue(strDollar),',','.');
		} catch (PageException e) {
			throw new FunctionException(pc,"dollarFormat",1,"number",e.getMessage());
		}*/
	}
}