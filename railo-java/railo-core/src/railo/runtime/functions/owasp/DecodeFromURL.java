package railo.runtime.functions.owasp;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public class DecodeFromURL implements Function{

	private static final long serialVersionUID = -7726736527978825663L;

	public static String call(PageContext pc , String item) throws PageException  {
		return ESAPIDecode.decode(item, ESAPIDecode.DEC_URL);
	}
}