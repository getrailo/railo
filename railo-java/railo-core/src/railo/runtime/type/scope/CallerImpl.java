package railo.runtime.type.scope;

import java.util.Iterator;

import railo.runtime.PageContext;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Duplicator;
import railo.runtime.type.Collection;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.StructSupport;

/**
 * caller scope
 */
public final class CallerImpl extends StructSupport implements Caller  {

	private static final long serialVersionUID = -6228400815042475435L;
	
	private PageContext pc;
    private Variables variablesScope;
    private Local localScope;
    private Argument argumentsScope;
    private boolean checkArgs;
    
    
    /**
     *
     * @see railo.runtime.type.Collection#get(railo.runtime.type.Collection.Key)
     */
    public Object get(Collection.Key key) throws PageException  {
    	
    	char c=key.lowerCharAt(0);
		if('a'==c) {
			if(KeyConstants._application.equalsIgnoreCase(key)) 		return pc.applicationScope();
			else if(KeyConstants._arguments.equalsIgnoreCase(key))		return pc.argumentsScope();
		}
		else if('c'==c) {
			if(KeyConstants._cgi.equalsIgnoreCase(key))					return pc.cgiScope();
			if(KeyConstants._cookie.equalsIgnoreCase(key))				return pc.cookieScope();
			if(KeyConstants._client.equalsIgnoreCase(key))				return pc.clientScope();
			if(KeyConstants._cluster.equalsIgnoreCase(key))				return pc.clusterScope(); 
		}
		else if('f'==c) {
			if(KeyConstants._form.equalsIgnoreCase(key))				return pc.formScope();
		}
		else if('r'==c) {
			if(KeyConstants._request.equalsIgnoreCase(key))				return pc.requestScope();
		}
		else if('l'==c) {
			if(KeyConstants._local.equalsIgnoreCase(key) && checkArgs)	return pc.localScope();
		}
		else if('s'==c) {
			if(KeyConstants._session.equalsIgnoreCase(key))				return pc.sessionScope();
			if(KeyConstants._server.equalsIgnoreCase(key))				return pc.serverScope();
		}
		else if('u'==c) {
			if(KeyConstants._url.equalsIgnoreCase(key))					return pc.urlScope();
		}
		else if('v'==c) {
			if(KeyConstants._variables.equalsIgnoreCase(key))			return variablesScope;
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
			if(KeyConstants._application.equalsIgnoreCase(key)){
				try {
					return pc.applicationScope();
				} 
				catch (PageException e) {}
			}
			else if(checkArgs && KeyConstants._arguments.equalsIgnoreCase(key))		return pc.argumentsScope();
		}
		else if('c'==c) {
			if(KeyConstants._cgi.equalsIgnoreCase(key))					return pc.cgiScope();
			if(KeyConstants._cookie.equalsIgnoreCase(key))				return pc.cookieScope();
			if(KeyConstants._client.equalsIgnoreCase(key)){
				try {
					return pc.clientScope();
				} 
				catch (PageException e) {}
			}
			if(KeyConstants._cluster.equalsIgnoreCase(key)){
				try {
					return pc.clusterScope();
				}
				catch (PageException e) {} 
			}
		}
		else if('f'==c) {
			if(KeyConstants._form.equalsIgnoreCase(key))				return pc.formScope();
		}
		else if('r'==c) {
			if(KeyConstants._request.equalsIgnoreCase(key))				return pc.requestScope();
		}
		else if('l'==c) {
			if(checkArgs && KeyConstants._local.equalsIgnoreCase(key))	return pc.localScope();
		}
		else if('s'==c) {
			if(KeyConstants._session.equalsIgnoreCase(key)){
				try {
					return pc.sessionScope();
				} 
				catch (PageException e) {}
			}
			if(KeyConstants._server.equalsIgnoreCase(key)){
				try {
					return pc.serverScope();
				} 
				catch (PageException e) {}
			}
		}
		else if('u'==c) {
			if(KeyConstants._url.equalsIgnoreCase(key))					return pc.urlScope();
		}
		else if('v'==c) {
			if(KeyConstants._variables.equalsIgnoreCase(key))			return variablesScope;
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
     * @see railo.runtime.type.scope.Scope#initialize(railo.runtime.PageContext)
     */
    public void initialize(PageContext pc) {
        this.pc=pc;
    }

    /**
     * @see railo.runtime.type.scope.Caller#setScope(railo.runtime.type.scope.Scope, railo.runtime.type.scope.Scope, railo.runtime.type.scope.Scope, boolean)
     */
    public void setScope(Variables variablesScope, Local localScope, Argument argumentsScope, boolean checkArgs) {
        this.variablesScope = variablesScope;
        this.localScope = localScope;
        this.argumentsScope = argumentsScope;
        this.checkArgs = checkArgs;
    }

    /**
     * @see railo.runtime.type.scope.Scope#isInitalized()
     */
    public boolean isInitalized() {
        return pc!=null;
    }

    @Override
    public void release() {
        this.pc=null;
    }

    @Override
    public void release(PageContext pc) {
        this.pc=null;
    }

    /**
     * @see railo.runtime.type.Collection#size()
     */
    public int size() {
        return variablesScope.size();
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
    public Iterator<Collection.Key> keyIterator() {
        return variablesScope.keyIterator();
    }
    
    @Override
	public Iterator<String> keysAsStringIterator() {
    	return variablesScope.keysAsStringIterator();
    }
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return variablesScope.entryIterator();
	}
	
	@Override
	public Iterator<Object> valueIterator() {
		return variablesScope.valueIterator();
	}
    
    /**
     * @see railo.runtime.type.Collection#duplicate(boolean)
     */
    public Collection duplicate(boolean deepCopy) {
        return (Collection) Duplicator.duplicate(variablesScope,deepCopy);
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
     * @see railo.runtime.type.scope.Scope#getType()
     */
    public int getType() {
        return SCOPE_CALLER;
    }

    /**
     * @see railo.runtime.type.scope.Scope#getTypeAsString()
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

	/**
	 * @see railo.runtime.type.scope.Caller#getVariablesScope()
	 */
	public Variables getVariablesScope() {
		return variablesScope;
	}

	/**
	 * @see railo.runtime.type.scope.Caller#getLocalScope()
	 */
	public Local getLocalScope() {
		return localScope;
	}

	/**
	 * @see railo.runtime.type.scope.Caller#getArgumentsScope()
	 */
	public Argument getArgumentsScope() {
		return argumentsScope;
	}
}