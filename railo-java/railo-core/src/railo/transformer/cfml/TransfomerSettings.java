package railo.transformer.cfml;

import railo.runtime.Mapping;
import railo.runtime.MappingImpl;


public class TransfomerSettings {
	private static final TransfomerSettings TRANS_SETTING_DOT_NOT_UPPER = new TransfomerSettings(true);
	private static final TransfomerSettings TRANS_SETTING_DOT_NOT_ORIGINAL = new TransfomerSettings(false);
	public final boolean dotNotationUpper;

	public TransfomerSettings(boolean dotNotationUpper) {
		this.dotNotationUpper = dotNotationUpper;
	}

	public static TransfomerSettings toSetting(Mapping mapping,Boolean dotNotationUpperCase) {
		if(dotNotationUpperCase!=null) 
			return dotNotationUpperCase.booleanValue()?TRANS_SETTING_DOT_NOT_UPPER:TRANS_SETTING_DOT_NOT_ORIGINAL;
		return ((MappingImpl)mapping).getDotNotationUpperCase()?TRANS_SETTING_DOT_NOT_UPPER:TRANS_SETTING_DOT_NOT_ORIGINAL;
	}
 
}
