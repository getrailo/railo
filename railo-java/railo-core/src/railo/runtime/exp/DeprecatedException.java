package railo.runtime.exp;

import railo.commons.lang.StringUtil;


/**
 * 
 */
public final class DeprecatedException extends ApplicationException {

	public DeprecatedException(String tagName, String attrName) {
		super(StringUtil.isEmpty(attrName)?
				"the tag ["+tagName+"] is longer supported":
				"the attribute ["+attrName+"] of the tag ["+tagName+"] is no longer supported");
	}
	public DeprecatedException(String msg) {
		super(msg);
	}
}
