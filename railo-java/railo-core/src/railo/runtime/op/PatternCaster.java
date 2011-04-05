package railo.runtime.op;

import railo.runtime.exp.PageException;
import railo.runtime.op.validators.ValidateCreditCard;

/**
* this Caster cast to types that are not CFML types, most are string that must match a specific pattern
 */
public final class PatternCaster {

    public static Object toCreditCard(String str) throws PageException {
		return ValidateCreditCard.toCreditcard(str);
	}
    
    public static Object toCreditCard(String str, String defaultValue) {
    	return ValidateCreditCard.toCreditcard(str,defaultValue);
	}
}
