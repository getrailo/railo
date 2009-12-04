package coldfusion.runtime;

import railo.runtime.CFMLFactoryImpl;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.type.Struct;
import railo.runtime.type.scope.ScopeContext;

public class SessionTracker {
	
	public static int getSessionCount(){
		PageContext pc = ThreadLocalPageContext.get();
		ScopeContext sc = ((CFMLFactoryImpl)pc.getCFMLFactory()).getScopeContext();
		return sc.getSessionCount(pc);
	}
	
	public static Struct getSessionCollection(String appName){
		PageContext pc = ThreadLocalPageContext.get();
		ScopeContext sc = ((CFMLFactoryImpl)pc.getCFMLFactory()).getScopeContext();
		return sc.getAllSessionScopes(pc.getConfig(), appName);
	}
	
	
	/*
	public static coldfusion.runtime.SessionScope getSession(java.lang.String,java.lang.String)
	public static coldfusion.runtime.SessionScope getSession(java.lang.String)
	public static coldfusion.runtime.SessionScope getSession(javax.servlet.http.HttpSession,java.lang.String)
	public static coldfusion.runtime.SessionScope getSession(java.lang.String,java.lang.String,java.lang.String)
	public static coldfusion.runtime.SessionScope createSession(java.lang.String,java.lang.String)
	public static coldfusion.runtime.SessionScope createSession(java.lang.String,java.lang.String,java.lang.String)
	public static coldfusion.runtime.SessionScope createSession(javax.servlet.http.HttpSession,java.lang.String)
	public static void cleanUp(java.lang.String,java.lang.String,java.lang.String)
	public static void cleanUp(javax.servlet.http.HttpSession,java.lang.String)
	public static void cleanUp(java.lang.String,java.lang.String)
	public static java.util.Enumeration getSessionKeys()
	public static java.util.Hashtable getMSessionPool()
	public static coldfusion.runtime.AppSessionCollection getSessionCollection(java.lang.String)
	*/


}
