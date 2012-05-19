package railo.runtime.type.scope;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import railo.runtime.ComponentScope;
import railo.runtime.PageContext;
import railo.runtime.PageContextImpl;
import railo.runtime.config.Config;
import railo.runtime.debug.DebuggerImpl;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.type.Collection;
import railo.runtime.type.KeyImpl;
import railo.runtime.type.Query;
import railo.runtime.type.Struct;
import railo.runtime.type.StructImpl;
import railo.runtime.type.UDF;
import railo.runtime.type.dt.DateTime;
import railo.runtime.type.util.KeyConstants;
import railo.runtime.type.util.StructSupport;
import railo.runtime.util.QueryStack;
import railo.runtime.util.QueryStackImpl;

/**
 * Undefined Scope
 */
public final class UndefinedImpl extends StructSupport implements Undefined {

	private static final long serialVersionUID = -5626787508494702023L;

	private Scope[] scopes;
	private QueryStackImpl qryStack=new QueryStackImpl();
	private Variables variable;
	private boolean allowImplicidQueryCall;
	private boolean checkArguments;
	


	private boolean localAlways;
	private short type;
	private boolean isInit;
	private Local local;
	private Argument argument;
	private PageContextImpl pc;
	private boolean debug;
	
	/**
	 * constructor of the class
	 * @param pageContextImpl 
	 * @param type type of the undefined scope (ServletConfigImpl.SCOPE_STRICT;ServletConfigImpl.SCOPE_SMALL;ServletConfigImpl.SCOPE_STANDART)
	 */
	public UndefinedImpl(PageContextImpl pc, short type) {
		this.type=type;
		this.pc=pc;
		this.debug=pc.getConfig().debug();
	}
	
	
	/**
     * @see railo.runtime.type.scope.Undefined#localScope()
     */
	public Local localScope() {
		return local;
	}
	
	/**
	 * @see railo.runtime.type.scope.Undefined#argumentsScope()
	 */
	public Argument argumentsScope() {
		return argument;
	}

	/**
	 * @see railo.runtime.type.scope.Undefined#variablesScope()
	 */
	public Variables variablesScope() {
		return variable;
	}
	
	/**
	 * @see railo.runtime.type.scope.Undefined#setMode(int)
	 */
	public int setMode(int mode) {
		int m=Undefined.MODE_NO_LOCAL_AND_ARGUMENTS;
		if(checkArguments) {
			if(localAlways)m=Undefined.MODE_LOCAL_OR_ARGUMENTS_ALWAYS;
			else m=Undefined.MODE_LOCAL_OR_ARGUMENTS_ONLY_WHEN_EXISTS;
		}
		
		checkArguments=mode!=Undefined.MODE_NO_LOCAL_AND_ARGUMENTS;
		localAlways=mode==Undefined.MODE_LOCAL_OR_ARGUMENTS_ALWAYS;
		return m;
	}
	
	public boolean getLocalAlways(){
		return localAlways;
	}
	
	
	/**
     * @see railo.runtime.type.scope.Undefined#setFunctionScopes(railo.runtime.type.scope.Scope, railo.runtime.type.scope.Scope)
     */
	public void setFunctionScopes(Local local, Argument argument) {
		this.local=local;
		this.argument=argument;
	}

	/**
     * @see railo.runtime.type.scope.Undefined#getCollectionStack()
     */
	public QueryStack getCollectionStack() {
		return getQueryStack();
	}
	
	/**
	 * @see railo.runtime.type.scope.Undefined#getQueryStack()
	 */
	public QueryStack getQueryStack() {
		return qryStack;
	}
	
	/**
     * @see railo.runtime.type.scope.Undefined#setCollectionStack(railo.runtime.util.QueryStack)
     */
	public void setCollectionStack(QueryStack collStack) {
		setQueryStack(collStack);
	}
	
	/**
	 * @see railo.runtime.type.scope.Undefined#setQueryStack(railo.runtime.util.QueryStack)
	 */
	public void setQueryStack(QueryStack qryStack) {
		this.qryStack=(QueryStackImpl) qryStack;
	}
	
	/**
     * @see railo.runtime.type.scope.Undefined#addCollection(railo.runtime.type.Query)
     */
	public void addCollection(Query coll) {
		addQuery(coll);
	}

