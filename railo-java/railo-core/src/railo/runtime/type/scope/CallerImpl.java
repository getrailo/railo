package railo.runtime.type.scope;

import java.util.Iterator;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.Scope;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.util.StructSupport;

/**
 * caller scope
 */
public final class CallerImpl extends StructSupport implements Caller  {
    

	private PageContext pc;
    private Variables variablesScope;
    private Scope localScope;
    private Scope argumentsScope;
    private boolean checkArgs;
    
    
    /**
     *
     * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
     */
    public Object get(Collection.Key key) throws PageException  {
    	
    	char c=key.lowerCharAt(0);
		if('a'==c) {
			if(ScopeSupport.APPLICATION.equalsIgnoreCase(key)) 		return pc.applicationScope();
			else if(ScopeSupport.ARGUMENTS.equalsIgnoreCase(key))		return pc.argumentsScope();
		}
		else if('c'==c) {
			if(ScopeSupport.CGI.equalsIgnoreCase(key))					return pc.cgiScope();
			if(ScopeSupport.COOKIE.equalsIgnoreCase(key))				return pc.cookieScope();
			if(ScopeSupport.CLIENT.equalsIgnoreCase(key))				return pc.clientScope();
			if(ScopeSupport.CLUSTER.equalsIgnoreCase(key))				return pc.clusterScope(); 
		}
		else if('f'==c) {
			if(ScopeSupport.FORM.equalsIgnoreCase(key))				return pc.formScope();
		}
		else if('r'==c) {
			if(ScopeSupport.REQUEST.equalsIgnoreCase(key))				return pc.requestScope();
		}
		else if('s'==c) {
			if(ScopeSupport.SESSION.equalsIgnoreCase(key))				return pc.sessionScope();
			if(ScopeSupport.SERVER.equalsIgnoreCase(key))				return pc.serverScope();
		}
		else if('u'==c) {
			if(ScopeSupport.URL.equalsIgnoreCase(key))					return pc.urlScope();
		}
		else if('v'==c) {
			if(ScopeSupport.VARIABLES.equalsIgnoreCase(key))			return variablesScope;
		}
    	
    	// upper variable scope
        Object o;
        
        if(checkArgs) {
            o=localScope.get(key,null);
            if(o!=null) return o;
            o=argumentsScope.get(key,null);
            if(o!=null) return o;
        }
        o=variablesScope.get(key,null);
        if(o!=null) return o;
        
        // get from cascaded scopes
        o=pc.undefinedScope().getCascading(key);
        if(o!=null) return o;
        
        /*
        // get scopes
        if(key.equalsIgnoreCase(VARIABLES)) {
            return variablesScope;//new StructImpl(getMap());
        }
        
        scope=VariableInterpreter.scopeKey2Int(key);
        if(scope!=Scope.SCOPE_UNDEFINED)
            return pc.scope(scope);
        */
        throw new ExpressionException("["+key.getString() +"] not found in caller scope");
    }
    
    /**
     *
     * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key, java.lang.Object)
     */
    public Object get(Collection.Key key, Object defaultValue) {
    	
    	char c=key.lowerCharAt(0);
		if('a'==c) {
			if(ScopeSupport.APPLICATION.equalsIgnoreCase(key)){
				try {
					return pc.applicationScope();
				} 
				catch (PageException e) {}
			}
			else if(ScopeSupport.ARGUMENTS.equalsIgnoreCase(key))		return pc.argumentsScope();
		}
		else if('c'==c) {
			if(ScopeSupport.CGI.equalsIgnoreCase(key))					return pc.cgiScope();
			if(ScopeSupport.COOKIE.equalsIgnoreCase(key))				return pc.cookieScope();
			if(ScopeSupport.CLIENT.equalsIgnoreCase(key)){
				try {
					return pc.clientScope();
				} 
				catch (PageException e) {}
			}
			if(ScopeSupport.CLUSTER.equalsIgnoreCase(key)){
				try {
					return pc.clusterScope();
				}
				catch (PageException e) {} 
			}
		}
		else if('f'==c) {
			if(ScopeSupport.FORM.equalsIgnoreCase(key))				return pc.formScope();
		}
		else if('r'==c) {
			if(ScopeSupport.REQUEST.equalsIgnoreCase(key))				return pc.requestScope();
		}
		else if('s'==c) {
			if(ScopeSupport.SESSION.equalsIgnoreCase(key)){
				try {
					return pc.sessionScope();
				} 
				catch (PageException e) {}
			}
			if(ScopeSupport.SERVER.equalsIgnoreCase(key)){
				try {
					return pc.serverScope();
				} 
				catch (PageException e) {}
			}
		}
		else if('u'==c) {
			if(ScopeSupport.URL.equalsIgnoreCase(key))					return pc.urlScope();
		}
		else if('v'==c) {
			if(ScopeSupport.VARIABLES.equalsIgnoreCase(key))			return variablesScope;
		}
    	
    	
    	
    	Object o;
        if(checkArgs) {
            o=localScope.get(key,null);
            if(o!=null) return o;
            o=argumentsScope.get(key,null);
            if(o!=null) return o;
        }
        o=variablesScope.get(key,null);
        if(o!=null) return o;
        
        
        // get from cascaded scopes
        o=pc.undefinedScope().getCascading(key);
        if(o!=null) return o;
        
        return defaultValue;
    }
    
