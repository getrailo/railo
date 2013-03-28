package railo.commons.io;

import java.nio.charset.Charset;

import railo.runtime.exp.PageException;
import railo.runtime.op.Caster;

public class CharsetUtil {
	public static final Charset UTF8;
	public static final Charset ISO88591;
	
	static {
		UTF8=toCharset("utf-8",null);
		ISO88591=toCharset("iso-8859-1",null);
	}

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