	/**
	 * @see railo.runtime.type.scope.Undefined#addQuery(railo.runtime.type.Query)
	 */
	public void addQuery(Query qry) {
		if(allowImplicidQueryCall)
			qryStack.addQuery(qry);
	}

	/**
     * @see railo.runtime.type.scope.Undefined#removeCollection()
     */
	public void removeCollection() {
		removeQuery();
	}

	/**
	 * @see railo.runtime.type.scope.Undefined#removeQuery()
	 */
	public void removeQuery() {
		if(allowImplicidQueryCall)
			qryStack.removeQuery();
	}

	/**
	 * @see railo.runtime.type.Collection#size()
	 */
	public int size() {
		return variable.size();
	}

	/**
	 * @see railo.runtime.type.Collection#keys()
	 */
	public Collection.Key[] keys() {
		return variable.keys();
	}

	/**
	 * @see railo.runtime.type.Collection#remove(railo.runtime.type.Collection.Key)
	 */
	public Object remove(Collection.Key key) throws PageException {
		if(checkArguments && local.containsKey(key))
			return local.remove(key);
		return variable.remove(key);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#removeEL(railo.runtime.type.Collection.Key)
	 */
	public Object removeEL(Collection.Key key) {
		if(checkArguments && local.containsKey(key))
			return local.removeEL(key);
		return variable.removeEL(key);
	}

	/**
	 * @see railo.runtime.type.Collection#clear()
	 */
	public void clear() {
		variable.clear();
	}
	
	public Object get(Collection.Key key) throws PageException {
		
		Object rtn=null;
		if(checkArguments) {
		    rtn=local.get(key,null);
		    if(rtn!=null) return rtn;

		    rtn=argument.getFunctionArgument(key,null);
		    if(rtn!=null) {
		    	if(debug) debugCascadedAccess(pc,argument.getTypeAsString(), key);
				return rtn;
		    }
		}
		
		// get data from queries
		if(allowImplicidQueryCall && !qryStack.isEmpty()) {
			rtn=qryStack.getDataFromACollection(pc,key);
			if(rtn!=null) {
				if(debug) debugCascadedAccess(pc,"query", key);
				return rtn;
		    }
		}
		
		// variable
		rtn=variable.get(key,null);
		if(rtn!=null) {
			if(debug && checkArguments) debugCascadedAccess(pc,variable,rtn, key);
			return rtn;
	    }
		
		// thread scopes
		if(pc.hasFamily()) {
			rtn = pc.getThreadScope(key,null);
			if(rtn!=null) {
				if(debug) debugCascadedAccess(pc,"thread", key);
				return rtn;
			}
		}
		
		// get a scope value
		for(int i=0;i<scopes.length;i++) {
		    rtn=scopes[i].get(key,null);
			if(rtn!=null) {
				if(debug) debugCascadedAccess(pc,scopes[i].getTypeAsString(),key);
				return rtn;
			}
		}
		throw new ExpressionException("variable ["+key.getString()+"] doesn't exist");
	}
	
	public static void debugCascadedAccess(PageContext pc,Variables var, Object value, Collection.Key key) {
		if(var instanceof ComponentScope){
			if(key.equals(KeyConstants._THIS) || key.equals(KeyConstants._SUPER)) return;
			if(value instanceof UDF) {
				return;
			}
		}
		
		debugCascadedAccess(pc,"variables", key);
	}
	
	public static void debugCascadedAccess(PageContext pc,String name, Collection.Key key) {
		if(pc!=null)pc.getDebugger().addImplicitAccess(name,key.getString());
	}
	
	/**
     * @see railo.runtime.type.scope.Undefined#getCollection(java.lang.String)
     */
	public Object getCollection(String key) throws PageException {
		return getCollection(KeyImpl.init(key));
	}
	
	public Struct getScope(Collection.Key key) {
		Object rtn=null;
		Struct sct=new StructImpl(Struct.TYPE_LINKED);
		
		if(checkArguments) {
		    rtn=local.get(key,null);
		    if(rtn!=null) sct.setEL(KeyImpl.LOCAL, rtn);
		    rtn=argument.getFunctionArgument(key,null);
		    if(rtn!=null) sct.setEL(KeyImpl.ARGUMENTS, rtn);
		}
				
		// get data from queries
		if(allowImplicidQueryCall && !qryStack.isEmpty()) {
			rtn=qryStack.getColumnFromACollection(key);
			if(rtn!=null) sct.setEL(KeyConstants._query, rtn);
		}
		
		// variable
		rtn=variable.get(key,null);
		if(rtn!=null) {
			sct.setEL(KeyImpl.VARIABLES, rtn);
		}
		
		// thread scopes
		if(pc.hasFamily()) {
			rtn = pc.getThreadScope(key,null);
			if(rtn!=null) sct.setEL(KeyImpl.THREAD, rtn); 
		}
		
		// get a scope value
		for(int i=0;i<scopes.length;i++) {
			rtn=scopes[i].get(key,null);
			if(rtn!=null) {
				sct.setEL(KeyImpl.init(scopes[i].getTypeAsString()), rtn); 
			}
		}
		return sct;
	}


	/**
	 * return a list of String with the scope names
	 * @param key
	 * @return
	 */
	public List<String> getScopeNames() {
		List<String> scopeNames=new ArrayList<String>();
		
		if(checkArguments) {
			scopeNames.add("local");
			scopeNames.add("arguments");
		}
		scopeNames.add("variables");
		
		// thread scopes
		if(pc.hasFamily()) {
			String[] names = pc.getThreadScopeNames();
			for(int i=0;i<names.length;i++)scopeNames.add(i,names[i]);
		}
		
		for(int i=0;i<scopes.length;i++) {
			scopeNames.add((scopes[i]).getTypeAsString()); 
		}
		return scopeNames;
	}

	public Object getCollection(Key key) throws PageException {
		Object rtn=null;
		
		if(checkArguments) {
		    rtn=local.get(key,null);
		    if(rtn!=null) return rtn;
		    rtn=argument.getFunctionArgument(key,null);
		    if(rtn!=null) {
		    	if(debug)debugCascadedAccess(pc,argument.getTypeAsString(), key);
		    	return rtn;
		    }
		}
				
		// get data from queries
		if(allowImplicidQueryCall && !qryStack.isEmpty()) {
			rtn=qryStack.getColumnFromACollection(key);
			if(rtn!=null) {
				if(debug)debugCascadedAccess(pc,"query", key);
				return rtn;
			}
		}
		
		// variable
		rtn=variable.get(key,null);
		if(rtn!=null) {
			if(debug && checkArguments) debugCascadedAccess(pc,variable,rtn, key);
			return rtn;
		}
		
		// thread scopes
		if(pc.hasFamily()) {
			rtn = pc.getThreadScope(key,null);
			if(rtn!=null) {
				if(debug) debugCascadedAccess(pc,"thread", key);
				return rtn;
			}
		}
		
		// get a scope value
		for(int i=0;i<scopes.length;i++) {
			rtn=scopes[i].get(key,null);
			if(rtn!=null) {
				if(debug)debugCascadedAccess(pc,scopes[i].getTypeAsString(),key);
				return rtn;
			}
		}
		throw new ExpressionException("variable ["+key.getString()+"] doesn't exist");
	}

    public Object get(Collection.Key key, Object defaultValue) {
		Object rtn=null;
        
        if(checkArguments) {
            rtn=local.get(key,null);
            if(rtn!=null) return rtn;
            rtn=argument.getFunctionArgument(key,null);
            if(rtn!=null) {
            	if(debug) debugCascadedAccess(pc,argument.getTypeAsString(), key);
				return rtn;
            }
        }
        
        // get data from queries
        if(allowImplicidQueryCall && !qryStack.isEmpty()) {
            rtn=qryStack.getDataFromACollection(key);
            if(rtn!=null) {
            	if(debug) debugCascadedAccess(pc,"query", key);
				return rtn;
            }
        }
        
        // variable
        rtn=variable.get(key,null);
        if(rtn!=null) {
        	if(debug && checkArguments) debugCascadedAccess(pc,variable, rtn, key);
			return rtn;
        }
		
		// thread scopes
		if(pc.hasFamily()) {
			rtn = pc.getThreadScope(key,null);
			if(rtn!=null) {
				if(debug && checkArguments) debugCascadedAccess(pc,"thread", key);
				return rtn;
			}
		}
        
        // get a scope value
        for(int i=0;i<scopes.length;i++) {
            rtn=scopes[i].get(key,null);
            if(rtn!=null) {
            	if(debug) debugCascadedAccess(pc,scopes[i].getTypeAsString(), key);
    			return rtn;
            }
        }
        return defaultValue;
    }
    
    
    /**
     * @see railo.runtime.type.scope.Undefined#getCascading(java.lang.String)
     */
    public Object getCascading(String strKey) {
        return getCascading(KeyImpl.init(strKey));
    }


	/**
	 *
	 * @see railo.runtime.type.scope.Undefined#getCascading(railo.runtime.type.Collection.Key)
	 */
	public Object getCascading(Collection.Key key) {
        Object rtn=null;
          
        // get a scope value
        for(int i=0;i<scopes.length;i++) {
            rtn=scopes[i].get(key,null);
            if(rtn!=null) {
                return rtn;
            }
        }
        return null;
	}

	/**
	 * @see railo.runtime.type.Collection#setEL(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object setEL(Collection.Key key, Object value) {
		if(checkArguments) {
            if(localAlways || local.containsKey(key))     return local.setEL(key,value);
            if(argument.containsFunctionArgumentKey(key))  {
            	if(debug)debugCascadedAccess(pc,argument.getTypeAsString(), key);
            	return argument.setEL(key,value);
            }
        }
			
		if(debug && checkArguments)debugCascadedAccess(pc,variable.getTypeAsString(), key);
    	return variable.setEL(key,value);
	}

	/**
	 *
	 * @see railo.runtime.type.Collection#set(railo.runtime.type.Collection.Key, java.lang.Object)
	 */
	public Object set(Collection.Key key, Object value) throws PageException {
		if(checkArguments) {
        	if(localAlways || local.containsKey(key))     return local.set(key,value);
            if(argument.containsFunctionArgumentKey(key))  {
            	if(debug)debugCascadedAccess(pc,argument.getTypeAsString(), key);
            	return argument.set(key,value);
            }
            
        }
		if(debug && checkArguments)debugCascadedAccess(pc,variable.getTypeAsString(), key);
    	return variable.set(key,value);
	}
	
	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		return variable.toDumpData(pageContext, maxlevel,dp);
	}

