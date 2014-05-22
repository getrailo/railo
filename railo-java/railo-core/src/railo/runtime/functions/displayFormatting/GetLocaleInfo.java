/**
 * Implements the CFML Function formatbasen
 */
package railo.runtime.functions.displayFormatting;

import java.util.Locale;

import railo.runtime.PageContext;
import railo.runtime.ext.function.Function;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.KeyConstants;

public final class GetLocaleInfo implements Function {

	private static final long serialVersionUID = -4084704416496042957L;

	public static Struct call(PageContext pc) {
		return _call(pc,pc.getLocale(), pc.getLocale());
	}
	
	public static Struct call(PageContext pc , Locale locale) {
		return _call(pc,locale, locale);
	}
	
	public static Struct call(PageContext pc , Locale locale, Locale dspLocale) {
		return _call(pc,locale, dspLocale);
	}
	
	private static Struct _call(PageContext pc , Locale locale, Locale dspLocale) {
		if(locale==null) locale=pc.getLocale();
		if(dspLocale==null) dspLocale=locale;
		
		Struct sct=new StructImpl();
		Struct dsp=new StructImpl();
		sct.setEL(KeyConstants._display, dsp);
		dsp.setEL(KeyConstants._country, locale.getDisplayCountry(dspLocale));
		dsp.setEL(KeyConstants._language, locale.getDisplayLanguage(dspLocale));
		
		sct.setEL(KeyConstants._country, locale.getCountry());
		sct.setEL(KeyConstants._language, locale.getLanguage());
		
		sct.setEL(KeyConstants._name, locale.getDisplayName(dspLocale));
		sct.setEL(KeyConstants._script, locale.getDisplayScript(dspLocale));
		sct.setEL("variant", locale.getDisplayVariant(dspLocale));
		
		Struct iso=new StructImpl();
		sct.setEL("iso",iso);
		iso.setEL(KeyConstants._country, locale.getISO3Country());
		iso.setEL(KeyConstants._language, locale.getISO3Language());
		
		return sct;
	}

	
	
}