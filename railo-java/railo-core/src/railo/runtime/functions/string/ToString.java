/**
 * Implements the Cold Fusion Function tostring
 */
package railo.runtime.functions.string;

import java.io.UnsupportedEncodingException;

import railo.commons.lang.StringUtil;
import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.op.Caster;

public final class ToString implements Function {
	public static String call(PageContext pc ) {
		return "";
	}
	public static String call(PageContext pc , Object object) throws PageException {
		String encoding = pc.getResponse().getCharacterEncoding();
		if(StringUtil.isEmpty(encoding,true))encoding="UTF-8";
		return call(pc,object,encoding);
	}
	public static String call(PageContext pc , Object object, String encoding) throws PageException {
		if(object instanceof byte[]){
			if(encoding!=null) {
        		try {
					return new String((byte[])object,encoding);
				} 
        		catch (UnsupportedEncodingException e) {}
        	}
        	return new String((byte[])object);
		}
		return Caster.toString(object);
	}
}