	/**
	 * @see railo.runtime.type.Collection#keyIterator()
	 */
	public Iterator<Collection.Key> keyIterator() {
		return variable.keyIterator();
	}
    
    @Override
	public Iterator<String> keysAsStringIterator() {
    	return variable.keysAsStringIterator();
    }
	
	@Override
	public Iterator<Entry<Key, Object>> entryIterator() {
		return variable.entryIterator();
	}
	
	@Override
	public Iterator<Object> valueIterator() {
		return variable.valueIterator();
	}
	
	/**
	 * @see railo.runtime.type.scope.Scope#isInitalized()
	 */
	public boolean isInitalized() {
		return isInit;
	}

	/**
	 * @see railo.runtime.type.scope.Scope#initialize(railo.runtime.PageContext)
	 */
	public void initialize(PageContext pc) {
		if(isInitalized()) return;
		isInit=true;
		variable=pc.variablesScope();
        argument=pc.argumentsScope();
		local=pc.localScope();
		allowImplicidQueryCall=pc.getConfig().allowImplicidQueryCall();
        type=pc.getConfig().getScopeCascadingType();
        
		// Strict
		if(type==Config.SCOPE_STRICT) {
			//print.ln("strict");
			scopes=new Scope[] {};
		}
		// small
		else if(type==Config.SCOPE_SMALL) {
			//print.ln("small");
			if(pc.getConfig().mergeFormAndURL()) {
				scopes=new Scope[] {
						pc.formScope()
					};
			}
			else {
				scopes=new Scope[] {
						pc.urlScope(),
						pc.formScope()
					};
			}
		}
		// standard
		else  {
			reinitialize( pc);
		}
		
		
	}

