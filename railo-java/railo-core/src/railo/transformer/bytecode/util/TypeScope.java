package railo.transformer.bytecode.util;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import railo.runtime.type.scope.Application;
import railo.runtime.type.scope.Argument;
import railo.runtime.type.scope.CGI;
import railo.runtime.type.scope.Client;
import railo.runtime.type.scope.Cluster;
import railo.runtime.type.scope.Cookie;
import railo.runtime.type.scope.Form;
import railo.runtime.type.scope.Local;
import railo.runtime.type.scope.Request;
import railo.runtime.type.scope.Scope;
import railo.runtime.type.scope.ScopeSupport;
import railo.runtime.type.scope.Server;
import railo.runtime.type.scope.Session;
import railo.runtime.type.scope.URL;
import railo.runtime.type.scope.Undefined;

public final class TypeScope {
	
	public final static Type SCOPE = Type.getType(Scope.class);
	public final static Type[] SCOPES = new Type[ScopeSupport.SCOPE_COUNT];
    static {
    	SCOPES[Scope.SCOPE_APPLICATION]=	Type.getType(Application.class);
    	SCOPES[Scope.SCOPE_ARGUMENTS]=		Type.getType(railo.runtime.type.scope.Argument.class);
    	SCOPES[Scope.SCOPE_CGI]=			Type.getType(CGI.class);
    	SCOPES[Scope.SCOPE_CLIENT]=			Type.getType(Client.class);
    	SCOPES[Scope.SCOPE_COOKIE]=			Type.getType(Cookie.class);
    	SCOPES[Scope.SCOPE_FORM]=			Type.getType(Form.class);
    	SCOPES[Scope.SCOPE_LOCAL]=			Type.getType(Local.class);
    	SCOPES[Scope.SCOPE_REQUEST]=		Type.getType(Request.class);
    	SCOPES[Scope.SCOPE_SERVER]=			Type.getType(Server.class);
    	SCOPES[Scope.SCOPE_SESSION]=		Type.getType(Session.class);
    	SCOPES[Scope.SCOPE_UNDEFINED]=		Type.getType(Undefined.class);
    	SCOPES[Scope.SCOPE_URL]=			Type.getType(URL.class);
    	SCOPES[Scope.SCOPE_VARIABLES]=		Types.VARIABLES;
    	SCOPES[Scope.SCOPE_CLUSTER]=		Type.getType(Cluster.class); 
    	SCOPES[ScopeSupport.SCOPE_VAR]=			SCOPES[Scope.SCOPE_LOCAL]; 
    }
    
	public final static Method[] METHODS = new Method[ScopeSupport.SCOPE_COUNT];
    static {
    	METHODS[Scope.SCOPE_APPLICATION]=	new Method("applicationScope",	SCOPES[Scope.SCOPE_APPLICATION],new Type[]{});
    	METHODS[Scope.SCOPE_ARGUMENTS]=		new Method("argumentsScope",	SCOPES[Scope.SCOPE_ARGUMENTS],new Type[]{});
    	METHODS[Scope.SCOPE_CGI]=			new Method("cgiScope",			SCOPES[Scope.SCOPE_CGI],new Type[]{});
    	METHODS[Scope.SCOPE_CLIENT]=		new Method("clientScope",		SCOPES[Scope.SCOPE_CLIENT],new Type[]{});
    	METHODS[Scope.SCOPE_COOKIE]=		new Method("cookieScope",		SCOPES[Scope.SCOPE_COOKIE],new Type[]{});
    	METHODS[Scope.SCOPE_FORM]=			new Method("formScope",			SCOPES[Scope.SCOPE_FORM],new Type[]{});
    	METHODS[Scope.SCOPE_LOCAL]=			new Method("localGet",			Types.OBJECT,new Type[]{});
    	METHODS[Scope.SCOPE_REQUEST]=		new Method("requestScope",		SCOPES[Scope.SCOPE_REQUEST],new Type[]{});
    	METHODS[Scope.SCOPE_SERVER]=		new Method("serverScope",		SCOPES[Scope.SCOPE_SERVER],new Type[]{});
    	METHODS[Scope.SCOPE_SESSION]=		new Method("sessionScope",		SCOPES[Scope.SCOPE_SESSION],new Type[]{});
    	METHODS[Scope.SCOPE_UNDEFINED]=		new Method("us",				SCOPES[Scope.SCOPE_UNDEFINED],new Type[]{});
    	METHODS[Scope.SCOPE_URL]=			new Method("urlScope",			SCOPES[Scope.SCOPE_URL],new Type[]{});
    	METHODS[Scope.SCOPE_VARIABLES]=		new Method("variablesScope",	SCOPES[Scope.SCOPE_VARIABLES],new Type[]{});
    	METHODS[Scope.SCOPE_CLUSTER]=		new Method("clusterScope",		SCOPES[Scope.SCOPE_CLUSTER],new Type[]{}); 
    	METHODS[ScopeSupport.SCOPE_VAR]=	new Method("localScope",		SCOPES[ScopeSupport.SCOPE_VAR],new Type[]{}); 
        }
    // Argument argumentsScope (boolean)
    public final static Method METHOD_ARGUMENT_BIND=new Method("argumentsScope",SCOPES[Scope.SCOPE_ARGUMENTS],new Type[]{Types.BOOLEAN_VALUE});
    public final static Method METHOD_VAR_BIND=new Method("localScope",SCOPES[ScopeSupport.SCOPE_VAR],new Type[]{Types.BOOLEAN_VALUE});
    
    public final static Method METHOD_LOCAL_EL=new Method("localGet",Types.OBJECT,new Type[]{Types.BOOLEAN_VALUE, Types.OBJECT});
    public final static Method METHOD_LOCAL_BIND=new Method("localGet",Types.OBJECT,new Type[]{Types.BOOLEAN_VALUE});
    public final static Method METHOD_LOCAL_TOUCH=new Method("localTouch",			Types.OBJECT,new Type[]{});
    public final static Method METHOD_THIS_BIND=new Method("thisGet",Types.OBJECT,new Type[]{Types.BOOLEAN_VALUE});
    public final static Method METHOD_THIS_TOUCH=new Method("thisTouch",			Types.OBJECT,new Type[]{});
	
    
    
    public final static Type SCOPE_ARGUMENT=		Type.getType(Argument.class);
    
    
    public static Type invokeScope(GeneratorAdapter adapter, int scope) {
		return invokeScope(adapter,TypeScope.METHODS[scope],Types.PAGE_CONTEXT);
	}
	
	public static Type invokeScope(GeneratorAdapter adapter, Method m, Type type) {
		if(type==null) 
			type=Types.PAGE_CONTEXT;
			adapter.invokeVirtual(type,m);
			return m.getReturnType();
	}
    
}
