package railo.runtime.functions.csrf;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;

public class CSRFVerifyToken implements Function {
	
	private static final long serialVersionUID = 4173843576558490732L;
	
	public static boolean call(PageContext pc, String token) throws PageException {
		return call(pc, token, null);
	}
	public static boolean call(PageContext pc, String token, String key) throws PageException {
		if(key==null) key="";
		
		return CSRFGenerateToken.getStorageScope(pc).verifyToken(token, key);
	}
}