	public void reinitialize(PageContext pc) {
		if(type!=Config.SCOPE_STANDARD) return;
		Client cs = pc.clientScopeEL();
//		print.ln("standard");
		if(pc.getConfig().mergeFormAndURL()) {
            scopes=new Scope[cs==null?3:4]; 
            scopes[0]=pc.cgiScope();
            scopes[1]=pc.formScope();
            scopes[2]=pc.cookieScope();
            if(cs!=null)scopes[3]=cs;
		}
		else {
            scopes=new Scope[cs==null?4:5]; 
            scopes[0]=pc.cgiScope();
            scopes[1]=pc.urlScope();
            scopes[2]=pc.formScope();
            scopes[3]=pc.cookieScope();
            if(cs!=null)scopes[4]=cs;
		}
	}


	/**
	 * @see railo.runtime.type.scope.Scope#release()
	 */
	public void release() {
		isInit=false;
		argument=null;
		local=null;
		variable=null;
		scopes=null;
		checkArguments=false;
		localAlways=false;
		if(allowImplicidQueryCall)qryStack.clear();
		
		
		//threads=null;
		//hasThreads=false;
	}

	/**
	 * @see railo.runtime.type.Collection#duplicate(boolean)
	 */
	public Collection duplicate(boolean deepCopy) {
		UndefinedImpl dupl = new UndefinedImpl(pc, type);
		dupl.allowImplicidQueryCall=allowImplicidQueryCall;
		dupl.checkArguments=checkArguments;
		dupl.argument=deepCopy?(Argument)argument.duplicate(deepCopy):argument;
		dupl.isInit=isInit;
		dupl.local=deepCopy?(Local)local.duplicate(deepCopy):local;
		dupl.localAlways=localAlways;
		dupl.qryStack= (deepCopy?(QueryStackImpl)qryStack.duplicate(deepCopy):qryStack);
		
		dupl.variable=deepCopy?(Variables)variable.duplicate(deepCopy):variable;
		dupl.pc=pc;
		dupl.debug=debug;
		
		// scopes
		if(deepCopy) {
			dupl.scopes=new Scope[scopes.length];
			for(int i=0;i<scopes.length;i++) {
				dupl.scopes[i]=(Scope)scopes[i].duplicate(deepCopy);
			}
		}
		else dupl.scopes=scopes;
		
		return dupl;
	}
	