    /**
     * @see railo.runtime.type.Scope#initialize(railo.runtime.PageContext)
     */
    public void initialize(PageContext pc) {
        this.pc=pc;
    }

    /**
     * @see railo.runtime.type.scope.Caller#setScope(railo.runtime.type.Scope, railo.runtime.type.Scope, railo.runtime.type.Scope, boolean)
     */
    public void setScope(Scope variablesScope, Scope localScope, Scope argumentsScope, boolean checkArgs) {
        this.variablesScope = (Variables)variablesScope;
        this.localScope = localScope;
        this.argumentsScope = argumentsScope;
        this.checkArgs = checkArgs;
    }

    /**
     * @see railo.runtime.type.Scope#isInitalized()
     */
    public boolean isInitalized() {
        return pc!=null;
    }

    /**
     * @see railo.runtime.type.Scope#release()
     */
    public void release() {
        this.pc=null;
    }

    /**
     * @see railo.runtime.type.Collection#size()
     */
    public int size() {
        return variablesScope.size();
    }

    /**
     * @see railo.runtime.type.Collection#keysAsString()
     */
    public String[] keysAsString() {
        return variablesScope.keysAsString();
    }

    /**
     * @see railo.runtime.type.Collection#keys()
     */
    public Collection.Key[] keys() {
        return variablesScope.keys();
    }

	/**
	 *
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Collection.Key key) throws PageException {
        if(checkArgs && localScope.containsKey(key))
            return localScope.remove(key);
        return variablesScope.remove(key);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Collection.Key key) {
        if(checkArgs && localScope.containsKey(key))
            return localScope.removeEL(key);
        return variablesScope.removeEL(key);
	}

    /**
     * @see railo.runtime.type.Collection#clear()
     */
    public void clear() {
        variablesScope.clear();
    }

	public Object set(Key key, Object value) throws PageException {
        if(checkArgs) {
            if(localScope.containsKey(key))     return localScope.set(key,value);
            if(argumentsScope.containsKey(key))  return argumentsScope.set(key,value);
        }
        return variablesScope.set(key,value);
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Key key, Object value) {
        if(checkArgs) {
            if(localScope.containsKey(key))     return localScope.setEL(key,value);
            if(argumentsScope.containsKey(key))  return argumentsScope.setEL(key,value);
        }
        return variablesScope.setEL(key,value);
	}

    /**
     * @see railo.runtime.type.Iteratorable#keyIterator()
     */
    public Iterator keyIterator() {
        return variablesScope.keyIterator();
    }
    
    /**
     * @see railo.runtime.type.Collection#duplicate(boolean)
     */
    public Collection duplicate(boolean deepCopy) {
        return variablesScope.duplicate(deepCopy);
    }

	/**
	 *
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Collection.Key key) {
        return get(key,null)!=null;
	}

    /**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
	    return variablesScope.toDumpData(pageContext, --maxlevel,dp);
    }

    /**
     * @see railo.runtime.op.Castable#castToString()
     */
    public String castToString() throws PageException {
        return variablesScope.castToString();
    }

    /**
     * @see railo.runtime.op.Castable#castToString(java.lang.String)
     */
    public String castToString(String defaultValue) {
        return variablesScope.castToString(defaultValue);
    }
    
    /**
     * @see railo.runtime.op.Castable#castToBooleanValue()
     */
    public boolean castToBooleanValue() throws PageException {
        return variablesScope.castToBooleanValue();
    }
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return variablesScope.castToBoolean(defaultValue);
    }

    /**
     * @see railo.runtime.op.Castable#castToDoubleValue()
     */
    public double castToDoubleValue() throws PageException {
        return variablesScope.castToDoubleValue();
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return variablesScope.castToDoubleValue(defaultValue);
    }

    /**
     * @see railo.runtime.op.Castable#castToDateTime()
     */
    public DateTime castToDateTime() throws PageException {
        return variablesScope.castToDateTime();
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return variablesScope.castToDateTime(defaultValue);
    }


	/**
	 * @throws PageException 
	 * @see railo.runtime.op.Castable#compare(boolean)
	 */
	public int compareTo(boolean b) throws PageException {
		return variablesScope.compareTo(b);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return variablesScope.compareTo(dt);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return variablesScope.compareTo(d);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		return variablesScope.compareTo(str);
	}

    /**
     * @see railo.runtime.type.Scope#getType()
     */
    public int getType() {
        return SCOPE_CALLER;
    }

    /**
     * @see railo.runtime.type.Scope#getTypeAsString()
     */
    public String getTypeAsString() {
        return "caller";
    }

	/**
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return variablesScope.containsValue(value);
	}

	/**
	 * @see java.util.Map#values()
	 */
	public java.util.Collection values() {
		return variablesScope.values();
	}


    /** FUTURE add to intrface Caller
	 * @return the variablesScope
	 */
	public Variables getVariablesScope() {
		return variablesScope;
	}

	/**FUTURE add to intrface Caller
	 * @return the localScope
	 */
	public Local getLocalScope() {
		return (Local)localScope;
	}

	/**FUTURE add to intrface Caller
	 * @return the argumentsScope
	 */
	public Argument getArgumentsScope() {
		return (Argument)argumentsScope;
	}
}