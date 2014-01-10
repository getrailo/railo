package coldfusion.runtime;

import railo.runtime.CFMLFactoryImpl;
import railo.runtime.PageContext;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.type.Struct;
import railo.runtime.type.scope.ScopeContext;

public class SessionTracker {

	/*
	 * prepare to restrict access to this class
	 * private String webAdminPassword;

	public SessionTracker(){
		throw new RuntimeException("for Security reasons we have restricted the usage of the class SessionTracker, " +
				"you now can only use this class by defining the web admin password, when you call the constructor of this class, " +
				"so instead of just doing the following: SessionTracker=createObject('java','coldfusion.runtime.SessionTracker') " +
				"and then use it, you have to call the constructor with the web admin password: SessionTracker=createObject('java','coldfusion.runtime.SessionTracker').init('yourwebadminpassword')");
		
		
	}
	public SessionTracker(String webAdminPassword) throws SAXException, IOException{
		this.webAdminPassword=webAdminPassword;
		
		PageContext pc = ThreadLocalPageContext.get();
		//ConfigWebAdmin admin = ConfigWebAdmin.newInstance((ConfigWebImpl)pc.getConfig(),webAdminPassword);
		// TODO verify password
	}*/
	
	
	public static int getSessionCount(){
		PageContext pc = ThreadLocalPageContext.get();
		ScopeContext sc = ((CFMLFactoryImpl)pc.getCFMLFactory()).getScopeContext();
		return sc.getSessionCount(pc);
	}
	
	public static Struct getSessionCollection(String appName){
		PageContext pc = ThreadLocalPageContext.get();
		ScopeContext sc = ((CFMLFactoryImpl)pc.getCFMLFactory()).getScopeContext();
		return sc.getAllSessionScopes(appName);
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