	/**
	 *
	 * @see railo.runtime.type.Collection#containsKey(railo.runtime.type.Collection.Key)
	 */
	public boolean containsKey(Key key) {
        return get(key,null)!=null;
	}

    /**
     * @see railo.runtime.op.Castable#castToString()
     */
    public String castToString() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Struct to String",
          "Use Build-In-Function \"serialize(Struct):String\" to create a String from Struct");
    }
    
	/**
	 * @see railo.runtime.type.util.StructSupport#castToString(java.lang.String)
	 */
	public String castToString(String defaultValue) {
		return defaultValue;
	}

    /**
     * @see railo.runtime.op.Castable#castToBooleanValue()
     */
    public boolean castToBooleanValue() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Struct to a boolean value");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }


    /**
     * @see railo.runtime.op.Castable#castToDoubleValue()
     */
    public double castToDoubleValue() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Struct to a number value");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return defaultValue;
    }


    /**
     * @see railo.runtime.op.Castable#castToDateTime()
     */
    public DateTime castToDateTime() throws ExpressionException {
        throw new ExpressionException("Can't cast Complex Object Type Struct to a Date");
    }
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return defaultValue;
    }

	/**
	 * @see railo.runtime.op.Castable#compare(boolean)
	 */
	public int compareTo(boolean b) throws ExpressionException {
		throw new ExpressionException("can't compare Complex Object Type Struct with a boolean value");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct with a DateTime Object");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct with a numeric value");
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) throws PageException {
		throw new ExpressionException("can't compare Complex Object Type Struct with a String");
	}

    /**
     * @see railo.runtime.type.scope.Undefined#setVariableScope(railo.runtime.type.scope.Scope)
     */
    public void setVariableScope(Variables scope) {
    	variable=scope;
    }

    /**
     * @see railo.runtime.type.scope.Scope#getType()
     */
    public int getType() {
        return SCOPE_UNDEFINED;
    }

    /**
     * @see railo.runtime.type.scope.Scope#getTypeAsString()
     */
    public String getTypeAsString() {
        return "undefined";
    }


	/**
	 * @return the allowImplicidQueryCall
	 */
	public boolean isAllowImplicidQueryCall() {
		return allowImplicidQueryCall;
	}


	/**
	 * @param allowImplicidQueryCall the allowImplicidQueryCall to set
	 */
	public boolean setAllowImplicidQueryCall(boolean allowImplicidQueryCall) {
		boolean old=this.allowImplicidQueryCall;
		this.allowImplicidQueryCall = allowImplicidQueryCall;
		return old;
	}
	
	/**
	 * @return the checkArguments
	 */
	public boolean getCheckArguments() {
		return checkArguments;
	}


}