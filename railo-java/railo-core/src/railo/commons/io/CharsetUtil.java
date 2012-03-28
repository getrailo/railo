package railo.commons.io;

import java.nio.charset.Charset;

import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class CharsetUtil {

	public static Charset toCharset(String charset) throws PageException {
		try{
			return Charset.forName(charset);
		}
		catch(Throwable t){
			throw Caster.toPageException(t);
		}
	}

	public static Charset toCharset(String charset,Charset defaultValue) {
		try{
			return Charset.forName(charset);
		}
		catch(Throwable t){
			return defaultValue;
		}
	}

}
