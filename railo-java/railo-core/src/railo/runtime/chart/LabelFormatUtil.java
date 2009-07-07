package railo.runtime.chart;

import java.util.Locale;

import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.PageException;
import railo.runtime.functions.international.LSCurrencyFormat;
import railo.runtime.functions.international.LSDateFormat;
import railo.runtime.op.Caster;
import railo.runtime.type.dt.DateTime;

public class LabelFormatUtil {


	public static final int LABEL_FORMAT_NUMBER = 0;
	public static final int LABEL_FORMAT_CURRENCY = 1;
	public static final int LABEL_FORMAT_PERCENT = 2;
	public static final int LABEL_FORMAT_DATE = 3;

	public static String formatDate(double value) {
		DateTime d = Caster.toDate(Caster.toDouble(value),true,null,null);
		
		try {
			return LSDateFormat.call(ThreadLocalPageContext.get(), d);
		} catch (PageException e) {
		}
		return Caster.toString(d,null);
	}

	public static String formatNumber(double value) {
		return Caster.toString(value);
	}

	public static String formatPercent(double value) {
		return Caster.toIntValue(value*100)+" %";
	}

	public static String formatCurrency(double value) {
		PageContext pc = ThreadLocalPageContext.get();
		Locale locale=pc==null?Locale.US:pc.getLocale();
		return LSCurrencyFormat.local(locale, value);
	}

	public static String format(int labelFormat, double value) {
		switch(labelFormat) {
		case LABEL_FORMAT_CURRENCY:	return formatCurrency(value);
		case LABEL_FORMAT_DATE:		return formatDate(value);
		case LABEL_FORMAT_NUMBER:	return formatNumber(value);
		case LABEL_FORMAT_PERCENT:	return formatPercent(value);
		}
		return Caster.toString(value);
	}
}
