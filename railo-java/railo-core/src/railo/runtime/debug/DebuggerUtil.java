package railo.runtime.debug;

import java.util.HashSet;
import java.util.Set;

import railo.runtime.PageContext;
import railo.runtime.config.ConfigImpl;
import railo.runtime.config.ConfigWebImpl;
import railo.runtime.exp.PageException;
import railo.runtime.type.Query;
import railo.runtime.type.QueryImpl;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.util.KeyConstants;

public class DebuggerUtil {

	
	public Struct pointOutClosuresInPersistentScopes(PageContext pc){
		Struct sct=new StructImpl();
		Set<Object> done=new HashSet<Object>();
		//Application Scope
		try {
			sct.set(
					KeyConstants._application, 
					_pointOutClosuresInPersistentScopes(pc,pc.applicationScope(),done));
		} 
		catch (PageException e) {}
		
		//Session Scope
		try {
			sct.set(
					KeyConstants._application, 
					_pointOutClosuresInPersistentScopes(pc,pc.sessionScope(),done));
		} 
		catch (PageException e) {}
		
		//Server Scope
		try {
			sct.set(
					KeyConstants._application, 
					_pointOutClosuresInPersistentScopes(pc,pc.serverScope(),done));
		} 
		catch (PageException e) {}
		
		
		
		return null;
	}

	private Struct _pointOutClosuresInPersistentScopes(PageContext pc, Struct sct, Set<Object> done) {
		
		return null;
	}
	
	public static boolean debugQueryUsage(PageContext pageContext, Query query) {
		if(pageContext.getConfig().debug() && query instanceof QueryImpl) {
			if(((ConfigWebImpl)pageContext.getConfig()).hasDebugOptions(ConfigImpl.DEBUG_QUERY_USAGE)){
				((QueryImpl)query).enableShowQueryUsage();
				return true;
			}
		}
		return false;
	}
}
