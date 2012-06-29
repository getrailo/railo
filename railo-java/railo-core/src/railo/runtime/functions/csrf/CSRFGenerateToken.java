package railo.runtime.functions.csrf;

import railo.runtime.PageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.ext.function.Function;
import railo.runtime.type.scope.Session;
import railo.runtime.type.scope.storage.StorageScope;

public class CSRFGenerateToken implements Function {
	
	private static final long serialVersionUID = -2411153524245619987L;

	public static String call(PageContext pc) throws PageException {
		return call(pc, null, false);
	}
	public static String call(PageContext pc, String key) throws PageException {
		return call(pc, key, false);
	}
	
	public static String call(PageContext pc, String key, boolean forceNew) throws PageException {
		if(key==null) key="";
		
		return getStorageScope(pc).generateToken(key, forceNew);
	}
	public static StorageScope getStorageScope(PageContext pc) throws PageException {
		Session session = pc.sessionScope();
		if(!(session instanceof StorageScope))
			throw new ExpressionException("this function only works with CF Sessions");
		return (StorageScope) session;
	}
}
