package railo.runtime.debug;

import java.util.HashSet;
import java.util.Set;

import railo.runtime.PageContext;
import railo.runtime.exp.PageException;
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
}
